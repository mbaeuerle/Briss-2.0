/**
 * 
 */
package at.laborg.briss.gui;

import java.awt.Point;
import java.awt.Rectangle;

@SuppressWarnings("serial")
public class DrawableCropRect extends Rectangle {

	static final int CORNER_DIMENSION = 20;

	private boolean selected = false;

	/**
	 * Copy constructor.
	 * 
	 * @param crop
	 */
	public DrawableCropRect(final DrawableCropRect crop) {
		super();
		this.x = crop.x;
		this.y = crop.y;
		this.height = crop.height;
		this.width = crop.width;
	}

	public DrawableCropRect() {
		super();
	}

	public final boolean isSelected() {
		return selected;
	}

	public final void setSelected(final boolean selected) {
		this.selected = selected;
	}

	public final void setNewHotCornerUL(final Point p) {
		int xLR = (int) getMaxX();
		int yLR = (int) getMaxY();
		setSize(xLR - p.x, yLR - p.y);
		x = p.x;
		y = p.y;
	}

	public final void setNewHotCornerLR(final Point p) {
		setSize(p.x - x, p.y - y);
	}

	public final boolean containsInHotCornerUL(final Point p) {
		return ((p.x > getX() && p.x <= getX() + CORNER_DIMENSION) && (p.y > getY() && p.y <= getY()
				+ CORNER_DIMENSION));
	}

	public final boolean containsInHotCornerLR(final Point p) {
		return ((p.x < getMaxX() && p.x > getMaxX() - CORNER_DIMENSION) && (p.y < getMaxY() && p.y > getMaxY()
				- CORNER_DIMENSION));
	}
}