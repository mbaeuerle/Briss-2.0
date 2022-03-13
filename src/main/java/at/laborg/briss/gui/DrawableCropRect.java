/**
 *
 */
package at.laborg.briss.gui;

import java.awt.Point;
import java.awt.Rectangle;

public class DrawableCropRect extends Rectangle {

    private static final int EDGE_THRESHOLD = 3;

	private static final long serialVersionUID = -8836495805271750636L;

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

    public DrawableCropRect(int x, int y, int width, int height) {
        super();
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public final boolean isSelected() {
        return selected;
    }

    public final void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public final void setNewHotCornerUL(final Point p) {
        resizeToCoordinates(p.x, p.y, (int) getMaxX(), (int) getMaxY());
    }

    public final void setNewHotCornerLR(final Point p) {
        resizeToCoordinates(x, y, p.x, p.y);
    }

    public final void setNewHotCornerUR(final Point p) {
        resizeToCoordinates(x, p.y, p.x, (int) getMaxY());
    }

    public final void setNewHotCornerLL(final Point p) {
        resizeToCoordinates(p.x, y, (int) getMaxX(), p.y);
    }

    public final boolean containsInHotCornerUL(final Point p) {
        return ((p.x > getX() && p.x <= getX() + CORNER_DIMENSION) && (p.y > getY() && p.y <= getY() + CORNER_DIMENSION));
    }

    public final boolean containsInHotCornerLR(final Point p) {
        return ((p.x < getMaxX() && p.x > getMaxX() - CORNER_DIMENSION) && (p.y < getMaxY() && p.y > getMaxY()
            - CORNER_DIMENSION));
    }

    public final boolean containsInHotCornerUR(final Point p) {
        return ((p.x < getMaxX() && p.x > getMaxX() - CORNER_DIMENSION) && (p.y > getY() && p.y <= getY() + CORNER_DIMENSION));
    }

    public final boolean containsInHotCornerLL(final Point p) {
        return ((p.x > getX() && p.x <= getX() + CORNER_DIMENSION) && (p.y < getMaxY() && p.y > getMaxY()
                - CORNER_DIMENSION));
    }

    public final void resizeToCoordinates(int xUL, int yUL, int xLR, int yLR) {
        setSize(xLR - xUL, yLR - yUL);

        x = xUL;
        y = yUL;
    }

	public boolean isOverRightEdge(Point p) {
		return Math.abs(p.x - getMaxX()) < EDGE_THRESHOLD && p.y > y && p.y - y < height;
	}

	public boolean isOverLeftEdge(Point p) {
		return Math.abs(p.x - x) < EDGE_THRESHOLD && p.y > y && p.y - y < height;
	}

	public boolean isOverUpperEdge(Point p) {
		return Math.abs(p.y - y) < EDGE_THRESHOLD && p.x > x && p.x - x < width;
	}

	public boolean isOverLowerEdge(Point p) {
		return Math.abs(p.y - getMaxY()) < EDGE_THRESHOLD && p.x > x && p.x - x < width;
	}

	public void moveLeftEdge(final Point p) {
		resizeToCoordinates(p.x, y, (int) getMaxX(), (int) getMaxY());
	}

	public void moveRightEdge(final Point p) {
		resizeToCoordinates(x, y, p.x, (int) getMaxY());
	}

	public void moveUpperEdge(final Point p) {
		resizeToCoordinates(x, p.y, (int) getMaxX(), (int) getMaxY());
	}

	public void moveLowerEdge(final Point p) {
		resizeToCoordinates(x, y, (int) getMaxX(), p.y);
	}
}
