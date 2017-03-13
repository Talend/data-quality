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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;

/**
 * created by yyin on 2014-9-15 Detailled comment
 * 
 */
public class AnalysisSwooshMatchRecordGrouping extends AnalysisMatchRecordGrouping {

    private Map<Integer, Attribute> attributesAsMatchKey;

    //used for the chart in the analysis
    private boolean needMatchInEnd = true;

    /**
     * DOC yyin AnalysisSwooshMatchRecordGrouping constructor comment.
     * 
     * @param matchResultConsumer
     */
    public AnalysisSwooshMatchRecordGrouping(MatchGroupResultConsumer matchResultConsumer) {
        super(matchResultConsumer);
    }

    public AnalysisSwooshMatchRecordGrouping() {

    }

    @Override
    public void setSurvivorShipAlgorithmParams(SurvivorShipAlgorithmParams survivorShipAlgorithmParams) {
        super.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);
        swooshGrouping.initialMFBForOneRecord(getCombinedRecordMatcher(), survivorShipAlgorithmParams);
    }

    @Override
    public void initialize() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        super.initialize();
        // get the match keys attributes, and put them in the map, no need to do this again for each record
        getKeyAttributes();
    }

    /**
     * DOC yyin Comment method "getKeyAttributes".
     */
    protected void getKeyAttributes() {
        attributesAsMatchKey = new HashMap<>();
        for (List<Map<String, String>> matchRule : getMultiMatchRules()) {
            for (Map<String, String> mkDef : matchRule) {
                String matcherType = mkDef.get(IRecordGrouping.MATCHING_TYPE);
                if (AttributeMatcherType.DUMMY.name().equals(matcherType)) {
                    continue;
                }
                int columnIndex = Integer.parseInt(mkDef.get(IRecordGrouping.COLUMN_IDX));
                Attribute attribute = new Attribute(mkDef.get(IRecordGrouping.ATTRIBUTE_NAME), columnIndex);
                attributesAsMatchKey.put(columnIndex, attribute);
            }
        }
    }

    @Override
    public void doGroup(RichRecord currentRecord) {//used only for analysis running
        // translate the record's attribute to -->origalRow, and attributes only contain match keys
        translateRecordForSwoosh(currentRecord);

        swooshGrouping.oneRecordMatch(currentRecord);

        needMatchInEnd = false;
    }

    /**
     * To move all attributes into: OriginRow, and change the current attributes to only contains the column which is
     * used to be the match keys.
     * 
     * @param currentRecord
     */
    private void translateRecordForSwoosh(RichRecord currentRecord) {
        List<Attribute> matchAttrs = new ArrayList<>();
        List<DQAttribute<?>> rowList = new ArrayList<>();
        for (Attribute attribute : currentRecord.getAttributes()) {
            DQAttribute<String> attri = new DQAttribute<>(attribute.getLabel(), attribute.getColumnIndex(), attribute.getValue());
            rowList.add(attri);
            // if the current attribute is definde as a match key.
            Attribute matchkey = attributesAsMatchKey.get(attribute.getColumnIndex());
            if (matchkey != null && StringUtils.equalsIgnoreCase(matchkey.getLabel(), attri.getLabel())) {
                matchAttrs.add(attri);
            }
        }
        currentRecord.getAttributes().clear();
        currentRecord.getAttributes().addAll(matchAttrs);
        currentRecord.setRecordSize(this.originalInputColumnSize);
        currentRecord.setOriginRow(rowList);
    }

    @Override
    public void end() throws IOException, InterruptedException {//used for analysis only 
        if (needMatchInEnd) {//used for the "chart" in the analysis
            combinedRecordMatcher.setDisplayLabels(true);
            swooshGrouping.swooshMatch(combinedRecordMatcher, survivorShipAlgorithmParams);
        }

        // out put
        swooshGrouping.afterAllRecordFinished();

        //TODO: check if it needed
        if (matchResultConsumer.isKeepDataInMemory()) {
            for (RichRecord row : tmpMatchResult) {
                // For swoosh algorithm, the GID can only be know after all of the records are computed.
                out(row);
            }
        }

        clear();
    }

    /**
     * DOC yyin Comment method "clear".
     */
    @Override
    protected void clear() {
        swooshGrouping.getOldGID2New().clear();
        tmpMatchResult.clear();
    }

    /**
     * used for analysis and tMatchGroup, and only after swoosh match finished.
     *  */
    @Override
    protected void outputRow(RichRecord row) {
        if (matchResultConsumer != null) {
            if (matchResultConsumer.isKeepDataInMemory()) {
                tmpMatchResult.add(row);
            } else {
                out(row);
            }
        }
    }

    @Override
    protected void out(RichRecord row) {
        List<DQAttribute<?>> row2 = getValuesFromOriginRow(row);

        Object[] strRow = getArrayFromAttributeList(row2, row2.size());

        outputRow(strRow);
    }

    protected List<DQAttribute<?>> getValuesFromOriginRow(RichRecord row) {
        //1, get the pure data
        List<DQAttribute<?>> row2 = row.getOutputRow(swooshGrouping.getOldGID2New(), this.isLinkToPrevious);

        //2, get the additional columns like GID, 
        row2.add(row.getGID());
        row2.add(row.getGRP_SIZE());
        row2.add(row.getMASTER());
        row2.add(row.getSCORE());
        row2.add(row.getGRP_QUALITY());

        //3, get other additional column by different options. 
        if (this.isOutputDistDetails()) {
            row2.add(row.getATTRIBUTE_SCORE());
        }
        return row2;
    }

    protected Object[] getArrayFromAttributeList(List<DQAttribute<?>> row, int arraySize) {
        Object[] strRow = new Object[arraySize];
        int idx = 0;
        for (DQAttribute<?> attr : row) {
            strRow[idx++] = attr.getValue();
        }
        return strRow;
    }

}
