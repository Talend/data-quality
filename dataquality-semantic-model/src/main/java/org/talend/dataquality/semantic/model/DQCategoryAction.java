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

import java.util.List;

public class DQCategoryAction extends DQAction {

    private List<DQCategory> categories;

    private String context;

    public static DQCategoryActionBuilder newBuilder() {
        return new DQCategoryActionBuilder();
    }

    public List<DQCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<DQCategory> categories) {
        this.categories = categories;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public static final class DQCategoryActionBuilder {

        private Action action;

        private String id;

        private List<DQCategory> categories;

        private String context;

        private DQCategoryActionBuilder() {
        }

        public DQCategoryActionBuilder action(Action action) {
            this.action = action;
            return this;
        }

        public DQCategoryActionBuilder id(String id) {
            this.id = id;
            return this;
        }

        public DQCategoryActionBuilder categories(List<DQCategory> categories) {
            this.categories = categories;
            return this;
        }

        public DQCategoryActionBuilder context(String context) {
            this.context = context;
            return this;
        }

        public DQCategoryAction build() {
            DQCategoryAction dQCategoryAction = new DQCategoryAction();
            dQCategoryAction.setAction(action);
            dQCategoryAction.setId(id);
            dQCategoryAction.setCategories(categories);
            dQCategoryAction.setContext(context);
            return dQCategoryAction;
        }
    }
}
