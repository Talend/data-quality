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
package org.talend.dataquality.semantic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.dataquality.semantic.api.DictionaryUtilsTest;
import org.talend.dataquality.semantic.api.LocalDictionaryCacheTest;
import org.talend.dataquality.semantic.broadcast.BroadcastIndexObjectTest;
import org.talend.dataquality.semantic.broadcast.BroadcastRegexObjectTest;
import org.talend.dataquality.semantic.broadcast.TdqCategoriesFactoryTest;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeserTest;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifierTest;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRegexValidatorTest;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifierTest;
import org.talend.dataquality.semantic.datamasking.ValueDataMaskerTest;
import org.talend.dataquality.semantic.validator.impl.DEPhoneNumberValidatorTest;
import org.talend.dataquality.semantic.validator.impl.FRPhoneNumberValidatorTest;
import org.talend.dataquality.semantic.validator.impl.SedolValidatorTest;
import org.talend.dataquality.semantic.validator.impl.UKPhoneNumberValidatorTest;
import org.talend.dataquality.semantic.validator.impl.USPhoneNumberValidatorTest;

/**
 * DOC yyin class global comment. Detailled comment
 */
@RunWith(Suite.class)
@SuiteClasses({ CategoryRecognizerTest.class, DictionaryUtilsTest.class, LocalDictionaryCacheTest.class,
        RespectiveCategoryRecognizerTest.class, UserDefinedClassifierTest.class, UserDefinedRegexValidatorTest.class,
        UDCategorySerDeserTest.class, DataDictFieldClassifierTest.class, SedolValidatorTest.class, ValueDataMaskerTest.class,
        DEPhoneNumberValidatorTest.class, FRPhoneNumberValidatorTest.class, SedolValidatorTest.class,
        UKPhoneNumberValidatorTest.class, USPhoneNumberValidatorTest.class, BroadcastIndexObjectTest.class,
        BroadcastRegexObjectTest.class, TdqCategoriesFactoryTest.class })
public class AllSemanticTests {

    public static final long RANDOM_SEED = 12345678;
}
