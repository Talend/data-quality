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
package org.talend.dataquality.standardization.action;

import java.util.Random;

/**
 * DOC zshen class global comment. Detailled comment
 */
public interface ITalendStrConversionAction {

    public String run(String str, int modifCount, String extraParameter, final Random random);
}
