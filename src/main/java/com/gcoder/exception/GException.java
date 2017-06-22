package com.gcoder.exception;

/**
 * Created by gcoder on 2017/6/7.
 */
public class GException extends RuntimeException {

    public GException() {}

    public GException(String message) {
        super(message);
    }

    public GException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
