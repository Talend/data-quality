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
package org.talend.dataquality.email.CommonCheck;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by talend on 2014年12月30日 Detailled comment
 *
 */
public class RegexValidator implements Serializable {

    private static final long serialVersionUID = -8832409930574867162L;

    private final Pattern[] patterns;

    /**
     * Construct a <i>case sensitive</i> validator for a single regular expression.
     *
     * @param regex The regular expression this validator will validate against
     */
    public RegexValidator(String regex) {
        this(regex, true);
    }

    /**
     * Construct a validator for a single regular expression with the specified case sensitivity.
     *
     * @param regex The regular expression this validator will validate against
     * @param caseSensitive when <code>true</code> matching is <i>case sensitive</i>, otherwise matching is <i>case
     * in-sensitive</i>
     */
    public RegexValidator(String regex, boolean caseSensitive) {
        this(new String[] { regex }, caseSensitive);
    }

    /**
     * Construct a <i>case sensitive</i> validator that matches any one of the set of regular expressions.
     *
     * @param regexs The set of regular expressions this validator will validate against
     */
    public RegexValidator(String[] regexs) {
        this(regexs, true);
    }

    /**
     * Construct a validator that matches any one of the set of regular expressions with the specified case sensitivity.
     *
     * @param regexs The set of regular expressions this validator will validate against
     * @param caseSensitive when <code>true</code> matching is <i>case sensitive</i>, otherwise matching is <i>case
     * in-sensitive</i>
     */
    public RegexValidator(String[] regexs, boolean caseSensitive) {
        if (regexs == null || regexs.length == 0) {
            throw new IllegalArgumentException("Regular expressions are missing");
        }
        patterns = new Pattern[regexs.length];
        int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        for (int i = 0; i < regexs.length; i++) {
            if (regexs[i] == null || regexs[i].length() == 0) {
                throw new IllegalArgumentException("Regular expression[" + i + "] is missing");
            }
            patterns[i] = Pattern.compile(regexs[i], flags);
        }
    }

    /**
     * Validate a value against the set of regular expressions.
     *
     * @param value The value to validate.
     * @return <code>true</code> if the value is valid otherwise <code>false</code>.
     */
    public boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        for (Pattern pattern : patterns) {
            if (pattern.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate a value against the set of regular expressions returning the array of matched groups.
     *
     * @param value The value to validate.
     * @return String array of the <i>groups</i> matched if valid or <code>null</code> if invalid
     */
    public String[] match(String value) {
        if (value == null) {
            return new String[0];
        }
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                int count = matcher.groupCount();
                String[] groups = new String[count];
                for (int j = 0; j < count; j++) {
                    groups[j] = matcher.group(j + 1);
                }
                return groups;
            }
        }
        return new String[0];
    }

    /**
     * Validate a value against the set of regular expressions returning a String value of the aggregated groups.
     *
     * @param value The value to validate.
     * @return Aggregated String value comprised of the <i>groups</i> matched if valid or <code>null</code> if invalid
     */
    public String validate(String value) {
        if (value == null) {
            return null;
        }
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                int count = matcher.groupCount();
                if (count == 1) {
                    return matcher.group(1);
                }
                StringBuilder buffer = new StringBuilder();
                for (int j = 0; j < count; j++) {
                    String component = matcher.group(j + 1);
                    if (component != null) {
                        buffer.append(component);
                    }
                }
                return buffer.toString();
            }
        }
        return null;
    }

    /**
     * Provide a String representation of this validator.
     * 
     * @return A String representation of this validator
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("RegexValidator{");
        for (int i = 0; i < patterns.length; i++) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append(patterns[i].pattern());
        }
        buffer.append("}");
        return buffer.toString();
    }

}
