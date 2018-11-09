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
package org.talend.dataquality.datamasking;

import org.talend.dataquality.datamasking.functions.*;
import org.talend.dataquality.datamasking.semantic.GenerateBetweenNumeric;
import org.talend.dataquality.datamasking.semantic.GenerateFromFileStringProvided;
import org.talend.dataquality.datamasking.semantic.ReplaceCharactersWithGeneration;

/**
 * created by jgonzalez on 18 juin 2015. This enum stores all the functions that can be used in the component.
 *
 */
public enum FunctionType {
    BETWEEN_INDEXES(BetweenIndexes.class),
    BETWEEN_INDEXES_REPLACE(BetweenIndexesReplace.class),
    BETWEEN_INDEXES_KEEP(BetweenIndexesKeep.class),
    BETWEEN_INDEXES_REMOVE(BetweenIndexesRemove.class),
    DATE_VARIANCE(DateVariance.class),
    GENERATE_ACCOUNT_NUMBER(GenerateAccountNumberSimple.class),
    GENERATE_ACCOUNT_NUMBER_FORMAT(GenerateAccountNumberFormat.class),
    GENERATE_BETWEEN(GenerateBetween.class),
    GENERATE_BETWEEN_DATE(GenerateBetweenDate.class),
    GENERATE_BETWEEN_DOUBLE(GenerateBetweenDouble.class),
    GENERATE_BETWEEN_FLOAT(GenerateBetweenFloat.class),
    GENERATE_BETWEEN_INT(GenerateBetweenInteger.class),
    GENERATE_BETWEEN_LONG(GenerateBetweenLong.class),
    GENERATE_BETWEEN_STRING(GenerateBetweenString.class),
    GENERATE_BETWEEN_NUMERIC(GenerateBetweenNumeric.class),
    GENERATE_CREDIT_CARD_FORMAT(GenerateCreditCardSimple.class),
    GENERATE_CREDIT_CARD_FORMAT_LONG(GenerateCreditCardFormatLong.class),
    GENERATE_CREDIT_CARD_FORMAT_STRING(GenerateCreditCardFormatString.class),
    GENERATE_CREDIT_CARD(GenerateCreditCardSimple.class),
    GENERATE_CREDIT_CARD_LONG(GenerateCreditCardLong.class),
    GENERATE_CREDIT_CARD_STRING(GenerateCreditCardString.class),
    GENERATE_FROM_FILE(GenerateFromFile.class),
    GENERATE_FROM_FILE_INT(GenerateFromFileInteger.class),
    GENERATE_FROM_FILE_LONG(GenerateFromFileLong.class),
    GENERATE_FROM_FILE_STRING(GenerateFromFileString.class),
    GENERATE_FROM_FILE_STRING_PROVIDED(GenerateFromFileStringProvided.class),
    GENERATE_FROM_FILE_HASH(GenerateFromFileHash.class),
    GENERATE_FROM_FILE_HASH_INT(GenerateFromFileHashInteger.class),
    GENERATE_FROM_FILE_HASH_LONG(GenerateFromFileHashLong.class),
    GENERATE_FROM_FILE_HASH_STRING(GenerateFromFileHashString.class),
    GENERATE_FROM_LIST(GenerateFromFile.class),
    GENERATE_FROM_LIST_INT(GenerateFromFileInteger.class),
    GENERATE_FROM_LIST_LONG(GenerateFromFileLong.class),
    GENERATE_FROM_LIST_STRING(GenerateFromFileString.class),
    GENERATE_FROM_LIST_HASH(GenerateFromFileHash.class),
    GENERATE_FROM_LIST_HASH_INT(GenerateFromFileHashInteger.class),
    GENERATE_FROM_LIST_HASH_LONG(GenerateFromFileHashLong.class),
    GENERATE_FROM_LIST_HASH_STRING(GenerateFromFileHashString.class),
    GENERATE_FROM_PATTERN(GenerateFromPattern.class),
    GENERATE_PHONE_NUMBER_FRENCH(GeneratePhoneNumberFrench.class),
    GENERATE_PHONE_NUMBER_GERMANY(GeneratePhoneNumberGermany.class),
    GENERATE_PHONE_NUMBER_JAPAN(GeneratePhoneNumberJapan.class),
    GENERATE_PHONE_NUMBER_UK(GeneratePhoneNumberUK.class),
    GENERATE_PHONE_NUMBER_US(GeneratePhoneNumberUS.class),
    GENERATE_UNIQUE_PHONE_NUMBER_FRENCH(GenerateUniquePhoneNumberFr.class),
    GENERATE_UNIQUE_PHONE_NUMBER_GERMANY(GenerateUniquePhoneNumberGermany.class),
    GENERATE_UNIQUE_PHONE_NUMBER_JAPAN(GenerateUniquePhoneNumberJapan.class),
    GENERATE_UNIQUE_PHONE_NUMBER_UK(GenerateUniquePhoneNumberUk.class),
    GENERATE_UNIQUE_PHONE_NUMBER_US(GenerateUniquePhoneNumberUs.class),
    GENERATE_SEQUENCE(null),
    GENERATE_SEQUENCE_DOUBLE(GenerateSequenceDouble.class),
    GENERATE_SEQUENCE_FLOAT(GenerateSequenceFloat.class),
    GENERATE_SEQUENCE_INT(GenerateSequenceInteger.class),
    GENERATE_SEQUENCE_LONG(GenerateSequenceLong.class),
    GENERATE_SEQUENCE_STRING(GenerateSequenceString.class),
    GENERATE_SSN_CHINA(GenerateSsnChn.class),
    GENERATE_SSN_FRENCH(GenerateSsnFr.class),
    GENERATE_SSN_GERMANY(GenerateSsnGermany.class),
    GENERATE_SSN_INDIA(GenerateSsnIndia.class),
    GENERATE_SSN_JAPAN(GenerateSsnJapan.class),
    GENERATE_SSN_UK(GenerateSsnUk.class),
    GENERATE_SSN_US(GenerateSsnUs.class),
    GENERATE_UNIQUE_SSN_FRENCH(GenerateUniqueSsnFr.class),
    GENERATE_UNIQUE_SSN_CHINA(GenerateUniqueSsnChn.class),
    GENERATE_UNIQUE_SSN_JAPAN(GenerateUniqueSsnJapan.class),
    GENERATE_UNIQUE_SSN_UK(GenerateUniqueSsnUk.class),
    GENERATE_UNIQUE_SSN_US(GenerateUniqueSsnUs.class),
    GENERATE_UNIQUE_SSN_GERMANY(GenerateUniqueSsnGermany.class),
    GENERATE_UNIQUE_SSN_INDIA(GenerateUniqueSsnIndia.class),
    GENERATE_UUID(GenerateUuid.class),
    KEEP_FIRST_AND_GENERATE(KeepFirstChars.class),
    KEEP_FIRST_AND_GENERATE_INT(KeepFirstCharsInteger.class),
    KEEP_FIRST_AND_GENERATE_LONG(KeepFirstCharsLong.class),
    KEEP_FIRST_AND_GENERATE_STRING(KeepFirstDigitsAndReplaceOtherDigits.class),
    KEEP_LAST_AND_GENERATE(KeepLastChars.class),
    KEEP_LAST_AND_GENERATE_INT(KeepLastCharsInteger.class),
    KEEP_LAST_AND_GENERATE_LONG(KeepLastCharsLong.class),
    KEEP_LAST_AND_GENERATE_STRING(KeepLastDigitsAndReplaceOtherDigits.class),
    KEEP_YEAR(KeepYear.class),
    MASK_ADDRESS(MaskAddress.class),
    MASK_EMAIL(MaskEmailLocalPartByX.class),
    MASK_EMAIL_LOCALPART_RANDOMLY(MaskEmailLocalPartRandomly.class),
    MASK_EMAIL_LOCALPART_BY_X(MaskEmailLocalPartByX.class),
    MASK_FULL_EMAIL_DOMAIN_BY_X(MaskFullEmailDomainByX.class),
    MASK_FULL_EMAIL_DOMAIN_RANDOMLY(MaskFullEmailDomainRandomly.class),
    MASK_TOP_LEVEL_EMAIL_DOMAIN_BY_X(MaskTopEmailDomainByX.class),
    MASK_TOP_LEVEL_EMAIL_DOMAIN_RANDOMLY(MaskTopEmailDomainRandomly.class),
    NUMERIC_VARIANCE(NumericVariance.class),
    NUMERIC_VARIANCE_DOUBLE(NumericVarianceDouble.class),
    NUMERIC_VARIANCE_FLOAT(NumericVarianceFloat.class),
    NUMERIC_VARIANCE_INT(NumericVarianceInteger.class),
    NUMERIC_VARIANCE_LONG(NumericVarianceLong.class),
    REMOVE_FIRST_CHARS(RemoveFirstChars.class),
    REMOVE_FIRST_CHARS_INT(RemoveFirstCharsInteger.class),
    REMOVE_FIRST_CHARS_LONG(RemoveFirstCharsLong.class),
    REMOVE_FIRST_CHARS_STRING(RemoveFirstCharsString.class),
    REMOVE_LAST_CHARS(RemoveLastChars.class),
    REMOVE_LAST_CHARS_INT(RemoveLastCharsInteger.class),
    REMOVE_LAST_CHARS_LONG(RemoveLastCharsLong.class),
    REMOVE_LAST_CHARS_STRING(RemoveLastCharsString.class),
    REPLACE_ALL(ReplaceAll.class),
    REPLACE_CHARACTERS(ReplaceCharacters.class),
    REPLACE_CHARACTERS_WITH_GENERATION(ReplaceCharactersWithGeneration.class),
    REPLACE_FIRST_CHARS(ReplaceFirstChars.class),
    REPLACE_FIRST_CHARS_INT(ReplaceFirstCharsInteger.class),
    REPLACE_FIRST_CHARS_LONG(ReplaceFirstCharsLong.class),
    REPLACE_FIRST_CHARS_STRING(ReplaceFirstCharsString.class),
    REPLACE_LAST_CHARS(ReplaceLastChars.class),
    REPLACE_LAST_CHARS_INT(ReplaceLastCharsInteger.class),
    REPLACE_LAST_CHARS_LONG(ReplaceLastCharsLong.class),
    REPLACE_LAST_CHARS_STRING(ReplaceLastCharsString.class),
    REPLACE_NUMERIC(ReplaceNumeric.class),
    REPLACE_NUMERIC_DOUBLE(ReplaceNumericDouble.class),
    REPLACE_NUMERIC_FLOAT(ReplaceNumericFloat.class),
    REPLACE_NUMERIC_INT(ReplaceNumericInteger.class),
    REPLACE_NUMERIC_LONG(ReplaceNumericLong.class),
    REPLACE_NUMERIC_STRING(ReplaceNumericString.class),
    SET_TO_NULL(SetToNull.class);

    private final Class<?> clazz;

    FunctionType(Class<?> clzz) {
        this.clazz = clzz;
    }

    /**
     * Getter for clazz.
     * 
     * @return The class of the function.
     */
    public Class<?> getClazz() {
        return this.clazz;
    }

}
