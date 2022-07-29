package at.laborg.briss;

import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;

import org.jpedal.exception.PdfException;

public interface BrissGUIApp {

	void alignSelRects(int x, int y, int w, int h);

	void resizeAndMoveSelectedRects(int width, int height, int x, int y);

	void moveSelectedRects(int x, int y);

	void deselectAllRects();

	void setDefinedSizeSelRects();

	void setPositionSelRects();

	void resizeSelRects(int w, int h);

	void propertyChange(PropertyChangeEvent evt);

	void componentResized(ComponentEvent e);

	void componentMoved(ComponentEvent e);

	void componentShown(ComponentEvent e);

	void componentHidden(ComponentEvent e);

	void importNewPdfFile(File loadFile) throws IOException, PdfException;

}