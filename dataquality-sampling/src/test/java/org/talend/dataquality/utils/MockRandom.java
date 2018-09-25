package org.talend.dataquality.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Mock for random tests
 */
public class MockRandom extends Random {

    private int next = 0;

    @Override
    public int nextInt(int bound) {
        return nextInt() % bound;
    }

    @Override
    public int nextInt() {
        return next++;
    }

    @Test
    public void nextIntTest() {
        Random random = new MockRandom();
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, random.nextInt());
        }
    }

    @Test
    public void nextIntBoundTest() {
        Random random = new MockRandom();
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, random.nextInt(10));
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, random.nextInt(10));
        }
    }

    public void setNext(int next) {
        this.next = next;
    }
}
