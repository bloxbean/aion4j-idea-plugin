package org.aion4j.avm.idea.service;

import java.io.Serializable;
import java.util.List;

public class MethodDescriptor implements Serializable {

    private String name;
    private List<String> params;
    private boolean isStatic;

    public MethodDescriptor() {

    }

    public MethodDescriptor(String name, List<String> params, boolean isStatic) {
        this.name = name;
        this.params = params;
        this.isStatic = isStatic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public String toString() {
        return "MethodDescriptor{" +
                "name='" + name + '\'' +
                ", params=" + params +
                ", isStatic=" + isStatic +
                '}';
    }
}
