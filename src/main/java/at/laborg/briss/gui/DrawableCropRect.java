/**
 *
 */
package at.laborg.briss.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Locale;

public class DrawableCropRect extends Rectangle {

	private static final Composite SMOOTH_NORMAL = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f);
	private static final Composite SMOOTH_SELECT = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
	private static final Composite XOR_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .8f);
	private static final int SELECT_BORDER_WIDTH = 1;
	private static final BasicStroke SELECTED_STROKE = new BasicStroke(SELECT_BORDER_WIDTH);

	private static final long serialVersionUID = -8836495805271750636L;

	public static final int CORNER_DIMENSION = 8;
	private static final int SELECTABLE_CORNER_DIMENSION = 20;
	private static final int EDGE_THRESHOLD = 5;
	public static final float FONT_SCALE_FACTOR = 0.95f;

	public static final float INCH_IN_USER_UNIT = 72f;
	public static final float INCH_IN_MILLIMETERS = 25.4f;

	private boolean selected = false;

	private ResizeHandle cornerUpperLeft;
	private ResizeHandle cornerUpperRight;
	private ResizeHandle cornerLowerLeft;
	private ResizeHandle cornerLowerRight;

	/**
	 * Copy constructor.
	 *
	 * @param crop
	 *            Crop rect to copy
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
		cornerUpperLeft = new ResizeHandle();
		cornerUpperRight = new ResizeHandle();
		cornerLowerLeft = new ResizeHandle();
		cornerLowerRight = new ResizeHandle();
	}

	private void setCornerLocations(int x, int y) {
		cornerUpperLeft.setLocation(x, y);
		cornerUpperRight.setLocation(x + width, y);
		cornerLowerLeft.setLocation(x, y + height);
		cornerLowerRight.setLocation(x + width, y + height);
	}

	public boolean hasEnoughSpaceForHandles() {
		return getWidth() >= 2 * DrawableCropRect.CORNER_DIMENSION
				&& getHeight() >= 2 * DrawableCropRect.CORNER_DIMENSION;
	}

	private Font scaleFont(String text, FontMetrics fontMetrics) {

		int size = MergedPanel.BASE_FONT.getSize();
		float width = fontMetrics.stringWidth(text);
		float height = fontMetrics.getHeight();
		if (width == 0 || height == 0)
			return MergedPanel.BASE_FONT;
		float scaleFactorWidth = this.width * FONT_SCALE_FACTOR / width;
		float scaleFactorHeight = this.height * FONT_SCALE_FACTOR / height;
		float scaledWidth = (scaleFactorWidth * size);
		float scaledHeight = (scaleFactorHeight * size);
		return MergedPanel.BASE_FONT.deriveFont((scaleFactorHeight > scaleFactorWidth) ? scaledWidth : scaledHeight);
	}

	public void draw(Graphics2D g2, int cropRectNumber, FontMetrics fontMetrics) {

		g2.setComposite(SMOOTH_NORMAL);
		if (hasEnoughSpaceForHandles()) {
			g2.setColor(Color.BLUE);
		} else {
			g2.setColor(Color.RED);
		}
		g2.fill(this);
		g2.setColor(Color.BLACK);
		g2.setFont(scaleFont(String.valueOf(cropRectNumber + 1), fontMetrics));
		g2.drawString(String.valueOf(cropRectNumber + 1), this.x, this.y + this.height);

		if (isSelected()) {
			drawSelectionOverlay(g2, fontMetrics);

			if (hasEnoughSpaceForHandles()) {
				drawResizeHandles(g2);
			}
		}
	}

	private void drawResizeHandles(Graphics2D g2) {
		g2.setPaintMode();
		cornerUpperLeft.draw(g2);
		cornerUpperRight.draw(g2);
		cornerLowerLeft.draw(g2);
		cornerLowerRight.draw(g2);
	}

	private void drawSelectionOverlay(Graphics2D g2, FontMetrics fontMetrics) {
		g2.setComposite(XOR_COMPOSITE);
		g2.setColor(Color.BLACK);

		g2.setStroke(SELECTED_STROKE);
		g2.draw(this);

		// display crop size in millimeters
		int w = Math.round(INCH_IN_MILLIMETERS * this.width / INCH_IN_USER_UNIT);
		int h = Math.round(INCH_IN_MILLIMETERS * this.height / INCH_IN_USER_UNIT);
		String size = w + "x" + h;
		// Add 1:x.xx aspect ratio
		if (h > 0 && w > 0) {
			float ratio = (float) w / (float) h;
			if (ratio < 1) {
				ratio = 1 / ratio;
			}
			// Locale.ROOT forces a dot as separator
			size += String.format(Locale.ROOT, " 1:%.2f", ratio);
		}
		g2.setFont(scaleFont(size, fontMetrics));
		g2.setColor(Color.YELLOW);
		g2.setComposite(SMOOTH_SELECT);
		g2.drawString(size, this.x, this.y + this.height);
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
		return super.contains(p) || isSelected() && (isOverRightEdge(p) || isOverLeftEdge(p) || isOverUpperEdge(p)
				|| isOverLowerEdge(p) || cornerUpperLeft.contains(p) || cornerUpperRight.contains(p)
				|| cornerLowerLeft.contains(p) || cornerLowerRight.contains(p));
	}

	static class ResizeHandle {

		Point corner;

		public ResizeHandle(int x, int y) {
			this.corner = new Point(x, y);
		}

		public ResizeHandle() {
			this.corner = new Point(0, 0);
		}

		public void setLocation(int x, int y) {
			this.corner.setLocation(x, y);
		}

		public void draw(Graphics2D g2) {
			int halfCornerDimension = CORNER_DIMENSION / 2;
			g2.setColor(Color.WHITE);
			g2.fillRect(corner.x - halfCornerDimension, corner.y - halfCornerDimension, CORNER_DIMENSION,
					CORNER_DIMENSION);
			g2.setColor(Color.BLACK);
			g2.drawRect(corner.x - halfCornerDimension, corner.y - halfCornerDimension, CORNER_DIMENSION,
					CORNER_DIMENSION);
		}

		public boolean contains(Point p) {
			return Math.abs(p.x - corner.x) < SELECTABLE_CORNER_DIMENSION / 2
					&& Math.abs(p.y - corner.y) < SELECTABLE_CORNER_DIMENSION / 2;
		}
	}
}
