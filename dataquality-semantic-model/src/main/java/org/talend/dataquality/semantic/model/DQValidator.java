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
package org.talend.dataquality.semantic.model;

import java.io.Serializable;

public class DQValidator implements Serializable {

    private static final long serialVersionUID = 2265314886790764196L;

    private String patternString;

    private String subValidatorClassName;

    private Boolean caseInsensitive = true;

    private boolean re2jCompliant = true;

    private boolean generexCompliant = true;

    public String getPatternString() {
        return patternString;
    }

    public void setPatternString(String patternString) {
        this.patternString = patternString;
    }

    public String getSubValidatorClassName() {
        return subValidatorClassName;
    }

    public void setSubValidatorClassName(String subValidatorClassName) {
        this.subValidatorClassName = subValidatorClassName;
    }

    public Boolean getCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public boolean isRe2jCompliant() {
        return re2jCompliant;
    }

    public void setRe2jCompliant(boolean re2jCompliant) {
        this.re2jCompliant = re2jCompliant;
    }

    public boolean isGenerexCompliant() {
        return generexCompliant;
    }

    public void setGenerexCompliant(boolean generexCompliant) {
        this.generexCompliant = generexCompliant;
    }
}
