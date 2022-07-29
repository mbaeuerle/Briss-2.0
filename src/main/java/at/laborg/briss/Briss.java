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

import at.laborg.briss.cli.BrissCMD;

public final class Briss {

	private Briss() {
	}

	public static void main(final String[] args) {

		// this needs to be set in order to cope with jp2000 images
		System.setProperty("org.jpedal.jai", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		// check if args are present, if so try to start commandline briss
		if (args.length > 1) {
			BrissCMD.autoCrop(args);
		} else {
			new BrissSwingGUI(args);
		}
	}
}
