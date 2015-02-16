package org.fiteagle.north.sfa.exceptions;

/**
 * Created by dne on 16.02.15.
 */
public class ForbiddenException extends RuntimeException {
    private String message;

    public ForbiddenException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}