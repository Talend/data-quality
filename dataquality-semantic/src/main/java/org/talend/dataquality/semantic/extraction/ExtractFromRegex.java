package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

public class ExtractFromRegex extends ExtractFromSemanticType {

    private Pattern pattern;

    public ExtractFromRegex(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
        pattern = Pattern.compile(getCleanedRegex());
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {

        List<MatchedPart> matchedParts = new ArrayList<>();
        String inputValue = tokenizedField.getValue();

        Matcher matcher = pattern.matcher(inputValue);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            MatchedPart matchedPart = getMatch(tokenizedField, start, end);
            if (matchedPart != null)
                matchedParts.add(matchedPart);
        }
        return matchedParts;
    }

    private MatchedPart getMatch(TokenizedString tokenizedField, int start, int end) {
        MatchedPart matchedPart = null;
        String input = tokenizedField.getValue();
        boolean isOkStart = false;
        boolean isOkEnd = false;
        if (start == 0 || tokenizedField.getSeparatorPattern().matcher(input.substring(start - 1, start)).matches())
            isOkStart = true;
        if (end == input.length() || tokenizedField.getSeparatorPattern().matcher(input.substring(end, end + 1)).matches())
            isOkEnd = true;

        if (isOkStart && isOkEnd) {
            int startToken = getTokenNumber(input.substring(0, start));
            int endToken = getTokenNumber(input.substring(0, end)) - 1;
            matchedPart = new MatchedPart(tokenizedField, startToken, endToken);
        }
        return matchedPart;
    }

    private int getTokenNumber(String string) {
        TokenizedString tokenizedString = new TokenizedString(string);
        return tokenizedString.getTokens().size();
    }

    private String getCleanedRegex() {
        String cleaned = dicoSnapshot.getRegexClassifier().getPatternStringByCategoryId(semancticCategory.getId());
        if (cleaned.startsWith("^"))
            cleaned = cleaned.substring(1);

        if (cleaned.endsWith("$") && !isLitteral(cleaned))
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        return cleaned;
    }

    private boolean isLitteral(String regex) {
        int position = regex.length() - 2;
        while (position >= 0 && regex.charAt(position) == '\\') {
            position--;
        }
        return (regex.length() - position) % 2 == 1;
    }
}
