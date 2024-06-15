package at.laborg.briss.model;

public record CropRectangle(float xToLeft, float yToBottom, float xToRight, float yToTop) {
	/**
	 * returns the ratio to crop the page x1,y1,x2,y2, origin = bottom left x1: from
	 * left edge to left edge of crop rectange y1: from lower edge to lower edge of
	 * crop rectange x2: from right edge to right edge of crop rectange y2: from top
	 * edge to top edge of crop rectange
	 *
	 * @return
	 */
}
