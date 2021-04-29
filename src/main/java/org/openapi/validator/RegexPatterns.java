package org.openapi.validator;

public class RegexPatterns {
    public static final String ORG  = "^[a-zA-Z0-9_.]*$";
    public static final String NAME = "^[a-zA-Z0-9_.]*$";
    public static final String VERSION  = "(0|[1-9]\\\\d*)\\\\.(0|[1-9]\\\\d*)\\\\.(0|[1-9]\\\\d*)(?:-((?:0|[1-9]\\\\d*|\\\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\\\.(?:0|[1-9]\\\\d*|\\\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\\\+([0-9a-zA-Z-]+(?:\\\\.[0-9a-zA-Z-]+)*))?$";
}
