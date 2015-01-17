package at.laborg.briss.utils;

import java.io.File;
import java.io.IOException;

public final class BrissFileHandling {

	private BrissFileHandling() {
	};

	private static final String RECOMMENDED_ENDING = "_cropped.pdf";

	public static File getRecommendedDestination(final File sourceFile) {
		// create file recommendation
		String origName = sourceFile.getAbsolutePath();
		String recommendedName = origName.substring(0, origName.length() - 4)
				+ RECOMMENDED_ENDING;
		return new File(recommendedName);
	}

	public static boolean checkValidStateAndCreate(File destinationFile)
			throws IOException, IllegalArgumentException {
		if (destinationFile == null)
			throw new IllegalArgumentException("Destination File musst be set!");
		if (!destinationFile.exists())
			return destinationFile.createNewFile();
		return true;
	}

}
