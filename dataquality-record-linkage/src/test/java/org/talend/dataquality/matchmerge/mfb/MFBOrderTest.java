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

public class MFBOrderTest extends TestCase {

    // private final Callback callback = new LoggerCallback();
    private final Callback callback = DefaultCallback.INSTANCE;

    private final List<Record> listOrder1 = new ArrayList<Record>() {

        {
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A0", 0, "OOOOO") }), "R0", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDE") }), "R1", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "TALEND") }), "R2", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "TALENT") }), "R3", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A4", 0, "ABCDF") }), "R4", 999L, "MFB"));
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A5", 0, "IIIII") }), "R5", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A6", 0, "ALEND") }), "R6", 999L, "MFB"));
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A7", 0, "ZZZZZ") }), "R7", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder2 = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "TALEND") }), "R2", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "TALENT") }), "R3", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A4", 0, "ABCDF") }), "R4", 999L, "MFB"));
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A5", 0, "IIIII") }), "R5", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A6", 0, "ALEND") }), "R6", 999L, "MFB"));
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A7", 0, "ZZZZZ") }), "R7", 999L, "MFB"));
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A0", 0, "OOOOO") }), "R0", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDE") }), "R1", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder3 = new ArrayList<Record>() {

        {
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A5", 0, "IIIII") }), "R5", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A6", 0, "ALEND") }), "R6", 999L, "MFB"));
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A7", 0, "ZZZZZ") }), "R7", 999L, "MFB"));
            // add(new Record(Arrays.asList(new Attribute[] { new Attribute("A0", 0, "OOOOO") }), "R0", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDE") }), "R1", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "TALEND") }), "R2", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "TALENT") }), "R3", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A4", 0, "ABCDF") }), "R4", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder4 = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDEFGHIJ") }), "R1", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "ABCDEFGHIX") }), "R2", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "ABCDEFGHXX") }), "R3", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A4", 0, "ABCDEFGXXX") }), "R4", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A5", 0, "ABCDEFXXXX") }), "R5", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A6", 0, "ABCDEXXXXX") }), "R6", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A7", 0, "ABCDXXXXXX") }), "R7", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A8", 0, "ABCXXXXXXX") }), "R8", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A9", 0, "ABXXXXXXXX") }), "R9", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A10", 0, "AXXXXXXXX") }), "R10", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A11", 0, "XXXXXXXXX") }), "R11", 999L, "MFB"));
        }
    };

    private final List<Record> listOrder5 = new ArrayList<Record>() {

        {
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A1", 0, "ABCDEFGHIJ") }), "R1", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A11", 0, "XXXXXXXXX") }), "R11", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A4", 0, "ABCDEFGXXX") }), "R4", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A7", 0, "ABCDXXXXXX") }), "R7", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A10", 0, "AXXXXXXXX") }), "R10", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A2", 0, "ABCDEFGHIX") }), "R2", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A6", 0, "ABCDEXXXXX") }), "R6", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A3", 0, "ABCDEFGHXX") }), "R3", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A5", 0, "ABCDEFXXXX") }), "R5", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A8", 0, "ABCXXXXXXX") }), "R8", 999L, "MFB"));
            add(new Record(Arrays.asList(new Attribute[] { new Attribute("A9", 0, "ABXXXXXXXX") }), "R9", 999L, "MFB"));
        }
    };

    private MatchMergeAlgorithm
            buildMFB(float attrThreshold, double minConfidence, SurvivorShipAlgorithmEnum mergeAlgo) {
        return MFB.build( //
                new AttributeMatcherType[] { AttributeMatcherType.LEVENSHTEIN }, // algorithms
                new String[] { "" }, // algo params
                new float[] { attrThreshold }, // thresholds
                minConfidence, // min confidence
                new SurvivorShipAlgorithmEnum[] { mergeAlgo }, // merge algos
                new String[] { "" }, // merge params
                new double[] { 1 }, // weights
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll }, // null optino
                new SubString[] { SubString.NO_SUBSTRING }, // substring option
                "MFB" // source
        );
    }

    public void testABCDE_Longest() {
        System.out.println("\n--------------- Longest (minConfidence = 0.4) -----------------------");
        MatchMergeAlgorithm algorithm = buildMFB(0.7F, 0.4, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 1: ");
        printResult(algorithm.execute(listOrder1.iterator(), callback));
        System.out.println("\nOrder 2: ");
        printResult(algorithm.execute(listOrder2.iterator(), callback));
        System.out.println("\nOrder 3: ");
        printResult(algorithm.execute(listOrder3.iterator(), callback));
    }

    public void testABCDEFGHIJ_Longest() {
        System.out.println("\n--------------- Longest (minConfidence = 0.4) -----------------------");
        MatchMergeAlgorithm algorithm = buildMFB(0.8F, 0.85, SurvivorShipAlgorithmEnum.LONGEST);
        System.out.println("Order 4: ");
        printResult(algorithm.execute(listOrder4.iterator(), callback));
        System.out.println("Order 5: ");
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
        MatchMergeAlgorithm algorithm = buildMFB(0.7F, 0.7, SurvivorShipAlgorithmEnum.CONCATENATE);
        System.out.println("Order 1:  ");
        printResult(algorithm.execute(listOrder1.iterator(), callback));
        System.out.println("\nOrder 2:  ");
        printResult(algorithm.execute(listOrder2.iterator(), callback));
        System.out.println("\nOrder 3:  ");
        printResult(algorithm.execute(listOrder3.iterator(), callback));
    }

    private void printResult(List<Record> mergedRecords) {
        for (Record rec : mergedRecords) {
            List<String> attrList =
                    rec.getAttributes().stream().map(attr -> attr.getValue()).collect(Collectors.toList());
            System.out.println("  " + rec + " " + attrList + "   Confidence: " + rec.getConfidence());
        }
    }
}
