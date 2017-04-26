package org.talend.dataquality.statistics.cardinality;

import java.io.Serializable;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;

/**
 * Created by afournier on 31/03/17.
 */
public abstract class AbstractCardinalityStatistics<T extends AbstractCardinalityStatistics> implements Serializable {

    protected long count = 0;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void incrementCount() {
        this.count++;
    }

    public abstract long getDistinctCount();

    public long getDuplicateCount() {
        return this.count - getDistinctCount();
    }

    public abstract void merge(T other) throws CardinalityMergeException;

    public abstract void add(Object colObj);
}
