// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.matchmerge.mfb.mdm.utils;

import java.util.List;
import java.util.Locale;

public class DQFieldMetadataImpl implements FieldMetadata {

    private String name = null;

    /**
     * 
     */
    public DQFieldMetadataImpl(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(MetadataVisitor<T> visitor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setData(String key, Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public <X> X getData(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isKey() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TypeMetadata getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getContainingType() {
        return "ContainingType";
    }

    @Override
    public void setContainingType(ComplexTypeMetadata typeMetadata) {
        // TODO Auto-generated method stub

    }

    @Override
    public TypeMetadata getDeclaringType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getHideUsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getWriteUsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getWorkflowAccessRights() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isMany() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isMandatory() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void adopt(ComplexTypeMetadata metadata) {
        // TODO Auto-generated method stub

    }

    @Override
    public FieldMetadata copy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FieldMetadata freeze() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void promoteToKey() {
        // TODO Auto-generated method stub

    }

    @Override
    public void validate(ValidationHandler handler) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerName(Locale locale, String name) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName(Locale locale) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerDescription(Locale locale, String description) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDescription(Locale locale) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVisibilityRule() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getNoAddRoles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getNoRemoveRoles() {
        // TODO Auto-generated method stub
        return null;
    }

}
