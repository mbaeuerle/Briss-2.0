package at.laborg.briss.model;

import at.laborg.briss.gui.DrawableCropRect;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SplitFinder {

	private static final double LOOK_RATIO = 0.5;
	private static final double MAX_DIST_RATIO = 0.1;
	private static final float ROW_OVERLAP_RATIO = 0.01f;

	private static float getSplitRatio(final BufferedImage image, int axis) {
		WritableRaster raster = image.getRaster();

		double[] sdOfDerivationX = ImageFinderUtil.createSdOfDerivation(raster, axis);

		int width = image.getWidth();
		int rangeStart = (int) Math.floor(width * (LOOK_RATIO - MAX_DIST_RATIO / 2));
		int rangeEnd = (int) Math.ceil(width * (LOOK_RATIO + MAX_DIST_RATIO / 2));

		double min = Double.MAX_VALUE;
		int minIndex = -1;

		for (int i = rangeStart; i < rangeEnd; i++) {
			if (sdOfDerivationX[i] < min) {
				min = sdOfDerivationX[i];
				minIndex = i;
			}
		}

		return ((float) minIndex) / width;
	}

	public static List<float[]> splitColumn(final BufferedImage image, float[] crop) {
		// TODO: Split within crop rect (currently finds split position on global
		// preview image)
		float columnRatio = SplitFinder.getSplitRatio(image, ImageFinderUtil.X_AXIS);

		float width = 1 - crop[2] - crop[0];
		float divider = crop[0] + columnRatio * width;

		return Arrays.asList(new float[]{crop[0], crop[1], 1 - divider, crop[3]},
				new float[]{divider, crop[1], crop[2], crop[3]});
	}

	public static List<DrawableCropRect> splitColumn(final BufferedImage image, DrawableCropRect crop) {
		float[] cropFloat = convertToFloatArray(image, crop);

		List<float[]> split = splitColumn(image, cropFloat);

		return split.stream().map(e -> convertToDrawableCropRect(image, e)).collect(Collectors.toList());
	}

	public static List<float[]> splitRow(final BufferedImage image, float[] crop) {
		// TODO: Split within crop rect (currently finds split position on global
		// preview image)
		float rowRatio = SplitFinder.getSplitRatio(image, ImageFinderUtil.Y_AXIS);

		float height = 1 - crop[1] - crop[3];
		float divider = crop[3] + rowRatio * height;

		return Arrays.asList(new float[]{crop[0], 1 - divider - ROW_OVERLAP_RATIO, crop[2], crop[3]},
				new float[]{crop[0], crop[1], crop[2], divider - ROW_OVERLAP_RATIO});
	}

	public static List<DrawableCropRect> splitRow(final BufferedImage image, DrawableCropRect crop) {
		float[] cropFloat = convertToFloatArray(image, crop);

		List<float[]> split = splitRow(image, cropFloat);

		ArrayList<DrawableCropRect> result = new ArrayList<>(split.size());
		for (float[] splitFloat : split) {
			result.add(convertToDrawableCropRect(image, splitFloat));
		}
		return result;
	}

	private static float[] convertToFloatArray(final BufferedImage image, DrawableCropRect crop) {
		float width = image.getWidth();
		float height = image.getHeight();

		return new float[]{crop.x / width, 1 - (crop.y + crop.height) / height, 1 - (crop.x + crop.width) / width,
				crop.y / height};
	}

	private static DrawableCropRect convertToDrawableCropRect(final BufferedImage image, float[] crop) {
		float width = image.getWidth();
		float height = image.getHeight();

		int x = (int) (crop[0] * width);
		int y = (int) (crop[3] * height);
		int cropWidth = (int) ((1 - crop[0] - crop[2]) * width);
		int cropHeight = (int) ((1 - crop[1] - crop[3]) * height);
		return new DrawableCropRect(x, y, cropWidth, cropHeight);
	}
}
