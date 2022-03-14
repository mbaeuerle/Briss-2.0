/**
 *
 */
package at.laborg.briss.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class DrawableCropRect extends Rectangle {

    private static final int EDGE_THRESHOLD = 3;

	private static final long serialVersionUID = -8836495805271750636L;

    static final int CORNER_DIMENSION = 15;

    private boolean selected = false;

    private Rectangle cornerUpperLeft;
    private Rectangle cornerUpperRight;
    private Rectangle cornerLowerLeft;
    private Rectangle cornerLowerRight;

    /**
     * Copy constructor.
     *
     * @param crop Crop rect to copy
     */
    public DrawableCropRect(final DrawableCropRect crop) {
        super();
        this.x = crop.x;
        this.y = crop.y;
        this.height = crop.height;
        this.width = crop.width;
        this.selected = false;
        setupCorners();
        setCornerLocations(x, y);
    }

    public DrawableCropRect(int x, int y, int width, int height) {
        super();
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        setupCorners();
        setCornerLocations(x, y);
    }

    private void setupCorners() {
        cornerUpperLeft = new Rectangle(CORNER_DIMENSION, CORNER_DIMENSION);
        cornerUpperRight = new Rectangle(CORNER_DIMENSION, CORNER_DIMENSION);
        cornerLowerLeft = new Rectangle(CORNER_DIMENSION, CORNER_DIMENSION);
        cornerLowerRight = new Rectangle(CORNER_DIMENSION, CORNER_DIMENSION);
    }

    private void setCornerLocations(int x, int y) {
        int halfCornerDimension = CORNER_DIMENSION / 2;
        cornerUpperLeft.setLocation(x - halfCornerDimension, y - halfCornerDimension);
        cornerUpperRight.setLocation(x + width - halfCornerDimension, y - halfCornerDimension);
        cornerLowerLeft.setLocation(x - halfCornerDimension, y + height - halfCornerDimension);
        cornerLowerRight.setLocation(x + width - halfCornerDimension, y + height - halfCornerDimension);
    }

    public void draw(Graphics2D g2) {

        if (isSelected()) {
            g2.setPaintMode();
            g2.setColor(Color.WHITE);
            g2.fill(cornerUpperLeft);
            g2.fill(cornerUpperRight);
            g2.fill(cornerLowerLeft);
            g2.fill(cornerLowerRight);
            g2.setColor(Color.BLACK);
            g2.draw(cornerUpperLeft);
            g2.draw(cornerUpperRight);
            g2.draw(cornerLowerLeft);
            g2.draw(cornerLowerRight);
        }
    }

    public final boolean isSelected() {
        return selected;
    }

    public final void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public final boolean containsInHotCornerUL(final Point p) {
        return isSelected() && cornerUpperLeft.contains(p);
    }

    public final boolean containsInHotCornerLR(final Point p) {
        return isSelected() && cornerLowerRight.contains(p);
    }

    public final boolean containsInHotCornerUR(final Point p) {
        return isSelected() && cornerUpperRight.contains(p);
    }

    public final boolean containsInHotCornerLL(final Point p) {
        return isSelected() && cornerLowerLeft.contains(p);
    }

	public boolean isOverRightEdge(Point p) {
		return isSelected() && Math.abs(p.x - getMaxX()) < EDGE_THRESHOLD && p.y > y && p.y - y < height;
	}

	public boolean isOverLeftEdge(Point p) {
		return isSelected() && Math.abs(p.x - x) < EDGE_THRESHOLD && p.y > y && p.y - y < height;
	}

	public boolean isOverUpperEdge(Point p) {
		return isSelected() && Math.abs(p.y - y) < EDGE_THRESHOLD && p.x > x && p.x - x < width;
	}

	public boolean isOverLowerEdge(Point p) {
		return isSelected() && Math.abs(p.y - getMaxY()) < EDGE_THRESHOLD && p.x > x && p.x - x < width;
	}

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        setCornerLocations(x, y);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        setCornerLocations(x, y);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        setCornerLocations(this.x, this.y);
    }

    @Override
    public boolean contains(Point p) {
        return super.contains(p) || isSelected() && (isOverRightEdge(p) || isOverLeftEdge(p) || isOverUpperEdge(p) || isOverLowerEdge(p) || cornerUpperLeft.contains(p) || cornerUpperRight.contains(p) || cornerLowerLeft.contains(p) || cornerLowerRight.contains(p));
    }
}
