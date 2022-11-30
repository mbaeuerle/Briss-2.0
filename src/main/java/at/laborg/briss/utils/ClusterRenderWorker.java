/**
 *
 */
package at.laborg.briss.utils;

import at.laborg.briss.model.ClusterDefinition;
import at.laborg.briss.model.PageCluster;
import java.awt.image.BufferedImage;
import java.io.File;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

public class ClusterRenderWorker extends Thread {

	public int workerUnitCounter = 1;
	private final File source;
	private final ClusterDefinition clusters;
	private final String password;

	public ClusterRenderWorker(final File source, String password, final ClusterDefinition clusters) {
		super();
		this.source = source;
		this.clusters = clusters;
		this.password = password;
	}

	@Override
	public final void run() {
		PdfDecoder pdfDecoder = new PdfDecoder();

		try {
			pdfDecoder.openPdfFile(source.getAbsolutePath(), password);
		} catch (PdfException e1) {
			e1.printStackTrace();
		}

		for (PageCluster cluster : clusters.getClusterList()) {
			for (Integer pageNumber : cluster.getPagesToMerge()) {
				try {
					BufferedImage renderedPage = pdfDecoder.getPageAsImage(pageNumber);
					cluster.getImageData().addImageToPreview(renderedPage);
					workerUnitCounter++;
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
