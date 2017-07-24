package org.talend.dataquality.statistics.exception;

public class DQStatisticsRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -902782445327067011L;

    public DQStatisticsRuntimeException() {
    }

    public DQStatisticsRuntimeException(String message) {
        super(message);
    }

    public DQStatisticsRuntimeException(Throwable cause) {
        super(cause);
    }

    public DQStatisticsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DQStatisticsRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
