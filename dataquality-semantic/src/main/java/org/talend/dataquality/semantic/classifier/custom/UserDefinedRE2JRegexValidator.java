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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.exception.DQSemanticRuntimeException;
import org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator;

import com.google.re2j.Pattern;
import com.google.re2j.PatternSyntaxException;

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
public class UserDefinedRE2JRegexValidator extends AbstractRegexSemanticValidator {

    private static final long serialVersionUID = -7832927422566889796L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDefinedRE2JRegexValidator.class);

    private Pattern caseSensitiveRe2JPattern;

    private Pattern caseInsensitiveRe2JPattern;

    @Override
    public void setPatternString(String patternString) {
        if (StringUtils.isEmpty(patternString)) {
            throw new DQSemanticRuntimeException("null argument of patternString is not allowed.");
        }
        this.patternString = patternString;
        try {
            caseInsensitiveRe2JPattern = caseInsensitive ? Pattern.compile(patternString, Pattern.CASE_INSENSITIVE)
                    : Pattern.compile(patternString);
            caseSensitiveRe2JPattern = Pattern.compile(patternString);
        } catch (IllegalArgumentException | PatternSyntaxException e) {
            LOGGER.error("Invalid regular expression: " + this.patternString, e);
        }
    }

    @Override
    public boolean isValid(String str, boolean caseSensitive) {
        if (!checkValid(str, caseSensitive)) {
            return false;
        }
        // else
        if (isSetSubValidator && !this.validateWithSubValidator(str)) {
            return false;
        }
        // else all checks validated
        return true;
    }

    private boolean checkValid(String str, boolean caseSensitive) {
        if (str == null || caseSensitiveRe2JPattern == null || caseInsensitiveRe2JPattern == null)
            return false;

        return (caseSensitive ? caseSensitiveRe2JPattern.matcher(str.trim()).find()
                : caseInsensitiveRe2JPattern.matcher(str.trim()).find());
    }

}
