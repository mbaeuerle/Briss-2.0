// $Id: SingleCluster.java 55 2011-02-22 21:45:59Z laborg $
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
import java.io.IOException;

import at.laborg.briss.model.ClusterDefinition;
import at.laborg.briss.model.PageCluster;
import at.laborg.briss.model.PageExcludes;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;

public final class ClusterCreator {
	private ClusterCreator() {
	};

	public static ClusterDefinition clusterPages(final File source,
			final PageExcludes pageExcludes) throws IOException {
		PdfReader reader = new PdfReader(source.getAbsolutePath());

		ClusterDefinition clusters = new ClusterDefinition();

		for (int page = 1; page <= reader.getNumberOfPages(); page++) {

			Rectangle layoutBox = getLayoutBox(reader, page);

			// create Cluster
			// if the pagenumber should be excluded then use it as a
			// discriminating parameter, else use default value

			boolean excluded = checkExclusionAndGetPageNumber(pageExcludes,
					page);

			PageCluster tmpCluster = new PageCluster(page % 2 == 0,
					(int) layoutBox.getWidth(), (int) layoutBox.getHeight(),
					excluded, page);

			clusters.addOrMergeCluster(tmpCluster);
		}
		reader.close();
		clusters.selectAndSetPagesForMerging();
		return clusters;
	}

	private static Rectangle getLayoutBox(final PdfReader reader, final int page) {
		Rectangle layoutBox = reader.getBoxSize(page, "crop");

		if (layoutBox == null) {
			layoutBox = reader.getBoxSize(page, "media");
		}
		return layoutBox;
	}

	private static boolean checkExclusionAndGetPageNumber(
			final PageExcludes pageExcludes, final int page) {
		return (pageExcludes != null && pageExcludes.containsPage(page));
	}
}