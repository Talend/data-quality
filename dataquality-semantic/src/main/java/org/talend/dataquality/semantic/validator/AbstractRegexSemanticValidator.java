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
package org.talend.dataquality.semantic.validator;

import java.util.regex.Pattern;

import org.talend.dataquality.semantic.classifier.custom.UserDefinedRE2JRegexValidator;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRegexValidator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "re2jCompliant", defaultImpl = UserDefinedRegexValidator.class)
@JsonSubTypes({ @JsonSubTypes.Type(value = UserDefinedRE2JRegexValidator.class, name = "true"),
        @JsonSubTypes.Type(value = UserDefinedRegexValidator.class, name = "false") })
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractRegexSemanticValidator implements ISemanticValidator {

    private static final long serialVersionUID = -8373360239860394354L;

    protected Pattern caseSensitivePattern;

    protected Pattern caseInsensitivePattern;

    public boolean isValid(String str, boolean caseSensitive) {
        if (str == null || caseSensitivePattern == null || caseInsensitivePattern == null) {
            return false;
        }
        return (caseSensitive ? caseSensitivePattern.matcher(str.trim()).find()
                : caseInsensitivePattern.matcher(str.trim()).find());
    }

    public boolean isValid(String str) {
        return isValid(str, false);
    }
}
