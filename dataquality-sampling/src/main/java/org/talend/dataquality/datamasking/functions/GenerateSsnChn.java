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

import java.util.List;

import org.talend.dataquality.datamasking.generic.fields.FieldDate;
import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnChn;

/**
 * 
 * @author dprot
 * This class proposes a pure-random Chinese SSN number
 * The Chinese SSN has 4 fields : the first one, on 6 digits, stands for the birth place; the second one, with format
 * YYYYMMDD for the date of birth; the third one, with 3 digits; the last one, on one digit, is a checksum key
 */
public class GenerateSsnChn extends Function<String> {

    private static final long serialVersionUID = 8845031997964609626L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);

        List<String> places = UtilsSsnChn.readChinaRegionFile();

        if (places != null) {
            result.append(places.get(rnd.nextInt(places.size())));
        }

        // Year
        int yyyy = rnd.nextInt(200) + 1900;
        result.append(yyyy);
        // Month
        int mm = rnd.nextInt(12) + 1;
        if (mm < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(mm);
        // Day
        int dd = 1 + rnd.nextInt(FieldDate.monthSize.get(mm - 1));
        if (dd < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        // Birth rank
        result.append(dd);
        for (int i = 0; i < 3; ++i) {
            result.append(nextRandomDigit());
        }

        // Add the security key specified for Chinese SSN
        result.append(UtilsSsnChn.computeChineseKey(result));

        return result.toString();
    }
}
