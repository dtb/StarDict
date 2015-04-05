package com.davidthomasbernal.stardict.util;

/**
 * Created by david on 2/6/15.
 */
public class WordStringFormatException extends RuntimeException {
    public WordStringFormatException() {
    }

    public WordStringFormatException(String message) {
        super(message);
    }

    public WordStringFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WordStringFormatException(Throwable cause) {
        super(cause);
    }

    public WordStringFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
