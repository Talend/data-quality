package org.talend.dataquality.record.linkage.exception;

public class DQRecordLinkageRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 5583915593872173206L;

    public DQRecordLinkageRuntimeException() {
    }

    public DQRecordLinkageRuntimeException(String message) {
        super(message);
    }

    public DQRecordLinkageRuntimeException(Throwable cause) {
        super(cause);
    }

    public DQRecordLinkageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DQRecordLinkageRuntimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
