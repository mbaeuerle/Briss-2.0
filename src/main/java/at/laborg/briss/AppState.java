package at.laborg.briss;

import java.awt.Rectangle;
import java.io.File;
import java.io.Serializable;

public class AppState implements Serializable {
	private static final long serialVersionUID = 2867878753648985421L;

	private final Rectangle windowRectangle;
	private final File latestOpenedDirectory;

	public AppState(Rectangle windowRectangle, File latestOpenedDirectory) {
		this.windowRectangle = new Rectangle(windowRectangle);

		this.latestOpenedDirectory = latestOpenedDirectory;
	}

	public Rectangle getWindowRectangle() {
		return windowRectangle;
	}

	public File getLatestOpenedDirectory() {
		return latestOpenedDirectory;
	}
}
