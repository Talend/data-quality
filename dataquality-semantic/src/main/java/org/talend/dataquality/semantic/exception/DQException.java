package org.talend.dataquality.semantic.exception;

public class DQException extends Exception {

    public DQException() {
    }

    public DQException(String message) {
        super(message);
    }

    public DQException(Throwable cause) {
        super(cause);
    }

    public DQException(String message, Throwable cause) {
        super(message, cause);
    }

    public DQException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
