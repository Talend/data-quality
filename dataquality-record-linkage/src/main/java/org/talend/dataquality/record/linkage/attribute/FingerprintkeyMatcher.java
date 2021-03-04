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

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.windowkey.FingerprintKeyer;

/**
 * created by zhao on 2015-6-2
 *
 */
public class FingerprintkeyMatcher extends AbstractAttributeMatcher {

    private static final long serialVersionUID = 1742388611163764334L;

    FingerprintKeyer keyer = new FingerprintKeyer();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.FINGERPRINTKEY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    protected double getWeight(String value1, String value2) {
        if (StringUtils.equalsIgnoreCase(keyer.key(value1), keyer.key(value2))) {
            return 1;
        }
        return 0;
    }

}
