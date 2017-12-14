package org.talend.dataquality.semantic.snapshot;

/**
 * Provider for dictionary object registered on cluster
 */
public class BroadcastDictionarySnapshotProvider implements DictionarySnapshotProvider {

    private DictionarySnapshot dictionarySnapshot = null;

    /**
     * @param dictionarySnapshot
     */
    public BroadcastDictionarySnapshotProvider(DictionarySnapshot dictionarySnapshot) {
        this.dictionarySnapshot = dictionarySnapshot;
    }

    @Override
    public synchronized DictionarySnapshot get() {
        return dictionarySnapshot;
    }
}
