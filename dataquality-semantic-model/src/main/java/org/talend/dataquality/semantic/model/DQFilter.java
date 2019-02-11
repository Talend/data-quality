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

public class DQFilter implements Serializable {

    private static final long serialVersionUID = 970248862880439269L;

    private String filterParam;

    private String filterType;

    public static DQFilterBuilder newBuilder() {
        return new DQFilterBuilder();
    }

    public String getFilterParam() {
        return filterParam;
    }

    public void setFilterParam(String filterParam) {
        this.filterParam = filterParam;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public static final class DQFilterBuilder {

        private String filterParam;

        private String filterType;

        private DQFilterBuilder() {
        }

        public DQFilterBuilder filterParam(String filterParam) {
            this.filterParam = filterParam;
            return this;
        }

        public DQFilterBuilder filterType(String filterType) {
            this.filterType = filterType;
            return this;
        }

        public DQFilter build() {
            DQFilter dQFilter = new DQFilter();
            dQFilter.setFilterParam(filterParam);
            dQFilter.setFilterType(filterType);
            return dQFilter;
        }
    }
}
