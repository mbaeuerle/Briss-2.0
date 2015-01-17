package at.laborg.briss.model;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public final class CropFinder {

	private CropFinder() {
	};

	private static final double RATIO_LOOK_AHEAD_SATISFY = 0.85;
	private static final int LOOK_AHEAD_PIXEL_NR = 30;
	private static final int SD_CALC_SIZE_NR = 5;
	private static final float SD_THRESHOLD_TO_BE_COUNTED = 0.2f;

	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int ORIENTATION_LEFT = 0;
	public static final int ORIENTATION_TOP = 1;
	public static final int ORIENTATION_RIGHT = 2;
	public static final int ORIENTATION_BOTTOM = 3;

	public static Float[] getAutoCropFloats(final BufferedImage image) {

		WritableRaster raster = image.getRaster();

		double[] sumX = sumFrom2dTo1d(raster, X_AXIS);
		double[] sumY = sumFrom2dTo1d(raster, Y_AXIS);

		double[] derivationX = createDerivation(sumX);
		double[] derivationY = createDerivation(sumY);

		double[] sdOfDerivationX = createSdOfDerivation(derivationX);
		double[] sdOfDerivationY = createSdOfDerivation(derivationY);

		int positionXLeft = findPosition(sdOfDerivationX, ORIENTATION_LEFT);
		int positionYTop = findPosition(sdOfDerivationY, ORIENTATION_TOP);
		int positionXRight = findPosition(sdOfDerivationX, ORIENTATION_RIGHT);
		int positionYBottom = findPosition(sdOfDerivationY, ORIENTATION_BOTTOM);

		Float[] result = new Float[4];
		result[0] = (positionXLeft / (float) image.getWidth());
		result[1] = ((image.getHeight() - positionYBottom) / (float) image
				.getHeight());
		result[2] = ((image.getWidth() - positionXRight) / (float) image
				.getWidth());
		result[3] = (positionYTop / (float) image.getHeight());
		return result;
	}

	private static double[] sumFrom2dTo1d(final WritableRaster raster,
			final int axis) {
		if (axis == X_AXIS) {
			double[] values = new double[raster.getWidth()];
			for (int i = 0; i < raster.getWidth(); i++) {
				int[] tmp = null;
				tmp = raster.getPixels(i, 0, 1, raster.getHeight(), tmp);
				for (int element : tmp) {
					values[i] += element;
				}
				values[i] /= raster.getHeight();
			}
			return values;
		} else if (axis == Y_AXIS) {
			double[] values = new double[raster.getHeight()];
			for (int i = 0; i < raster.getHeight(); i++) {
				int[] tmp = null;
				tmp = raster.getPixels(0, i, raster.getWidth(), 1, tmp);
				for (int element : tmp) {
					values[i] += element;
				}
				values[i] /= raster.getWidth();
			}
			return values;
		}
		return null;
	}

	private static double[] createDerivation(final double[] values) {
		double[] derivedValues = new double[values.length - 1];
		for (int i = 0; i < derivedValues.length; i++) {
			derivedValues[i] = values[i + 1] - values[i];
		}
		return derivedValues;
	}

	private static int findPosition(final double[] sds,
			final int orientationLeft) {
		int position = 0;

		switch (orientationLeft) {
		case ORIENTATION_TOP:
			for (int i = 0; i < sds.length - LOOK_AHEAD_PIXEL_NR; i++) {
				int cnt = diffCounter(sds, i, i + LOOK_AHEAD_PIXEL_NR);
				if (cnt > RATIO_LOOK_AHEAD_SATISFY * LOOK_AHEAD_PIXEL_NR) {
					position = i;
					break;
				}
			}
			break;
		case ORIENTATION_LEFT:
			for (int i = 0; i < sds.length - LOOK_AHEAD_PIXEL_NR; i++) {
				int cnt = diffCounter(sds, i, i + LOOK_AHEAD_PIXEL_NR);
				if (cnt > RATIO_LOOK_AHEAD_SATISFY * LOOK_AHEAD_PIXEL_NR) {
					position = i;
					break;
				}
			}
			break;
		case ORIENTATION_BOTTOM:
			for (int i = sds.length - 1; i >= 0 + LOOK_AHEAD_PIXEL_NR; i--) {
				int cnt = diffCounter(sds, i - LOOK_AHEAD_PIXEL_NR, i);
				if (cnt > RATIO_LOOK_AHEAD_SATISFY * LOOK_AHEAD_PIXEL_NR) {
					position = i;
					break;
				}
			}
			break;
		case ORIENTATION_RIGHT:
			for (int i = sds.length - 1; i >= 0 + LOOK_AHEAD_PIXEL_NR; i--) {
				int cnt = diffCounter(sds, i - LOOK_AHEAD_PIXEL_NR, i);
				if (cnt > RATIO_LOOK_AHEAD_SATISFY * LOOK_AHEAD_PIXEL_NR) {
					position = i;
					break;
				}
			}
			break;
		default:
			break;
		}
		return position;
	}

	private static double[] createSdOfDerivation(final double[] diffOut) {

		double[] sds = new double[diffOut.length];

		for (int i = 0; i < diffOut.length; i++) {
			double[] tmp = new double[SD_CALC_SIZE_NR];
			for (int j = 0; (j < tmp.length); j++) {
				if (i + j < diffOut.length) {
					tmp[j] = diffOut[i + j];
				} else {
					tmp[j] = 0;
				}

			}
			sds[i] = sd(tmp);
		}
		return sds;
	}

	private static int diffCounter(final double[] values, final int start,
			final int end) {

		int cnt = 0;
		for (int i = start; i < end; i++) {
			if (values[i] > SD_THRESHOLD_TO_BE_COUNTED) {
				cnt++;
			}
		}
		return cnt;
	}

	private static double sd(final double[] values) {
		// get mean
		double mean = 0;
		double sum = 0;
		for (double value : values) {
			sum += value;
		}
		mean = sum / values.length;
		double sd = 0;
		for (double value : values) {
			sd += (value - mean) * (value - mean);
		}
		sd = Math.sqrt(sd / values.length);
		return sd;
	}

}
