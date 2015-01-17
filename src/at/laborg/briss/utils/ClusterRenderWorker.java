/**
 * 
 */
package at.laborg.briss.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import at.laborg.briss.model.ClusterDefinition;
import at.laborg.briss.model.PageCluster;

public class ClusterRenderWorker extends Thread {

	public int workerUnitCounter = 1;
	private final File source;
	private final ClusterDefinition clusters;

	public ClusterRenderWorker(final File source,
			final ClusterDefinition clusters) {
		super();
		this.source = source;
		this.clusters = clusters;
	}

	@Override
	public final void run() {
		PdfDecoder pdfDecoder = new PdfDecoder();
		try {
			pdfDecoder.openPdfFile(source.getAbsolutePath());
		} catch (PdfException e1) {
			e1.printStackTrace();
		}

		for (PageCluster cluster : clusters.getClusterList()) {
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