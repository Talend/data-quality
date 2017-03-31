package org.talend.dataquality.statistics.cardinality;

/**
 * Created by afournier on 31/03/17.
 */
public abstract class AbstractCardinalityStatistics {

    protected long count = 0;

    public long getCount() {
        return this.count;
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

    public abstract boolean merge(AbstractCardinalityStatistics other);
}
