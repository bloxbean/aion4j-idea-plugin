package org.aion4j.avm.idea;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AvmDetails {
    public static Map<Class<?>, MethodDescriptor[]> getClassLibraryWhiteList() {

        Map<Class<?>, MethodDescriptor[]> map = new HashMap<>();

        MethodDescriptor addAll = new MethodDescriptor("addAll", new Class[] {Collection.class});
        MethodDescriptor add = new MethodDescriptor("add", new Class[] {Object.class});

        map.put(java.util.List.class, new MethodDescriptor[]{addAll});
        map.put(java.util.List.class, new MethodDescriptor[]{add});

        MethodDescriptor method2 = new MethodDescriptor("setReadable", new Class[] {boolean.class, boolean.class});
        map.put(File.class, new MethodDescriptor[] {method2});

        return map;
    }

    public static class MethodDescriptor {
        public final String name;
        public final Class<?>[] arguments;

        public MethodDescriptor(String name, Class<?>[] arguments) {
            this.name = name;
            this.arguments = arguments;
        }
    }
}
