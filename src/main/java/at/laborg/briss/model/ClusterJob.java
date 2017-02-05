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
import java.util.Set;

public class ClusterJob {

	private Set<Integer> excludedPageSet;
	private final File source;
	private final ClusterCollection clusterCollection;

	public ClusterJob(File inFile) {
		clusterCollection = new ClusterCollection();
		this.source = inFile;
	}

	public ClusterCollection getClusterCollection() {
		return clusterCollection;
	}

	public Set<Integer> getExcludedPageSet() {
		return excludedPageSet;
	}

	public void setExcludedPageSet(Set<Integer> excludedPageSet) {
		this.excludedPageSet = excludedPageSet;
	}

	public File getSource() {
		return source;
	}

	public int getTotalWorkUnits() {
		int size = 0;
		for (SingleCluster cluster : clusterCollection.getAsList()) {
			size += cluster.getPagesToMerge().size();
		}
		return size;
	}

}