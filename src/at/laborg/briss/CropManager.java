/**
 * Copyright 2010 Gerhard Aigner
 * 
 * This file is part of BRISS.
 * 
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.laborg.briss.model.ClusterJob;
import at.laborg.briss.model.CropJob;
import at.laborg.briss.model.SingleCluster;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.SimpleBookmark;

public class CropManager {

	public static CropJob createCropJob(ClusterJob curClusterJob)
			throws IOException {
		File source = curClusterJob.getSource();
		if (source != null && source.exists()) {
			PdfReader reader = new PdfReader(source.getAbsolutePath());
			CropJob result = new CropJob(source, reader.getNumberOfPages(),
					reader.getInfo(), SimpleBookmark.getBookmark(reader));
			reader.close();
			result.setClusterCollection(curClusterJob.getClusterCollection());
			return result;
		}
		return null;
	}

	public static CropJob createCropJob(File source) throws IOException {
		CropJob result = null;
		if (source != null && source.exists()) {
			PdfReader reader = new PdfReader(source.getAbsolutePath());
			result = new CropJob(source, reader.getNumberOfPages(), reader
					.getInfo(), SimpleBookmark.getBookmark(reader));
			reader.close();
			return result;
		}
		return result;
	}

	public static void crop(CropJob cropJob) throws IOException,
			DocumentException {

		// first make a copy containing the right amount of pages
		File multipliedTmpFile = copyToMultiplePages(cropJob);

		// now crop all pages according to their ratios
		cropMultipliedFile(multipliedTmpFile, cropJob);
	}

	private static File copyToMultiplePages(CropJob cropJob)
			throws IOException, DocumentException {

		PdfReader reader = new PdfReader(cropJob.getSource().getAbsolutePath());
		Document document = new Document();

		File resultFile = File.createTempFile("cropped", ".pdf");
		PdfSmartCopy pdfCopy = new PdfSmartCopy(document, new FileOutputStream(
				resultFile));
		document.open();
		PdfImportedPage page;

		for (int pageNumber = 1; pageNumber <= cropJob.getSourcePageCount(); pageNumber++) {
			SingleCluster currentCluster = cropJob.getClusterCollection()
					.getSingleCluster(pageNumber);
			page = pdfCopy.getImportedPage(reader, pageNumber);
			pdfCopy.addPage(page);
			for (int j = 1; j < currentCluster.getRatiosList().size(); j++) {
				pdfCopy.addPage(page);
			}
		}
		document.close();
		pdfCopy.close();
		reader.close();
		return resultFile;
	}

	private static void cropMultipliedFile(File source, CropJob cropJob)
			throws FileNotFoundException, DocumentException, IOException {

		PdfReader reader = new PdfReader(source.getAbsolutePath());
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(
				cropJob.getDestinationFile()));
		stamper.setMoreInfo(cropJob.getSourceMetaInfo());

		PdfDictionary pageDict;
		int newPageNumber = 1;
		for (int origPageNumber = 1; origPageNumber <= cropJob.getSourcePageCount(); origPageNumber++) {
			SingleCluster cluster = cropJob.getClusterCollection().getSingleCluster(
					origPageNumber);

			// if no crop was selected do nothing
			if (cluster.getRatiosList().size() == 0) {
				newPageNumber++;
				continue;
			}

			for (Float[] ratios : cluster.getRatiosList()) {

				pageDict = reader.getPageN(newPageNumber);

				List<Rectangle> boxes = new ArrayList<Rectangle>();
				boxes.add(reader.getBoxSize(newPageNumber, "media"));
				boxes.add(reader.getBoxSize(newPageNumber, "crop"));
				int rotation = reader.getPageRotation(newPageNumber);

				Rectangle scaledBox = calculateScaledRectangle(boxes, ratios,
						rotation);

				PdfArray scaleBoxArray = new PdfArray();
				scaleBoxArray.add(new PdfNumber(scaledBox.getLeft()));
				scaleBoxArray.add(new PdfNumber(scaledBox.getBottom()));
				scaleBoxArray.add(new PdfNumber(scaledBox.getRight()));
				scaleBoxArray.add(new PdfNumber(scaledBox.getTop()));

				pageDict.put(PdfName.CROPBOX, scaleBoxArray);
				pageDict.put(PdfName.MEDIABOX, scaleBoxArray);
				// increment the pagenumber
				newPageNumber++;
			}
			int[] range = new int[2];
			range[0] = newPageNumber - 1;
			range[1] = cropJob.getSourcePageCount()
					+ (newPageNumber - origPageNumber);
			SimpleBookmark.shiftPageNumbers(cropJob.getSourceBookmarks(),
					cluster.getRatiosList().size() - 1, range);
		}
		stamper.setOutlines(cropJob.getSourceBookmarks());
		stamper.close();
		reader.close();
	}

	private static Rectangle calculateScaledRectangle(List<Rectangle> boxes,
			Float[] ratios, int rotation) {
		if (ratios == null || boxes.size() == 0)
			return null;
		Rectangle smallestBox = null;
		// find smallest box
		float smallestSquare = Float.MAX_VALUE;
		for (Rectangle box : boxes) {
			if (box != null) {
				if (smallestBox == null) {
					smallestBox = box;
				}
				if (smallestSquare > box.getWidth() * box.getHeight()) {
					// set new smallest box
					smallestSquare = box.getWidth() * box.getHeight();
					smallestBox = box;
				}
			}
		}
		if (smallestBox == null)
			return null; // no useable box was found

		// rotate the ratios according to the rotation of the page
		float[] rotRatios = rotateRatios(ratios, rotation);

		// use smallest box as basis for calculation
		Rectangle scaledBox = new Rectangle(smallestBox);

		scaledBox.setLeft(smallestBox.getLeft()
				+ (smallestBox.getWidth() * rotRatios[0]));
		scaledBox.setBottom(smallestBox.getBottom()
				+ (smallestBox.getHeight() * rotRatios[1]));
		scaledBox.setRight(smallestBox.getLeft()
				+ (smallestBox.getWidth() * (1 - rotRatios[2])));
		scaledBox.setTop(smallestBox.getBottom()
				+ (smallestBox.getHeight() * (1 - rotRatios[3])));

		return scaledBox;
	}

	/**
	 * Rotates the ratios counter clockwise until its at 0
	 * 
	 * @param ratios
	 * @param rotation
	 * @return
	 */
	private static float[] rotateRatios(Float[] ratios, int rotation) {
		float[] tmpRatios = new float[4];
		for (int i = 0; i < 4; i++) {
			tmpRatios[i] = ratios[i];
		}
		while (rotation > 0 && rotation < 360) {
			float tmpValue = tmpRatios[0];
			// left
			tmpRatios[0] = tmpRatios[1];
			// bottom
			tmpRatios[1] = tmpRatios[2];
			// right
			tmpRatios[2] = tmpRatios[3];
			// top
			tmpRatios[3] = tmpValue;
			rotation += 90;
		}
		return tmpRatios;
	}
}
