package org.talend.dataquality.statistics.frequency.recognition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PatternMatcher {

    private PatternMatcher() {

    }

    public static boolean matchCharacterPattern(String value, String pattern) {

        if (value == null)
            return false;

        List<AbstractPatternRecognizer> recognizers = new ArrayList();

        //this list should be synchronized with the one used in CompositePatternFrequencyAnalyzer
        recognizers.add(new DateTimePatternRecognizer());
        recognizers.add(new EmptyPatternRecognizer());
        recognizers.add(new GenericCharPatternRecognizer());

        for (AbstractPatternRecognizer recognizer : recognizers) {
            if (recognizer.getValuePattern(value).contains(pattern))
                return true;
        }

        return false;
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
