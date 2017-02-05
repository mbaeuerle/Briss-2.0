package at.laborg.briss.model;

import at.laborg.briss.gui.DrawableCropRect;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class SplitFinder {

    private final static double LOOK_RATIO = 0.5;
    private final static double MAX_DIST_RATIO = 0.1;
    private final static float ROW_OVERLAP_RATIO = 0.01f;

    private SplitFinder() {
    };

    private static Float getSplitRatio(final BufferedImage image, int axis) {
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

    public static List<Float[]> splitColumn(final BufferedImage image, Float[] crop) {
        // TODO: Split within crop rect (currently finds split position on global preview image)
        float columnRatio = SplitFinder.getSplitRatio(image, ImageFinderUtil.X_AXIS);

        float width = 1 - crop[2] - crop[0];
        float divider = crop[0] + columnRatio * width;

        return Arrays.asList(
            new Float[] { crop[0], crop[1], 1 - divider, crop[3] },
            new Float[] { divider, crop[1], crop[2], crop[3] }
        );
    }

    public static List<DrawableCropRect> splitColumn(final BufferedImage image, DrawableCropRect crop) {
        Float[] cropFloat = convertToFloatArray(image, crop);

        List<Float[]> split = splitColumn(image, cropFloat);

        ArrayList<DrawableCropRect> result = new ArrayList<>(split.size());
        for (Float[] splitFloat : split) {
            result.add(convertToDrawableCropRect(image, splitFloat));
        }
        return result;
    }

    public static List<Float[]> splitRow(final BufferedImage image, Float[] crop) {
        // TODO: Split within crop rect (currently finds split position on global preview image)
        float rowRatio = SplitFinder.getSplitRatio(image, ImageFinderUtil.Y_AXIS);

        float height = 1 - crop[1] - crop[3];
        float divider = crop[3] + rowRatio * height;

        return Arrays.asList(
                new Float[] { crop[0], 1 - divider - ROW_OVERLAP_RATIO, crop[2], crop[3] },
                new Float[] { crop[0], crop[1], crop[2], divider - ROW_OVERLAP_RATIO }
        );
    }

    public static List<DrawableCropRect> splitRow(final BufferedImage image, DrawableCropRect crop) {
        Float[] cropFloat = convertToFloatArray(image, crop);

        List<Float[]> split = splitRow(image, cropFloat);

        ArrayList<DrawableCropRect> result = new ArrayList<>(split.size());
        for (Float[] splitFloat : split) {
            result.add(convertToDrawableCropRect(image, splitFloat));
        }
        return result;
    }

    private static Float[] convertToFloatArray(final BufferedImage image, DrawableCropRect crop) {
        float width = image.getWidth();
        float height = image.getHeight();

        return new Float[] {
            crop.x / width,
            1 - (crop.y + crop.height) / height,
            1 - (crop.x + crop.width) / width,
            crop.y / height
        };
    }

    private static DrawableCropRect convertToDrawableCropRect(final BufferedImage image, Float[] crop) {
        float width = image.getWidth();
        float height = image.getHeight();

        DrawableCropRect rect = new DrawableCropRect();
        rect.setLocation((int) (crop[0] * width), (int) (crop[3] * height));
        rect.setSize((int) ((1 - crop[0] - crop[2]) * width), (int) ((1 - crop[1] - crop[3]) * height));
        return rect;
    }
}
