package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

public class ExtractFromRegex extends ExtractFromSemanticType {

    public ExtractFromRegex(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {

        List<MatchedPart> matchedParts = new ArrayList<>();
        String inputValue = tokenizedField.getValue();
        String cleanedRegex = getCleanedRegex();

        Pattern pattern = Pattern.compile(cleanedRegex);
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
        if (end == input.length() - 1 || tokenizedField.getSeparatorPattern().matcher(input.substring(end, end + 1)).matches())
            isOkEnd = true;

        if (isOkStart && isOkEnd) {
            int startToken = getTokenNumber(input.substring(0, start + 1));
            int endToken = getTokenNumber(input.substring(0, end));
            matchedPart = new MatchedPart(tokenizedField, startToken, endToken);
        }
        return matchedPart;
    }

    private int getTokenNumber(String string) {
        TokenizedString tokenizedString = new TokenizedString(string);
        return tokenizedString.getTokens().size() - 1;
    }

    private String getCleanedRegex() {
        String cleaned = semancticCategory.getRegEx().getValidator().getPatternString();
        if (cleaned.startsWith("^"))
            cleaned = cleaned.substring(1);

        if (cleaned.endsWith("$") && !isLitteral(cleaned))
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        return cleaned;
    }

    private boolean isLitteral(String regex) {
        // If removing the last $ makes the regex invalid, then it was a litteral $
        regex = regex.substring(0, regex.length() - 1);
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException p) {
            return true;
        }
        return false;
    }
}
