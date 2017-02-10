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

import java.net.URL;
import java.util.Date;
import java.util.List;

public class DQCategory {

    private String id;

    private String name;

    private String label;

    private String description;

    private CategoryType type; // A type: RE, DD, KW (needed? How to manage OR clause: RE or in DD?)

    private CategoryPrivacyLevel privacyLevel;

    private URL accessLink;// The Regex or the DD/KW access link

    private List<URL> dataSources;// data sources where possible

    private String version;

    private String creator;

    private String technicalDataType;

    private List<String> countries;

    private List<String> languages;

    private DQRegEx regEx;

    private Date modifiedAt;

    private String lastModifier;

    private Boolean completeness;

    private Boolean draft;

    private Boolean published;

    private Date lastPublished;

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

    public URL getAccessLink() {
        return accessLink;
    }

    public void setAccessLink(URL accessLink) {
        this.accessLink = accessLink;
    }

    public List<URL> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<URL> dataSources) {
        this.dataSources = dataSources;
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

    public String getTechnicalDataType() {
        return technicalDataType;
    }

    public void setTechnicalDataType(String technicalDataType) {
        this.technicalDataType = technicalDataType;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
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

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String toString() {
        return String.format(
                "Category [ID=%s  Type=%s  Name=%-20s  Label=%-20s  Completeness=%-5s  Description=%s Creator=%s Last Modifier=%s Draft=%-5s Published=%-5s Last published=%s]",
                id, type, name, label, completeness, description, creator, lastModifier, draft, published, lastPublished);
    }

}
