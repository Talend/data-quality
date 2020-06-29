package org.talend.dataquality.common.inference;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Implements analysis on Avro records. Implementations are expected to be:
 * <ul>
 * <li>Stateful.</li>
 * <li>Not thread safe (no need to enforce thread safety in implementations).</li>
 * </ul>
 */
public interface AvroAnalyzer extends Serializable, AutoCloseable {

    /**
     * Prepare implementation for analysis. Implementations may perform various tasks like internal initialization or
     * connection establishment. This method should only be called once.
     *
     * The semantic schema used to analyze each record needs to be available in the schema of
     * the first IndexedRecord sent to analyze.
     */
    void init();

    /**
     * init() with the semantic schema that will be used to analyze each record.
     */
    void init(Schema semanticSchema);

    /**
     * Analyze a single record (row).
     *
     * To use if you don't care about value level metadata.
     *
     * @param record A record (row) with a value in each column.
     * @return <code>true</code> if analyze was ok, <code>false</code> otherwise.
     */
    boolean analyze(IndexedRecord record);

    /**
     * Analyze a set of records.
     *
     * @param records A set of records.
     * @return a stream of *value level metadata* record out.
     */
    Stream<IndexedRecord> analyze(Stream<IndexedRecord> records);

    /**
     * Ends the analysis (implementations may perform result optimizations after the repeated call to
     * {@link #analyze(IndexedRecord)}).
     */
    default void end() {
        // Nothing to do
    }

    /**
     * Retrieve the semantic schema metadata based on values submitted in {@link #analyze(IndexedRecord)} and the initial semantic schema.
     */
    Schema getResult();

    /**
     * Retrieve the semantic schema metadata (always one element in the list).
     */
    List<Schema> getResults();
}
