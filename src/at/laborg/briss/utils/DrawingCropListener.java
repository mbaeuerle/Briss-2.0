package at.laborg.briss.utils;

import java.util.List;

import at.laborg.briss.gui.DrawableCropRect;

public interface DrawingCropListener {

	void onCropRectanglesAltered(List<DrawableCropRect> rectangle,
			int width, int height);
}
