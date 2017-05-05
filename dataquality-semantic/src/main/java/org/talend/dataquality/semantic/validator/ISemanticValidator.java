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
package org.talend.dataquality.semantic.validator;

import java.io.Serializable;

/**
 * Created by sizhaoliu on 16.03.15.
 */
public interface ISemanticValidator extends Serializable {

    public boolean isValid(String str);

    public boolean isValid(String str, boolean caseSensitive);
}
