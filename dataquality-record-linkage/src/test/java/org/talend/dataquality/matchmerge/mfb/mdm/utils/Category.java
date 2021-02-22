/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge.mfb.mdm.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Category {

    private String name;

    private List<String> fields;

    private Map<Locale, String> labels;

    public Category() {
    }

    public Category(String name, List<String> fields, Map<Locale, String> labels) {
        this.name = name;
        this.fields = fields;
        this.labels = labels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Map<Locale, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<Locale, String> labels) {
        this.labels = labels;
    }
}