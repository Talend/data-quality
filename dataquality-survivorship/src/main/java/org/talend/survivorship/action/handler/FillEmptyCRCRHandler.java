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
package org.talend.survivorship.action.handler;

import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.FilledAttribute;
import org.talend.survivorship.model.Record;
import org.talend.survivorship.model.SubDataSet;

/**
 * The class to handle fill empty action
 */
public class FillEmptyCRCRHandler extends DataCleanCRCRHandler {

    /**
     * The constructor of FillEmptyCRCRHandler.
     * 
     * @param handlerParameter
     */
    public FillEmptyCRCRHandler(HandlerParameter handlerParameter) {
        super(handlerParameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.CRCRHandler#doHandle(java.lang.Integer, java.lang.String)
     */
    @Override
    protected void doHandle(Integer rowNum, String columnName) {
        Record record = handlerParameter.getDataset().getRecordList().get(rowNum);
        Attribute tarAttribute = record.getAttribute(handlerParameter.getTarColumn().getName());
        Object value = tarAttribute.getValue();
        if (isNeedFillColumn(value, handlerParameter.getRefColumn().getName())) {

            Attribute sourceAttribute = record.getAttribute(handlerParameter.getRefColumn().getName());

            FilledAttribute filledAttribute = new FilledAttribute(tarAttribute, sourceAttribute);
            ((SubDataSet) this.getHandlerParameter().getDataset()).addFillAttributeMap(filledAttribute);
        }
        // record conflict data index of previous handler
        super.doHandle(rowNum, columnName);

    }

}
