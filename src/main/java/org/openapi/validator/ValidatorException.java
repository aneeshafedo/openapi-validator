package org.openapi.validator;

public class ValidatorException extends Exception{
    ValidatorException(String message, Throwable e) {
        super(message, e);
    }
    ValidatorException(String message) {
        super(message);
    }
}
