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

import java.io.File;

public class WorkingSet {
	private File sourceFile;
	private String sourceFilePassword;
	private ClusterDefinition clusters;
	private PageExcludes pageExcludes;

	public WorkingSet(final File sourceFile, String password) {
		this.sourceFile = sourceFile;
		this.sourceFilePassword = password;
		clusters = new ClusterDefinition();
	}

	public final File getSourceFile() {
		return sourceFile;
	}

	public final void setSourceFile(final File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public final ClusterDefinition getClusterDefinition() {
		return clusters;
	}

	public final void setClusters(final ClusterDefinition clusters) {
		this.clusters = clusters;
	}

	public final PageExcludes getPageExcludes() {
		return pageExcludes;
	}

	public final void setPageExcludes(final PageExcludes pageExcludes) {
		this.pageExcludes = pageExcludes;
	}

	public String getSourceFilePassword() {
		return sourceFilePassword;
	}
}
