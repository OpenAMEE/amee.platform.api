package com.amee.platform.science;

/**
 * A RuntimeException for capturing exceptions arising from algorithm calculations.
 */
public class AlgorithmException extends RuntimeException {

    private int errorCode = -1;

    public AlgorithmException(String message) {
        super(message);
    }

    public AlgorithmException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getError() {
        return errorCode + ":" + getMessage();
    }
}