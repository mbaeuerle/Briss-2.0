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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CropDefinition {

    private final File sourceFile;
    private final File destinationFile;
    private final Map<Integer, List<float[]>> pageToCropRectangles;

    private CropDefinition(final File source, final File destination,
                           final Map<Integer, List<float[]>> pageToCropRectangles) {
        this.sourceFile = source;
        this.destinationFile = destination;
        this.pageToCropRectangles = pageToCropRectangles;
    }

    public static CropDefinition createCropDefinition(final File source, final File destination,
                                                      final ClusterDefinition clusters) throws IOException {
        if (source == null)
            throw new IllegalArgumentException("Source must be provided");
        if (!source.exists())
            throw new IllegalArgumentException("Source(" + source.getAbsolutePath() + ") file doesn't exist");

        Map<Integer, List<float[]>> pagesToCrops = new HashMap<>();

        for (PageCluster cluster : clusters.getClusterList()) {
            for (Integer pageNumber : cluster.getAllPages()) {
                List<float[]> cropRectangles = pagesToCrops.get(pageNumber);
                if (cropRectangles == null) {
                    cropRectangles = new ArrayList<float[]>();
                }
                cropRectangles.addAll(cluster.getRatiosList());
                pagesToCrops.put(pageNumber, cropRectangles);
            }
        }

        CropDefinition result = new CropDefinition(source, destination, pagesToCrops);

        return result;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public File getDestinationFile() {
        return destinationFile;
    }

    public List<float[]> getRectanglesForPage(final Integer page) {
        if (pageToCropRectangles.containsKey(page))
            return pageToCropRectangles.get(page);
        else
            return Collections.emptyList();
    }

}
