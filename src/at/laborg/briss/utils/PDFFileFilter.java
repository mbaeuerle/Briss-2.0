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

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PDFFileFilter extends FileFilter {
	@Override
	public boolean accept(File pathname) {
                // sometimes we get null-ed file
                if (pathname == null)
                        return false;

		if (pathname.isDirectory())
			return true;
		return pathname.toString().toLowerCase().endsWith(".pdf");
	}

	@Override
	public final String getDescription() {
		return null;
	}
}
