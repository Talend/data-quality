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
package org.talend.dataquality.record.linkage.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * created by zshen on Nov 14, 2013 Detailled comment
 * 
 */
public class CustomAttributeMatcherClassNameConvert {

    public static final String REGEXKEY = "\\|\\|"; //$NON-NLS-1$

    public static final char QUOTE = '\"';

    private static final Logger LOG = Logger.getLogger(CustomAttributeMatcherClassNameConvert.class);

    /**
     * DOC zshen Comment method "getClassName".
     * 
     * @param classPathParameter like "CustomMatcherTest.jar||MycustomMatch.jar||testCustomMatcher.myCustomMatcher" last
     * one is fully qualified name("testCustomMatcher" is package name and "myCustomMatcher" is class name).
     * @return the class name as the demo will return testCustomMatcher.myCustomMatcher
     */
    public static String getClassName(String classPathParameter) {
        String[] allElements = classPathParameter.split(REGEXKEY);
        if (allElements.length > 0) {
            return allElements[allElements.length - 1];
        } else {
            return classPathParameter;
        }
    }

    /**
     * DOC zshen Comment method "getClassName".
     *
     * @param classPathParameter
     * @return
     */
    public static String getClassNameAndAddQuot(String classPathParameter) {
        String className = getClassName(classPathParameter);
        return addQuotationMarks(className);
    }

    /**
     * DOC zshen Comment method "addQuotationMarks".
     *
     * @param className
     */
    private static String addQuotationMarks(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        String result = className;
        if (QUOTE != className.charAt(0)) {
            result = QUOTE + className;
        }
        if (QUOTE != className.charAt(className.length() - 1)) {
            result += QUOTE;
        }
        return result;
    }

    /**
     * A helper method to convert the concatenated paths to URL arrays.
     * 
     * @param concatenatedPaths the absolute path delimited by {@link CustomAttributeMatcherClassNameConvert#REGEXKEY}
     * @return the URL array of each jar.
     */
    public static URL[] changeJarPathToURLArray(String concatenatedPaths) {
        String[] allElements = concatenatedPaths.split(CustomAttributeMatcherClassNameConvert.REGEXKEY);
        List<URL> jarURLs = new ArrayList<URL>();
        for (String allElement : allElements) {
            try {
                jarURLs.add((new File(allElement)).toURI().toURL());
            } catch (MalformedURLException e) {
                LOG.error(e, e);
            }
        }
        return jarURLs.toArray(new URL[jarURLs.size()]);
    }

}
