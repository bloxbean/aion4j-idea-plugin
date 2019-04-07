package org.aion4j.avm.idea.action;

public class InvokeParam {
    private String name;
    private String type;
    private String avmType;
    private boolean isArray;
    private boolean is2DArray;
    private String defaultValue;
    private String value;

    public InvokeParam(String name, String type, String avmType, boolean isArray, boolean is2DArray, String defaultValue) {
        this.name = name;
        this.type = type;
        this.avmType = avmType;
        this.isArray = isArray;
        this.is2DArray = is2DArray;
        this.defaultValue = defaultValue;
    }

    public String getType() {
        return type;
    }

    public String getAvmType() {
        return avmType;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean is2DArray() {
        return is2DArray;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
