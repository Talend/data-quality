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
package org.talend.dataquality.semantic.sampling;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by zhao on 2015-4-27 Semantic category bean which can be a bridge from UI to semantic API.
 *
 */
public class SemanticCategory {

    public static final SemanticCategory EMPTY = new SemanticCategory(StringUtils.EMPTY, StringUtils.EMPTY, 0L, 0.0);

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticCategory.class);
    // TODO private CategoryFrequency categoryFrequency;

    /**
     * 
     * SemanticCategory constructor comment.
     * 
     * @param categoryId the category id
     * @param categoryName the category name
     * @param count the number of time this id appears
     * @param freq the frequency
     */
    public SemanticCategory(String categoryId, String categoryName, long count, double freq) {
        if (categoryName == null) {
            LOGGER.error("ERROR: category name is null.");
        }
        assert categoryName != null : "category name is null for " + categoryId;
        this.semanticCategoryId = categoryId;
        this.count = count;
        this.frequency = freq;
        this.semanticName = categoryName;
    }

    private final String semanticCategoryId;

    private String semanticName;

    private long count = 0L;

    private double frequency = 0d;

    private boolean isTextFieldValue = false;

    /**
     * Getter for semanticCategory.
     * 
     * @return the semanticCategory
     */
    public String getSemanticCategoryID() {
        return this.semanticCategoryId;
    }

    public String getSemanticName() {
        return semanticName;
    }

    public void setSemanticName(String semanticName) {
        this.semanticName = semanticName;
    }

    /**
     * Getter for count.
     * 
     * @return the count
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Getter for frequencies.
     * 
     * @return the frequencies
     */
    public double getFrequency() {
        return this.frequency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString() Show frequencies when the value more than zero or semanticCategory is not blank
     * else show semanticCategory only
     */
    @Override
    public String toString() {
        if (isNotShowFrequency()) {
            return this.semanticName;
        } else {
            return formatStr();
        }
    }

    /**
     * DOC talend Comment method "formatStr".
     * 
     * @return
     */
    private String formatStr() {
        StringBuilder strBuf = new StringBuilder();
        strBuf.append(semanticName);
        if (Double.compare(frequency, 0.0D) == 0) {
            strBuf.append("                       (previous match)");
        } else {
            for (int index = strBuf.length(); index < Math.max(2, 50 - semanticName.length()); index++) {
                strBuf.append(" "); //$NON-NLS-1$
            }
            if (frequency >= 1) {
                strBuf.append((int) Math.floor(this.frequency));
                strBuf.append("%"); //$NON-NLS-1$
            } else {
                strBuf.append("<1%");//$NON-NLS-1$
            }
        }

        return strBuf.toString();
    }

    /**
     * DOC talend Comment method "isShowFrequency".
     * 
     * @return
     */
    private boolean isNotShowFrequency() {
        return this.frequency < 0d || StringUtils.isBlank(semanticCategoryId) || isTextFieldValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public SemanticCategory clone() {
        try {
            return (SemanticCategory) super.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.info(e.getMessage(), e);
            return new SemanticCategory(this.semanticCategoryId, this.semanticName, this.count, this.frequency);
        }
    }

    /**
     * Getter for isShowTextFieldValue.
     * 
     * @return the isShowTextFieldValue
     */
    public boolean isTextFieldValue() {
        return this.isTextFieldValue;
    }

    /**
     * Sets the isShowTextFieldValue.
     * 
     * @param isShowTextFieldValue the isShowTextFieldValue to set
     */
    public void setTextFieldValue(boolean isShowTextFieldValue) {
        this.isTextFieldValue = isShowTextFieldValue;
    }

}
