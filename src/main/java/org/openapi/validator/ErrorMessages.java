package org.openapi.validator;

public class ErrorMessages {
    static String invalidFilePath(String path) {
        return String.format("OpenAPI contract doesn't exist in the given location:%n%s", path);
    }
    static String invalidFile() {
        return "Invalid file type. Provide either a .yaml or .json file.";
    }
    static String invalidRootDirectory(String path) {
        return String.format("Invalid root directory: %s", path);
    }
    static String propertyFileNotFound(String path) {
        return String.format("Property file not found in the given directory: %s", path);
    }
    static String parserException(String path) {
        return String.format("Couldn't read the OpenAPI contract from the given file: %s", path);
    }

    static String displayExtentionNotFound(String path) {
        return String.format("Could not find x-display extension in the given yaml file: %s", path);
    }
    static String displayExtentionInvalid(String path) {
        return String.format("Invalid display extension: %s", path);
    }
    static String invalidPackageOrg(String path) {
        return String.format("invalid 'org' under [package]: 'org' should be `ballerinax`: %s", path);
    }
    static String invalidPackageName(String path) {
        return String.format("invalid 'name' under [package]: 'name' can only contain alphanumerics, underscores and periods and the maximum length is 256 characters: %s", path);
    }
    static String invalidPackageVersion(String path) {
        return String.format("invalid 'version' under [package]: 'version' should be compatible with server: %s", path);
    }
    static String undocumentedResourcePath(String path) {
        return String.format("Ballerina service contains a Resource that is not"
                + " documented in the OpenAPI contract."
                + " Error Resource path '%s'", path);
    }

    static String undocumentedResourceParameter(String paramName, String method, String path) {
        return String.format("'%s' parameter for the method '%s' " +
                "of the resource associated with the path '%s' " +
                "is not documented in the OpenAPI contract", paramName, method, path);
    }

    static String undocumentedResourceMethods(String methods, String path) {
        return String.format("OpenAPI contract doesn't contain the" +
                " documentation for http method(s) '%s' for the path '%s'", methods, path);
    }

    static String unimplementedOpenAPIPath(String path) {
        return String.format("Couldn't find a Ballerina service resource for the path '%s' " +
                "which is documented in the OpenAPI contract", path);
    }

    static String unimplementedOpenAPIOperationsForPath(String methods, String path) {
        return String.format("Couldn't find Ballerina service resource(s) for http method(s) '%s' " +
                "for the path '%s' which is documented in the OpenAPI contract", methods, path);
    }

    static String unimplementedParameterForOperation(String paramName, String method, String path) {
        return String.format("Couldn't find '%s' parameter in the Ballerina service resource for the method '%s' " +
                "of the path '%s' which is documented in the OpenAPI contract", paramName, method, path);
    }

    static String undocumentedFieldInRecordParam(String fieldName, String paramName, String method, String path) {
        return String.format("The '%s' field in the '%s' type record of the parameter " +
                        "is not documented in the OpenAPI contract for the method '%s' of the path '%s'",
                fieldName, paramName, method, path);
    }

    static String unimplementedFieldInOperation(String fieldName, String paramName, String operation, String path) {
        return String.format("Couldn't find the '%s' field in the record type of the parameter '%s' " +
                        "for the method '%s' of the path '%s' which is documented in the OpenAPI contract",
                fieldName, paramName, operation, path);
    }

    static String tagFilterEnable() {
        return String.format("Both Tags and excludeTags fields include the same tag(s). Make sure to use one" +
                " field of tag filtering when using the OpenAPI annotation. ");
    }

    static String operationFilterEnable() {
        return String.format("Both Operations and excludeOperations fields include" +
                " the same operation(s). Make sure to use one field of operation filtering" +
                " when using the OpenAPI annotation.");
    }

    static String typeMismatching(String fieldName, String openapiType, String ballerinType,
                                  String method, String path) {
        return String.format("Type mismatch with parameter '%s' for the method" +
                        " '%s' of the path '%s'.In OpenAPI contract its type is '%s' and resources type is '%s'. ",
                fieldName, method,  path, openapiType, ballerinType);
    }

    static String typeMismatchingRecord(String fieldName, String paramName, String openapiType, String ballerinType,
                                        String method, String path) {
        return String.format("Type mismatching '%s' field in the record type of the parameter '%s' for the method" +
                        " '%s' of the path '%s'.In OpenAPI contract its type is '%s' and resources type is '%s'. ",
                fieldName, paramName, method,  path, openapiType , ballerinType);
    }

    static String typeMismatchOneOfObject(String fieldName, String paramName, String openapiType, String ballerinType,
                                          String method, String path) {
        return String.format("Type mismatch with '%s' field in the object type of the parameter '%s' for the method" +
                        " '%s' of the path '%s'.OpenAPI object schema expected '%s' type and resources has '%s' type for field.",
                fieldName, paramName, method,  path, openapiType, ballerinType);
    }

    static String typeMismatchOneOfRecord(String fieldName, String paramName, String openapiType, String ballerinType,
                                          String method, String path) {
        return String.format("Type mismatch with '%s' field in the record type of the parameter '%s' " +
                        "for the method '%s' of the path '%s'.OpenAPI object schema expected '%s' " +
                        "type and resources has '%s' type for field.",
                fieldName, paramName, method,  path, openapiType, ballerinType);
    }
}
