package org.fiteagle.north.sfa.exceptions;

/**
 * Created by dne on 16.02.15.
 */
public class BadArgumentsException extends RuntimeException {
    private String message;

    public BadArgumentsException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}