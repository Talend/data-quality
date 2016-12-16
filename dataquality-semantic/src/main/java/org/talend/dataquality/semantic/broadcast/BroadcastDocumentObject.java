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
import java.util.Set;

/**
 * A POJO representing index document to be diffused across cluster computing executers such as Apache Spark.
 */
public class BroadcastDocumentObject implements Serializable {

    private static final long serialVersionUID = -1650549578529804062L;

    private String category;

    private Set<String> valueSet;

    // No argument constructor needed for Jackson Deserialization
    public BroadcastDocumentObject() {
    }

    /**
     * @param category the category reference
     * @param valueSet the values of the document
     */
    public BroadcastDocumentObject(String category, Set<String> valueSet) {
        this.category = category;
        this.valueSet = valueSet;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<String> getValueSet() {
        return valueSet;
    }

    public void setValueSet(Set<String> valueSet) {
        this.valueSet = valueSet;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(category).append(" -> ");
        for (String str : valueSet) {
            builder.append(str).append(", ");
        }
        return builder.toString();
    }
}
