package at.laborg.briss;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import at.laborg.briss.cli.BrissCMD;
import at.laborg.briss.utils.BrissFileHandling;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class AutoCropTest {

	@Test
	public void testAutocrop() throws Exception {
		Path outputDirectory = Files.createTempDirectory(AutoCropTest.class.getCanonicalName());

		Path documentPath = Path.of("src/test/resources/pdfs/CREATIVE_COMMONS.pdf");
		File recommended = BrissFileHandling.getRecommendedDestination(documentPath.toFile());

		String[] jobargs = new String[]{"-s", documentPath.toString(), "-d",
				outputDirectory.resolve(recommended.getName()).toString()};

		assertDoesNotThrow(() -> BrissCMD.autoCrop(jobargs));
	}

	@Test
	public void testCrop() throws Exception {
		Path outputDirectory = Files.createTempDirectory(AutoCropTest.class.getCanonicalName());

		Path documentPath = Path.of("src/test/resources/pdfs/example.pdf");

        File recommended = BrissFileHandling.getRecommendedDestination(documentPath.toFile());
		String[] jobargs = new String[]{"-s", documentPath.toString(), "-d",
            outputDirectory.resolve(recommended.getName()).toString()};

		assertDoesNotThrow(() -> BrissCMD.autoCrop(jobargs));
	}

	@Test
	public void testCropWithPasswordProtectedFile() throws Exception {
		Path outputDirectory = Files.createTempDirectory(AutoCropTest.class.getCanonicalName());

		Path documentPath = Path.of("src/test/resources/pdfs/example-protected.pdf");
        File recommended = BrissFileHandling.getRecommendedDestination(documentPath.toFile());

        String[] jobargs = new String[]{"-s", documentPath.toString(), "-p", "secret", "-d",
            outputDirectory.resolve(recommended.getName()).toString()};

		assertDoesNotThrow(() -> BrissCMD.autoCrop(jobargs));
	}
}
