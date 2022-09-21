package at.laborg.briss;

import at.laborg.briss.cli.BrissCMD;
import at.laborg.briss.utils.BrissFileHandling;
import java.io.File;
import java.util.Date;

public final class AutoCropTest {

    private AutoCropTest() {}

    /**
     * @param args
     */
    public static void main(final String[] args) {
        File wd = new File(System.getProperty("user.dir") + File.separatorChar + "pdftests");
        File outputDirectory = new File(wd.getAbsolutePath() + File.separatorChar + new Date().toString());
        outputDirectory.mkdir();

        for (File file :
                wd.listFiles(file -> file.getAbsolutePath().toLowerCase().endsWith(".pdf"))) {
            String[] jobargs = new String[4];
            jobargs[0] = "-s";
            jobargs[1] = file.getAbsolutePath();
            jobargs[2] = "-d";
            File recommended = BrissFileHandling.getRecommendedDestination(file);

            String output = outputDirectory.getAbsolutePath() + File.separatorChar + recommended.getName();
            jobargs[3] = output;
            BrissCMD.autoCrop(jobargs);
        }
    }
}
