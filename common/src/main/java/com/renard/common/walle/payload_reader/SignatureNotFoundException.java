package com.renard.common.walle.payload_reader;


public class SignatureNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public SignatureNotFoundException(final String message) {
        super(message);
    }

    public SignatureNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
