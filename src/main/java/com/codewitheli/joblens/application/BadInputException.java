package com.codewitheli.joblens.application;

/**
 * Thrown when a request is syntactically valid but cannot be processed — e.g. an
 * unreadable PDF, an unreachable or disallowed URL, or an AI-only feature invoked
 * in demo mode. Surfaces to the client as HTTP 422 with an {@code ApiError} body.
 */
public class BadInputException extends RuntimeException {
    public BadInputException(String message) {
        super(message);
    }
}
