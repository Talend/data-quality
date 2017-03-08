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
import java.util.List;

public class DQCategoryWithDocuments implements Serializable {

    private DQCategory category;

    private List<DQDocument> documents;

    public DQCategory getCategory() {
        return category;
    }

    public void setCategory(DQCategory category) {
        this.category = category;
    }

    public List<DQDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DQDocument> documents) {
        this.documents = documents;
    }
}
