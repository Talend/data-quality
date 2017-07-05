// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.sampling;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.sampling.exception.DQException;

/**
 * created by zhao<br>
 * Bridge from data source extraction to data sample API
 * <p>
 * The call sequences for following methods must be comply with. <br>
 * 1. {{@link #prepareData()}<br>
 * 2. {{@link #hasNext()}<br>
 * 3. {{@link #getRecord()}<br>
 * 4. {{@link #finalizeDataSampling()}<br>
 * Step 2 and 3 can be executed several times
 *
 */
public class DataSamplingBridge {

    private SamplingOption samplingOption = SamplingOption.TopN;

    private ReservoirSampler<Object[]> reservoirSampler;

    private List<Object[]> reservoirSamplingData = new ArrayList<Object[]>();

    private int sampleSize = 1000;

    private SamplingDataSource<?> dataSource;

    private long currentRandomSeed = System.currentTimeMillis();

    // The cursor of reservoir sampling records.
    private int recordCursor = 0;

    private long dataSourceCursor = 0;

    private boolean stopRequested = false;

    public DataSamplingBridge(SamplingDataSource<?> ds) {
        this.dataSource = ds;
    }

    public void setSampleSize(int size) {
        sampleSize = size;
    }

    public void setSamplingOption(SamplingOption option) {
        samplingOption = option;
    }

    public SamplingOption getSamplingOption() {
        return samplingOption;
    }

    /**
     * 
     * DOC zhao see if there exist next record or not.
     * 
     * @return true if there is next sampling record, false otherwise.
     * @throws Exception
     */
    public boolean hasNext() throws DQException {
        if (recordCursor >= sampleSize) {
            // Stop getting sample from data source.
            return false;
        }
        if (SamplingOption.Reservoir == samplingOption) {
            return recordCursor < reservoirSamplingData.size();
        } else {

            return dataSource.hasNext();
        }
    }

    /**
     * 
     * DOC zhao Do prepare work before getting real data, work such as JDBC or file connection creation.
     * 
     * @return true if success, false otherwise.
     * @throws Exception When unexpected exception occurs
     */
    public boolean prepareData(long randomSeed) throws DQException {
        // Reset record cursor
        switch (samplingOption) {
        case TopN:
            break;
        case Percentage:
            break;
        case Reservoir:
            dataSourceCursor = 0;
            reservoirSamplingData = new ArrayList<Object[]>();
            reservoirSampler = new ReservoirSampler<Object[]>(sampleSize, randomSeed);
            reservoirSampler.clear();
            while (!stopRequested && dataSource.hasNext()) {
                reservoirSampler.onNext(dataSource.getRecord());
                dataSourceCursor++;
            }
            reservoirSampler.onCompleted(true);
            reservoirSamplingData = reservoirSampler.sample();
            break;
        default:
            break;
        }
        return false;
    }

    public void prepareData(String[] columnHeaders) throws DQException {
        prepareData(currentRandomSeed);
    }

    /**
     * Sets the randomSeed.
     * 
     * @param randomSeed the randomSeed to set
     */
    public void setRandomSeed(long randomSeed) {
        this.currentRandomSeed = randomSeed;
    }

    /**
     * 
     * Get one record from iterator of data source, note that this function is data-source type dependent such a case is
     * that in JDBC connection, the integration is from ResultSet while in file connection it's from file delimiter API.<br>
     * Before call this method, the method {{@link #hasNext()} should be called to check if there are data available
     * from data source.
     * 
     * @return true if success, false otherwise
     * @throws Exception occurs when there are unexpected exceptions.
     */
    public Object[] getRecord() throws DQException {
        Object[] records = null;
        switch (samplingOption) {
        case TopN:
            records = dataSource.getRecord();
            break;
        case Percentage:
            break;
        case Reservoir:
            if (reservoirSampler == null) {
                throw new DQException("DataSamplingBridge is not initialized");
            }
            records = reservoirSamplingData.get(recordCursor);
            break;
        default:
            break;
        }
        recordCursor++;
        return records;
    }

    /**
     * 
     * DOC zhao Finalize the data sample , some operation need to be done here such as closing csv file stream.
     * 
     * @return true if success, false otherwise.
     * @throws Exception When unexpected exception occurs
     */
    public boolean finalizeDataSampling() throws DQException {
        reservoirSamplingData = null;
        dataSource.finalizeDataSampling();
        return false;
    }

    public static final long RANDOM_SEED = 12345678;

    public boolean isStopRequested() {
        return stopRequested;
    }

    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }

    public long getDataSourceCursor() {
        return dataSourceCursor;
    }
}
