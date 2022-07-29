/**
 * Copyright 2010 Gerhard Aigner
 * <p>
 * This file is part of BRISS.
 * <p>
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss;

import org.jpedal.exception.PdfException;

import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

final class BrissTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1L;
    private final BrissGUIApp brissGUI;

    BrissTransferHandler(final BrissGUIApp brissGUI) {
        this.brissGUI = brissGUI;
    }

    @Override
    public boolean canImport(final TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);

    }

    @Override
    public boolean importData(final TransferSupport support) {
        if (!canImport(support))
            return false;

        // Fetch the Transferable and its data
        Transferable t = support.getTransferable();
        try {
            String dropInput = (String) t.getTransferData(DataFlavor.stringFlavor);

            String[] filenames = dropInput.split("\n");

            for (String filename : filenames) {
                filename = filename.replaceAll("\\n", "");
                filename = filename.replaceAll("\\t", "");
                filename = filename.replaceAll("\\r", "");

                if (filename.trim().endsWith(".pdf")) {
                    File loadFile = null;
                    try {
                        URI uri = new URI(filename);
                        loadFile = new File(uri);
                    } catch (URISyntaxException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    try {
                        this.brissGUI.importNewPdfFile(loadFile);
                    } catch (PdfException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
            }

        } catch (UnsupportedFlavorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }
}
