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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import at.laborg.briss.model.ClusterCollection;
import at.laborg.briss.model.ClusterJob;
import at.laborg.briss.model.SingleCluster;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;

public class ClusterManager {

	public static ClusterJob createClusterJob(File origFile)
			throws IOException, PdfException {

		PdfReader reader = new PdfReader(origFile.getAbsolutePath());
		ClusterJob clusterJob = new ClusterJob(origFile);
		reader.close();
		return clusterJob;
	}

	public static void clusterPages(ClusterJob clusterJob) throws IOException {
		PdfReader reader = new PdfReader(clusterJob.getSource()
				.getAbsolutePath());

		ClusterCollection clusters=clusterJob.getClusterCollection();
		for (int page = 1; page <= reader.getNumberOfPages(); page++) {
			Rectangle layoutBox = reader.getBoxSize(page, "crop");

			if (layoutBox == null) {
				layoutBox = reader.getBoxSize(page, "media");
			}

			// create Cluster
			// if the pagenumber should be excluded then use it as a
			// discriminating parameter, else use default value

			int pageNumber = -1;
			if (clusterJob.getExcludedPageSet() != null
					&& clusterJob.getExcludedPageSet().contains(page)) {
				pageNumber = page;
			}

			SingleCluster tmpCluster = new SingleCluster(page % 2 == 0,
					(int) layoutBox.getWidth(), (int) layoutBox.getHeight(),
					pageNumber);

			clusters.addPageToCluster(tmpCluster, page);
		}

		// for every cluster create a set of pages on which the preview will
		// be based
		for (SingleCluster cluster : clusters.getClusterToPagesMapping().keySet()) {
			cluster.choosePagesToMerge(clusters.getClusterToPagesMapping().get(cluster));
		}
		reader.close();
	}

	public static class ClusterRenderWorker extends Thread {

		public int workerUnitCounter = 1;
		private final ClusterJob clusterJob;

		public ClusterRenderWorker(ClusterJob clusterJob) {
			this.clusterJob = clusterJob;
		}

		@Override
		public void run() {
			PdfDecoder pdfDecoder = new PdfDecoder(true);
			try {
				pdfDecoder
						.openPdfFile(clusterJob.getSource().getAbsolutePath());
			} catch (PdfException e1) {
				e1.printStackTrace();
			}

			for (SingleCluster cluster : clusterJob.getClusterCollection()
					.getAsList()) {
				for (Integer pageNumber : cluster.getPagesToMerge()) {
					// TODO jpedal isn't able to render big images
					// correctly, so let's check if the image is big an
					// throw it away
					try {
						if (cluster.getImageData().isRenderable()) {
							BufferedImage renderedPage = pdfDecoder
									.getPageAsImage(pageNumber);
							cluster.getImageData().addImageToPreview(renderedPage);
							workerUnitCounter++;
						}
					} catch (PdfException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			// now close the reader as it's not used anymore
			pdfDecoder.closePdfFile();
		}
	}
}
