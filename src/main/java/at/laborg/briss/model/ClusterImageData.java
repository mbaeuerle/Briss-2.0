package at.laborg.briss.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ClusterImageData {

    private static final int MAX_PAGE_HEIGHT = 900;
    private static final int MAX_IMAGE_RENDER_SIZE = 2000 * 2000;
    public static final double IDENTICAL_PIXELS_THRESHOLD = 0.8;

    private final boolean renderable;
    private BufferedImage outputImage = null;
    private int outputImageHeight = -1;
    private int outputImageWidth = -1;
    private BufferedImage[] inputImages;
    private int imageCnt = 0;
    private final int totalImages;

    public ClusterImageData(final int pageWidth, final int pageHeight, final int nrOfImages) {
        this.renderable = pageWidth * pageHeight < MAX_IMAGE_RENDER_SIZE;
        totalImages = nrOfImages;
    }

    public final boolean isRenderable() {
        return renderable;
    }

    public final void addImageToPreview(final BufferedImage imageToAdd) {
        if (!renderable) return;
        if (outputImageHeight == -1) {
            initializeOutputImage(imageToAdd);
        }
        add(scaleImage(imageToAdd, outputImageWidth, outputImageHeight));
    }

    private void initializeOutputImage(final BufferedImage imageToAdd) {
        outputImageHeight = imageToAdd.getHeight() > MAX_PAGE_HEIGHT ? MAX_PAGE_HEIGHT : imageToAdd.getHeight();
        float scaleFactor = (float) outputImageHeight / imageToAdd.getHeight();
        outputImageWidth = (int) (imageToAdd.getWidth() * scaleFactor);
        inputImages = new BufferedImage[totalImages];
    }

    private void add(final BufferedImage image) {
        inputImages[imageCnt] = image;
        imageCnt++;
    }

    public final BufferedImage getPreviewImage() {

        if (!renderable) return getUnrenderableImage();
        if (outputImage == null) {
            outputImage = renderOutputImage();
            inputImages = null;
        }
        return outputImage;
    }

    private BufferedImage renderOutputImage() {
        if ((outputImageWidth <= 0) || (outputImageHeight <= 0)) {
            // we have no image data - jpedal was probably not able to provide us with
            // the data
            // so we create an empty image
            BufferedImage im = new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_BINARY);
            WritableRaster raster = im.getRaster();
            for (int h = 0; h < 100; h++) for (int w = 0; w < 100; w++) raster.setSample(w, h, 0, 1);
            addImageToPreview(im);
        }

        BufferedImage outputImage =
                new BufferedImage(outputImageWidth, outputImageHeight, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = outputImage.getRaster().createCompatibleWritableRaster();

        int[][] overlay = calculateOverlayOfImages(inputImages, imageCnt);
        for (int w = 0; w < outputImage.getWidth(); ++w) {
            for (int h = 0; h < outputImage.getHeight(); ++h) {
                raster.setSample(w, h, 0, overlay[w][h]);
            }
        }
        outputImage.setData(raster);
        return outputImage;
    }

    private static BufferedImage scaleImage(final BufferedImage bsrc, final int width, final int height) {

        BufferedImage bdest = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = bdest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(
                (double) bdest.getWidth() / bsrc.getWidth(), (double) bdest.getHeight() / bsrc.getHeight());
        g.drawRenderedImage(bsrc, at);
        g.dispose();

        return bdest;
    }

    private static BufferedImage getUnrenderableImage() {
        int width = 200;
        int height = 200;

        // Create buffered image that does not support transparency
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bimage.createGraphics();

        // Draw on the image
        g2d.setColor(Color.WHITE);
        g2d.drawRect(5, 5, 190, 190);

        Font font = new Font("Sansserif", Font.BOLD | Font.PLAIN, 22);
        g2d.setFont(font);

        g2d.setColor(Color.WHITE);
        g2d.drawString("Image to Big!", 10, 110);

        g2d.dispose();
        return bimage;
    }

    private int[][] calculateOverlayOfImages(final BufferedImage[] images, final int imageCnt) {
        int width = images[0].getWidth();
        int height = images[0].getHeight();

        int[][] fallbackOverlay = new int[width][height];
        int[][] sum = new int[width][height];
        int[][] mean = new int[width][height];
        int[][] sd = new int[width][height];

        int identicalPixels = 0;
        int whitePixels = 0;

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                fallbackOverlay[w][h] = 255;
                for (int k = 0; k < imageCnt; k++) {
                    int currentImagePixel = images[k].getRaster().getPixel(w, h, (int[]) null)[0];
                    fallbackOverlay[w][h] = Math.min(currentImagePixel, fallbackOverlay[w][h]);
                    sum[w][h] += currentImagePixel;
                }
                mean[w][h] = sum[w][h] / imageCnt;
                sum[w][h] = 0;
                for (int k = 0; k < imageCnt; k++) {
                    int currentImagePixel = images[k].getRaster().getPixel(w, h, (int[]) null)[0];
                    sum[w][h] += (currentImagePixel - mean[w][h]) * (currentImagePixel - mean[w][h]);
                }
                sd[w][h] = 255 - (int) Math.sqrt(sum[w][h] / (imageCnt - 1.0));
                if (mean[w][h] < 255) {
                    if (sd[w][h] == 255) {
                        identicalPixels++;
                    }
                } else {
                    whitePixels++;
                }
            }
        }
        if (identicalPixels > width * height * IDENTICAL_PIXELS_THRESHOLD - whitePixels) {
            return fallbackOverlay;
        }

        return sd;
    }
}
