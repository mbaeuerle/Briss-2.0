package at.laborg.briss.gui;

import java.util.ArrayList;
import java.util.List;

public enum ClipBoard {

	INSTANCE;
	private final List<DrawableCropRect> cropRectsClipBoard = new ArrayList<>();

	public static ClipBoard getInstance() {
		return INSTANCE;
	}

	public void clear() {
		cropRectsClipBoard.clear();
	}

	public void addCrops(final List<DrawableCropRect> listToAdd) {
		cropRectsClipBoard.addAll(listToAdd);
	}

	public List<DrawableCropRect> getCrops() {
		return cropRectsClipBoard;
	}

	public void addCrop(final DrawableCropRect crop) {
		if (!cropRectsClipBoard.contains(crop)) {
			cropRectsClipBoard.add(crop);
		}
	}

	public int getAmountOfCropsInClipBoard() {
		return cropRectsClipBoard.size();
	}
}
