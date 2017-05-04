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
package org.talend.survivorship.action.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.survivorship.action.ISurvivoredAction;
import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.Record;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.SubDataSet;
import org.talend.survivorship.model.SurvivedResult;

/**
 * Create by zshen test for CRCRHandler
 */
public class CRCRHandlerTest {

    /**
     * Test method for {@link org.talend.survivorship.action.handler.CRCRHandler#handleRequest(java.lang.Object, int)}.
     */
    @Test
    public void testHandleRequestObjectInt() {
        Column col1 = new Column("city1", "String");
        Column col2 = new Column("city2", "String");
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        DataSet dataset = new DataSet(columns);
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1");
        Attribute att2 = new Attribute(record1, col2, "value2");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        dataset.getRecordList().add(record1);
        List<Integer> conflictRowNum = new ArrayList<>();
        conflictRowNum.add(0);
        dataset.getConflictDataMap().get().put("city2", conflictRowNum);
        ISurvivoredAction action = RuleDefinition.Function.MostCommon.getAction();
        Column refColumn = col1;
        Column tarColumn = col2;
        String ruleName = "rule1";
        String expression = null;
        boolean isIgnoreBlank = false;
        String fillColumn = "city2";
        boolean isDealDup = false;
        Map<String, Integer> columnIndexMap = new HashMap<>();
        columnIndexMap.put("city1", 0);
        columnIndexMap.put("city2", 1);
        FunctionParameter functionParameter = new FunctionParameter(action, expression, isIgnoreBlank, isDealDup);
        HandlerParameter handlerParameter = new HandlerParameter(dataset, refColumn, tarColumn, ruleName, columnIndexMap,
                fillColumn, functionParameter);
        CRCRHandler crcrHandler = new CRCRHandler(handlerParameter);
        DataSet subDataset = null;
        for (int index = 0; index < 5; index++) {
            crcrHandler.handleRequest(null, index);

            if (index == 0) {
                subDataset = crcrHandler.getHandlerParameter().getDataset();
                Assert.assertNotEquals("subDataset should be create so that dataset should different with subDataset", dataset,
                        subDataset);
            } else {
                Assert.assertEquals("crcrHandler only should be execute one time so that dataset will not change agian",
                        subDataset, crcrHandler.getHandlerParameter().getDataset());
            }
        }
    }

    /**
     * Test method for {@link org.talend.survivorship.action.handler.CRCRHandler#handleRequest()}.
     */
    @Test
    public void testHandleRequest() {
        HandlerParameter handlerParameter = createHandlerParameter(null);
        CRCRHandler crcrHandler = new CRCRHandler(handlerParameter);
        crcrHandler.handleRequest();
        Assert.assertEquals("The record of dataset is empty so that no subDataset generated", handlerParameter.getDataset(), //$NON-NLS-1$
                crcrHandler.getHandlerParameter().getDataset());
    }

    /**
     * Test method for {@link org.talend.survivorship.action.handler.CRCRHandler#initConflictRowNum(java.util.Map)}.
     */
    @Test
    public void testInitConflictRowNum() {
        HandlerParameter handlerParameter = createHandlerParameter(null);
        CRCRHandler crcrHandler = new CRCRHandler(handlerParameter);
        Map<Integer, String> preConflictRowNum = new HashMap<>();
        preConflictRowNum.put(1, "city1");
        preConflictRowNum.put(2, "city1");
        preConflictRowNum.put(3, "city2");
        crcrHandler.initConflictRowNum(preConflictRowNum);
        Assert.assertEquals("the column name of record 1 should be city1", "city1", crcrHandler.conflictRowNum.get(1));
        Assert.assertEquals("the column name of record 2 should be city1", "city1", crcrHandler.conflictRowNum.get(2));
        Assert.assertEquals("the column name of record 3 should be city2", "city2", crcrHandler.conflictRowNum.get(3));
    }

    /**
     * Create by zshen create handler Parameter
     * 
     * @return
     */
    private HandlerParameter createHandlerParameter(ISurvivoredAction action) {
        List<Column> columns = new ArrayList<>();
        DataSet dataset = new DataSet(columns);
        if (action == null) {
            action = RuleDefinition.Function.MostCommon.getAction();
        }
        Column refColumn = new Column("city1", "String");
        Column tarColumn = new Column("city2", "String");
        String ruleName = "rule1";
        String expression = "value11";
        boolean isIgnoreBlank = false;
        String fillColumn = "city2";
        boolean isDealDup = false;
        Map<String, Integer> columnIndexMap = new HashMap<>();
        FunctionParameter functionParameter = new FunctionParameter(action, expression, isIgnoreBlank, isDealDup);
        HandlerParameter handlerParameter = new HandlerParameter(dataset, refColumn, tarColumn, ruleName, columnIndexMap,
                fillColumn, functionParameter);
        return handlerParameter;
    }

    /**
     * Test method for {@link org.talend.survivorship.action.handler.CRCRHandler#isContinue(java.lang.Object, int)}.
     */
    @Test
    public void testIsContinue() {
        Column col1 = new Column("city1", "String");
        Column col2 = new Column("city2", "String");
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        DataSet dataset = new DataSet(columns);
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1");
        Attribute att2 = new Attribute(record1, col2, "value2");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        dataset.getRecordList().add(record1);
        List<Integer> conflictRowNum = new ArrayList<>();
        conflictRowNum.add(0);
        dataset.getConflictDataMap().get().put("city2", conflictRowNum);
        ISurvivoredAction action = RuleDefinition.Function.MostCommon.getAction();
        Column refColumn = col1;
        Column tarColumn = col2;
        String ruleName = "rule1";
        String expression = null;
        boolean isIgnoreBlank = false;
        String fillColumn = "city2";
        boolean isDealDup = false;
        Map<String, Integer> columnIndexMap = new HashMap<>();
        columnIndexMap.put("city1", 0);
        columnIndexMap.put("city2", 1);
        FunctionParameter functionParameter = new FunctionParameter(action, expression, isIgnoreBlank, isDealDup);
        HandlerParameter handlerParameter = new HandlerParameter(dataset, refColumn, tarColumn, ruleName, columnIndexMap,
                fillColumn, functionParameter);
        CRCRHandler crcrHandler = new CRCRHandler(handlerParameter);
        Map<Integer, String> preConflictRowNum = new HashMap<>();
        preConflictRowNum.put(0, "city1");
        crcrHandler.initConflictRowNum(preConflictRowNum);
        boolean continue1 = crcrHandler.isContinue(new Object[] { "value1", "value2" }, 0);
        boolean continue2 = crcrHandler.isContinue(new Object[] { "value1", "value2" }, 1);
        boolean continue3 = crcrHandler.isContinue(new Object[] { "value1", "value2" }, 2);
        Assert.assertEquals("0 is conflict row Number so that continue1 should be true", true, continue1);
        Assert.assertEquals("1 is not conflict row Number so that continue2 should be false", false, continue2);
        Assert.assertEquals("2 is not conflict row Number so that continue3 should be false", false, continue3);
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.handler.CRCRHandler#linkSuccessor(org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler)}
     * .
     */
    @Test
    public void testLinkSuccessor() {
        HandlerParameter createHandlerParameter = this.createHandlerParameter(null);
        AbstractChainResponsibilityHandler crcr1 = new CRCRHandler(createHandlerParameter);
        AbstractChainResponsibilityHandler crcr2 = new CRCRHandler(createHandlerParameter);
        AbstractChainResponsibilityHandler mtcr = new MTCRHandler(createHandlerParameter);
        AbstractChainResponsibilityHandler link1 = crcr1.linkSuccessor(crcr2);
        AbstractChainResponsibilityHandler link2 = crcr2.linkSuccessor(mtcr);
        Assert.assertEquals("link1 should same with crcr2", link1, crcr2);
        Assert.assertEquals("link2 should keep crcr2 too because of mtcr is not conflict resolve handler", link2, crcr2);
    }

    /**
     * Test method for {@link org.talend.survivorship.action.handler.CRCRHandler#getSurvivoredRowNum()}.
     */
    @Test
    public void testGetSurvivoredRowNum() {

        Column col1 = new Column("city1", "String");
        Column col2 = new Column("city2", "String");
        Column col3 = new Column("city3", "String");
        Column col4 = new Column("city4", "String");
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
        columns.add(col4);
        DataSet dataset = new DataSet(columns);
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1");
        Attribute att2 = new Attribute(record1, col2, "value2");
        Attribute att3 = new Attribute(record1, col2, "value33");
        Attribute att4 = new Attribute(record1, col2, "value4");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        record1.putAttribute("city3", att3);
        record1.putAttribute("city4", att4);
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        col3.putAttribute(record1, att3);
        col4.putAttribute(record1, att4);
        Record record2 = new Record();
        record2.setId(2);
        att1 = new Attribute(record2, col1, "value11");
        att2 = new Attribute(record2, col2, null);
        att3 = new Attribute(record2, col2, "value3");
        att4 = new Attribute(record2, col2, "value4");
        record2.putAttribute("city1", att1);
        record2.putAttribute("city2", att2);
        record2.putAttribute("city3", att3);
        record2.putAttribute("city4", att4);
        col1.putAttribute(record2, att1);
        col2.putAttribute(record2, att2);
        col3.putAttribute(record2, att3);
        col4.putAttribute(record2, att4);
        dataset.getRecordList().add(record1);
        dataset.getRecordList().add(record2);
        HandlerParameter createHandlerParameter = this.createHandlerParameter(null);
        createHandlerParameter.setDataset(dataset);
        CRCRHandler crcr1 = new CRCRHandler(createHandlerParameter);
        AbstractChainResponsibilityHandler crcr2 = new CRCRHandler(createHandlerParameter);
        CRCRHandler crcr3 = new CRCRHandler(createHandlerParameter);
        CRCRHandler crcr4 = new CRCRHandler(createHandlerParameter);
        CRCRHandler crcr5 = new CRCRHandler(createHandlerParameter);
        crcr1.linkSuccessor(crcr2);

        // link case and last handler contain 1 result
        Map<Integer, String> preConflictRowNum = new HashMap<>();
        preConflictRowNum.put(0, "city1");
        preConflictRowNum.put(1, "city1");
        crcr1.initConflictRowNum(preConflictRowNum);
        preConflictRowNum = new HashMap<>();
        preConflictRowNum.put(0, "city2");
        crcr2.initConflictRowNum(preConflictRowNum);
        SurvivedResult survivoredRowNum = crcr1.getSurvivoredRowNum();
        Assert.assertEquals("The column name should be city2", "city2", survivoredRowNum.getColumnName());
        Assert.assertEquals("The row number should be 0", 0, survivoredRowNum.getRowNum().intValue());
        // single handler and not a single result
        preConflictRowNum = new HashMap<>();
        preConflictRowNum.put(0, "city3");
        preConflictRowNum.put(1, "city4");
        crcr3.initConflictRowNum(preConflictRowNum);
        survivoredRowNum = crcr3.getSurvivoredRowNum();
        Assert.assertNotNull("survivoredRowNum should not be null", survivoredRowNum);
        Assert.assertEquals("The column name should be city3", "city3", survivoredRowNum.getColumnName());
        Assert.assertEquals("The row number should be 0", 0, survivoredRowNum.getRowNum().intValue());
        // single handler and size of preConflictRowNum is zero
        preConflictRowNum = new HashMap<>();
        crcr4.initConflictRowNum(preConflictRowNum);
        survivoredRowNum = crcr4.getSurvivoredRowNum();
        Assert.assertNull("survivoredRowNum should be null", survivoredRowNum);
        // single handler and not a single result but all of result value are same
        preConflictRowNum = new HashMap<>();
        preConflictRowNum.put(0, "city4");
        preConflictRowNum.put(1, "city4");
        crcr5.initConflictRowNum(preConflictRowNum);
        survivoredRowNum = crcr5.getSurvivoredRowNum();
        Assert.assertEquals("The column name should be city4", "city4", survivoredRowNum.getColumnName());
        Assert.assertEquals("The row number should be 0", 0, survivoredRowNum.getRowNum().intValue());

    }

    /**
     * Test method for {@link org.talend.survivorship.action.handler.CRCRHandler#getLongestResult(java.lang.Object)}.
     */
    @Test
    public void testGetLongestResult() {
        Column col1 = new Column("city1", "String");
        Column col2 = new Column("city2", "String");
        Column col3 = new Column("city3", "String");
        Column col4 = new Column("city4", "String");
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
        columns.add(col4);

        DataSet dataset = new DataSet(columns);
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1");
        Attribute att2 = new Attribute(record1, col2, "value2");
        Attribute att3 = new Attribute(record1, col2, "value33");
        Attribute att4 = new Attribute(record1, col2, "value4");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        record1.putAttribute("city3", att3);
        record1.putAttribute("city4", att4);
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        col3.putAttribute(record1, att3);
        col4.putAttribute(record1, att4);
        Record record2 = new Record();
        record2.setId(2);
        att1 = new Attribute(record2, col1, "value11");
        att2 = new Attribute(record2, col2, null);
        att3 = new Attribute(record2, col2, "value3");
        att4 = new Attribute(record2, col2, "value4");
        record2.putAttribute("city1", att1);
        record2.putAttribute("city2", att2);
        record2.putAttribute("city3", att3);
        record2.putAttribute("city4", att4);
        col1.putAttribute(record2, att1);
        col2.putAttribute(record2, att2);
        col3.putAttribute(record2, att3);
        col4.putAttribute(record2, att4);
        dataset.getRecordList().add(record1);
        dataset.getRecordList().add(record2);
        HandlerParameter createHandlerParameter = this.createHandlerParameter(null);
        createHandlerParameter.setTarColumn(col1);
        List<Integer> conflictIndex = new ArrayList<>();
        conflictIndex.add(0);
        conflictIndex.add(1);
        createHandlerParameter.setDataset(new SubDataSet(dataset, conflictIndex));
        CRCRHandler crcr1 = new CRCRHandler(createHandlerParameter);

        Object longestResult = crcr1.getLongestResult("value");
        Assert.assertEquals("The result of logest value should be value11", "value11", longestResult.toString());

        longestResult = crcr1.getLongestResult("value11");
        Assert.assertEquals("The result of logest value should be value1", "value1", longestResult.toString());

        createHandlerParameter.setRefColumn(col2);
        createHandlerParameter.setTarColumn(col2);
        createHandlerParameter.setFillColumn("city1");
        longestResult = crcr1.getLongestResult("value");
        Assert.assertEquals("The result of logest value should be value11", "value11", longestResult.toString());

        createHandlerParameter.setFillColumn(null);
        longestResult = crcr1.getLongestResult("value");
        Assert.assertEquals("The result of logest value should be value2", "value2", longestResult.toString());

        createHandlerParameter = this.createHandlerParameter(RuleDefinition.Function.Exclusiveness.getAction());
        createHandlerParameter.setTarColumn(col1);
        conflictIndex = new ArrayList<>();
        conflictIndex.add(0);
        conflictIndex.add(1);
        createHandlerParameter.setDataset(new SubDataSet(dataset, conflictIndex));
        crcr1 = new CRCRHandler(createHandlerParameter);

        longestResult = crcr1.getLongestResult("value");
        Assert.assertEquals("The result of logest value should be value1", "value1", longestResult.toString());

    }

    /**
     * Test method for {@link org.talend.survivorship.action.handler.CRCRHandler#getNonDupResult(java.lang.Object)}.
     */
    @Test
    public void testGetNonDupResult() {
        Column col1 = new Column("city1", "String");
        Column col2 = new Column("city2", "String");
        Column col3 = new Column("city3", "String");
        Column col4 = new Column("city4", "String");
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
        columns.add(col4);

        DataSet dataset = new DataSet(columns);
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1");
        Attribute att2 = new Attribute(record1, col2, "value2");
        Attribute att3 = new Attribute(record1, col2, "value33");
        Attribute att4 = new Attribute(record1, col2, "value4");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        record1.putAttribute("city3", att3);
        record1.putAttribute("city4", att4);
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        col3.putAttribute(record1, att3);
        col4.putAttribute(record1, att4);
        Record record2 = new Record();
        record2.setId(2);
        att1 = new Attribute(record2, col1, "value11");
        att2 = new Attribute(record2, col2, null);
        att3 = new Attribute(record2, col2, "value3");
        att4 = new Attribute(record2, col2, "value4");
        record2.putAttribute("city1", att1);
        record2.putAttribute("city2", att2);
        record2.putAttribute("city3", att3);
        record2.putAttribute("city4", att4);
        col1.putAttribute(record2, att1);
        col2.putAttribute(record2, att2);
        col3.putAttribute(record2, att3);
        col4.putAttribute(record2, att4);
        dataset.getRecordList().add(record1);
        dataset.getRecordList().add(record2);
        HandlerParameter createHandlerParameter = this.createHandlerParameter(null);
        createHandlerParameter.setTarColumn(col1);
        List<Integer> conflictIndex = new ArrayList<>();
        conflictIndex.add(0);
        conflictIndex.add(1);
        createHandlerParameter.setDataset(new SubDataSet(dataset, conflictIndex));
        CRCRHandler crcr1 = new CRCRHandler(createHandlerParameter);

        Object nonDupResult = crcr1.getNonDupResult("value");
        Assert.assertEquals("The result of no duplicate value should be empty", "", nonDupResult.toString());

        createHandlerParameter.setFillColumn(null);
        nonDupResult = crcr1.getNonDupResult("value");
        Assert.assertEquals("The result of logest value should be value11", "value11", nonDupResult.toString());

        createHandlerParameter.setFillColumn("");
        nonDupResult = crcr1.getNonDupResult("value");
        Assert.assertEquals("The result of logest value should be value11", "value11", nonDupResult.toString());
    }

}
