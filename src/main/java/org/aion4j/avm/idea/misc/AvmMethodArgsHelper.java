package org.aion4j.avm.idea.misc;

import org.aion4j.avm.idea.action.InvokeParam;

import java.util.List;

public class AvmMethodArgsHelper {

    public static String getAvmType(String type) {

        String avmType = null;
        switch (type) {
            case "avm.Address":
                avmType = "-A";
                break;

            case "boolean":
            case "java.lang.Boolean":
                avmType = "-Z";
                break;

            case "byte":
            case "java.lang.Byte":
                avmType = "-B";
                break;

            case "char":
            case "java.lang.Character":
                avmType = "-C";
                break;

            case "double":
            case "java.lang.Double":
                avmType = "-D";
                break;

            case "float":
            case "java.lang.Float":
                avmType = "-F";
                break;

            case "int":
            case "java.lang.Integer":
                avmType = "-I";
                break;

            case "long":
            case "java.lang.Long":
                avmType = "-J";
                break;

            case "short":
            case "java.lang.Short":
                avmType = "-S";
                break;

            case "java.lang.String":
                avmType = "-T";
                break;

            default:
                avmType = null;
                break;
        }

        return avmType;
    }

    public static String buildMethodArgsString(List<InvokeParam> paramList) {
        if(paramList == null || paramList.size() == 0) return null;

        StringBuilder sb = new StringBuilder();
        for(InvokeParam param: paramList) {
            sb.append(param.getAvmType());
            if(param.isArray())
                sb.append("[]");
            else if(param.is2DArray())
                sb.append("[][]");
            sb.append(" ");
            if("-T".equals(param.getAvmType()) && !param.isArray() && !param.is2DArray()) {
                sb.append("'");
                sb.append(param.getValue());
                sb.append("'");
            } else {
                sb.append(param.getValue());
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
