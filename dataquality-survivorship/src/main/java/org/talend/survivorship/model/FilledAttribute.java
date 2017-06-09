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
package org.talend.survivorship.model;

/**
 * The Attribute which be filled by other column
 */
public class FilledAttribute extends Attribute {

    private Attribute orignalAttribute = null;

    public FilledAttribute(Attribute tarAttribute, Attribute souAttribute) {
        super(tarAttribute.getRecord(), souAttribute.getColumn(), souAttribute.getValue());
        orignalAttribute = tarAttribute;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.model.Attribute#isAlive()
     */
    @Override
    public boolean isAlive() {
        return orignalAttribute.isAlive();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.model.Attribute#isSurvived()
     */
    @Override
    public boolean isSurvived() {
        return orignalAttribute.isSurvived();
    }

    /**
     * Getter for orignalAttribute.
     * 
     * @return the orignalAttribute
     */
    protected Attribute getOrignalAttribute() {
        return this.orignalAttribute;
    }

}
