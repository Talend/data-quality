package org.talend.dataquality.common.character;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class AcronymTest {

    private String delimiters = "[[\\p{Punct}&&[^&/_#%$']]\\s\\u00A0\\u2007\\u202F\\u3000]+";

    private List<String> inputs = Arrays.asList("United Nations Educational, Scientific and Cultural Organization",
            "TriNitroToluene", "ASYNChronous transmission", "Easy-2-Read", "BElgium, NEtherlands and LUXembourg",
            "AT&T Mathematical Programming Language", "ante meridiem", "Input / Output",
            "American Telephone & Telegraph", "3COM corporation", "V5 User Adaption");

    @Test
    public void transformNullString() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_LETTERS_IGNORE_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.NONE)
                .build();

        assertEquals(StringUtils.EMPTY, acronym.transform(null));
    }

    @Test
    public void firstLettersIgnoreNumericsNoSeparators() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_LETTERS_IGNORE_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.NONE)
                .build();
        List<String> expected =
                Arrays.asList("UNESaCO", "T", "At", "ER", "BNaL", "AMPL", "am", "I/O", "AT&T", "Cc", "VUA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstLettersKeepNumericsNoSeparators() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_LETTERS_KEEP_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.NONE)
                .build();
        List<String> expected =
                Arrays.asList("UNESaCO", "T", "At", "E2R", "BNaL", "AMPL", "am", "I/O", "AT&T", "3c", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersIgnoreNumericsNoSeparators() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.NONE)
                .build();
        List<String> expected = Arrays.asList("UNESCO", "T", "A", "ER", "BNL", "AMPL", "", "I/O", "AT&T", "C", "VUA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersKeepNumericsNoSeparators() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_UPPER_CASE_LETTERS_KEEP_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.NONE)
                .build();
        List<String> expected = Arrays.asList("UNESCO", "T", "A", "E2R", "BNL", "AMPL", "", "I/O", "AT&T", "3", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void allUpperCaseLettersIgnoreNumericsNoSeparators() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.ALL_UPPER_CASE_LETTERS_IGNORE_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.NONE)
                .build();
        List<String> expected =
                Arrays.asList("UNESCO", "TNT", "ASYNC", "ER", "BENELUX", "AT&TMPL", "", "I/O", "AT&T", "COM", "VUA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void allUpperCaseLettersKeepNumericsNoSeparators() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.NONE)
                .build();
        List<String> expected =
                Arrays.asList("UNESCO", "TNT", "ASYNC", "E2R", "BENELUX", "AT&TMPL", "", "I/O", "AT&T", "3COM", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstLettersWithPeriods() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_LETTERS_IGNORE_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.PERIOD)
                .build();
        List<String> expected = Arrays.asList("U.N.E.S.a.C.O.", "T.", "A.t.", "E.R.", "B.N.a.L.", "A.M.P.L.", "a.m.",
                "I./.O.", "A.T.&.T.", "C.c.", "V.U.A.");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersWithSpaces() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.SPACE)
                .build();
        List<String> expected =
                Arrays.asList("U N E S C O", "T", "A", "E R", "B N L", "A M P L", "", "I / O", "A T & T", "C", "V U A");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void firstUpperCaseLettersWithDashes() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.DASH)
                .build();
        List<String> expected =
                Arrays.asList("U-N-E-S-C-O", "T", "A", "E-R", "B-N-L", "A-M-P-L", "", "I-/-O", "A-T-&-T", "C", "V-U-A");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }

    @Test
    public void allUpperCaseLettersWithSeparatorsAsIs() {
        Acronym acronym = Acronym
                .newBuilder()
                .withDelimiters(delimiters)
                .withContraction(Acronym.AcronymContraction.ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC)
                .withSeparator(Acronym.AcronymSeparator.AS_IS)
                .build();
        List<String> expected = Arrays.asList("UNE,SCO", "TNT", "ASYNC", "E-2-R", "BE,NELUX", "AT&TMPL", "", "I/O",
                "AT&T", "3COM", "V5UA");

        assert (expected.size() == inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(expected.get(i), acronym.transform(inputs.get(i)));
        }
    }
}
