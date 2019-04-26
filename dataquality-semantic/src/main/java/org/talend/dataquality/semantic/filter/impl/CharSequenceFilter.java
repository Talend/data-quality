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
package org.talend.dataquality.semantic.filter.impl;

import org.talend.dataquality.semantic.filter.ISemanticFilter;

/**
 * Created by sizhaoliu on 20/03/15.
 */
public class CharSequenceFilter implements ISemanticFilter {

    private static final long serialVersionUID = 721733127885462464L;

    private CharSequenceFilterType filterType;

    private String filterParam;

    /**
     * created by talend on 2015-07-28 Detailled comment.
     *
     */
    public enum CharSequenceFilterType {
        MUST_CONTAIN,
        MUST_NOT_CONTAIN,
        MUST_START_WITH,
        MUST_END_WITH
    }

    public CharSequenceFilter() {
        // No need to implement
    }

    public CharSequenceFilter(CharSequenceFilterType type, String param) {
        this.filterType = type;
        this.filterParam = param;

    }

    @Override
    public boolean isQualified(String str) {
        switch (filterType) {
        case MUST_CONTAIN:
            return str.contains(filterParam);
        case MUST_NOT_CONTAIN:
            return !str.contains(filterParam);
        case MUST_START_WITH:
            return str.startsWith(filterParam);
        case MUST_END_WITH:
            return str.endsWith(filterParam);
        default:
            break;
        }

        return true;
    }

    /**
     * Getter for filterType.
     * 
     * @return the filterType
     */
    public CharSequenceFilterType getFilterType() {
        return this.filterType;
    }

    /**
     * Sets the filterType.
     * 
     * @param filterType the filterType to set
     */
    public void setFilterType(CharSequenceFilterType filterType) {
        this.filterType = filterType;
    }

    /**
     * Getter for filterParam.
     * 
     * @return the filterParam
     */
    public String getFilterParam() {
        return this.filterParam;
    }

    /**
     * Sets the filterParam.
     * 
     * @param filterParam the filterParam to set
     */
    public void setFilterParam(String filterParam) {
        this.filterParam = filterParam;
    }
}
