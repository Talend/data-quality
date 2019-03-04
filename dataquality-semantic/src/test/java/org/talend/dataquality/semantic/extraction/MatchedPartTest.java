package org.talend.dataquality.semantic.extraction;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MatchedPartTest {

    private TokenizedString field = new TokenizedString("Manchester United States Of America");

    @Test(expected = IllegalArgumentException.class)
    public void startOutOfBounds() {
        new MatchedPartDict(field, -1, 120, "fgdfgdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void endOutOfBounds() {
        new MatchedPartDict(field, -1, 120, "fgdfgdf");
    }

    @Test
    public void equals() {
        MatchedPart x = new MatchedPartDict(field, 0, 1, "Manchester United");
        MatchedPart y = new MatchedPartDict(field, 0, 1, "Manchester United");

        assertEquals(x, y);
        assertNotEquals(x, field);
    }

    @Test
    public void equalsNull() {
        assertNotEquals(field, null);
    }

    @Test
    public void smallerIsLessImportant() {
        MatchedPart x = new MatchedPartDict(field, 0, 1, "Manchester United");
        MatchedPart y = new MatchedPartDict(field, 1, 4, "United States Of America");
        assertEquals(1, x.compareTo(y));
    }

    @Test
    public void longerIsMoreImportant() {
        MatchedPart x = new MatchedPartDict(field, 0, 2, "Manchester United States");
        MatchedPart y = new MatchedPartDict(field, 1, 2, "United States");
        assertEquals(-1, x.compareTo(y));
    }

    @Test
    public void priorityBreaksEquality() {
        MatchedPart x = new MatchedPartDict(field, 0, 1, "Manchester United");
        MatchedPart y = new MatchedPartDict(field, 1, 2, "United States");
        x.setPriority(0);
        y.setPriority(1);
        assertEquals(-1, x.compareTo(y));
        x.setPriority(1);
        y.setPriority(0);
        assertEquals(1, x.compareTo(y));
    }

    @Test
    public void comparisonSymmetry() {
        MatchedPart x = new MatchedPartDict(field, 0, 1, "Manchester United");
        MatchedPart y = new MatchedPartDict(field, 1, 4, "United States Of America");
        assertEquals(x.compareTo(y), -y.compareTo(x));
    }

    @Test
    public void comparisonTransitivity() {
        MatchedPart x = new MatchedPartDict(field, 0, 1, "Manchester United");
        MatchedPart y = new MatchedPartDict(field, 1, 2, "United States");
        MatchedPart z = new MatchedPartDict(field, 3, 4, "Of America");
        x.setPriority(0);
        y.setPriority(0);
        z.setPriority(0);
        assertEquals(0, x.compareTo(z));
        assertEquals(0, x.compareTo(y));
        assertEquals(0, y.compareTo(z));
    }

    @Test
    public void doubleEquality() {
        MatchedPart x = new MatchedPartDict(field, 0, 1, "Manchester United");
        MatchedPart y = new MatchedPartDict(field, 0, 1, "Manchester United");
        MatchedPart z = new MatchedPartDict(field, 3, 4, "Of America");
        x.setPriority(0);
        y.setPriority(0);
        z.setPriority(0);
        assertEquals(0, x.compareTo(y));
        assertEquals(x.compareTo(z), y.compareTo(z));
    }
}
