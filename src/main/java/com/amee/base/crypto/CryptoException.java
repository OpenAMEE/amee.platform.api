package com.amee.base.crypto;

public class CryptoException extends Exception {

    private boolean internal;

    public CryptoException(boolean internal) {
        super();
        this.internal = internal;
    }

    public CryptoException(boolean internal, Throwable cause) {
        super(cause);
        this.internal = internal;
    }

    public String getMessage() {
        return (internal ? "Internal" : "External") + " encryption error." +
                ((getCause() != null) ? " Caused by " + getCause().getClass().getSimpleName() + "." : "");
    }

    public boolean isInternal() {
        return internal;
    }
}