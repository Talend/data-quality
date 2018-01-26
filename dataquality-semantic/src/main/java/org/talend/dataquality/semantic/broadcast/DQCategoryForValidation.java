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
package org.talend.dataquality.semantic.broadcast;

import java.io.Serializable;
import java.util.List;

import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.ValidationMode;

/**
 * Core semantic category metadata for validation.
 */
public class DQCategoryForValidation implements Serializable {

    private static final long serialVersionUID = 4995560372449434190L;

    private String id;

    private String name;

    private String label;

    private CategoryType type; // A type: RE, DD, KW (needed? How to manage OR clause: RE or in DD?)

    private Boolean completeness;

    private ValidationMode validationMode;

    private List<DQCategoryForValidation> children;

    private List<DQCategoryForValidation> parents;

    private Boolean modified = Boolean.FALSE;

    private Boolean deleted = Boolean.FALSE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public Boolean getCompleteness() {
        return completeness;
    }

    public void setCompleteness(Boolean completeness) {
        this.completeness = completeness;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
    }

    public List<DQCategoryForValidation> getChildren() {
        return children;
    }

    public void setChildren(List<DQCategoryForValidation> children) {
        this.children = children;
    }

    public List<DQCategoryForValidation> getParents() {
        return parents;
    }

    public void setParents(List<DQCategoryForValidation> parents) {
        this.parents = parents;
    }

    public Boolean getModified() {
        return modified;
    }

    public void setModified(Boolean modified) {
        this.modified = modified;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
