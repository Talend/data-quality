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

import org.apache.commons.codec.language.Soundex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SoundexMatcher extends AbstractAttributeMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(SoundexMatcher.class);

    private static final long serialVersionUID = 5208397253497469289L;

    private static final Soundex soundex = new Soundex();

    /**
     * soundex returns 4 when strings are similar.
     */
    private static final double MAX = 4.0;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String str1, String str2) {
        try {
            int diff = soundex.difference(str1, str2);
            return diff / MAX;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.SOUNDEX;
    }

}
