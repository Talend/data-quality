package org.talend.dataquality.semantic.extraction;

import java.util.List;

public class MatchedPart implements Comparable<MatchedPart> {

    private final TokenizedString originalField;

    private final List<Integer> tokenPositions;

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

    /**
     * Implement {@link Comparable} interface to make possible the sorting of a list of matches.
     * We want the matches to be sorted in ascending order of priority.
     * The rules are the following :
     * <ul>
     *     <li>A match with a greater number of tokens matched is more important.</li>
     *     <li>If the number of token is equal, then the priority level is compared.</li>
     * </ul>
     *
     * The priority level is set via the method {@link #setPriority(int)} used in {@link FieldExtractionFunction#extractFieldParts(String)}.
     *
     * @apiNote x.compareTo(y) == 0 does not imply x.equals(y).
     * @param o the object to compare the current object with.
     * @return -1 if the current object is more important than the argument, 1 if it is less important, 0 if there are of same priority.
     */
    @Override
    public int compareTo(MatchedPart o) {
        int compared = Integer.compare(o.getNumberOfTokens(), this.getNumberOfTokens());
        return compared == 0 ? Integer.compare(this.priority, o.priority) : compared;
    }
}
