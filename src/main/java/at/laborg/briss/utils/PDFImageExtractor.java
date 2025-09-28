package at.laborg.briss.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDFImageExtractor implements AutoCloseable {
	private final PDDocument document;
	private final PDFRenderer pdfRenderer;

	public PDFImageExtractor(File pdfFile, String password) throws IOException {
		this.document = Loader.loadPDF(pdfFile, password);

		this.pdfRenderer = new PDFRenderer(document);
	}

	public BufferedImage extractImage(int pageNumber) throws IOException {
		return pdfRenderer.renderImageWithDPI(pageNumber, 300, ImageType.RGB);
	}

	@Override
	public void close() throws IOException {
		this.document.close();
	}
}
