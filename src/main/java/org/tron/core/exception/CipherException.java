package org.tron.core.exception;

/**
 * Cipher exception wrapper.
 */
public class CipherException extends Exception {
    private static final long serialVersionUID = 0L;

    public CipherException(String message) {
        super(message);
    }

    public CipherException(Throwable cause) {
        super(cause);
    }

    public CipherException(String message, Throwable cause) {
        super(message, cause);
    }
}
