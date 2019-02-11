package org.talend.dataquality.semantic.extraction;

import java.util.List;

public class MatchedPart implements Comparable<MatchedPart> {

    private TokenizedString originalField;

    private List<Integer> tokenPositions;

    private int priority;

    public MatchedPart(TokenizedString originalField, List<Integer> tokenPositions) {
        this.originalField = originalField;
        this.tokenPositions = tokenPositions;
    }

    @Override
    public String toString() {
        List<String> tokens = originalField.getTokens();
        List<String> separators = originalField.getSeparators();

        StringBuilder sb = new StringBuilder(tokens.get(tokenPositions.get(0)));
        for (int i = 1; i < tokenPositions.size(); i++) {
            sb.append(separators.get(tokenPositions.get(i - 1))).append(tokens.get(tokenPositions.get(i)));
        }
        return sb.toString();
    }

    public int getNumberOfTokens() {
        return tokenPositions.size();
    }

    public List<Integer> getTokenPositions() {
        return tokenPositions;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(MatchedPart o) {
        int compared = Integer.compare(o.getNumberOfTokens(), this.getNumberOfTokens());
        return compared == 0 ? Integer.compare(this.priority, o.priority) : compared;
    }
}
