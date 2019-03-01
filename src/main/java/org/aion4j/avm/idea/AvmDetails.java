package org.aion4j.avm.idea;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AvmDetails {

    private static Map<Class<?>, MethodDescriptor[]> map;

    public static Map<Class<?>, MethodDescriptor[]> getClassLibraryWhiteList() {

        if(map == null) {

            map = new HashMap<>();

            MethodDescriptor addAll = new MethodDescriptor("addAll", new Class[]{Collection.class});
            MethodDescriptor add = new MethodDescriptor("add", new Class[]{Object.class});

            map.put(java.util.List.class, new MethodDescriptor[]{addAll});
            map.put(java.util.List.class, new MethodDescriptor[]{add});

            MethodDescriptor method2 = new MethodDescriptor("setReadable", new Class[]{boolean.class, boolean.class});
            map.put(File.class, new MethodDescriptor[]{method2});

            Class[] jclWhiteListClasses = getJCLWhitelistClasses();

            for (Class clazz : jclWhiteListClasses) {
                map.put(clazz, new MethodDescriptor[]{});
            }

        }
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
    
    private static Class[] getJCLWhitelistClasses() {
        return new Class<?>[] {
                java.lang.AssertionError.class
                , java.lang.Boolean.class
                , java.lang.Byte.class
                , java.lang.Character.class
                , java.lang.CharSequence.class
                , java.lang.Class.class
                , java.lang.Comparable.class
                , java.lang.Double.class
                , java.lang.Enum.class
                , java.lang.EnumConstantNotPresentException.class
                , java.lang.Error.class
                , java.lang.Exception.class
                , java.lang.Float.class
                , java.lang.Integer.class
                , java.lang.Iterable.class
                , java.lang.Long.class
                , java.lang.Math.class
                , java.lang.Number.class
                , java.lang.Object.class
                , java.lang.Runnable.class
                , java.lang.RuntimeException.class
                , java.lang.Short.class
                , java.lang.StrictMath.class
                , java.lang.String.class
                , java.lang.StringBuffer.class
                , java.lang.StringBuilder.class
                , java.lang.System.class
                , java.lang.Throwable.class
                , java.lang.TypeNotPresentException.class

                , java.lang.invoke.LambdaMetafactory.class
//                , java.lang.invoke.StringConcatFactory.class
                , java.math.BigDecimal.class
                , java.math.BigInteger.class
                , java.math.MathContext.class
                , java.math.RoundingMode.class

                , java.util.Arrays.class
                , java.util.Collection.class
                , java.util.Iterator.class
                , java.util.ListIterator.class
                , java.util.Map.class
                , java.util.Map.Entry.class
                , java.util.NoSuchElementException.class
                , java.util.Set.class
                , java.util.List.class
                , java.util.function.Function.class

                , java.util.concurrent.TimeUnit.class
        };
    }
}
