// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.numeric.quantile;

import com.clearspring.analytics.stream.quantile.TDigest;

/**
 * Quantile statistics bean implemented in a probability method. <br>
 * Refer to <a href=
 * "https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/stream/quantile/TDigest.java"
 * >TDigest</a>
 * 
 * @author zhao
 *
 */
public class TDigestQuantileStatistics {

    private TDigest dist = null;

    public TDigestQuantileStatistics() {
        dist = new TDigest(100);
    }

    public void initTDigest(int compression) {
        dist = new TDigest(compression);
    }

    public void add(double value) {
        dist.add(value);
    }

    public double getMedian() {
        if (dist.centroidCount() == 1) {
            return dist.centroids().iterator().next().mean();
        }
        return dist.quantile(0.5);
    }

    public double getLowerQuartile() {
        if (dist.centroidCount() == 1) {
            return dist.centroids().iterator().next().mean();
        }
        return dist.quantile(0.25);
    }

    public double getUpperQuartile() {
        if (dist.centroidCount() == 1) {
            return dist.centroids().iterator().next().mean();
        }
        return dist.quantile(0.75);
    }

    /**
     * Get quantile value given percentage.
     * 
     * @param percentage the percentage of the quantile
     * @return quantile value given of specified percentage.
     */
    public double getQuantile(double percentage) {
        if (dist.centroidCount() == 1) {
            return dist.centroids().iterator().next().mean();
        }
        return dist.quantile(percentage);
    }

}
