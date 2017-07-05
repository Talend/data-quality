package org.talend.dataquality.standardization.exception;

public class DQException extends Exception {

    private static final long serialVersionUID = 4709869098107977869L;

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
