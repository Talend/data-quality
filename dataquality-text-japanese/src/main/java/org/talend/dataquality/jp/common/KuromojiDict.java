// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.jp.common;

public enum KuromojiDict {
    IPADIC("ipadic"),
    JUMANDIC("jumandic"),
    NAIST_JDIC("naist.jdic"),
    UNIDIC("unidic"),
    UNIDIC_KANAACCENT("unidic.kanaaccent");

    private final String dictName;

    public String getDictName() {
        return dictName;
    }

    KuromojiDict(String dictName) {
        this.dictName = dictName;
    }
}
