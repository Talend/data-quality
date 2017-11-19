package org.talend.dataquality.semantic.recognizer;

import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;

public class DictionaryConstituentsProviders {

    private static final Logger LOGGER = Logger.getLogger(DictionaryConstituentsProviders.class);

    /**
     * Interface of DQ dictionary provider
     */
    public interface DicoProviderInterface {

        /**
         * Getter for DictianaryConstituents instance
         */
        DictionaryConstituents get();

    }

    /**
     * Provider of tenant-specific dictionary object
     */
    public static class SingletonProvider implements DicoProviderInterface {

        @Override
        public synchronized DictionaryConstituents get() {
            return CategoryRegistryManager.getInstance().getCustomDictionaryHolder().getDictionaryConstituents();
        }

    }

    /**
     * Provider for dictionary object registered on cluster
     */
    public static class RegisteredProvider implements DicoProviderInterface {

        DictionaryConstituents constituents = null;

        /**
         * @param constituents
         */
        public RegisteredProvider(DictionaryConstituents constituents) {
            this.constituents = constituents;
        }

        @Override
        public synchronized DictionaryConstituents get() {
            return constituents;
        }

    }

}
