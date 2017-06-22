package com.gcoder.exception;

/**
 * Created by Administrator on 2017/6/17.
 */
public class TransactionException extends GException {

    public TransactionException() {}

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
