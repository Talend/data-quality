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
package org.talend.dataquality.record.linkage.record;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.record.linkage.Messages;
import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;

/**
 * @author scorreia
 * 
 * Factory to create the record matchers.
 */
public final class RecordMatcherFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RecordMatcherFactory.class);

    private static List<String> labels = new ArrayList<String>();

    /**
     * private RecordMatcherFactory constructor.
     */
    private RecordMatcherFactory() {
    }

    public static IRecordMatcher createMatcher(String matcherLabel) {
        for (RecordMatcherType type : RecordMatcherType.values()) {
            if (type.getLabel().equalsIgnoreCase(matcherLabel)) {
                return createMatcher(type);
            }
        }
        LOG.warn("matcher not found: [matcherLabel=" + matcherLabel + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    /**
     * Method "createMatcher".
     * 
     * @param name the name of the matcher
     * @return a matcher given the name or null
     */
    public static IRecordMatcher createMatcher(RecordMatcherType type) {
        if (type != null) {

            switch (type) {
            case simpleVSRMatcher:
                return new SimpleVSRRecordMatcher();
            default:
                break;
            }
        }
        // else
        return null;
    }

    public static CombinedRecordMatcher createCombinedRecordMatcher() {
        return new CombinedRecordMatcher();
    }

    /**
     * Method "createMatcher" creates a matcher of the given type with the given attribute matchers and attribute
     * weights
     * 
     * @param type the type of the matcher
     * @param attributeMatcherAlgorithms the attribute matcher algorithm names
     * @param attributeWeights the attribute weights (the size of this array must be the same as the number of attribute
     * matcher algorithms)
     * @return the new IRecordMatcher or null.
     */
    public static IRecordMatcher createMatcher(RecordMatcherType type, String[] attributeMatcherAlgorithms,
            double[] attributeWeights) {
        IRecordMatcher recMatcher = RecordMatcherFactory.createMatcher(type);
        if (recMatcher == null) {
            return null;
        }

        int nbMatchKey = attributeMatcherAlgorithms.length;

        // initialize matcher
        recMatcher.setRecordSize(nbMatchKey);
        // create attribute matchers for each of the join key
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[nbMatchKey];
        for (int i = 0; i < attributeMatchers.length; i++) {
            attributeMatchers[i] = AttributeMatcherFactory.createMatcher(attributeMatcherAlgorithms[i]);
        }
        recMatcher.setAttributeMatchers(attributeMatchers);

        // set the weights chosen by the user
        if (!recMatcher.setAttributeWeights(attributeWeights)) {
            LOG.warn(Messages.getString("RecordMatcherFactory.0", type.getLabel())); //$NON-NLS-1$
            return null; // DO NOT CREATE AN INVALID MATCHER
        }
        return recMatcher;
    }

    /**
     * Method "getMatcherLabels".
     * 
     * @return the labels of all matchers
     */
    public static List<String> getMatcherLabels() {
        if (!labels.isEmpty()) {
            return labels;
        }
        // else fill in the labels
        for (RecordMatcherType type : RecordMatcherType.values()) {
            labels.add(type.getLabel());
        }
        return labels;
    }
}
