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
package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jteuladedenantes class global comment. Detailled comment
 * 
 * Japan pattern: aaaaaaaaaaaa aaaaaaaaaaaa: 1 -> 1000000000000 - 1
 */

public class GenerateUniqueSsnJapan extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = -2321693247791991249L;

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();
        fields.add(new FieldInterval(1, 1000000000000L - 1));
        return fields;
    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 12));

        if (ssnPattern == null) {
            List<AbstractField> fields = createFieldsListFromPattern();
            ssnPattern = new GenerateUniqueRandomPatterns(fields);
        }

        return ssnPattern.generateUniqueString(strs);
    }
}
