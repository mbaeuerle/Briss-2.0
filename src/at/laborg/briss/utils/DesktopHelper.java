/**
 * Copyright 2010 Gerhard Aigner
 * 
 * This file is part of BRISS.
 * 
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class DesktopHelper {

	private DesktopHelper() {
	};

	public static void openFileWithDesktopApp(final File cropDestinationFile)
			throws IOException {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(cropDestinationFile);
		}
	}

	public static void openDonationLink(final String uri) throws IOException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			URI donationURI;
			try {
				donationURI = new URI(uri);
				desktop.browse(donationURI);
			} catch (URISyntaxException e) {
			}
		}
	}
}
