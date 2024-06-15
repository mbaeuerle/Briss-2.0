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
package at.laborg.briss.model;

import java.util.ArrayList;
import java.util.List;

public class ClusterDefinition {

	private final List<PageCluster> clusters = new ArrayList<>();

	public final PageCluster getSingleCluster(final int pageNumber) {
		return clusters.stream().filter(e -> e.getAllPages().contains(pageNumber)).findFirst().orElse(null);
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
		return clusters.stream().filter(e -> e.isClusterNearlyEqual(clusterToCheck)).findFirst().orElse(null);
	}

	public final void selectAndSetPagesForMerging() {
		clusters.forEach(c -> c.choosePagesToMerge());
	}

	public final int getNrOfPagesToRender() {
		return clusters.stream().mapToInt(e -> e.getPagesToMerge().size()).sum();
	}
}
