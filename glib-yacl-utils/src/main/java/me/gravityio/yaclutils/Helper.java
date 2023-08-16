package me.gravityio.yaclutils;

import me.gravityio.yaclutils.annotations.Getter;
import me.gravityio.yaclutils.annotations.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Helper {

    public static String DEFAULT_NAMESPACED_FORMAT = "yacl.%s.%s";
    public static String DEFAULT_LABEL_FORMAT = DEFAULT_NAMESPACED_FORMAT + ".label";
    public static String DEFAULT_DESCRIPTION_FORMAT = DEFAULT_NAMESPACED_FORMAT + ".description";

    /**
     * Finds the getter method for a particular ID
     */
    public static Method getGetterMethod(Method[] methods, String id) {
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Getter.class)) continue;
            Getter getter = method.getAnnotation(Getter.class);
            var gid = getter.id();
            if (gid.equals("")) gid = method.getName();
            if (gid.equals(id)) return method;
        }
        return null;
    }

    /**
     * Finds the setter method for a particular ID
     */
    public static Method getSetterMethod(Method[] methods, String id) {
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Setter.class)) continue;
            Setter getter = method.getAnnotation(Setter.class);
            var sid = getter.id();
            if (sid.equals("")) sid = method.getName();
            if (sid.equals(id)) return method;
        }
        return null;
    }

    /**
     * Gets the value of a field
     */
    public static Object doGetField(Object instance, Field field) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void doSetField(Object instance, Field field, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a supplier using the getter of a config option
     */
    public static Supplier<Object> getSupplier(Object instance, Method method) {
        return () -> {
            try {
                return method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Creates a consumer using the setter of a config option
     */
    public static Consumer<Object> getConsumer(Object instance, Method method) {
        return (v) -> {
            try {
                method.invoke(instance, v);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
