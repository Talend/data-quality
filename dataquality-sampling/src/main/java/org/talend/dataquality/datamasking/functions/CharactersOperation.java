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

import java.util.Random;

import org.talend.dataquality.common.pattern.TextPatternUtil;

/**
 * @author jteuladedenantes
 * 
 * This class groups all characters operations as removing, replacing specific characters.
 */
public abstract class CharactersOperation<T> extends Function<T> {

    private static final long serialVersionUID = -1326050500008572996L;

    /**
     * the index from which we replace
     */
    protected int beginIndex = 0;

    /**
     * the last index we stop to replace
     */
    protected int endIndex = Integer.MAX_VALUE;

    /**
     * the number we want to replace. By default, it's Integer.MAX_VALUE and we won't use it
     */
    protected int endNumberToReplace = Integer.MAX_VALUE;

    /**
     * the last number we want to keep. By default, it's Integer.MAX_VALUE and we won't use it
     */
    protected int endNumberToKeep = 0;

    /**
     * if charToReplace is null, we randomly find a new character according to the current character type
     */
    protected Character charToReplace = null;

    /**
     * we can remove characters instead of replace characters
     */
    protected boolean toRemove = false;

    protected boolean isValidParameters = false;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        isValidParameters = validParameters();
        if (isValidParameters) {
            this.initAttributes();
            isValidParameters = beginIndex >= 0 && beginIndex <= endIndex;
        }
        if (!isValidParameters) {
            throw new IllegalArgumentException("The parameters are not valid");
        }
    }

    @Override
    protected T doGenerateMaskedField(T t) {
        if (!isValidParameters || t == null) {
            return getDefaultOutput();
        }
        String str = t.toString();
        StringBuilder sb = new StringBuilder();

        int strCPCount = str.codePointCount(0, str.length());
        int beginAux = Math.min(Math.max(beginIndex, strCPCount - endNumberToReplace), strCPCount);
        int endAux = Math.max(Math.min(endIndex, strCPCount - endNumberToKeep), 0);
        sb.append(str, 0, str.offsetByCodePoints(0, beginAux));
        if (!toRemove) {
            for (int i = beginAux; i < endAux; i++) {
                Integer codePoint = str.codePointAt(str.offsetByCodePoints(0, i));
                sb.append(Character.toChars(replaceChar(codePoint)));
            }
        }
        sb.append(str.substring(str.offsetByCodePoints(0, endAux)));
        if (sb.length() == 0) {
            return getDefaultOutput();
        }
        return getOutput(sb.toString());
    }

    private Integer replaceChar(Integer codePoint) {
        if (isNeedCheckSpecialCase() && !isGoodType(codePoint)) {
            return codePoint;
        }
        if (charToReplace != null) {
            return (int) charToReplace;
        }
        return TextPatternUtil.replaceCharacter(codePoint, rnd);
    }

    /**
     * Judge whether need to check first.
     * 
     * @return
     */
    protected boolean isNeedCheckSpecialCase() {
        return false;
    }

    /**
     * This method allows to replace only some specific types
     * 
     * @param the character c to ckeck
     * @return true if c type is ok
     */
    protected boolean isGoodType(Integer codePoint) {
        return codePoint != null;
    }

    /**
     * @param string to parse in T-type
     * @return the string in the T-type
     */
    protected abstract T getOutput(String string);

    /**
     * Initialization of the attributes (the indexes, char to replace, etc.)
     */
    protected abstract void initAttributes();

    /**
     * @return the default output in the T-type
     */
    protected abstract T getDefaultOutput();

    /**
     * @return true is the parameters array attribute is valid
     */
    protected abstract boolean validParameters();
}
