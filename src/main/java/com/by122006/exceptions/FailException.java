package com.by122006.exceptions;

public class FailException extends RuntimeException {

    public int error;


    public FailException(int error, String message) {
        super(message);
        this.error = error;
    }
    public FailException(String message) {
        super(message);
        this.error = Integer.MIN_VALUE;
    }
}
