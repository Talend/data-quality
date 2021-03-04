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
package org.talend.dataquality.record.linkage.attribute;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * @author jteuladedenantes
 * 
 * Hamming matcher. The Hamming distance is the minimum number of substitutions required to change one string into the
 * other.
 */
public class HammingMatcher extends AbstractAttributeMatcher {

    private static final long serialVersionUID = 9059579472242450392L;

    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.HAMMING;
    }

    @Override
    public double getWeight(String record1, String record2) {
        // Strings must have the same length
        if (record1.length() != record2.length()) {
            return 0;
        }

        int hammingDistance = 0;
        for (int i = 0; i < record1.length(); i++) {
            // we increase the distance only if the characters are identical
            if (record1.charAt(i) != record2.charAt(i)) {
                hammingDistance++;
            }
        }

        return 1.0 - ((double) hammingDistance / record1.length());
    }

}
