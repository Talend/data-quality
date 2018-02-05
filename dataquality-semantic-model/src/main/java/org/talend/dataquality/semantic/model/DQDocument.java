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
import java.util.Date;
import java.util.Set;

public class DQDocument implements Serializable {

    private static final long serialVersionUID = 4167438961487139761L;

    private String id;

    private Set<String> values;

    private DQCategory category;

    private String creator;

    private Date createdAt;

    private Date modifiedAt;

    private String lastModifier;

    private CategoryState state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }

    public DQCategory getCategory() {
        return category;
    }

    public void setCategory(DQCategory category) {
        this.category = category;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public CategoryState getState() {
        return state;
    }

    public void setState(CategoryState state) {
        this.state = state;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("DQDocument [ID=%s]", id);
    }
}
