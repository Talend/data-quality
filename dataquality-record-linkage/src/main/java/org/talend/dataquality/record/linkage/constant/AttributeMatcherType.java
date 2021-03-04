// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.constant;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.record.linkage.Messages;

/**
 * @author scorreia
 * 
 * Enumeration of all available attribute matcher algorithms.
 */
public enum AttributeMatcherType {
    EXACT("Exact"), //$NON-NLS-1$
    EXACT_IGNORE_CASE("Exact - ignore case"), //$NON-NLS-1$
    SOUNDEX("Soundex"), //$NON-NLS-1$
    SOUNDEX_FR("Soundex FR"), //$NON-NLS-1$
    LEVENSHTEIN("Levenshtein"), //$NON-NLS-1$
    METAPHONE("Metaphone"), //$NON-NLS-1$
    DOUBLE_METAPHONE("Double Metaphone"), //$NON-NLS-1$
    JARO("Jaro"), //$NON-NLS-1$
    FINGERPRINTKEY("Fingerprintkey"), //$NON-NLS-1$
    JARO_WINKLER("Jaro-Winkler"), //$NON-NLS-1$
    Q_GRAMS("q-grams"), //$NON-NLS-1$
    HAMMING("Hamming"), //$NON-NLS-1$
    CUSTOM("custom"), //$NON-NLS-1$
    // LCS should behind the custom until we support it to fix TDQ-12895
    LCS("Longest Common Substring"), //$NON-NLS-1$
    // Dummy should be last one because of we filter it by the code else we can not use ordinal to location custom type
    DUMMY("Dummy"); //$NON-NLS-1$

    private final String componentValue;

    private static final String CLASSNAME_PREFIX = "AttributeMatcherType."; //$NON-NLS-1$

    AttributeMatcherType(String componentValue) {
        this.componentValue = componentValue;
    }

    /**
     * Getter for componentValue.
     * 
     * @return the componentValue
     */
    public String getComponentValue() {
        return this.componentValue;
    }

    /**
     * Getter for label.
     * 
     * @return the internationalized label
     */
    public String getLabel() {
        return Messages.getString(CLASSNAME_PREFIX + this.name());
    }

    /**
     * @return all Attribute Matcher Types except the internal DUMMY algorithm.
     */
    public static String[] getAllTypes() {
        List<String> types = new ArrayList<String>();
        for (int i = 0; i < AttributeMatcherType.values().length; i++) {
            // LCS is removed from all types until TDQ-12704 is done
            if (i != DUMMY.ordinal() && i != LCS.ordinal()) {
                types.add(AttributeMatcherType.values()[i].getLabel());
            }
        }
        return types.toArray(new String[types.size() - 1]);
    }

    /**
     * Get AttributeMatcherType by component value, keep the short method name for components
     * 
     * @param componentValue
     * @return the AttributeMatcherType
     */
    public static AttributeMatcherType get(String componentValue) {
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type.name().equalsIgnoreCase(componentValue)
                    || type.getComponentValue().equalsIgnoreCase(componentValue)) {
                return type;
            }
        }
        return null;
    }
}
