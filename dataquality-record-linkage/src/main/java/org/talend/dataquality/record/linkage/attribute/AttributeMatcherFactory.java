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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * @author scorreia
 * 
 * Factory that helps to create attribute matchers from their label or type.
 */
public final class AttributeMatcherFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AttributeMatcherFactory.class);

    private static List<String> labels = new ArrayList<String>();

    /**
     * private constructor.
     */
    private AttributeMatcherFactory() {
    }

    /**
     * Method "createMatcher".
     * 
     * @param matcherLabel the type of attribute matcher
     * @return the attribute matcher or null if the input type is null
     */
    public static IAttributeMatcher createMatcher(String matcherLabel) {
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type.name().equalsIgnoreCase(matcherLabel)) {
                return createMatcher(type);
            }
        }
        LOG.warn("matcher not found: [matcherLabel=" + matcherLabel + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    /**
     * Method "createMatcher". If the type is {@link AttributeMatcherType#CUSTOM}, then the resulting IAttributeMatcher
     * class is instantiated given the className argument. If the type is different, then the
     * {@link AttributeMatcherFactory#createMatcher(AttributeMatcherType)} method is called.
     * 
     * @param type the type of the attribute matcher
     * @param className the class name that implements IAttributeMatcher
     * @param classLoader the class loader used to load customized class.
     * @return the instantiated Attribute Matcher class
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static IAttributeMatcher createMatcher(AttributeMatcherType type, String className, ClassLoader classLoader)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (type != null) {
            switch (type) {
            case CUSTOM:
                return (IAttributeMatcher) Class.forName(className, true, classLoader).newInstance();
            default:
                return createMatcher(type);
            }
        }
        LOG.warn("matcher not found: [type=" + type + "][className=" + className + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return null;
    }

    /**
     * Method "createMatcher". If the type is {@link AttributeMatcherType#CUSTOM}, then the resulting IAttributeMatcher
     * class is instantiated given the className argument. If the type is different, then the
     * {@link AttributeMatcherFactory#createMatcher(AttributeMatcherType)} method is called.
     * 
     * @param type the type of the attribute matcher
     * @param className the class name that implements IAttributeMatcher
     * @return the instantiated Attribute Matcher class
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static IAttributeMatcher createMatcher(AttributeMatcherType type, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return createMatcher(type, className, AttributeMatcherFactory.class.getClassLoader());
    }

    /**
     * Method "createMatcher".
     * 
     * @param type the type of attribute matcher
     * @return the attribute matcher or null if the input type is null
     */
    public static IAttributeMatcher createMatcher(AttributeMatcherType type) {
        if (type != null) {
            switch (type) {
            case DUMMY:
                return new DummyMatcher();
            case EXACT:
                return new ExactMatcher();
            case LEVENSHTEIN:
                return new LevenshteinMatcher();
            case SOUNDEX:
                return new SoundexMatcher();
            case DOUBLE_METAPHONE:
                return new DoubleMetaphoneMatcher();
            case EXACT_IGNORE_CASE:
                return new ExactIgnoreCaseMatcher();
            case METAPHONE:
                return new MetaphoneMatcher();
            case JARO:
                return new JaroMatcher();
            case JARO_WINKLER:
                return new JaroWinklerMatcher();
            case SOUNDEX_FR:
                return new SoundexFRMatcher();
            case Q_GRAMS:
                return new QGramsMatcher();
            case HAMMING:
                return new HammingMatcher();
            case FINGERPRINTKEY:
                return new FingerprintkeyMatcher();
            case LCS:
                return new LCSMatcher();
            default:
                break;
            }
        }
        // else
        LOG.warn("matcher not found: [type=" + type + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    public static List<String> getMatcherLabels() {
        if (!labels.isEmpty()) {
            return labels;
        }
        // else fill in the labels
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            labels.add(type.getLabel());
        }
        return labels;
    }
}
