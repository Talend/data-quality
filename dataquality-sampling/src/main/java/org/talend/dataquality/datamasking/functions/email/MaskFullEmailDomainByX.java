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
package org.talend.dataquality.datamasking.functions.email;

import java.util.ArrayList;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This class masks the full domain name by the character X with the realistic format, for example, the email address
 * <i>"example@talend.com"</i> will be masked to <i>"example@XXXXXX.XXX"</i><br>
 * 
 * <b>See also:</b> {@link MaskEmail}
 */
public class MaskFullEmailDomainByX extends MaskEmailByX {

    private static final long serialVersionUID = 3570889674995454850L;

    /**
     * DOC qzhao Comment method "replaceFullDomainByX".<br>
     * 
     * Replaces all the domains by X with the original points
     * 
     * @param str
     * @return masked full domain address
     */
    @Override
    protected String maskEmail(String str) {
        StringBuilder sb = new StringBuilder(str);
        int splitAddress = str.indexOf('@');
        ArrayList<Integer> pointsPosition = getPointPostions(str, splitAddress);
        pointsPosition.add(str.length());
        Character maskingCrct = getMaskingCharacter();
        int c = splitAddress;

        for (Integer position : pointsPosition) {
            for (int i = c + 1; i < position; i++) {
                sb.setCharAt(i, maskingCrct);
            }
            c = position;
        }
        return sb.toString();
    }

}
