package org.talend.dataquality.semantic.classifier.custom;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.exception.DQSemanticRuntimeException;

import com.google.re2j.Pattern;
import com.google.re2j.PatternSyntaxException;

public class UserDefinedRE2JRegexValidator extends UserDefinedRegexValidator {

    private static final long serialVersionUID = -7087456754020469957L;

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
