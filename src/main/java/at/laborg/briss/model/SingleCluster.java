// $Id: SingleCluster.java 70 2012-05-26 16:20:19Z laborg $
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

public class SingleCluster implements Comparable<SingleCluster> {

    private final static int MERGE_VARIABILITY = 20;
    private final static int MAX_MERGE_PAGES = 20;

    private List<Integer> pagesToMerge;
    private List<Integer> allPages;
    private final List<Float[]> cropRatiosList = new ArrayList<Float[]>();

    private ClusterImageData imageData;

    private int excludedPageNumber = -1;
    private final boolean evenPage;
    private final int pageWidth;
    private final int pageHeight;

    public SingleCluster(boolean isEvenPage, int pageWidth, int pageHeight, int excludedPageNumber) {
        super();
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.evenPage = isEvenPage;
        this.excludedPageNumber = excludedPageNumber;
        this.pagesToMerge = new ArrayList<Integer>();
    }

    public ClusterImageData getImageData() {
        if (imageData == null)
            imageData = new ClusterImageData(pageWidth, pageHeight, pagesToMerge.size());
        return imageData;
    }

    /**
     * returns the ratio to crop the page x1,y1,x2,y2, origin = bottom left x1:
     * from left edge to left edge of crop rectange y1: from lower edge to lower
     * edge of crop rectange x2: from right edge to right edge of crop rectange
     * y2: from top edge to top edge of crop rectange
     *
     * @return
     */
    public List<Float[]> getRatiosList() {
        return cropRatiosList;
    }

    public void clearRatios() {
        cropRatiosList.clear();
    }

    public void addRatios(Float[] ratios) {
        // check if already in
        if (!cropRatiosList.contains(ratios)) {
            cropRatiosList.add(ratios);
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (evenPage ? 1231 : 1237);
        result = prime * result + excludedPageNumber;
        result = prime * result + getRoundedPageHeight();
        result = prime * result + getRoundedPageWidth();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SingleCluster other = (SingleCluster) obj;
        if (evenPage != other.evenPage)
            return false;
        if (excludedPageNumber != other.excludedPageNumber)
            return false;
        if (getRoundedPageHeight() != other.getRoundedPageHeight())
            return false;
        return getRoundedPageWidth() == other.getRoundedPageWidth();
    }

    public boolean isEvenPage() {
        return evenPage;
    }

    public int getRoundedPageHeight() {
        int tmp = pageHeight / MERGE_VARIABILITY;
        return tmp * MERGE_VARIABILITY;
    }

    public int getRoundedPageWidth() {
        int tmp = pageWidth / MERGE_VARIABILITY;
        return tmp * MERGE_VARIABILITY;
    }

    public void choosePagesToMerge(List<Integer> pages) {
        allPages = pages;
        if (pages.size() < MAX_MERGE_PAGES) {
            // use all pages
            pagesToMerge = pages;
        } else {
            // use an equal distribution
            float stepWidth = (float) pages.size() / MAX_MERGE_PAGES;
            float totalStepped = 0;
            for (int i = 0; i < MAX_MERGE_PAGES; i++) {
                pagesToMerge.add(pages.get(Integer.valueOf((int) Math.floor(totalStepped))));
                totalStepped += stepWidth;
            }
        }
    }

    public List<Integer> getAllPages() {
        return allPages;
    }

    public List<Integer> getPagesToMerge() {
        return pagesToMerge;
    }

    public int compareTo(SingleCluster that) {
        return this.getFirstPage() - that.getFirstPage();
    }

    private int getFirstPage() {
        int small = Integer.MAX_VALUE;
        for (Integer tmp : allPages) {
            if (tmp < small) {
                small = tmp;
            }
        }
        return small;
    }

}
