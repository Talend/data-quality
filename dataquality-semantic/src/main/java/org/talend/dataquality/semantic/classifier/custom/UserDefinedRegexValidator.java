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
package org.talend.dataquality.semantic.classifier.custom;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.exception.DQRuntimeException;
import org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator;
import org.talend.dataquality.semantic.validator.ISemanticSubValidator;

/**
 * The regex validator can have a sub-validator defined in json file. Like : <br/>
 * <code>
 *         "validator" : { 
 *         "patternString" : "^(?<Sedol>[B-Db-dF-Hf-hJ-Nj-nP-Tp-tV-Xv-xYyZz\\d]{6}\\d)$",
 *         "subValidatorClassName": "org.talend.dataquality.semantic.validator.impl.SedolValidator" 
 *         }</code> <br>
 * Or set with setter {{@link #setSubValidatorClassName(String)}<br>
 * When the regex matches, then do another check with sub-validator if provided.
 */
public class UserDefinedRegexValidator extends AbstractRegexSemanticValidator {

    private static final long serialVersionUID = -7832927422566889796L;

    private static final Logger LOG = Logger.getLogger(UserDefinedRegexValidator.class);

    private String patternString;

    private Boolean caseInsensitive = true;

    /**
     * an optional secondary validator.
     */
    private String subValidatorClassName = "";

    private ISemanticSubValidator subValidator;

    private boolean isSetSubValidator = false;

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

    boolean isSetSubValidator() {
        return isSetSubValidator;
    }

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

    public String getPatternString() {
        return patternString;
    }

    public void setPatternString(String patternString) {
        if (StringUtils.isEmpty(patternString)) {
            throw new DQRuntimeException("null argument of patternString is not allowed.");
        }
        this.patternString = patternString;
        try {
            caseInsensitivePattern = caseInsensitive ? Pattern.compile(patternString, Pattern.CASE_INSENSITIVE)
                    : Pattern.compile(patternString);
            caseSensitivePattern = Pattern.compile(patternString);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid regular expression: " + this.patternString, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String str) {
        return isValid(str, false);
    }

    @Override
    public boolean isValid(String str, boolean caseSensitive) {
        if (!super.isValid(str, caseSensitive)) {
            return false;
        }
        // else
        if (isSetSubValidator && !this.validateWithSubValidator(str)) {
            return false;
        }
        // else all checks validated
        return true;
    }

    private ISemanticSubValidator createSubValidator(String validatorName) {
        if (validatorName != null && !validatorName.isEmpty()) {
            try {
                Class<?> subSemanticValidator = Class.forName(validatorName);
                return (ISemanticSubValidator) subSemanticValidator.newInstance();
            } catch (ClassNotFoundException e) {
                LOG.error(e, e);
            } catch (InstantiationException e) {
                LOG.error(e, e);
            } catch (IllegalAccessException e) {
                LOG.error(e, e);
            }
            // exception caught => default subValidator
            // remove any existing subvalidator
            this.isSetSubValidator = false;
            this.subValidator = null;
            throw new IllegalArgumentException("Invalid validator class name: " + validatorName); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * Method "validateWithSubValidator".
     * 
     * @param str the string to check
     * @return true when the subValidator class validates the given string, false otherwise.
     */
    private boolean validateWithSubValidator(String str) {
        return this.subValidator.isValid(str);
    }
}
