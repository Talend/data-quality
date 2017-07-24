package org.talend.dataquality.semantic.exception;

public class DQSemanticRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -617440686721994515L;

    public DQSemanticRuntimeException() {
    }

    public DQSemanticRuntimeException(String message) {
        super(message);
    }

    public DQSemanticRuntimeException(Throwable cause) {
        super(cause);
    }

    public DQSemanticRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DQSemanticRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
