package org.talend.dataquality.semantic.exception;

public class DQRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -617440686721994515L;

    public DQRuntimeException() {
    }

    public DQRuntimeException(String message) {
        super(message);
    }

    public DQRuntimeException(Throwable cause) {
        super(cause);
    }

    public DQRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DQRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
