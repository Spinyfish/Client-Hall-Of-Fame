package tech.dort.dortware.impl.utils.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {

    public static Object call(Object owner, String className, String methodName, Object... args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        for (Method method : Class.forName(className).getMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                return method.invoke(owner, args);
            }
        }
        return null;
    }

    public static Object getField(Object owner, String className, String name) throws ClassNotFoundException, IllegalAccessException {
        for (Field field : Class.forName(className).getFields()) {
            if (field.getName().equalsIgnoreCase(name))
                return field.get(owner);
        }
        return null;
    }

    public static Object newInstance(String className, Class<?>[] paramTypes, Object... arguments) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = Class.forName(className).getConstructor(paramTypes);
        return constructor.newInstance(arguments);
    }
}
