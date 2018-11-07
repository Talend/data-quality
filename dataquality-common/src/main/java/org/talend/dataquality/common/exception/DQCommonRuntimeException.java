package org.talend.dataquality.common.exception;

public class DQCommonRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8314753283830549683L;

    public DQCommonRuntimeException() {
        super();
    }

    public DQCommonRuntimeException(String message) {
        super(message);
    }

    public DQCommonRuntimeException(Throwable cause) {
        super(cause);
    }

    public DQCommonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DQCommonRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
