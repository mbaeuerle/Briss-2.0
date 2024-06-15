package at.laborg.briss.model;

import java.util.Set;

public record PageExcludes(Set<Integer> excludedPageSet) {

	public boolean containsPage(final int page) {
		return excludedPageSet.contains(Integer.valueOf(page));
	}
}
