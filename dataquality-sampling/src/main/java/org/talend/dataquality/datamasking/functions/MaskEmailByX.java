package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;

public abstract class MaskEmailByX extends MaskEmail {

    private static final long serialVersionUID = 6433172298783284738L;

    /**
     * DOC qzhao Comment method "getPointPostions".<br>
     * Gets the points' postions in the email domain
     * 
     * @param address the original email address
     * @param count @'s position
     * @return a list of integer
     */
    protected ArrayList<Integer> getPointPostions(String address, int count) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int c = address.indexOf('.', count);
        while (c > 0) {
            list.add(c++);
            c = address.indexOf('.', c);
        }
        return list;
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
        if (parameters == null || parameters.length != 1 || parameters[0].length() != 1
                || !Character.isLetter(parameters[0].charAt(0)))
            parameters = new String[] { "X" };
    }

    protected Character getMaskingCharacter() {
        return parameters[0].charAt(0);
    }

    @Override
    protected String maskInvalidEmail(String address) {
        StringBuilder sb = new StringBuilder(address);
        Character maskingCrct = getMaskingCharacter();
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, maskingCrct);
        }
        return sb.toString();
    }
}
