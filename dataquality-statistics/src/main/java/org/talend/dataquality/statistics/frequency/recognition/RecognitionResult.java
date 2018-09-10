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
package org.talend.dataquality.statistics.frequency.recognition;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Recognition result bean. <b>Important</b> note that this result's instance is intended to be reused due to the memory
 * considerations when analyzing large dataset. So the you must not expect something like :<br>
 * List<RecognitionResult> results = recognize(List<String> stringToRecognize);<br>
 * But please use it to get the information of one result immediately per iteration of recognizer.
 * 
 * @author mzhao
 * @since 1.3.0
 */
public class RecognitionResult {

    private Map<String, Locale> patternToLocale = new HashMap<>();

    private boolean isComplete = false;

    public Set<String> getPatternStringSet() {
        return patternToLocale.keySet();
    }

    public Map<String, Locale> getPatternToLocale() {
        return patternToLocale;
    }

    /**
     * Whether the recognition is complete and should stop in a composite recognizer.
     * 
     * @return true if the recognition pattern match with the Pattern Recognizer used.
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Set the result with the replaced pattern string and the indicator of whether the pattern replacement is complete
     * or not.
     * 
     * @param patternString
     * @param isComplete
     */
    protected void setResult(Set<String> patternString, boolean isComplete) {
        patternToLocale = new HashMap<>();
        patternString.forEach(p -> patternToLocale.put(p, null));
        this.isComplete = isComplete;
    }

    /**
     * Set the result with the replaced pattern string and the locale associated and the indicator of whether the pattern replacement is complete
     * or not.
     *
     * @param patternToLocale
     * @param isComplete
     */
    protected void setResult(Map<String, Locale> patternToLocale, boolean isComplete) {
        this.patternToLocale = patternToLocale;
        this.isComplete = isComplete;
    }
}
