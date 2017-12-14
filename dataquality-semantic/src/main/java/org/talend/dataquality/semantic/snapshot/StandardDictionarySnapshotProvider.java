package org.talend.dataquality.semantic.snapshot;

import org.talend.dataquality.semantic.api.CategoryRegistryManager;

/**
 * Used in discovery and validation in server mode
 */
public class StandardDictionarySnapshotProvider implements DictionarySnapshotProvider {

    @Override
    public synchronized DictionarySnapshot get() {
        return CategoryRegistryManager.getInstance().getCustomDictionaryHolder().getDictionarySnapshot();
    }
}
