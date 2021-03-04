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
package org.talend.dataquality.record.linkage.analyzer;

import java.io.Serializable;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * Use to match & merge values between different blocks.
 */
public class PostMerge implements Serializable {

    private static final long serialVersionUID = 2043501709176462242L;

    AttributeMatcherType matcher;

    float threshold;

    public PostMerge(AttributeMatcherType matcher, float threshold) {
        this.matcher = matcher;
        this.threshold = threshold;
    }
}
