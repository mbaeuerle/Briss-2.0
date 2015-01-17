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
package at.laborg.briss.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CropJob {
	private final File source;
	private final int sourcePageCount;
	private final HashMap<String, String> sourceMetaInfo;
	private final List<HashMap<String, Object>> sourceBookmarks;
	private File destinationFile;

	private ClusterCollection clusterCollection;

	private static final String RECOMMENDED_ENDING = "_cropped.pdf";

	public CropJob(File source, int pageCount,
			HashMap<String, String> metaInfo,
			List<HashMap<String, Object>> bookmarks) {
		super();
		this.source = source;
		this.sourcePageCount = pageCount;
		this.sourceMetaInfo = metaInfo;
		this.sourceBookmarks = bookmarks;
	}

	public HashMap<String, String> getSourceMetaInfo() {
		return sourceMetaInfo;
	}

	public List<HashMap<String, Object>> getSourceBookmarks() {
		return sourceBookmarks;
	}

	public File getSource() {
		return source;
	}

	public int getSourcePageCount() {
		return sourcePageCount;
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public void setAndCreateDestinationFile(File destinationFile)
			throws IOException {
		if (!destinationFile.exists()) {
			destinationFile.createNewFile();
		}
		this.destinationFile = destinationFile;
	}

	public File getRecommendedDestination() {
		// create file recommendation
		String origName = getSource().getAbsolutePath();
		String recommendedName = origName.substring(0, origName.length() - 4)
				+ RECOMMENDED_ENDING;
		return new File(recommendedName);
	}

	public ClusterCollection getClusterCollection() {
		return clusterCollection;
	}

	public void setClusterCollection(ClusterCollection clusters) {
		this.clusterCollection = clusters;
	}

}
