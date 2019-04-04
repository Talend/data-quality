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

import org.talend.dataquality.datamasking.functions.address.MaskAddress;
import org.talend.dataquality.datamasking.functions.bank.GenerateAccountNumberFormat;
import org.talend.dataquality.datamasking.functions.bank.GenerateAccountNumberSimple;
import org.talend.dataquality.datamasking.functions.bank.GenerateCreditCardFormatLong;
import org.talend.dataquality.datamasking.functions.bank.GenerateCreditCardFormatString;
import org.talend.dataquality.datamasking.functions.bank.GenerateCreditCardLong;
import org.talend.dataquality.datamasking.functions.bank.GenerateCreditCardSimple;
import org.talend.dataquality.datamasking.functions.bank.GenerateCreditCardString;
import org.talend.dataquality.datamasking.functions.date.DateVariance;
import org.talend.dataquality.datamasking.functions.date.GenerateBetweenDate;
import org.talend.dataquality.datamasking.functions.date.KeepYear;
import org.talend.dataquality.datamasking.functions.email.MaskEmailLocalPartByX;
import org.talend.dataquality.datamasking.functions.email.MaskEmailLocalPartRandomly;
import org.talend.dataquality.datamasking.functions.email.MaskFullEmailDomainByX;
import org.talend.dataquality.datamasking.functions.email.MaskFullEmailDomainRandomly;
import org.talend.dataquality.datamasking.functions.email.MaskTopEmailDomainByX;
import org.talend.dataquality.datamasking.functions.email.MaskTopEmailDomainRandomly;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFile;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileHash;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileHashInteger;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileHashLong;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileHashString;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileInteger;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileLong;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileString;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromPattern;
import org.talend.dataquality.datamasking.functions.generation.GenerateSequenceDouble;
import org.talend.dataquality.datamasking.functions.generation.GenerateSequenceFloat;
import org.talend.dataquality.datamasking.functions.generation.GenerateSequenceInteger;
import org.talend.dataquality.datamasking.functions.generation.GenerateSequenceLong;
import org.talend.dataquality.datamasking.functions.generation.GenerateSequenceString;
import org.talend.dataquality.datamasking.functions.generation.GenerateUuid;
import org.talend.dataquality.datamasking.functions.misc.SetToNull;
import org.talend.dataquality.datamasking.functions.number.GenerateBetween;
import org.talend.dataquality.datamasking.functions.number.GenerateBetweenDouble;
import org.talend.dataquality.datamasking.functions.number.GenerateBetweenFloat;
import org.talend.dataquality.datamasking.functions.number.GenerateBetweenInteger;
import org.talend.dataquality.datamasking.functions.number.GenerateBetweenLong;
import org.talend.dataquality.datamasking.functions.number.GenerateBetweenString;
import org.talend.dataquality.datamasking.functions.number.NumericVariance;
import org.talend.dataquality.datamasking.functions.number.NumericVarianceDouble;
import org.talend.dataquality.datamasking.functions.number.NumericVarianceFloat;
import org.talend.dataquality.datamasking.functions.number.NumericVarianceInteger;
import org.talend.dataquality.datamasking.functions.number.NumericVarianceLong;
import org.talend.dataquality.datamasking.functions.phone.GeneratePhoneNumberFrench;
import org.talend.dataquality.datamasking.functions.phone.GeneratePhoneNumberGermany;
import org.talend.dataquality.datamasking.functions.phone.GeneratePhoneNumberJapan;
import org.talend.dataquality.datamasking.functions.phone.GeneratePhoneNumberUK;
import org.talend.dataquality.datamasking.functions.phone.GeneratePhoneNumberUS;
import org.talend.dataquality.datamasking.functions.phone.GenerateUniquePhoneNumberFr;
import org.talend.dataquality.datamasking.functions.phone.GenerateUniquePhoneNumberGermany;
import org.talend.dataquality.datamasking.functions.phone.GenerateUniquePhoneNumberJapan;
import org.talend.dataquality.datamasking.functions.phone.GenerateUniquePhoneNumberUk;
import org.talend.dataquality.datamasking.functions.phone.GenerateUniquePhoneNumberUs;
import org.talend.dataquality.datamasking.functions.ssn.GenerateSsnChn;
import org.talend.dataquality.datamasking.functions.ssn.GenerateSsnFr;
import org.talend.dataquality.datamasking.functions.ssn.GenerateSsnGermany;
import org.talend.dataquality.datamasking.functions.ssn.GenerateSsnIndia;
import org.talend.dataquality.datamasking.functions.ssn.GenerateSsnJapan;
import org.talend.dataquality.datamasking.functions.ssn.GenerateSsnUk;
import org.talend.dataquality.datamasking.functions.ssn.GenerateSsnUs;
import org.talend.dataquality.datamasking.functions.ssn.GenerateUniqueSsnChn;
import org.talend.dataquality.datamasking.functions.ssn.GenerateUniqueSsnFr;
import org.talend.dataquality.datamasking.functions.ssn.GenerateUniqueSsnGermany;
import org.talend.dataquality.datamasking.functions.ssn.GenerateUniqueSsnIndia;
import org.talend.dataquality.datamasking.functions.ssn.GenerateUniqueSsnJapan;
import org.talend.dataquality.datamasking.functions.ssn.GenerateUniqueSsnUk;
import org.talend.dataquality.datamasking.functions.ssn.GenerateUniqueSsnUs;
import org.talend.dataquality.datamasking.functions.text.BetweenIndexes;
import org.talend.dataquality.datamasking.functions.text.keep.BetweenIndexesKeep;
import org.talend.dataquality.datamasking.functions.text.keep.KeepFirstChars;
import org.talend.dataquality.datamasking.functions.text.keep.KeepFirstCharsInteger;
import org.talend.dataquality.datamasking.functions.text.keep.KeepFirstCharsLong;
import org.talend.dataquality.datamasking.functions.text.keep.KeepFirstDigitsAndReplaceOtherDigits;
import org.talend.dataquality.datamasking.functions.text.keep.KeepLastChars;
import org.talend.dataquality.datamasking.functions.text.keep.KeepLastCharsInteger;
import org.talend.dataquality.datamasking.functions.text.keep.KeepLastCharsLong;
import org.talend.dataquality.datamasking.functions.text.keep.KeepLastDigitsAndReplaceOtherDigits;
import org.talend.dataquality.datamasking.functions.text.keep.RemoveFirstChars;
import org.talend.dataquality.datamasking.functions.text.remove.BetweenIndexesRemove;
import org.talend.dataquality.datamasking.functions.text.remove.RemoveFirstCharsInteger;
import org.talend.dataquality.datamasking.functions.text.remove.RemoveFirstCharsLong;
import org.talend.dataquality.datamasking.functions.text.remove.RemoveFirstCharsString;
import org.talend.dataquality.datamasking.functions.text.remove.RemoveLastChars;
import org.talend.dataquality.datamasking.functions.text.remove.RemoveLastCharsInteger;
import org.talend.dataquality.datamasking.functions.text.remove.RemoveLastCharsLong;
import org.talend.dataquality.datamasking.functions.text.remove.RemoveLastCharsString;
import org.talend.dataquality.datamasking.functions.text.replace.BetweenIndexesReplace;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceAll;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceCharacters;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceFirstChars;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceFirstCharsInteger;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceFirstCharsLong;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceFirstCharsString;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceLastChars;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceLastCharsInteger;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceLastCharsLong;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceLastCharsString;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceNumeric;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceNumericDouble;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceNumericFloat;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceNumericInteger;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceNumericLong;
import org.talend.dataquality.datamasking.functions.text.replace.ReplaceNumericString;
import org.talend.dataquality.datamasking.semantic.GenerateBetweenNumeric;
import org.talend.dataquality.datamasking.semantic.GenerateFromFileStringProvided;
import org.talend.dataquality.datamasking.semantic.ReplaceCharactersWithGeneration;

/**
 * created by jgonzalez on 18 juin 2015. This enum stores all the functions that can be used in the component.
 *
 */
public enum FunctionType {

    /**
     * SET_TO_NULL
     */
    SET_TO_NULL(SetToNull.class, FunctionCategory.SET_TO_NULL),

    /**
     * EMAIL_MASKING
     */
    MASK_EMAIL(MaskEmailLocalPartByX.class, FunctionCategory.EMAIL_MASKING),
    MASK_EMAIL_LOCALPART_BY_X(MaskEmailLocalPartByX.class, FunctionCategory.EMAIL_MASKING),
    MASK_EMAIL_LOCALPART_RANDOMLY(MaskEmailLocalPartRandomly.class, FunctionCategory.EMAIL_MASKING),
    MASK_FULL_EMAIL_DOMAIN_BY_X(MaskFullEmailDomainByX.class, FunctionCategory.EMAIL_MASKING),
    MASK_FULL_EMAIL_DOMAIN_RANDOMLY(MaskFullEmailDomainRandomly.class, FunctionCategory.EMAIL_MASKING),
    MASK_TOP_LEVEL_EMAIL_DOMAIN_BY_X(MaskTopEmailDomainByX.class, FunctionCategory.EMAIL_MASKING),
    MASK_TOP_LEVEL_EMAIL_DOMAIN_RANDOMLY(MaskTopEmailDomainRandomly.class, FunctionCategory.EMAIL_MASKING),

    /**
     * SSN_GENERATION
     */
    GENERATE_SSN_CHINA(GenerateSsnChn.class, FunctionCategory.SSN_GENERATION),
    GENERATE_SSN_FRENCH(GenerateSsnFr.class, FunctionCategory.SSN_GENERATION),
    GENERATE_SSN_GERMANY(GenerateSsnGermany.class, FunctionCategory.SSN_GENERATION),
    GENERATE_SSN_INDIA(GenerateSsnIndia.class, FunctionCategory.SSN_GENERATION),
    GENERATE_SSN_JAPAN(GenerateSsnJapan.class, FunctionCategory.SSN_GENERATION),
    GENERATE_SSN_UK(GenerateSsnUk.class, FunctionCategory.SSN_GENERATION),
    GENERATE_SSN_US(GenerateSsnUs.class, FunctionCategory.SSN_GENERATION),

    /**
     * PHONE_GENERATION
     */
    GENERATE_PHONE_NUMBER_FRENCH(GeneratePhoneNumberFrench.class, FunctionCategory.PHONE_GENERATION),
    GENERATE_PHONE_NUMBER_GERMANY(GeneratePhoneNumberGermany.class, FunctionCategory.PHONE_GENERATION),
    GENERATE_PHONE_NUMBER_JAPAN(GeneratePhoneNumberJapan.class, FunctionCategory.PHONE_GENERATION),
    GENERATE_PHONE_NUMBER_UK(GeneratePhoneNumberUK.class, FunctionCategory.PHONE_GENERATION),
    GENERATE_PHONE_NUMBER_US(GeneratePhoneNumberUS.class, FunctionCategory.PHONE_GENERATION),

    /**
     * DATE_GENERATION
     */
    GENERATE_FROM_FILE_STRING_PROVIDED(GenerateFromFileStringProvided.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_PATTERN(GenerateFromPattern.class, FunctionCategory.DATA_GENERATION),
    GENERATE_UUID(GenerateUuid.class, FunctionCategory.DATA_GENERATION),

    GENERATE_SEQUENCE(null, FunctionCategory.DATA_GENERATION),
    GENERATE_SEQUENCE_DOUBLE(GenerateSequenceDouble.class, FunctionCategory.DATA_GENERATION),
    GENERATE_SEQUENCE_FLOAT(GenerateSequenceFloat.class, FunctionCategory.DATA_GENERATION),
    GENERATE_SEQUENCE_INT(GenerateSequenceInteger.class, FunctionCategory.DATA_GENERATION),
    GENERATE_SEQUENCE_LONG(GenerateSequenceLong.class, FunctionCategory.DATA_GENERATION),
    GENERATE_SEQUENCE_STRING(GenerateSequenceString.class, FunctionCategory.DATA_GENERATION),

    GENERATE_FROM_FILE(GenerateFromFile.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_FILE_INT(GenerateFromFileInteger.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_FILE_LONG(GenerateFromFileLong.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_FILE_STRING(GenerateFromFileString.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_FILE_HASH(GenerateFromFileHash.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_FILE_HASH_INT(GenerateFromFileHashInteger.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_FILE_HASH_LONG(GenerateFromFileHashLong.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_FILE_HASH_STRING(GenerateFromFileHashString.class, FunctionCategory.DATA_GENERATION),

    GENERATE_FROM_LIST(GenerateFromFile.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_LIST_INT(GenerateFromFileInteger.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_LIST_LONG(GenerateFromFileLong.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_LIST_STRING(GenerateFromFileString.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_LIST_HASH(GenerateFromFileHash.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_LIST_HASH_INT(GenerateFromFileHashInteger.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_LIST_HASH_LONG(GenerateFromFileHashLong.class, FunctionCategory.DATA_GENERATION),
    GENERATE_FROM_LIST_HASH_STRING(GenerateFromFileHashString.class, FunctionCategory.DATA_GENERATION),

    /**
     * SSN_MASKING
     */
    GENERATE_UNIQUE_SSN_CHINA(GenerateUniqueSsnChn.class, FunctionCategory.SSN_MASKING),
    GENERATE_UNIQUE_SSN_FRENCH(GenerateUniqueSsnFr.class, FunctionCategory.SSN_MASKING),
    GENERATE_UNIQUE_SSN_GERMANY(GenerateUniqueSsnGermany.class, FunctionCategory.SSN_MASKING),
    GENERATE_UNIQUE_SSN_INDIA(GenerateUniqueSsnIndia.class, FunctionCategory.SSN_MASKING),
    GENERATE_UNIQUE_SSN_JAPAN(GenerateUniqueSsnJapan.class, FunctionCategory.SSN_MASKING),
    GENERATE_UNIQUE_SSN_UK(GenerateUniqueSsnUk.class, FunctionCategory.SSN_MASKING),
    GENERATE_UNIQUE_SSN_US(GenerateUniqueSsnUs.class, FunctionCategory.SSN_MASKING),

    /**
     * PHONE_MASKING
     */
    GENERATE_UNIQUE_PHONE_NUMBER_FRENCH(GenerateUniquePhoneNumberFr.class, FunctionCategory.PHONE_MASKING),
    GENERATE_UNIQUE_PHONE_NUMBER_GERMANY(GenerateUniquePhoneNumberGermany.class, FunctionCategory.PHONE_MASKING),
    GENERATE_UNIQUE_PHONE_NUMBER_JAPAN(GenerateUniquePhoneNumberJapan.class, FunctionCategory.PHONE_MASKING),
    GENERATE_UNIQUE_PHONE_NUMBER_UK(GenerateUniquePhoneNumberUk.class, FunctionCategory.PHONE_MASKING),
    GENERATE_UNIQUE_PHONE_NUMBER_US(GenerateUniquePhoneNumberUs.class, FunctionCategory.PHONE_MASKING),

    /**
     * CHARACTER_HANDLING
     */
    REPLACE_ALL(ReplaceAll.class, FunctionCategory.CHARACTER_HANDLING),

    REPLACE_FIRST_CHARS(ReplaceFirstChars.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_FIRST_CHARS_INT(ReplaceFirstCharsInteger.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_FIRST_CHARS_LONG(ReplaceFirstCharsLong.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_FIRST_CHARS_STRING(ReplaceFirstCharsString.class, FunctionCategory.CHARACTER_HANDLING),

    REPLACE_LAST_CHARS(ReplaceLastChars.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_LAST_CHARS_INT(ReplaceLastCharsInteger.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_LAST_CHARS_LONG(ReplaceLastCharsLong.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_LAST_CHARS_STRING(ReplaceLastCharsString.class, FunctionCategory.CHARACTER_HANDLING),

    BETWEEN_INDEXES(BetweenIndexes.class, FunctionCategory.CHARACTER_HANDLING),
    BETWEEN_INDEXES_REPLACE(BetweenIndexesReplace.class, FunctionCategory.CHARACTER_HANDLING),
    BETWEEN_INDEXES_REPLACE_CONSISTENT(BetweenIndexesReplace.class, FunctionCategory.CHARACTER_HANDLING),
    BETWEEN_INDEXES_REPLACE_BIJECTIVE(BetweenIndexesReplace.class, FunctionCategory.CHARACTER_HANDLING),

    REPLACE_CHARACTERS(ReplaceCharacters.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_CHARACTERS_CONSISTENT(ReplaceCharacters.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_CHARACTERS_BIJECTIVE(ReplaceCharacters.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_CHARACTERS_WITH_GENERATION(ReplaceCharactersWithGeneration.class, FunctionCategory.CHARACTER_HANDLING),

    REPLACE_NUMERIC(ReplaceNumeric.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_NUMERIC_DOUBLE(ReplaceNumericDouble.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_NUMERIC_FLOAT(ReplaceNumericFloat.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_NUMERIC_INT(ReplaceNumericInteger.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_NUMERIC_LONG(ReplaceNumericLong.class, FunctionCategory.CHARACTER_HANDLING),
    REPLACE_NUMERIC_STRING(ReplaceNumericString.class, FunctionCategory.CHARACTER_HANDLING),

    KEEP_FIRST_AND_GENERATE(KeepFirstChars.class, FunctionCategory.CHARACTER_HANDLING),
    KEEP_FIRST_AND_GENERATE_INT(KeepFirstCharsInteger.class, FunctionCategory.CHARACTER_HANDLING),
    KEEP_FIRST_AND_GENERATE_LONG(KeepFirstCharsLong.class, FunctionCategory.CHARACTER_HANDLING),
    KEEP_FIRST_AND_GENERATE_STRING(KeepFirstDigitsAndReplaceOtherDigits.class, FunctionCategory.CHARACTER_HANDLING),

    KEEP_LAST_AND_GENERATE(KeepLastChars.class, FunctionCategory.CHARACTER_HANDLING),
    KEEP_LAST_AND_GENERATE_INT(KeepLastCharsInteger.class, FunctionCategory.CHARACTER_HANDLING),
    KEEP_LAST_AND_GENERATE_LONG(KeepLastCharsLong.class, FunctionCategory.CHARACTER_HANDLING),
    KEEP_LAST_AND_GENERATE_STRING(KeepLastDigitsAndReplaceOtherDigits.class, FunctionCategory.CHARACTER_HANDLING),

    BETWEEN_INDEXES_KEEP(BetweenIndexesKeep.class, FunctionCategory.CHARACTER_HANDLING),
    BETWEEN_INDEXES_REMOVE(BetweenIndexesRemove.class, FunctionCategory.CHARACTER_HANDLING),

    REMOVE_FIRST_CHARS(RemoveFirstChars.class, FunctionCategory.CHARACTER_HANDLING),
    REMOVE_FIRST_CHARS_INT(RemoveFirstCharsInteger.class, FunctionCategory.CHARACTER_HANDLING),
    REMOVE_FIRST_CHARS_LONG(RemoveFirstCharsLong.class, FunctionCategory.CHARACTER_HANDLING),
    REMOVE_FIRST_CHARS_STRING(RemoveFirstCharsString.class, FunctionCategory.CHARACTER_HANDLING),

    REMOVE_LAST_CHARS(RemoveLastChars.class, FunctionCategory.CHARACTER_HANDLING),
    REMOVE_LAST_CHARS_INT(RemoveLastCharsInteger.class, FunctionCategory.CHARACTER_HANDLING),
    REMOVE_LAST_CHARS_LONG(RemoveLastCharsLong.class, FunctionCategory.CHARACTER_HANDLING),
    REMOVE_LAST_CHARS_STRING(RemoveLastCharsString.class, FunctionCategory.CHARACTER_HANDLING),

    /**
     * DATE_HANDLING
     */
    DATE_VARIANCE(DateVariance.class, FunctionCategory.DATE_HANDLING),
    KEEP_YEAR(KeepYear.class, FunctionCategory.DATE_HANDLING),
    GENERATE_BETWEEN_DATE(GenerateBetweenDate.class, FunctionCategory.DATE_HANDLING),

    /**
     * NUMBER_HANDLING
     */
    GENERATE_BETWEEN(GenerateBetween.class, FunctionCategory.NUMBER_HANDLING),
    GENERATE_BETWEEN_DOUBLE(GenerateBetweenDouble.class, FunctionCategory.NUMBER_HANDLING),
    GENERATE_BETWEEN_FLOAT(GenerateBetweenFloat.class, FunctionCategory.NUMBER_HANDLING),
    GENERATE_BETWEEN_INT(GenerateBetweenInteger.class, FunctionCategory.NUMBER_HANDLING),
    GENERATE_BETWEEN_LONG(GenerateBetweenLong.class, FunctionCategory.NUMBER_HANDLING),
    GENERATE_BETWEEN_STRING(GenerateBetweenString.class, FunctionCategory.NUMBER_HANDLING),
    GENERATE_BETWEEN_NUMERIC(GenerateBetweenNumeric.class, FunctionCategory.NUMBER_HANDLING),
    NUMERIC_VARIANCE(NumericVariance.class, FunctionCategory.NUMBER_HANDLING),
    NUMERIC_VARIANCE_DOUBLE(NumericVarianceDouble.class, FunctionCategory.NUMBER_HANDLING),
    NUMERIC_VARIANCE_FLOAT(NumericVarianceFloat.class, FunctionCategory.NUMBER_HANDLING),
    NUMERIC_VARIANCE_INT(NumericVarianceInteger.class, FunctionCategory.NUMBER_HANDLING),
    NUMERIC_VARIANCE_LONG(NumericVarianceLong.class, FunctionCategory.NUMBER_HANDLING),

    /**
     * ADDRESS_MASKING
     */
    MASK_ADDRESS(MaskAddress.class, FunctionCategory.ADDRESS_MASKING),

    /**
     * CREDIT_CARD_GENERATION
     */
    GENERATE_CREDIT_CARD_FORMAT(GenerateCreditCardSimple.class, FunctionCategory.CREDIT_CARD_GENERATION),
    GENERATE_CREDIT_CARD_FORMAT_LONG(GenerateCreditCardFormatLong.class, FunctionCategory.CREDIT_CARD_GENERATION),
    GENERATE_CREDIT_CARD_FORMAT_STRING(GenerateCreditCardFormatString.class, FunctionCategory.CREDIT_CARD_GENERATION),
    GENERATE_CREDIT_CARD(GenerateCreditCardSimple.class, FunctionCategory.CREDIT_CARD_GENERATION),
    GENERATE_CREDIT_CARD_LONG(GenerateCreditCardLong.class, FunctionCategory.CREDIT_CARD_GENERATION),
    GENERATE_CREDIT_CARD_STRING(GenerateCreditCardString.class, FunctionCategory.CREDIT_CARD_GENERATION),

    /**
     * BANK_ACCOUNT_GENERATION
     */
    GENERATE_ACCOUNT_NUMBER(GenerateAccountNumberSimple.class, FunctionCategory.BANK_ACCOUNT_GENERATION),
    GENERATE_ACCOUNT_NUMBER_FORMAT(GenerateAccountNumberFormat.class, FunctionCategory.BANK_ACCOUNT_GENERATION);

    private final Class<?> clazz;

    private FunctionCategory category;

    FunctionType(Class<?> clzz, FunctionCategory category) {
        this.clazz = clzz;
        this.category = category;
    }

    /**
     * Getter for clazz.
     * 
     * @return The class of the function.
     */
    public Class<?> getClazz() {
        return this.clazz;
    }

    /**
     * Getter for category.
     * 
     * @return The category of the function.
     */
    public FunctionCategory getCategory() {
        return this.category;
    }

    protected static FunctionType getByName(String functionName) {
        try {
            return valueOf(functionName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
