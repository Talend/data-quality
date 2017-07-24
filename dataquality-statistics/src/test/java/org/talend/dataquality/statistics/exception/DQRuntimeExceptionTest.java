package org.talend.dataquality.statistics.exception;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DQRuntimeExceptionTest {

    @Test
    public void testDQRuntimeException() {
        try {
            throw new DQRuntimeException();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDQRuntimeExceptionString() {
        try {
            throw new DQRuntimeException("msg");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDQRuntimeExceptionThrowable() {
        try {
            throw new DQRuntimeException(new Exception());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDQRuntimeExceptionStringThrowable() {
        try {
            throw new DQRuntimeException("msg", new Exception());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDQRuntimeExceptionStringThrowableBooleanBoolean() {
        try {
            throw new DQRuntimeException("msg", new Exception(), false, false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

}
