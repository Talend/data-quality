package org.talend.dataquality.matchmerge.mfb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.TestCase;

import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class MultiColumnMFBOrderTest extends TestCase {

    // private final Callback callback = new LoggerCallback();

    private final Callback callback = DefaultCallback.INSTANCE;

    private final List<Record> listOrder1 = new ArrayList<Record>() {

        {
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A1", 0, "AAAAAAAAAA"), new Attribute("B1", 1, "OOOOOOOOOO") }),
                    "R1", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A2", 0, "AAAAAAABBB"), new Attribute("B2", 1, "OOOOOIIIIZ") }),
                    "R2", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A3", 0, "AAAAAACCBB"), new Attribute("B3", 1, "OOOOOOOZZZ") }),
                    "R3", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder2 = new ArrayList<Record>() {

        {
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A2", 0, "AAAAAAABBB"), new Attribute("B2", 1, "OOOOOIIIIZ") }),
                    "R2", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A3", 0, "AAAAAACCBB"), new Attribute("B3", 1, "OOOOOOOZZZ") }),
                    "R3", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A1", 0, "AAAAAAAAAA"), new Attribute("B1", 1, "OOOOOOOOOO") }),
                    "R1", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder3 = new ArrayList<Record>() {

        {
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A3", 0, "AAAAAACCBB"), new Attribute("B3", 1, "OOOOOOOZZZ") }),
                    "R3", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A1", 0, "AAAAAAAAAA"), new Attribute("B1", 1, "OOOOOOOOOO") }),
                    "R1", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(new Attribute[] { new Attribute("A2", 0, "AAAAAAABBB"), new Attribute("B2", 1, "OOOOOIIIIZ") }),
                    "R2", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder1a = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDE"), new Attribute("B1", 1, "ALLMM") }),
                    "R1", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "ABCDF"), new Attribute("B2", 1, "BLLLL") }),
                    "R2", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "ABCFF"), new Attribute("B3", 1, "CLLLM") }),
                    "R3", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder2a = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "ABCDF"), new Attribute("B2", 1, "BLLLL") }),
                    "R2", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "ABCFF"), new Attribute("B3", 1, "CLLLM") }),
                    "R3", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDE"), new Attribute("B1", 1, "ALLMM") }),
                    "R1", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder3a = new ArrayList<Record>() {

        // ----A1----A2----A3----------------B1-----B2--------B3
        // A1---1---0.8---0.6----------B1----1-----0.4--------0.6
        // -----------------------------------------------------
        // A2--0.8---1----0.8----------B2---0.4-----1---------0.6
        // -------------------------------------------------------
        // A3--0.6--0.8---1------------B3---0.6-----0.6--------1

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "ABCFF"), new Attribute("B3", 1, "CLLLM") }),
                    "R3", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDE"), new Attribute("B1", 1, "ALLMM") }),
                    "R1", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "ABCDF"), new Attribute("B2", 1, "BLLLL") }),
                    "R2", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder4 = new ArrayList<Record>() {

        {
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name1", 0, "Maximilian"), new Attribute("city1", 1, "Concepcion") }),
                    "R1", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name2", 0, "MaximiliaX"), new Attribute("city2", 1, "ConcepcioX") }),
                    "R2", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name3", 0, "MaximiliXX"), new Attribute("city3", 1, "ConcepciXX") }),
                    "R3", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name4", 0, "MaximilXXX"), new Attribute("city4", 1, "ConcepcXXX") }),
                    "R4", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name5", 0, "MaximiXXXX"), new Attribute("city5", 1, "ConcepXXXX") }),
                    "R5", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name6", 0, "MaximXXXXX"), new Attribute("city6", 1, "ConceXXXXX") }),
                    "R6", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name7", 0, "MaxiXXXXXX"), new Attribute("city7", 1, "ConcXXXXXX") }),
                    "R7", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name8", 0, "MaxXXXXXXX"), new Attribute("city8", 1, "ConXXXXXXX") }),
                    "R8", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name9", 0, "MaXXXXXXXX"), new Attribute("city9", 1, "CoXXXXXXXX") }),
                    "R9", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name10", 0, "MXXXXXXXX"), new Attribute("city10", 1, "CXXXXXXXX") }),
                    "R10", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name10", 0, "XXXXXXXXX"), new Attribute("city11", 1, "XXXXXXXXX") }),
                    "R11", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder5 = new ArrayList<Record>() {

        {
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name10", 0, "XXXXXXXXX"), new Attribute("city11", 1, "XXXXXXXXX") }),
                    "R11", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name7", 0, "MaxiXXXXXX"), new Attribute("city7", 1, "ConcXXXXXX") }),
                    "R7", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name4", 0, "MaximilXXX"), new Attribute("city4", 1, "ConcepcXXX") }),
                    "R4", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name1", 0, "Maximilian"), new Attribute("city1", 1, "Concepcion") }),
                    "R1", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name3", 0, "MaximiliXX"), new Attribute("city3", 1, "ConcepciXX") }),
                    "R3", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name9", 0, "MaXXXXXXXX"), new Attribute("city9", 1, "CoXXXXXXXX") }),
                    "R9", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name5", 0, "MaximiXXXX"), new Attribute("city5", 1, "ConcepXXXX") }),
                    "R5", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name6", 0, "MaximXXXXX"), new Attribute("city6", 1, "ConceXXXXX") }),
                    "R6", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name8", 0, "MaxXXXXXXX"), new Attribute("city8", 1, "ConXXXXXXX") }),
                    "R8", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name10", 0, "MXXXXXXXX"), new Attribute("city10", 1, "CXXXXXXXX") }),
                    "R10", 999L, "MFB"));
            add(new Record(
                    Arrays.asList(
                            new Attribute[] { new Attribute("name2", 0, "MaximiliaX"), new Attribute("city2", 1, "ConcepcioX") }),
                    "R2", 999L, "MFB"));
        }
    };

    private MatchMergeAlgorithm buildMFB(float attrThreshold, double minConfidence, SurvivorShipAlgorithmEnum mergeAlgo) {
        return MFB.build( //
                new AttributeMatcherType[] { AttributeMatcherType.LEVENSHTEIN, AttributeMatcherType.LEVENSHTEIN }, // algorithms
                new String[] { "", "" }, // algo params
                new float[] { 0.7f, 0.4f }, // thresholds
                minConfidence, // min confidence
                new SurvivorShipAlgorithmEnum[] { mergeAlgo, mergeAlgo }, // merge algos
                new String[] { "", "" }, // merge params
                new double[] { 6, 4 }, // weights
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll,
                        IAttributeMatcher.NullOption.nullMatchAll }, // null optino
                new SubString[] { SubString.NO_SUBSTRING, SubString.NO_SUBSTRING }, // substring option
                "MFB" // source
        );
    }

    public void testABCDE_Longest() {
        System.out.println("\n--------------- Longest (minConfidence = 0.55) -----------------------");
        MatchMergeAlgorithm algorithm = buildMFB(0.5F, 0.55, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 1: ");
        printResult(algorithm.execute(listOrder1a.iterator(), callback));
        System.out.println("\nOrder 2:  ");
        printResult(algorithm.execute(listOrder2a.iterator(), callback));
        System.out.println("\nOrder 3: ");
        printResult(algorithm.execute(listOrder3a.iterator(), callback));
    }

    public void testNameAndCity_Longest() {
        System.out.println("\n--------------- Longest (minConfidence = 0.4) -----------------------");
        MatchMergeAlgorithm algorithm = buildMFB(0.5F, 0.55, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 4: ");
        printResult(algorithm.execute(listOrder4.iterator(), callback));
        System.out.println("\nOrder 5:  ");
        printResult(algorithm.execute(listOrder5.iterator(), callback));
    }

    public void testABCDE_Longest_LowMinConfidence() {
        System.out.println("\n--------------- Longest (minConfidence = 0.1) -----------------------");
        MatchMergeAlgorithm algorithm = buildMFB(0.1F, 0.1, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 1:  ");
        printResult(algorithm.execute(listOrder1.iterator(), callback));
        System.out.println("\nOrder 2: ");
        printResult(algorithm.execute(listOrder2.iterator(), callback));
        System.out.println("\nOrder 3: ");
        printResult(algorithm.execute(listOrder3.iterator(), callback));
    }

    public void testABCDE_Concat() {
        System.out.println("\n--------------- Concat  -----------------------");
        MatchMergeAlgorithm algorithm = buildMFB(0.6F, 0.4, SurvivorShipAlgorithmEnum.CONCATENATE);
        System.out.println("Order 1:  ");
        printResult(algorithm.execute(listOrder1.iterator(), callback));
        System.out.println("\nOrder 2:  ");
        printResult(algorithm.execute(listOrder2.iterator(), callback));
        System.out.println("\nOrder 3:  ");
        printResult(algorithm.execute(listOrder3.iterator(), callback));
    }

    private void printResult(List<Record> mergedRecords) {
        for (Record rec : mergedRecords) {
            List<String> attrList = rec.getAttributes().stream().map(attr -> attr.getValue()).collect(Collectors.toList());
            System.out.println("  " + rec + " " + attrList + "   Confidence: " + rec.getConfidence());
        }
    }
}