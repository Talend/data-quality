package org.talend.dataquality.common.character;

import static org.junit.Assert.assertEquals;
import static org.talend.dataquality.common.character.AbbreviationMode.ALL_UPPER_CASE_LETTERS_IGNORE_NUMERIC;
import static org.talend.dataquality.common.character.AbbreviationMode.ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC;
import static org.talend.dataquality.common.character.AbbreviationMode.FIRST_LETTERS_IGNORE_NUMERIC;
import static org.talend.dataquality.common.character.AbbreviationMode.FIRST_LETTERS_KEEP_NUMERIC;
import static org.talend.dataquality.common.character.AbbreviationMode.FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC;
import static org.talend.dataquality.common.character.AbbreviationMode.FIRST_UPPER_CASE_LETTERS_KEEP_NUMERIC;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.talend.dataquality.common.character.Acronym.AcronymSeparator;

public class AcronymTest {

    private String delimiters = "[[\\p{Punct}&&[^']]\\s\\u00A0\\u2007\\u202F\\u3000]+";

    private List<String> inputs = Arrays.asList("United Nations Educational, Scientific and Cultural Organization",
            "TriNitroToluene", "ASYNChronous transmission", "Easy-2-Read", "BElgium, NEtherlands and LUXembourg",
            "AT&T Mathematical Programming Language", "ante meridiem", "Input / Output",
            "American Telephone & Telegraph", "3COM corporation", "V5 User Adaption");

    @Test
    public void transformNullString() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.NONE);

        assertEquals(StringUtils.EMPTY, acronym.transform(null));
    }

    @Test
    public void noTokenAppliedWithSeparator() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.DASH);

        assertEquals(StringUtils.EMPTY, acronym.transform("06 01 02 03 04"));
    }

    @Test
    public void noTokenAppliedKeepSpecialChars() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.KEEP_SPECIAL_CHARS);

        assertEquals(StringUtils.EMPTY, acronym.transform("06.01.02.03.04"));
        assertEquals(StringUtils.EMPTY, acronym.transform("06-01-02-03-04"));
    }

    @Test
    public void firstTokenNotApplied() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.DASH);

        assertEquals("D-O-F", acronym.transform("3 Degrees Of Freedom"));
    }

    @Test
    public void middleTokenNotApplied() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.DASH);

        assertEquals("D-O-F", acronym.transform("Degrees 3 Of Freedom"));
    }

    @Test
    public void lastTokenNotApplied() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.DASH);

        assertEquals("D-O-F", acronym.transform("Degrees Of Freedom 3"));
    }

    @Test
    public void firstLettersIgnoreNumericsNoSeparators() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.NONE);
        List<String> expected =
                Arrays.asList("UNESaCO", "T", "At", "ER", "BNaL", "ATMPL", "am", "IO", "ATT", "Cc", "VUA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstLettersKeepNumericsNoSeparators() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_KEEP_NUMERIC, AcronymSeparator.NONE);
        List<String> expected =
                Arrays.asList("UNESaCO", "T", "At", "E2R", "BNaL", "ATMPL", "am", "IO", "ATT", "3c", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersIgnoreNumericsNoSeparators() {
        Acronym acronym = buildAcronym(FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC, AcronymSeparator.NONE);
        List<String> expected = Arrays.asList("UNESCO", "T", "A", "ER", "BNL", "ATMPL", "", "IO", "ATT", "C", "VUA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersKeepNumericsNoSeparators() {
        Acronym acronym = buildAcronym(FIRST_UPPER_CASE_LETTERS_KEEP_NUMERIC, AcronymSeparator.NONE);
        List<String> expected = Arrays.asList("UNESCO", "T", "A", "E2R", "BNL", "ATMPL", "", "IO", "ATT", "3", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void allUpperCaseLettersIgnoreNumericsNoSeparators() {
        Acronym acronym = buildAcronym(ALL_UPPER_CASE_LETTERS_IGNORE_NUMERIC, AcronymSeparator.NONE);
        List<String> expected =
                Arrays.asList("UNESCO", "TNT", "ASYNC", "ER", "BENELUX", "ATTMPL", "", "IO", "ATT", "COM", "VUA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void allUpperCaseLettersKeepNumericsNoSeparators() {
        Acronym acronym = buildAcronym(ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC, AcronymSeparator.NONE);
        List<String> expected =
                Arrays.asList("UNESCO", "TNT", "ASYNC", "E2R", "BENELUX", "ATTMPL", "", "IO", "ATT", "3COM", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstLettersWithPeriods() {
        Acronym acronym = buildAcronym(FIRST_LETTERS_IGNORE_NUMERIC, AcronymSeparator.PERIOD);
        List<String> expected = Arrays.asList("U.N.E.S.a.C.O.", "T.", "A.t.", "E.R.", "B.N.a.L.", "A.T.M.P.L.", "a.m.",
                "I.O.", "A.T.T.", "C.c.", "V.U.A.");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersWithSpaces() {
        Acronym acronym = buildAcronym(FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC, AcronymSeparator.SPACE);
        List<String> expected =
                Arrays.asList("U N E S C O", "T", "A", "E R", "B N L", "A T M P L", "", "I O", "A T T", "C", "V U A");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersWithDashes() {
        Acronym acronym = buildAcronym(FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC, AcronymSeparator.DASH);
        List<String> expected =
                Arrays.asList("U-N-E-S-C-O", "T", "A", "E-R", "B-N-L", "A-T-M-P-L", "", "I-O", "A-T-T", "C", "V-U-A");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void allUpperCaseLettersKeepSpecialChars() {
        Acronym acronym = buildAcronym(ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC, AcronymSeparator.KEEP_SPECIAL_CHARS);

        List<String> expected = Arrays.asList("UNESCO", "TNT", "ASYNC", "E-2-R", "BENELUX", "AT&TMPL", "", "I/O",
                "AT&T", "3COM", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    private Acronym buildAcronym(AbbreviationMode abbrevMode, AcronymSeparator separator) {
        return Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withAbbreviationMode(abbrevMode)
                .withSeparator(separator)
                .build();
    }
}
