package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that contains one matched part of a given {@link #originalField}.
 *
 * A match consists of a list of tokens from the {@link #originalField}.
 * Their positions are stored in {@link #tokenPositions}.
 *
 * A match can be built using either a list of token positions or with a {@link #start} position and an {@link #end}.
 * The latter is possible because a match always consists of a list of contiguous tokens.
 *
 * This class implements {@link Comparable} so that
 * the list of matched parts can be sorted by length and priority.
 *
 * The priority is given by the position of the semantic category
 * in the list of all the semantic categories used to extract parts of the field.
 *
 * @author afournier
 */
public class MatchedPart implements Comparable<MatchedPart> {

    private final TokenizedString originalField;

    private final int start;

    private final int end;

    private final List<Integer> tokenPositions;

    private int priority;

    public MatchedPart(TokenizedString originalField, List<Integer> tokenPositions) {
        this.originalField = originalField;
        this.tokenPositions = tokenPositions;
        start = tokenPositions.get(0);
        end = tokenPositions.get(tokenPositions.size() - 1);
    }

    public MatchedPart(TokenizedString originalField, int start, int end) {
        checkBounds(start, end);
        this.originalField = originalField;
        this.start = start;
        this.end = end;
        tokenPositions = new ArrayList<>(end - start + 1);
        for (int i = start; i <= end; i++) {
            tokenPositions.add(i);
        }
    }

    private void checkBounds(int start, int end) {
        if (start < 0 || end < 0 || end < start) {
            throw new IllegalArgumentException("Bounds for match are incorrect : start = {}, end = {}" + start + end);
        }
    }

    @Override
    public String toString() {
        List<String> tokens = originalField.getTokens();
        List<String> separators = originalField.getSeparators();

        StringBuilder sb = new StringBuilder(tokens.get(start));
        for (int i = start; i < end; i++) {
            sb.append(separators.get(i)).append(tokens.get(i + 1));
        }
        return sb.toString();
    }

    private int getNumberOfTokens() {
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
     * The priority level is set via the method {@link #setPriority(int)}
     * used in {@link FieldExtractionFunction#extractFieldParts(String)}.
     *
     * @apiNote x.compareTo(y) == 0 does not imply x.equals(y).
     * @param o the object to compare the current object with.
     * @return -1 if the current object is more important than the argument,
     *          1 if it is less important,
     *          0 if there are of same priority.
     */
    @Override
    public int compareTo(MatchedPart o) {
        int compared = Integer.compare(o.getNumberOfTokens(), this.getNumberOfTokens());
        return compared == 0 ? Integer.compare(this.priority, o.priority) : compared;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MatchedPart)) {
            return false;
        }

        MatchedPart otherMatchedPart = (MatchedPart) o;
        return originalField.toString().equals(otherMatchedPart.originalField.toString())
                && tokenPositions.equals(otherMatchedPart.tokenPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalField, tokenPositions, priority);
    }
}
