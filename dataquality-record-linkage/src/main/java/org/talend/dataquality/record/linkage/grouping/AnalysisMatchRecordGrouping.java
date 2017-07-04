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
package org.talend.dataquality.record.linkage.grouping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;

/**
 * created by zshen on Aug 7, 2013 Detailled comment
 * 
 */
public class AnalysisMatchRecordGrouping extends AbstractRecordGrouping<Object> {

    /**
     * 
     */
    private static final String ISMASTER = "true"; //$NON-NLS-1$

    private static Logger LOG = Logger.getLogger(AnalysisMatchRecordGrouping.class);

    private List<String[]> resultStrList = new ArrayList<String[]>();

    List<Object[]> inputList = new ArrayList<Object[]>();

    // Temporarily store the match result so that it can be iterated to be handled later after all of the records are
    // computed.
    protected List<RichRecord> tmpMatchResult = new ArrayList<RichRecord>();

    protected MatchGroupResultConsumer matchResultConsumer = null;

    @SuppressWarnings("deprecation")
    public AnalysisMatchRecordGrouping(MatchGroupResultConsumer matchResultConsumer) {
        this.matchResultConsumer = matchResultConsumer;
        setColumnDelimiter(columnDelimiter);
        setIsOutputDistDetails(true);
        setSeperateOutput(Boolean.TRUE);
    }

    public AnalysisMatchRecordGrouping() {

    }

    public void addRuleMatcher(List<Map<String, String>> ruleMatcherConvertResult) {
        addMatchRule(ruleMatcherConvertResult);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#setSurvivorShipAlgorithmParams(org.talend
     * .dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams)
     */
    @Override
    public void setSurvivorShipAlgorithmParams(SurvivorShipAlgorithmParams survivorShipAlgorithmParams) {
        super.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);
    }

    /**
     * Sets the inputList.
     * 
     * @param inputRows the inputList to set
     */
    public void setMatchRows(List<Object[]> inputRows) {
        this.inputList = inputRows;
    }

    /**
     * 
     * The initialize(); method must be called before run.
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void run() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        try {
            for (Object[] inputRow : inputList) {
                String[] inputStrRow = new String[inputRow.length];
                int index = 0;
                for (Object obj : inputRow) {
                    inputStrRow[index++] = obj == null ? null : obj.toString();
                }
                doGroup(inputStrRow);
            }
            end();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
            // clean up state...
            Thread.currentThread().interrupt();
        }
    }

    /**
     * When running the match analysis, use the Record when do group.
     * 
     * @param currentRecord
     * @throws IOException
     * @throws InterruptedException
     */
    public void doGroup(RichRecord currentRecord) throws IOException, InterruptedException {
        String[] inputStrRow = new String[currentRecord.getAttributes().size()];
        int index = 0;
        for (Attribute obj : currentRecord.getAttributes()) {
            inputStrRow[index++] = obj.getValue() == null ? null : obj.getValue();
        }
        doGroup(inputStrRow);
    }

    /**
     * Getter for resultStrList.
     * 
     * @return the resultStrList
     */
    public List<String[]> getResultStrList() {
        return this.resultStrList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(COLUMN[])
     */
    @Override
    protected void outputRow(Object[] row) {
        matchResultConsumer.handle(row);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#end()
     */
    @Override
    public void end() throws IOException, InterruptedException {
        // output the masters
        for (Object[] mst : masterRecords) {
            outputRow(mst);
        }

        if (matchResultConsumer.isKeepDataInMemory) {
            for (RichRecord row : tmpMatchResult) {
                // For swoosh algorithm, the GID can only be know after all of the records are computed.
                out(row);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(org.talend.dataquality.record
     * .linkage.grouping.swoosh.RichRecord)
     */
    @Override
    protected void outputRow(RichRecord row) {
        if (matchResultConsumer.isKeepDataInMemory) {
            tmpMatchResult.add(row);
        } else {
            out(row);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#isMaster(java.lang.Object)
     */
    @Override
    protected boolean isMaster(Object col) {
        return ISMASTER.equals(col);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#modifyGroupSize(java.lang.Object)
     */
    @Override
    protected String incrementGroupSize(Object oldGroupSize) {
        return String.valueOf(Integer.parseInt(String.valueOf(oldGroupSize)) + 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#getCOLUMNFromObject(java.lang.Object)
     */
    @Override
    protected String castAsType(Object objectValue) {
        return String.valueOf(objectValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#createTYPEArray(int)
     */
    @Override
    protected Object[] createTYPEArray(int size) {
        return new Object[size];
    }

    protected void out(RichRecord row) {
        List<DQAttribute<?>> originRow = row.getOutputRow(swooshGrouping.getOldGID2New());
        String[] strRow = new String[originRow.size()];
        int idx = 0;
        for (DQAttribute<?> attr : originRow) {
            strRow[idx] = attr.getValue();
            idx++;
        }
        outputRow(strRow);
    }
}
