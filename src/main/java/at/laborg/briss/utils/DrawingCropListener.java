package at.laborg.briss.utils;

import at.laborg.briss.gui.DrawableCropRect;
import java.util.List;

public interface DrawingCropListener {

    void onCropRectanglesAltered(List<DrawableCropRect> rectangle, int width, int height);
}
