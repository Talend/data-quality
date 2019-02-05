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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.pattern.TextPatternUtil;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.generic.Alphabet;
import org.talend.dataquality.datamasking.generic.GenerateFromAlphabet;

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
     * the number we want to keep. By default, it's 0 and we won't use it
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

    private int counter;

    private GenerateFromAlphabet ff1Cipher;

    private Alphabet alphabet;

    @Override
    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    @Override
    public void setSecret(FormatPreservingMethod method, String password) {
        if (alphabet == null) {
            throw new IllegalArgumentException(
                    "The method setAlphabet should be called before the method setSecret for the function "
                            + this.getClass().getName());
        }
        ff1Cipher = new GenerateFromAlphabet(alphabet, method, password);
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
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
        return doGenerateMaskedField(t, FunctionMode.RANDOM);
    }

    @Override
    protected T doGenerateMaskedField(T t, FunctionMode mode) {
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
            String replacedString;
            switch (mode) {
            case CONSISTENT:
                replacedString = generateConsistentString(str, beginAux, endAux);
                break;
            case BIJECTIVE:
                replacedString = generateBijectiveString(str, beginAux, endAux);
                break;
            default:
                replacedString = generateRandomString(str, beginAux, endAux);
                break;
            }
            if (replacedString == null) {
                return null;
            }

            sb.append(replacedString);
        }
        sb.append(str.substring(str.offsetByCodePoints(0, endAux)));
        return getResult(sb);
    }

    private String generateConsistentString(String str, int beginAux, int endAux) {
        String toBeReplaced = findStringToReplace(str, beginAux, endAux);
        Random random = getRandomForObject(toBeReplaced);
        List<Integer> replacedCodePoints = TextPatternUtil.replaceStringCodePoints(toBeReplaced, random);
        String substring = str.substring(str.offsetByCodePoints(0, beginAux), str.offsetByCodePoints(0, endAux));

        return replaceConsistent(substring, replacedCodePoints);
    }

    private String generateBijectiveString(String str, int beginAux, int endAux) {
        List<Integer> codePoints = new ArrayList<>();
        for (int i = beginAux; i < endAux; i++) {
            codePoints.add(str.codePointAt(str.offsetByCodePoints(0, i)));
        }

        List<Integer> replacedCodePoints = ff1Cipher.generateUniqueCodePoints(codePoints);
        if (replacedCodePoints.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        replacedCodePoints.forEach(cp -> sb.append(Character.toChars(cp)));

        return sb.toString();
    }

    private String generateRandomString(String str, int beginAux, int endAux) {
        StringBuilder sb = new StringBuilder();
        for (int i = beginAux; i < endAux; i++) {
            Integer codePoint = str.codePointAt(str.offsetByCodePoints(0, i));
            sb.append(Character.toChars(replaceChar(codePoint)));
        }

        return sb.toString();
    }

    private String replaceConsistent(String substringToReplace, List<Integer> replacedString) {
        StringBuilder stringBuilder = new StringBuilder();
        long codePointCounts = substringToReplace.codePoints().count();
        counter = 0;
        for (int i = 0; i < codePointCounts; i++) {
            Integer codePoint = substringToReplace.codePointAt(substringToReplace.offsetByCodePoints(0, i));
            Integer nextReplaceCodePoint = counter < replacedString.size() ? replacedString.get(counter) : null;
            Integer codePointToReplace = replaceChar(codePoint, nextReplaceCodePoint);
            stringBuilder.append(Character.toChars(codePointToReplace));
        }
        return stringBuilder.toString();
    }

    private String findStringToReplace(String str, int beginAux, int endAux) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = beginAux; i < endAux; i++) {
            int codePoint = str.codePointAt(str.offsetByCodePoints(0, i));
            if ((!isNeedCheckSpecialCase() || isGoodType(codePoint)) && charToReplace == null)
                stringBuilder.append(Character.toChars(codePoint));
        }
        return stringBuilder.toString();
    }

    private Integer replaceChar(Integer codePoint) {
        return replaceChar(codePoint, null);
    }

    private Integer replaceChar(Integer codePoint, Integer replaceCodePoint) {
        if (isNeedCheckSpecialCase() && !isGoodType(codePoint)) {
            return codePoint;
        }
        if (charToReplace != null) {
            return (int) charToReplace;
        }
        Integer replace;
        if (replaceCodePoint == null) {
            replace = TextPatternUtil.replaceCharacter(codePoint, rnd);
        } else {
            replace = replaceCodePoint;
            counter++;
        }
        return replace;
    }

    private T getResult(StringBuilder sb) {
        if (sb.length() == 0) {
            return getDefaultOutput();
        }
        return getOutput(sb.toString());
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
     * @param codePoint the character c to ckeck
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
