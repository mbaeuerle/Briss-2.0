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

import java.util.ArrayList;
import java.util.List;

public class ClusterDefinition {

	private final List<PageCluster> clusters = new ArrayList<PageCluster>();

	public final PageCluster getSingleCluster(final int pageNumber) {
		for (PageCluster cluster : clusters) {
			if (cluster.getAllPages().contains(pageNumber))
				return cluster;
		}
		return null;
	}

	public final List<PageCluster> getClusterList() {
		return clusters;
	}

	public final void addOrMergeCluster(final PageCluster tmpCluster) {
		PageCluster existingCluster = findNearlyEqualCluster(tmpCluster);
		if (existingCluster != null) {
			existingCluster.mergeClusters(tmpCluster);
		} else {
			clusters.add(tmpCluster);
		}
	}

	private PageCluster findNearlyEqualCluster(final PageCluster clusterToCheck) {
		for (PageCluster cluster : clusters) {
			if (cluster.isClusterNearlyEqual(clusterToCheck))
				return cluster;
		}
		return null;
	}

	public final void selectAndSetPagesForMerging() {
		for (PageCluster cluster : clusters) {
			cluster.choosePagesToMerge();
		}
	}

	public final int getNrOfPagesToRender() {
		int size = 0;
		for (PageCluster cluster : clusters) {
			size += cluster.getPagesToMerge().size();
		}
		return size;
	}
}