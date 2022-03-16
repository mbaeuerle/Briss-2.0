package at.laborg.briss.utils;

import java.io.IOException;

import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfReader;

public class PDFReaderUtil {

    public static PdfReader getPdfReader(String absolutePath, String password) throws IOException {
        if (password == null) {
            return new PdfReader(absolutePath);
        }

        return new PdfReader(absolutePath, password.getBytes());
    }

    public static boolean isEncrypted(String absolutePath) throws IOException {
        try {
            PdfReader pdfReader = new PdfReader(absolutePath);

            pdfReader.close();

            return false;
        }
        catch (BadPasswordException badPasswordException) {
            return true;
        }
    }

    public static boolean isInvalidPassword(String absolutePath, String password) throws IOException {
        if (password == null) {
            return true;
        }

        try {
            PdfReader pdfReader = new PdfReader(absolutePath, password.getBytes());

            pdfReader.close();

            return false;
        }
        catch (BadPasswordException badPasswordException) {
            return true;
        }
    }
}
