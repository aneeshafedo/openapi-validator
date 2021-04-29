package org.openapi.validator;

public enum PackageInfo {
    ORG("package.org"), Name("package.name"), VERSION("package.version");
    private String value;
    PackageInfo(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
