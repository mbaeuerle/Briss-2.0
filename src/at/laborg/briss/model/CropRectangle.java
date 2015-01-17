package at.laborg.briss.model;

public class CropRectangle {
	/**
	 * returns the ratio to crop the page x1,y1,x2,y2, origin = bottom left x1:
	 * from left edge to left edge of crop rectange y1: from lower edge to lower
	 * edge of crop rectange x2: from right edge to right edge of crop rectange
	 * y2: from top edge to top edge of crop rectange
	 * 
	 * @return
	 */
	private float xToLeft, yToBottom, xToRight, yToTop;

	public CropRectangle(final float xToLeft, final float yToBottom, final float xToRight,
			final float yToTop) {
		super();
		this.xToLeft = xToLeft;
		this.yToBottom = yToBottom;
		this.xToRight = xToRight;
		this.yToTop = yToTop;
	}

	public final float getxToLeft() {
		return xToLeft;
	}

	public final void setxToLeft(final float xToLeft) {
		this.xToLeft = xToLeft;
	}

	public final float getyToBottom() {
		return yToBottom;
	}

	public final void setyToBottom(final float yToBottom) {
		this.yToBottom = yToBottom;
	}

	public final float getxToRight() {
		return xToRight;
	}

	public final void setxToRight(final float xToRight) {
		this.xToRight = xToRight;
	}

	public final float getyToTop() {
		return yToTop;
	}

	public final void setyToTop(final float yToTop) {
		this.yToTop = yToTop;
	}

}
