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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DQCategory implements Serializable {

    private static final long serialVersionUID = 4593691452129397269L;

    private String id;

    private String name;

    private String label;

    private String description;

    private CategoryType type; // A type: RE, DD, KW (needed? How to manage OR clause: RE or in DD?)

    private CategoryPrivacyLevel privacyLevel;

    private String version;

    private String creator;

    private String creatorName;

    private Date createdAt;

    private DQRegEx regEx;

    private Date modifiedAt;

    private String lastModifier;

    private String lastModifierName;

    private Boolean completeness;

    private Date publishedAt;

    private String lastPublisher;

    private String lastPublisherName;

    private ValidationMode validationMode;

    private CategoryState state;

    private List<DQCategory> children;

    private List<DQCategory> parents;

    @JsonIgnore
    private Boolean modified = Boolean.FALSE;

    @JsonIgnore
    private Boolean deleted = Boolean.FALSE;

    public DQCategory(String id) {
        this.id = id;
    }

    public DQCategory() {

    }

    public static DQCategoryBuilder newBuilder() {
        return new DQCategoryBuilder();
    }

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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public CategoryPrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(CategoryPrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public DQRegEx getRegEx() {
        return regEx;
    }

    public void setRegEx(DQRegEx regEx) {
        this.regEx = regEx;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleteness() {
        return completeness;
    }

    public void setCompleteness(Boolean completeness) {
        this.completeness = completeness;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public String getLastModifierName() {
        return lastModifierName;
    }

    public void setLastModifierName(String lastModifierName) {
        this.lastModifierName = lastModifierName;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getLastPublisher() {
        return lastPublisher;
    }

    public void setLastPublisher(String lastPublisher) {
        this.lastPublisher = lastPublisher;
    }

    public String getLastPublisherName() {
        return lastPublisherName;
    }

    public void setLastPublisherName(String lastPublisherName) {
        this.lastPublisherName = lastPublisherName;
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

    public List<DQCategory> getChildren() {
        return children;
    }

    public void setChildren(List<DQCategory> children) {
        this.children = children;
    }

    public List<DQCategory> getParents() {
        return parents;
    }

    public void setParents(List<DQCategory> parents) {
        this.parents = parents;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
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

    @Override
    public String toString() {
        return String.format(
                "Category [ID=%s  Type=%s  Name=%-20s  Label=%-20s  Completeness=%s  Modified=%-5s  Creator=%s Last Modifier=%s State=%-20s Last published=%s]",
                id, type, name, label, completeness, modified, creator, lastModifier, state, publishedAt);
    }

    public static final class DQCategoryBuilder {

        private String id;

        private String name;

        private String label;

        private String description;

        private CategoryType type; // A type: RE, DD, KW (needed? How to manage OR clause: RE or in DD?)

        private CategoryPrivacyLevel privacyLevel;

        private String version;

        private String creator;

        private String creatorName;

        private Date createdAt;

        private DQRegEx regEx;

        private Date modifiedAt;

        private String lastModifier;

        private String lastModifierName;

        private Boolean completeness;

        private Date publishedAt;

        private String lastPublisher;

        private String lastPublisherName;

        private ValidationMode validationMode;

        private CategoryState state;

        private List<DQCategory> children;

        private List<DQCategory> parents;

        private Boolean modified = Boolean.FALSE;

        private Boolean deleted = Boolean.FALSE;

        private DQCategoryBuilder() {
        }

        public DQCategoryBuilder id(String id) {
            this.id = id;
            return this;
        }

        public DQCategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DQCategoryBuilder label(String label) {
            this.label = label;
            return this;
        }

        public DQCategoryBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DQCategoryBuilder type(CategoryType type) {
            this.type = type;
            return this;
        }

        public DQCategoryBuilder privacyLevel(CategoryPrivacyLevel privacyLevel) {
            this.privacyLevel = privacyLevel;
            return this;
        }

        public DQCategoryBuilder version(String version) {
            this.version = version;
            return this;
        }

        public DQCategoryBuilder creator(String creator) {
            this.creator = creator;
            return this;
        }

        public DQCategoryBuilder creatorName(String creatorName) {
            this.creatorName = creatorName;
            return this;
        }

        public DQCategoryBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DQCategoryBuilder regEx(DQRegEx regEx) {
            this.regEx = regEx;
            return this;
        }

        public DQCategoryBuilder modifiedAt(Date modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public DQCategoryBuilder lastModifier(String lastModifier) {
            this.lastModifier = lastModifier;
            return this;
        }

        public DQCategoryBuilder lastModifierName(String lastModifierName) {
            this.lastModifierName = lastModifierName;
            return this;
        }

        public DQCategoryBuilder completeness(Boolean completeness) {
            this.completeness = completeness;
            return this;
        }

        public DQCategoryBuilder publishedAt(Date publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public DQCategoryBuilder lastPublisher(String lastPublisher) {
            this.lastPublisher = lastPublisher;
            return this;
        }

        public DQCategoryBuilder lastPublisherName(String lastPublisherName) {
            this.lastPublisherName = lastPublisherName;
            return this;
        }

        public DQCategoryBuilder validationMode(ValidationMode validationMode) {
            this.validationMode = validationMode;
            return this;
        }

        public DQCategoryBuilder state(CategoryState state) {
            this.state = state;
            return this;
        }

        public DQCategoryBuilder children(List<DQCategory> children) {
            this.children = children;
            return this;
        }

        public DQCategoryBuilder parents(List<DQCategory> parents) {
            this.parents = parents;
            return this;
        }

        public DQCategoryBuilder modified(Boolean modified) {
            this.modified = modified;
            return this;
        }

        public DQCategoryBuilder deleted(Boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public DQCategory build() {
            DQCategory dQCategory = new DQCategory();
            dQCategory.setId(id);
            dQCategory.setName(name);
            dQCategory.setLabel(label);
            dQCategory.setDescription(description);
            dQCategory.setType(type);
            dQCategory.setPrivacyLevel(privacyLevel);
            dQCategory.setVersion(version);
            dQCategory.setCreator(creator);
            dQCategory.setCreatorName(creatorName);
            dQCategory.setCreatedAt(createdAt);
            dQCategory.setRegEx(regEx);
            dQCategory.setModifiedAt(modifiedAt);
            dQCategory.setLastModifier(lastModifier);
            dQCategory.setLastModifierName(lastModifierName);
            dQCategory.setCompleteness(completeness);
            dQCategory.setPublishedAt(publishedAt);
            dQCategory.setLastPublisher(lastPublisher);
            dQCategory.setLastPublisherName(lastPublisherName);
            dQCategory.setValidationMode(validationMode);
            dQCategory.setState(state);
            dQCategory.setChildren(children);
            dQCategory.setParents(parents);
            dQCategory.setModified(modified);
            dQCategory.setDeleted(deleted);
            return dQCategory;
        }
    }
}
