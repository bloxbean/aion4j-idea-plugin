package org.aion4j.avm.idea.inspection.types;

import java.util.HashMap;
import java.util.Map;

public class AvmTypes {
    private static Map<String, String> allowedTypes;

    public static boolean isAllowedType(String type) {
        if(allowedTypes == null) {
            allowedTypes = new HashMap<>();
            populate();
        }

        return allowedTypes.containsKey(type);
    }

    private static void populate() {
        allowedTypes.put("byte", "");
        allowedTypes.put("boolean", "");
        allowedTypes.put("char", "");
        allowedTypes.put("short", "");
        allowedTypes.put("int", "");
        allowedTypes.put("float", "");
        allowedTypes.put("long", "");
        allowedTypes.put("double", "");
        allowedTypes.put("java.lang.String", "");
        allowedTypes.put("avm.Address", "");

        allowedTypes.put("byte[]", "");
        allowedTypes.put("boolean[]", "");
        allowedTypes.put("char[]", "");
        allowedTypes.put("short[]", "");
        allowedTypes.put("int[]", "");
        allowedTypes.put("float[]", "");
        allowedTypes.put("long[]", "");
        allowedTypes.put("double[]", "");
        allowedTypes.put("java.lang.String[]", "");
        allowedTypes.put("avm.Address[]", "");

        allowedTypes.put("byte[][]", "");
        allowedTypes.put("boolean[][]", "");
        allowedTypes.put("char[][]", "");
        allowedTypes.put("short[][]", "");
        allowedTypes.put("int[][]", "");
        allowedTypes.put("float[][]", "");
        allowedTypes.put("long[][]", "");
        allowedTypes.put("double[][]", "");
    }


}
