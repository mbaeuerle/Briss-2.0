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

public class ExportImportHelper {

	// public static String export(ClusterJob clusterJob) {
	//
	// StringBuffer result = new StringBuffer();
	// result.append(exportExcludePages(clusterJob.getPageExcludes().getExcludedPageSet())
	// + "\n");
	// for (SingleCluster cluster :
	// clusterJob.getClusterCollection().getAsList()) {
	// result.append(exportRatios(cluster.getRatiosList()) + "\n");
	// }
	// return result.toString();
	// }
	//
	// private static String exportRatios(List<Float[]> ratiosList) {
	// StringBuffer result = new StringBuffer();
	// for (Float[] ratios : ratiosList) {
	// result.append(ratios[0] + " " + ratios[1] + " " + ratios[2] + " "
	// + ratios[3] + " ");
	// }
	// return result.toString();
	// }
	//
	// private static String exportExcludePages(Set<Integer> excludedPages) {
	// if (excludedPages != null) {
	// StringBuffer result = new StringBuffer();
	// for (Integer page : excludedPages) {
	// result.append(page + " ");
	// }
	// return result.toString();
	// }
	// return null;
	// }
	//
	// public static ClusterJob importClusterJobData(File pdf, File inFileText)
	// {
	// // create clusterjob
	// // add excluded Pages
	// // run merging
	// // set rectangles
	// // return data
	// return null;
	// }
}
