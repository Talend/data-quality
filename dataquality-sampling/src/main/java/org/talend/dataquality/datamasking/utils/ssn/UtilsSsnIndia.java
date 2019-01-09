package org.talend.dataquality.datamasking.utils.ssn;

public class UtilsSsnIndia {

    private UtilsSsnIndia() {

    }

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

    /**
     * Compute the key for an Indian SSN
     */
    public static final String computeIndianKey(StringBuilder str) {
        String reversed = new StringBuilder(str).reverse().toString();
        int length = reversed.length();
        int[] ssnNumbers = new int[length];

        for (int i = 0; i < length; i++) {
            ssnNumbers[i] = Character.getNumericValue(reversed.charAt(i));
        }

        int c = 0;
        for (int i = 0; i < ssnNumbers.length; i++) {
            c = D[c][P[(i + 1) % 8][ssnNumbers[i]]];
        }
        return Integer.toString(INV[c]);
    }
}
