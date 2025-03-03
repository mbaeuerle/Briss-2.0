// $Id: SingleCluster.java 55 2011-02-22 21:45:59Z laborg $
/**
 * Copyright 2010 Gerhard Aigner
 * <p>
 * This file is part of BRISS.
 * <p>
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss.utils;

import at.laborg.briss.exception.CropException;
import at.laborg.briss.model.CropDefinition;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.SimpleNamedDestination;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public final class DocumentCropper {

	public static File crop(final CropDefinition cropDefinition, String password)
			throws IOException, DocumentException, CropException {

		// check if everything is ready
		if (!BrissFileHandling.checkValidStateAndCreate(cropDefinition.getDestinationFile()))
			throw new IOException("Destination file not valid");

		// read out necessary meta information
		var pdfMetaInformation = new PdfMetaInformation(cropDefinition.getSourceFile(), password);

		// first make a copy containing the right amount of pages
		File intermediatePdf = copyToMultiplePages(cropDefinition, pdfMetaInformation, password);

		// now crop all pages according to their ratios
		cropMultipliedFile(cropDefinition, intermediatePdf, pdfMetaInformation, password);
		return cropDefinition.getDestinationFile();
	}

	private static File copyToMultiplePages(final CropDefinition cropDefinition,
			final PdfMetaInformation pdfMetaInformation, String password) throws IOException, DocumentException {

		PdfReader reader = PDFReaderUtil.getPdfReader(cropDefinition.getSourceFile().getAbsolutePath(), password);
		HashMap<String, String> map = SimpleNamedDestination.getNamedDestination(reader, false);
		var document = new Document();

		File resultFile = File.createTempFile("cropped", ".pdf");
		try (OutputStream outputStream = Files.newOutputStream(resultFile.toPath())) {
			PdfSmartCopy pdfCopy = new PdfSmartCopy(document, outputStream);
			document.open();

			Map<Integer, List<String>> pageNrToDestinations = new HashMap<Integer, List<String>>();
			for (String single : map.keySet()) {
				StringTokenizer st = new StringTokenizer(map.get(single), " ");
				if (st.hasMoreElements()) {
					String pageNrString = (String) st.nextElement();
					int pageNr = Integer.parseInt(pageNrString);
					List<String> singleList = (pageNrToDestinations.get(pageNr));
					if (singleList == null) {
						singleList = new ArrayList<String>();
						singleList.add(single);
						pageNrToDestinations.put(pageNr, singleList);
					} else {
						singleList.add(single);
					}
				}
			}

			int outputPageNumber = 0;
			for (int pageNumber = 1; pageNumber <= pdfMetaInformation.getSourcePageCount(); pageNumber++) {

				PdfImportedPage pdfPage = pdfCopy.getImportedPage(reader, pageNumber);

				pdfCopy.addPage(pdfPage);
				outputPageNumber++;
				List<String> destinations = pageNrToDestinations.get(pageNumber);
				if (destinations != null) {
					for (String destination : destinations)
						pdfCopy.addNamedDestination(destination, outputPageNumber,
								new PdfDestination(PdfDestination.FIT));
				}
				List<float[]> rectangles = cropDefinition.getRectanglesForPage(pageNumber);
				for (int j = 1; j < rectangles.size(); j++) {
					pdfCopy.addPage(pdfPage);
					outputPageNumber++;
				}
			}
			document.close();
			pdfCopy.close();
			reader.close();
			return resultFile;
		}
	}

	private static void cropMultipliedFile(final CropDefinition cropDefinition, final File multipliedDocument,
			final PdfMetaInformation pdfMetaInformation, String password) throws DocumentException, IOException {

		PdfReader reader = PDFReaderUtil.getPdfReader(multipliedDocument.getAbsolutePath(), password);

		try (OutputStream outputStream = Files.newOutputStream(cropDefinition.getDestinationFile().toPath())) {
			PdfStamper stamper = new PdfStamper(reader, outputStream);
			stamper.setMoreInfo(pdfMetaInformation.getSourceMetaInfo());

			PdfDictionary pageDict;
			int newPageNumber = 1;

			for (int sourcePageNumber = 1; sourcePageNumber <= pdfMetaInformation
					.getSourcePageCount(); sourcePageNumber++) {

				List<float[]> rectangleList = cropDefinition.getRectanglesForPage(sourcePageNumber);

				// if no crop was selected do nothing
				if (rectangleList.isEmpty()) {
					newPageNumber++;
					continue;
				}

				for (float[] ratios : rectangleList) {
					pageDict = reader.getPageN(newPageNumber);

					List<Rectangle> boxes = new ArrayList<Rectangle>();
					boxes.add(reader.getBoxSize(newPageNumber, "media"));
					boxes.add(reader.getBoxSize(newPageNumber, "crop"));
					int rotation = reader.getPageRotation(newPageNumber);

					Rectangle scaledBox = RectangleHandler.calculateScaledRectangle(boxes, ratios, rotation);

					PdfArray scaleBoxArray = createScaledBoxArray(scaledBox);

					pageDict.put(PdfName.CROPBOX, scaleBoxArray);
					pageDict.put(PdfName.MEDIABOX, scaleBoxArray);
					// increment the pagenumber
					newPageNumber++;
				}
				int[] range = new int[2];
				range[0] = newPageNumber - 1;
				range[1] = pdfMetaInformation.getSourcePageCount() + (newPageNumber - sourcePageNumber);
				SimpleBookmark.shiftPageNumbers(pdfMetaInformation.getSourceBookmarks(), rectangleList.size() - 1,
						range);
			}
			stamper.setOutlines(pdfMetaInformation.getSourceBookmarks());
			stamper.close();
			reader.close();
		}
	}

	private static PdfArray createScaledBoxArray(final Rectangle scaledBox) {
		PdfArray scaleBoxArray = new PdfArray();
		scaleBoxArray.add(new PdfNumber(scaledBox.getLeft()));
		scaleBoxArray.add(new PdfNumber(scaledBox.getBottom()));
		scaleBoxArray.add(new PdfNumber(scaledBox.getRight()));
		scaleBoxArray.add(new PdfNumber(scaledBox.getTop()));
		return scaleBoxArray;
	}

	private static class PdfMetaInformation {

		private final int sourcePageCount;
		private final HashMap<String, String> sourceMetaInfo;
		private final List<HashMap<String, Object>> sourceBookmarks;

		public PdfMetaInformation(final File source, String password) throws IOException {
			PdfReader reader = PDFReaderUtil.getPdfReader(source.getAbsolutePath(), password);
			this.sourcePageCount = reader.getNumberOfPages();
			this.sourceMetaInfo = reader.getInfo();
			this.sourceBookmarks = SimpleBookmark.getBookmark(reader);
			reader.close();
		}

		public int getSourcePageCount() {
			return sourcePageCount;
		}

		public HashMap<String, String> getSourceMetaInfo() {
			return sourceMetaInfo;
		}

		public List<HashMap<String, Object>> getSourceBookmarks() {
			return sourceBookmarks;
		}
	}
}
