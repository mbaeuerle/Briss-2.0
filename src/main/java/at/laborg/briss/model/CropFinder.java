package at.laborg.briss.model;

import com.sun.media.jai.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public final class CropFinder {

	private CropFinder() {
	};

	public static Float[] getAutoCropFloats(final BufferedImage image) {

		WritableRaster raster = image.getRaster();

		double[] sdOfDerivationX = ImageFinderUtil.createSdOfDerivation(raster, ImageFinderUtil.X_AXIS);
		double[] sdOfDerivationY = ImageFinderUtil.createSdOfDerivation(raster, ImageFinderUtil.Y_AXIS);

		int positionXLeft = ImageFinderUtil.findPosition(sdOfDerivationX, ImageFinderUtil.ORIENTATION_LEFT);
		int positionYTop = ImageFinderUtil.findPosition(sdOfDerivationY, ImageFinderUtil.ORIENTATION_TOP);
		int positionXRight = ImageFinderUtil.findPosition(sdOfDerivationX, ImageFinderUtil.ORIENTATION_RIGHT);
		int positionYBottom = ImageFinderUtil.findPosition(sdOfDerivationY, ImageFinderUtil.ORIENTATION_BOTTOM);

		Float[] result = new Float[4];
		result[0] = (positionXLeft / (float) image.getWidth());
		result[1] = ((image.getHeight() - positionYBottom) / (float) image.getHeight());
		result[2] = ((image.getWidth() - positionXRight) / (float) image.getWidth());
		result[3] = (positionYTop / (float) image.getHeight());
		return result;
	}
}
