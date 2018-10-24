package org.talend.dataquality.datamasking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO : Delete this class when the utils package will be incorporated
public class ssnUtils {

    private static final List<String> departments = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "2A", "2B", "21", "22", "23", "24", "25", "26", "27", "28",
            "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
            "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66",
            "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85",
            "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99");

    private static final int MOD97 = 97; // $NON-NLS-1$

    public static List<String> getFrenchDepartments() {
        return departments;
    }

    public static int getNumberOfFrenchDepartments() {
        return departments.size();
    }

    public static String computeFrenchKey(String string) {
        StringBuilder keyResult = new StringBuilder(string);
        if (keyResult.charAt(5) == '2') {
            keyResult.setCharAt(5, '1');
            keyResult.setCharAt(6, (keyResult.charAt(6) == 'A') ? '9' : '8');
        }
        int controlKey = 97 - (int) (Long.valueOf(keyResult.toString()) % MOD97);
        StringBuilder res = new StringBuilder();
        if (controlKey < 10)
            res.append("0");
        return res.append(controlKey).toString();
    }

    // TODO : Add this method to true UtilsSsnFr class
    public static List<String> splitFields(String ssn) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(ssn.substring(0, 1));
        strs.add(ssn.substring(1, 3));
        strs.add(ssn.substring(3, 5));
        strs.add(ssn.substring(5, 7));
        strs.add(ssn.substring(7, 10));
        strs.add(ssn.substring(10, 13));

        return strs;
    }
}
