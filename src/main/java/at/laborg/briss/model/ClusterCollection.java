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

import java.util.*;

public class ClusterCollection {
	private final HashMap<Integer, SingleCluster> pageToClustersMapping;
	private final HashMap<SingleCluster, List<Integer>> clusterToPagesMapping;
	private boolean dirty;

	public ClusterCollection() {
		this.dirty = true;
		this.pageToClustersMapping = new HashMap<>();
		this.clusterToPagesMapping = new HashMap<>();
	}

	private <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	public List<SingleCluster> getAsList() {
		return asSortedList(getClusterToPagesMapping().keySet());
	}

	public HashMap<SingleCluster, List<Integer>> getClusterToPagesMapping() {
		return clusterToPagesMapping;
	}

	public SingleCluster getSingleCluster(int pageNumber) {
		if (dirty) {
			for (Map.Entry<SingleCluster, List<Integer>> singleClusterListEntry : getClusterToPagesMapping()
					.entrySet()) {
				singleClusterListEntry.getValue()
						.forEach(e -> pageToClustersMapping.put(e - 1, singleClusterListEntry.getKey()));
			}
			dirty = false;
		}
		return pageToClustersMapping.get(pageNumber - 1);
	}

	public void addPageToCluster(SingleCluster tmpCluster, int pageNumber) {
		List<Integer> pages = Optional.ofNullable(getClusterToPagesMapping().get(tmpCluster))
				.orElseGet(() -> getClusterToPagesMapping().put(tmpCluster, new ArrayList<>()));
		pages.add(pageNumber);
		// whenever a page was added the pagesToClustersMapping isn't useful
		// anymore. This must be handled when reading the pages
		dirty = true;
	}
}
