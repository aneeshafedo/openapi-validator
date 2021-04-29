package org.openapi.validator;

public class OpenApiException extends Exception{
    OpenApiException(String message, Throwable e) {
        super(message, e);
    }

    OpenApiException(String message) {
        super(message);
    }
}
