package com.davidthomasbernal.stardict.parsers;

/**
 * Created by david on 2/6/15.
 */
public class IndexFormatException extends RuntimeException {
    public IndexFormatException() {
    }

    public IndexFormatException(String message) {
        super(message);
    }

    public IndexFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexFormatException(Throwable cause) {
        super(cause);
    }

    public IndexFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
