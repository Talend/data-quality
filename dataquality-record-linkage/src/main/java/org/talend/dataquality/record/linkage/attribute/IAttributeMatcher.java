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
package org.talend.dataquality.record.linkage.attribute;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public interface IAttributeMatcher {

    /**
     * The different ways to handle a comparison with null values.
     */
    public static enum NullOption {
        nullMatchNull, // null = null
        nullMatchAll, // null = any string
        nullMatchNone // null != null
    }

    /**
     * Note: this is not a mathematical probability (the sum won't yield 1)
     * 
     * @param str1 a first string (must not be null)
     * @param str2 a second string (must not be null)
     * @return the probability that the two strings match (should return a value between 0 and 1).
     */
    double getMatchingWeight(String str1, String str2);

    /**
     * Method "getMatchType".
     * 
     * @return the matching type.
     */
    AttributeMatcherType getMatchType();

    // TODO handle other types of data

    /**
     * Method "setNullOption" sets the option about the comparison of null values.
     * 
     * @param option the option
     */
    void setNullOption(NullOption option);

    /**
     * Method "setNullOption" sets the option about the comparison of null values.
     * 
     * @param option the option
     */
    void setNullOption(String option);

    /**
     * @return Return the option about the comparison of null values.
     */
    NullOption getNullOption();

    /**
     * Method "getAttributeName" returns the attribute name.
     * 
     * @return the attribute name or null when not set.
     */
    String getAttributeName();

    /**
     * Method "setAttributeName" sets the attribute name.
     * 
     * @param name the attribute name
     */
    void setAttributeName(String name);

    /**
     * the value will come from the studio UI. no need to set this value.
     * 
     * @return The minimum score to consider a match between 2 string values.
     */
    float getThreshold();

    /**
     * the value will come from the studio UI. no need to set this value.
     * 
     * @return The weight of the attribute matcher (high value means the attribute matcher matches will have more
     * importance in the final score of a 2-record match).
     */
    double getWeight();

}
