package at.laborg.briss.utils;

public class OSDetector {
	private static final boolean _IS_WINDOWS;

	static {
		String osName = System.getProperty("os.name");

		if (osName == null) {
			throw new Error("os.name system property is not set");
		}

		if (osName.contains("Mac OS")) {
			_IS_WINDOWS = false;
		} else if (osName.contains("Windows")) {
			_IS_WINDOWS = true;
		} else {
			_IS_WINDOWS = false;
		}
	}

	public static boolean isWindows() {
		return _IS_WINDOWS;
	}
}
