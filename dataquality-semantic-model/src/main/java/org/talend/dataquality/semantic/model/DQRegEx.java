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

public class DQRegEx implements Serializable {

    private String mainCategory;

    private DQFilter filter;

    private DQValidator validator;

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public DQFilter getFilter() {
        return filter;
    }

    public void setFilter(DQFilter filter) {
        this.filter = filter;
    }

    public DQValidator getValidator() {
        return validator;
    }

    public void setValidator(DQValidator validator) {
        this.validator = validator;
    }
}
