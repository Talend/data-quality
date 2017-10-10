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
package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author dprot
 * 
 * Indian pattern: abbbbbbbbbbc a: 1 -> 9 b: 0 -> 9 c: checksum with Verhoeff' algorithm
 */
public class GenerateUniqueSsnIndia extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 4514471121590047091L;

    // The multiplication table (for checksum)
    private static final int[][] D = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
            { 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 }, { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 }, { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
            { 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 }, { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 }, { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
            { 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 }, { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };

    // The permutation table (for checksum)
    private static final int[][] P = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
            { 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 }, { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 }, { 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
            { 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 }, { 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 }, { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };

    // The inverse table (for checksum)
    private static final int[] INV = { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 1));
        strs.add(str.substring(1, 11));

        StringBuilder result = ssnPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }

        // add the security key specified for Indian SSN
        String controlKey = computeIndianKey(result.toString());
        result.append(controlKey);

        return result;
    }

    /**
     * 
     * @return the list of each field
     */
    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();

        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(9)));
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(9999999999L)));

        checkSumSize = 1;
        return fields;
    }

    /**
     * 
     * Compute the key for an Indian SSN
     * 
     * @param string
     * @return
     */
    private String computeIndianKey(String string) {

        int c = 0;
        int[] myArray = stringToReversedIntArray(string);

        for (int i = 0; i < myArray.length; i++) {
            c = D[c][P[(i + 1) % 8][myArray[i]]];
        }

        return Integer.toString(INV[c]);
    }

    /*
     * Converts a string to a reversed integer array.
     */
    private static int[] stringToReversedIntArray(String num) {

        int[] myArray = new int[num.length()];

        for (int i = 0; i < num.length(); i++) {
            myArray[i] = Integer.parseInt(num.substring(i, i + 1));
        }

        myArray = reverse(myArray);
        return myArray;
    }

    /*
     * Reverses an int array
     */
    private static int[] reverse(int[] myArray) {
        int[] reversed = new int[myArray.length];

        for (int i = 0; i < myArray.length; i++) {
            reversed[i] = myArray[myArray.length - (i + 1)];
        }

        return reversed;
    }
}
