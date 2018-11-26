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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRE2JRegexValidator;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRegexValidator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "re2jCompliant", defaultImpl = UserDefinedRE2JRegexValidator.class)
@JsonSubTypes({ @JsonSubTypes.Type(value = UserDefinedRE2JRegexValidator.class, name = "true"),
        @JsonSubTypes.Type(value = UserDefinedRegexValidator.class, name = "false") })
public abstract class AbstractRegexSemanticValidator implements ISemanticValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegexSemanticValidator.class);

    protected String patternString;

    public String getPatternString() {
        return patternString;
    }

    /**
     * an optional secondary validator.
     */
    private String subValidatorClassName = "";

    private ISemanticSubValidator subValidator;

    protected boolean isSetSubValidator = false;

    public void setSetSubValidator(boolean setSubValidator) {
        isSetSubValidator = setSubValidator;
    }

    public void setSubValidator(ISemanticSubValidator subValidator) {
        this.subValidator = subValidator;
    }

    protected Boolean caseInsensitive = true;

    /**
     * Getter for caseInsensitive.
     *
     * @return the caseInsensitive
     */
    public Boolean getCaseInsensitive() {
        return this.caseInsensitive;
    }

    /**
     * Sets the caseInsensitive.
     *
     * @param caseInsensitive the caseInsensitive to set
     */
    public void setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * Getter for subValidatorClassName.
     *
     * @return the subValidatorClassName
     */
    public String getSubValidatorClassName() {
        return this.subValidatorClassName;
    }

    /**
     * Sets the subValidatorClassName. <br>
     * The subValidatorClassName should be a full qualified class name like
     * <code>org.talend.dataquality.semantic.validator.impl.SedolValidator</code> <br>
     * A runtime exception will be thrown if given class name is incorrect or not loaded properly.
     *
     * @param subValidatorClassName the subValidatorClassName to set
     */
    public void setSubValidatorClassName(String subValidatorClassName) {
        this.subValidatorClassName = subValidatorClassName;
        this.subValidator = createSubValidator(subValidatorClassName);
        isSetSubValidator = this.subValidator != null;
    }

    public boolean isSetSubValidator() {
        return isSetSubValidator;
    }

    private ISemanticSubValidator createSubValidator(String validatorName) {
        if (validatorName != null && !validatorName.isEmpty()) {
            try {
                Class<?> subSemanticValidator = Class.forName(validatorName);
                return (ISemanticSubValidator) subSemanticValidator.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                LOGGER.error(e.getMessage(), e);
            }
            // exception caught => default subValidator
            // remove any existing subvalidator
            this.isSetSubValidator = false;
            this.subValidator = null;
            throw new IllegalArgumentException("Invalid validator class name: " + validatorName); //$NON-NLS-1$
        }
        return null;
    }

    abstract public void setPatternString(String patternString);

    public boolean isValid(String str) {
        return isValid(str, false);
    }

    /**
     * Method "validateWithSubValidator".
     *
     * @param str the string to check
     * @return true when the subValidator class validates the given string, false otherwise.
     */
    protected boolean validateWithSubValidator(String str) {
        return this.subValidator.isValid(str);
    }
}
