package org.talend.dataquality.statistics.frequency.recognition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.talend.dataquality.datamasking.semantic.DatePatternHelper;

public class PatternMatcher {

    static List<AbstractPatternRecognizer> patternRecognizerList;

    static DateTimePatternRecognizer dateTimePatternRecognizer;

    static EmptyPatternRecognizer emptyPatternRecognizer;

    static GenericCharPatternRecognizer genericCharPatternRecognizer;

    static Set<String> datePatterns;

    private PatternMatcher() {

    }

    static {
        //this list should be synchronized with the one used in CompositePatternFrequencyAnalyzer
        dateTimePatternRecognizer = new DateTimePatternRecognizer();
        emptyPatternRecognizer = new EmptyPatternRecognizer();
        genericCharPatternRecognizer = new GenericCharPatternRecognizer();
        patternRecognizerList = new ArrayList<>();
        patternRecognizerList.add(new EmptyPatternRecognizer());
        patternRecognizerList.add(new DateTimePatternRecognizer());
        patternRecognizerList.add(new GenericCharPatternRecognizer());
        datePatterns = DatePatternHelper.getDatePatterns();
    }

    public static boolean matchCharDatePattern(String value, String pattern) {

        if (value == null)
            return false;

        Set<String> patterns = new HashSet<>();
        for (AbstractPatternRecognizer patternRecognizer : patternRecognizerList)
            patterns.addAll(patternRecognizer.getValuePattern(value));

        // --- a value that match a date pattern but with a non-date pattern must not match
        if (!isDatePattern(pattern) && containsDatePattern(patterns))
            return false;

        return patterns.contains(pattern);
    }

    private static boolean containsDatePattern(Set<String> patterns) {
        for (String pattern : patterns)
            if (datePatterns.contains(pattern))
                return true;
        return false;
    }

    private static boolean isDatePattern(String pattern) {
        return datePatterns.contains(pattern);
    }

    public static boolean matchWordPattern(String value, String pattern) {
        return matchWordPattern(value, pattern, false);
    }

    public static boolean matchWordPattern(String value, String pattern, boolean caseSensitive) {
        if (caseSensitive)
            return TypoUnicodePatternRecognizer.withCase().matchPattern(value, pattern);
        else
            return TypoUnicodePatternRecognizer.noCase().matchPattern(value, pattern);
    }
}
