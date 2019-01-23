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

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRE2JRegexValidator;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRegexValidator;

/**
 * A serializable object to hold REGEX classifiers.
 * In the future, it is also expected to hold custom validator resources uploaded by users.
 */
public class BroadcastRegexObject implements Serializable {

    private static final long serialVersionUID = -7692833458780761739L;

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastRegexObject.class);

    private UserDefinedClassifier regexClassifier;

    // No argument constructor needed for Jackson Deserialization
    public BroadcastRegexObject() {
    }

    public BroadcastRegexObject(UserDefinedClassifier regexClassifier) {
        this.regexClassifier = regexClassifier;
    }

    public BroadcastRegexObject(UserDefinedClassifier udc, Set<String> categories) {
        this.regexClassifier = new UserDefinedClassifier();
        for (ISubCategory c : udc.getClassifiers()) {
            if (categories.contains(c.getId())) {
                UserDefinedRegexValidator validator = (UserDefinedRegexValidator) c.getValidator();
                if (validator instanceof UserDefinedRE2JRegexValidator) {
                    UserDefinedCategory newCat = (UserDefinedCategory) SerializationUtils.clone(c);
                    UserDefinedRegexValidator newValidator = new UserDefinedRegexValidator();
                    newValidator.setCaseInsensitive(validator.getCaseInsensitive());
                    newValidator.setPatternString(validator.getPatternString());
                    newValidator.setSubValidatorClassName(validator.getSubValidatorClassName());
                    newCat.setValidator(newValidator);
                    this.regexClassifier.addSubCategory(newCat);
                } else {
                    this.regexClassifier.addSubCategory(c);
                }
            }
        }
    }

    public BroadcastRegexObject(URI regexPath) {
        try {
            this.regexClassifier = UDCategorySerDeser.readJsonFile(regexPath);
        } catch (IOException e) {
            LOGGER.error("Failed to load resource from " + regexPath, e);
        }
    }

    public UserDefinedClassifier getRegexClassifier() {
        return regexClassifier;
    }

    public void setRegexClassifier(UserDefinedClassifier regexClassifier) {
        this.regexClassifier = regexClassifier;
    }
}
