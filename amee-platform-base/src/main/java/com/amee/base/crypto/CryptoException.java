package com.amee.base.crypto;

/**
 * An {@link Exception} which can be thrown by {@link BaseCrypto} or {@link InternalCrypto}. Provides a flag
 * to indicate if the error was internal, a system error, or external, a client code error. Most
 * errors are internal.
 */
public class CryptoException extends Exception {

    /**
     * Indicates if the exception is considered internal,
     */
    private boolean internal;

    /**
     * Construct a CryptoException with the supplied internal state.
     *
     * @param internal state flag
     */
    public CryptoException(boolean internal) {
        super();
        this.internal = internal;
    }

    /**
     * Construct a CryptoException with the supplied internal state and cause.
     *
     * @param internal state flag
     * @param cause    the {@link Throwable} causing this exception
     */
    public CryptoException(boolean internal, Throwable cause) {
        super(cause);
        this.internal = internal;
    }

    /**
     * Get the String message. Useful for logging.
     *
     * @return message as a String
     */
    public String getMessage() {
        return (internal ? "Internal" : "External") + " encryption error." +
                ((getCause() != null) ? " Caused by " + getCause().getClass().getSimpleName() + "." : "");
    }

    /**
     * Returns true if the exception was an internal error.
     *
     * @return true if the exception was an internal error
     */
    public boolean isInternal() {
        return internal;
    }
}