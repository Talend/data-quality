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
package org.talend.dataquality.statistics.frequency.pattern;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.talend.dataquality.statistics.frequency.AbstractFrequencyStatistics;

/**
 * Frequency statistics bean which delegate the computation to evaluator.
 * 
 * @author jteuladedenantes
 *
 */
public class PatternFrequencyStatistics extends AbstractFrequencyStatistics {

    private Map<String, Set<Locale>> pattern2locales = new HashMap<>();

    public void add(String pattern, Locale locale) {
        super.add(pattern);
        Set<Locale> locales = pattern2locales.get(pattern);
        if (locales == null)
            locales = new HashSet<>();
        locales.add(locale);
        pattern2locales.put(pattern, locales);
    }

    public void add(Map.Entry<String, Locale> patternAndLocale) {
        this.add(patternAndLocale.getKey(), patternAndLocale.getValue());
    }

    public Set<Locale> getLocales(String pattern) {
        return pattern2locales.get(pattern);
    }

}
