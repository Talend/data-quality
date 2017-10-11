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

import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.filter.ISemanticFilter;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter;
import org.talend.dataquality.semantic.model.MainCategory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class UserDefinedCategory implements ISubCategory {

    private static final long serialVersionUID = 8905048019099749771L;

    private String id;

    private String name;

    private String label;

    private String description;

    private MainCategory mainCategory;

    private ISemanticFilter filter;

    private UserDefinedRegexValidator validator;

    @JsonCreator
    public UserDefinedCategory(@JsonProperty("name") String name, @JsonProperty("label") String label) {
        if (name == null) {
            throw new IllegalArgumentException("A category has no name. Give a name, any name.");
        }
        this.name = name;
        this.label = label; // avoid null name here
    }

    public UserDefinedCategory(String name) {
        this(name, name);
    }

    public UserDefinedCategory(String name, SemanticCategoryEnum cat) {
        if (name == null) {
            throw new IllegalArgumentException("A category has no name. Give a name, any name.");
        }
        this.name = name;
        this.label = (cat == null) ? name : cat.getDisplayName(); // avoid null name here
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter for description.
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ISemanticFilter getFilter() {
        return filter;
    }

    public void setFilter(CharSequenceFilter filter) {
        this.filter = filter;
    }

    @Override
    public UserDefinedRegexValidator getValidator() {
        return validator;
    }

    public void setValidator(UserDefinedRegexValidator validator) {
        this.validator = validator;
    }

    /**
     * Getter for mainCategory.
     * 
     * @return the mainCategory
     */
    public MainCategory getMainCategory() {
        return this.mainCategory;
    }

    /**
     * Sets the mainCategory.
     * 
     * @param mainCategory the mainCategory to set
     */
    public void setMainCategory(MainCategory mainCategory) {
        this.mainCategory = mainCategory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null) ? this.name.equals(((UserDefinedCategory) obj).name) : false;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
