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
package org.talend.dataquality.semantic.broadcast;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;

/**
 * A serializable object to hold REGEX classifiers.
 * In the future, it is also expected to hold custom validator resources uploaded by users.
 */
public class BroadcastRegexObject implements Serializable {

    private static final long serialVersionUID = -7692833458780761739L;

    private static final Logger LOGGER = Logger.getLogger(BroadcastRegexObject.class);

    private UserDefinedClassifier regexClassifier;

    /**
     * @param regexClassifier
     */
    public BroadcastRegexObject(UserDefinedClassifier regexClassifier) {
        this.regexClassifier = regexClassifier;
    }

    /**
     * @param udc
     * @param categories
     */
    public BroadcastRegexObject(UserDefinedClassifier udc, Set<String> categories) {
        this.regexClassifier = new UserDefinedClassifier();
        for (ISubCategory c : udc.getClassifiers()) {
            if (categories.contains(c.getId())) {
                this.regexClassifier.addSubCategory(c);
            }
        }
    }

    /**
     * @param regexPath
     */
    public BroadcastRegexObject(URI regexPath) {
        try {
            this.regexClassifier = UDCategorySerDeser.readJsonFile(regexPath);
        } catch (IOException e) {
            LOGGER.error("Failed to load resource from " + regexPath, e);
        }
    }

    public UserDefinedClassifier get() {
        return regexClassifier;
    }

}
