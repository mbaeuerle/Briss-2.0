package at.laborg.briss;

import java.io.File;
import java.io.IOException;

public interface BrissGUIApp {

	void alignSelRects(int x, int y, int w, int h);

	void resizeAndMoveSelectedRects(int width, int height, int x, int y);

	void moveSelectedRects(int x, int y);

	void deselectAllRects();

	void resizeSelRects(int w, int h);

	void importNewPdfFile(File loadFile) throws IOException;
}
