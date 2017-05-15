package at.laborg.briss.model;

import java.util.Set;

public class PageExcludes {
	private final Set<Integer> excludedPageSet;

	public PageExcludes(final Set<Integer> excludedPageSet) {
		this.excludedPageSet = excludedPageSet;
	}

	public final Set<Integer> getExcludedPageSet() {
		return excludedPageSet;
	}

	public final boolean containsPage(final int page) {
		return excludedPageSet.contains(new Integer(page));
	}
}
