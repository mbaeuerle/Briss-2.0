package at.laborg.briss.utils;

import java.util.List;

import com.itextpdf.text.Rectangle;

public final class RectangleHandler {

	private RectangleHandler() {
	};

	public static Rectangle calculateScaledRectangle(
			final List<Rectangle> boxes, final Float[] ratios,
			final int rotation) {
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
	 * Rotates the ratios counter clockwise until its at 0.
	 * 
	 * @param ratios
	 * @param rotation
	 * @return
	 */
	private static float[] rotateRatios(final Float[] ratios, final int rotation) {
		float[] tmpRatios = new float[4];
		for (int i = 0; i < 4; i++) {
			tmpRatios[i] = ratios[i];
		}
		int tmpRotation = rotation;
		while (tmpRotation > 0 && tmpRotation < 360) {
			float tmpValue = tmpRatios[0];
			// left
			tmpRatios[0] = tmpRatios[1];
			// bottom
			tmpRatios[1] = tmpRatios[2];
			// right
			tmpRatios[2] = tmpRatios[3];
			// top
			tmpRatios[3] = tmpValue;
			tmpRotation += 90;
		}
		return tmpRatios;
	}
}
