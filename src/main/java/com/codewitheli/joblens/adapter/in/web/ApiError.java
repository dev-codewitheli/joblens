package com.codewitheli.joblens.adapter.in.web;

import java.util.Map;

/**
 * Consistent error payload returned by the API.
 *
 * @param error  a human-readable message
 * @param fields optional per-field validation messages (null when not applicable)
 */
public record ApiError(String error, Map<String, String> fields) {

    public static ApiError of(String message) {
        return new ApiError(message, null);
    }

    public static ApiError of(String message, Map<String, String> fields) {
        return new ApiError(message, fields);
    }
}
