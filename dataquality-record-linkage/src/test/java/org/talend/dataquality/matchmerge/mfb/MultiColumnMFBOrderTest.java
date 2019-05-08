package org.talend.dataquality.matchmerge.mfb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.TestCase;

import org.junit.Assert;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class MultiColumnMFBOrderTest extends TestCase {

    private final Callback callback = DefaultCallback.INSTANCE;

    private static final List<Record> listOrder1 = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "AAAAAAAAAA"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B1", 1, "OOOOOOOOOO") }), "R1", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "AAAAAAABBB"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B2", 1, "OOOOOIIIIZ") }), "R2", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "AAAAAACCBB"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B3", 1, "OOOOOOOZZZ") }), "R3", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    };

    private static final List<Record> listOrder2 = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "AAAAAAABBB"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B2", 1, "OOOOOIIIIZ") }), "R2", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "AAAAAACCBB"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B3", 1, "OOOOOOOZZZ") }), "R3", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "AAAAAAAAAA"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B1", 1, "OOOOOOOOOO") }), "R1", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    };

    private static final List<Record> listOrder3 = new ArrayList<Record>() {

        // ----A1----A2----A3----------------B1-----B2--------B3
        // A1---1---0.8---0.6----------B1----1-----0.4--------0.6
        // -----------------------------------------------------
        // A2--0.8---1----0.8----------B2---0.4-----1---------0.6
        // -------------------------------------------------------
        // A3--0.6--0.8---1------------B3---0.6-----0.6--------1
        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "AAAAAACCBB"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B3", 1, "OOOOOOOZZZ") }), "R3", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "AAAAAAAAAA"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B1", 1, "OOOOOOOOOO") }), "R1", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "AAAAAAABBB"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("B2", 1, "OOOOOIIIIZ") }), "R2", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    };

    private static final List<Record> listOrder4 = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name1", 0, "Maximilian"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city1", 1, "Concepcion") }), "R1", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name2", 0, "MaximiliaX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city2", 1, "ConcepcioX") }), "R2", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name3", 0, "MaximiliXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city3", 1, "ConcepciXX") }), "R3", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name4", 0, "MaximilXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city4", 1, "ConcepcXXX") }), "R4", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name5", 0, "MaximiXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city5", 1, "ConcepXXXX") }), "R5", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name6", 0, "MaximXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city6", 1, "ConceXXXXX") }), "R6", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name7", 0, "MaxiXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city7", 1, "ConcXXXXXX") }), "R7", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name8", 0, "MaxXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city8", 1, "ConXXXXXXX") }), "R8", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name9", 0, "MaXXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city9", 1, "CoXXXXXXXX") }), "R9", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name10", 0, "MXXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city10", 1, "CXXXXXXXX") }), "R10", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name10", 0, "XXXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city11", 1, "XXXXXXXXX") }), "R11", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    };

    private static final List<Record> listOrder5 = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name10", 0, "XXXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city11", 1, "XXXXXXXXX") }), "R11", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name7", 0, "MaxiXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city7", 1, "ConcXXXXXX") }), "R7", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name4", 0, "MaximilXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city4", 1, "ConcepcXXX") }), "R4", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name1", 0, "Maximilian"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city1", 1, "Concepcion") }), "R1", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name3", 0, "MaximiliXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city3", 1, "ConcepciXX") }), "R3", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name9", 0, "MaXXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city9", 1, "CoXXXXXXXX") }), "R9", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name5", 0, "MaximiXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city5", 1, "ConcepXXXX") }), "R5", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name6", 0, "MaximXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city6", 1, "ConceXXXXX") }), "R6", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name8", 0, "MaxXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city8", 1, "ConXXXXXXX") }), "R8", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name10", 0, "MXXXXXXXX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city10", 1, "CXXXXXXXX") }), "R10", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("name2", 0, "MaximiliaX"), //$NON-NLS-1$ //$NON-NLS-2$
                    new Attribute("city2", 1, "ConcepcioX") }), "R2", 999L, "MFB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    };

    private MatchMergeAlgorithm buildMFB(float[] attrThresholds, double minConfidence, SurvivorShipAlgorithmEnum mergeAlgo) {
        return MFB.build( //
                new AttributeMatcherType[] { AttributeMatcherType.LEVENSHTEIN, AttributeMatcherType.LEVENSHTEIN }, // algorithms
                new String[] { "", "" }, // algo params //$NON-NLS-1$ //$NON-NLS-2$
                attrThresholds, // thresholds
                minConfidence, // min confidence
                new SurvivorShipAlgorithmEnum[] { mergeAlgo, mergeAlgo }, // merge algos
                new String[] { "", "" }, // merge params //$NON-NLS-1$ //$NON-NLS-2$
                new double[] { 6, 4 }, // weights
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll,
                        IAttributeMatcher.NullOption.nullMatchAll }, // null optino
                new SubString[] { SubString.NO_SUBSTRING, SubString.NO_SUBSTRING }, // substring option
                "MFB" // source //$NON-NLS-1$
        );
    }

    public void testABCDELongest() {
        System.out.println("\n--------------- Longest (minConfidence = 0.55) -----------------------"); //$NON-NLS-1$
        MatchMergeAlgorithm algorithm = buildMFB(new float[] { 0.7f, 0.4f }, 0.55, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 1: "); //$NON-NLS-1$
        List<Record> mergeRecordList1 = algorithm.execute(listOrder1.iterator(), callback);
        printResult(mergeRecordList1);
        System.out.println("\nOrder 2:  "); //$NON-NLS-1$
        List<Record> mergeRecordList2 = algorithm.execute(listOrder2.iterator(), callback);
        printResult(mergeRecordList2);
        System.out.println("\nOrder 3: "); //$NON-NLS-1$
        List<Record> mergeRecordList3 = algorithm.execute(listOrder3.iterator(), callback);
        printResult(mergeRecordList3);
        Assert.assertTrue(assertResult(mergeRecordList1, mergeRecordList2));
        Assert.assertTrue(assertResult(mergeRecordList1, mergeRecordList3));
    }

    public void testNameAndCityLongest() {
        System.out.println("\n--------------- Longest (minConfidence = 0.4) -----------------------"); //$NON-NLS-1$
        MatchMergeAlgorithm algorithm = buildMFB(new float[] { 0.7f, 0.4f }, 0.55, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 4: "); //$NON-NLS-1$
        List<Record> mergeRecordList4 = algorithm.execute(listOrder4.iterator(), callback);
        printResult(mergeRecordList4);
        System.out.println("\nOrder 5:  "); //$NON-NLS-1$
        List<Record> mergeRecordList5 = algorithm.execute(listOrder5.iterator(), callback);
        printResult(mergeRecordList5);
        Assert.assertTrue(assertResult(mergeRecordList4, mergeRecordList5));
    }

    public void testABCDELongestLowMinConfidence() {
        System.out.println("\n--------------- Longest (minConfidence = 0.1) -----------------------"); //$NON-NLS-1$
        MatchMergeAlgorithm algorithm = buildMFB(new float[] { 0.7f, 0.4f }, 0.1, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 1:  "); //$NON-NLS-1$
        List<Record> mergeRecordList1 = algorithm.execute(listOrder1.iterator(), callback);
        printResult(mergeRecordList1);
        System.out.println("\nOrder 2: "); //$NON-NLS-1$
        List<Record> mergeRecordList2 = algorithm.execute(listOrder2.iterator(), callback);
        printResult(mergeRecordList2);
        System.out.println("\nOrder 3: "); //$NON-NLS-1$
        List<Record> mergeRecordList3 = algorithm.execute(listOrder3.iterator(), callback);
        printResult(mergeRecordList3);
        Assert.assertTrue(assertResult(mergeRecordList1, mergeRecordList2));
        Assert.assertTrue(assertResult(mergeRecordList1, mergeRecordList3));
    }

    public void testABCDEConcat() {
        System.out.println("\n--------------- Concat  -----------------------"); //$NON-NLS-1$
        MatchMergeAlgorithm algorithm = buildMFB(new float[] { 0.7f, 0.4f }, 0.4, SurvivorShipAlgorithmEnum.CONCATENATE);
        System.out.println("Order 1:  "); //$NON-NLS-1$
        List<Record> mergeRecordList1 = algorithm.execute(listOrder1.iterator(), callback);
        printResult(mergeRecordList1);
        System.out.println("\nOrder 2:  "); //$NON-NLS-1$
        List<Record> mergeRecordList2 = algorithm.execute(listOrder2.iterator(), callback);
        printResult(mergeRecordList2);
        System.out.println("\nOrder 3:  "); //$NON-NLS-1$
        List<Record> mergeRecordList3 = algorithm.execute(listOrder3.iterator(), callback);
        printResult(mergeRecordList3);
        Assert.assertTrue(assertResult(mergeRecordList1, mergeRecordList2, true, new Integer[] { 0 }));
        Assert.assertTrue(assertResult(mergeRecordList1, mergeRecordList3, true, new Integer[] { 0 }));
        Assert.assertTrue(assertResult(mergeRecordList2, mergeRecordList3));
    }

    private boolean assertResult(List<Record> expectMergeRecordList, List<Record> actualMergeRecordList) {
        return assertResult(expectMergeRecordList, actualMergeRecordList, false, null);
    }

    private boolean assertResult(List<Record> expectMergeRecordList, List<Record> actualMergeRecordList,
            boolean hasDifferentConfidence, Integer[] differentIndexs) {
        Assert.assertNotNull("expectMergeRecordList should not be null", expectMergeRecordList); //$NON-NLS-1$
        Assert.assertNotNull("actualMergeRecordList should not be null", actualMergeRecordList); //$NON-NLS-1$
        Assert.assertEquals("The size of groups should be same", expectMergeRecordList.size(), //$NON-NLS-1$
                actualMergeRecordList.size());
        for (int index = 0; index < expectMergeRecordList.size(); index++) {
            Record actualRecord = actualMergeRecordList.get(index);
            Record expectRecord = expectMergeRecordList.get(index);
            Assert.assertTrue("Both groups should contains same ids", //$NON-NLS-1$
                    actualRecord.getRelatedIds().containsAll(expectRecord.getRelatedIds()));
            if (hasDifferentConfidence && Arrays.asList(differentIndexs).contains(index)) {
                Assert.assertTrue("Both groups should have different confidence value", //$NON-NLS-1$
                        actualRecord.getConfidence() != expectRecord.getConfidence());
            } else {
                Assert.assertTrue("Both groups should have same confidence value", //$NON-NLS-1$
                        actualRecord.getConfidence() == expectRecord.getConfidence());
            }
        }
        return true;
    }

    private void printResult(List<Record> mergedRecords) {
        for (Record rec : mergedRecords) {
            List<String> attrList = rec.getAttributes().stream().map(attr -> attr.getValue()).collect(Collectors.toList());
            System.out.println("  " + rec + " " + attrList + "   Confidence: " + rec.getConfidence()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }
}
