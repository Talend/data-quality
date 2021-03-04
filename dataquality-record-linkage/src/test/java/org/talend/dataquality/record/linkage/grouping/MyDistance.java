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

package org.talend.dataquality.record.linkage.grouping;

import org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * @author scorreia
 * 
 * Example of Matching distance.
 */
public class MyDistance extends AbstractAttributeMatcher {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        // a custom implementation should return this type AttributeMatcherType.custom
        return AttributeMatcherType.CUSTOM;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String arg0, String arg1) {
        // Here goes the custom implementation of the matching distance between the two given strings.
        // the algorithm should return a value between 0 and 1.

        // in this example, we consider that 2 strings match if their first 4 characters are identical
        // the arguments are not null (the check for nullity is done by the caller)
        int MAX_CHAR = 4;
        final int max = Math.min(MAX_CHAR, Math.min(arg0.length(), arg1.length()));
        int nbIdenticalChar = 0;
        for (; nbIdenticalChar < max; nbIdenticalChar++) {
            if (arg0.charAt(nbIdenticalChar) != arg1.charAt(nbIdenticalChar)) {
                break;
            }
        }
        if (arg0.length() < MAX_CHAR && arg1.length() < MAX_CHAR) {
            MAX_CHAR = Math.max(arg0.length(), arg1.length());
        }
        return (nbIdenticalChar) / ((double) MAX_CHAR);
    }

    // This method is only for testing the class. It's not required when developping a new distance.
    // Delete it if you reuse this code to build your own library.
    // public static void main(String[] args) {
    // MyDistance dist = new MyDistance();
    // String[] strings = { "testlong", "testlon", "bad", "testlong", "test", "te", "te", "mad" };
    // for (String a : strings) {
    // for (String b : strings) {
    // System.out.println("Dist(" + a + "," + b + ")= " + dist.getWeight(a, b));
    // }
    // }
    //
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getThreshold()
     */
    @Override
    public float getThreshold() {
        return 0.88f;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight()
     */
    @Override
    public double getWeight() {
        return 1.0;
    }

}
