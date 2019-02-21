package org.talend.dataquality.semantic.extraction;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MatchedPartTest {

    private TokenizedString field = new TokenizedString("Manchester United States Of America");

    @Test(expected = IllegalArgumentException.class)
    public void outOfBounds() {
        new MatchedPartDict(field, -1, 120);
    }

    @Test
    public void equals() {
        MatchedPart x = new MatchedPartDict(field, 0, 1);
        MatchedPart y = new MatchedPartDict(field, 0, 1);

        assertEquals(x, y);
        assertNotEquals(x, field);
    }

    @Test
    public void equalsNull() {
        assertNotEquals(field, null);
    }

    @Test
    public void smallerIsLessImportant() {
        MatchedPart x = new MatchedPartDict(field, 0, 1);
        MatchedPart y = new MatchedPartDict(field, 1, 4);
        assertEquals(1, x.compareTo(y));
    }

    @Test
    public void longerIsMoreImportant() {
        MatchedPart x = new MatchedPartDict(field, 0, 2);
        MatchedPart y = new MatchedPartDict(field, 1, 2);
        assertEquals(-1, x.compareTo(y));
    }

    @Test
    public void priorityBreaksEquality() {
        MatchedPart x = new MatchedPartDict(field, 0, 1);
        MatchedPart y = new MatchedPartDict(field, 1, 2);
        x.setPriority(0);
        y.setPriority(1);
        assertEquals(-1, x.compareTo(y));
        x.setPriority(1);
        y.setPriority(0);
        assertEquals(1, x.compareTo(y));
    }

    @Test
    public void comparisonSymmetry() {
        MatchedPart x = new MatchedPartDict(field, 0, 1);
        MatchedPart y = new MatchedPartDict(field, 1, 4);
        assertEquals(x.compareTo(y), -y.compareTo(x));
    }

    @Test
    public void comparisonTransitivity() {
        MatchedPart x = new MatchedPartDict(field, 0, 1);
        MatchedPart y = new MatchedPartDict(field, 1, 2);
        MatchedPart z = new MatchedPartDict(field, 3, 4);
        x.setPriority(0);
        y.setPriority(0);
        z.setPriority(0);
        assertEquals(0, x.compareTo(z));
        assertEquals(0, x.compareTo(y));
        assertEquals(0, y.compareTo(z));
    }

    @Test
    public void doubleEquality() {
        MatchedPart x = new MatchedPartDict(field, 0, 1);
        MatchedPart y = new MatchedPartDict(field, 0, 1);
        MatchedPart z = new MatchedPartDict(field, 3, 4);
        x.setPriority(0);
        y.setPriority(0);
        z.setPriority(0);
        assertEquals(0, x.compareTo(y));
        assertEquals(x.compareTo(z), y.compareTo(z));
    }
}
