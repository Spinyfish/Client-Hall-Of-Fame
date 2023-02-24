package me.kansio.client.utils.java;

import org.reflections.Reflections;

import java.util.List;
import java.util.stream.Collectors;

public class ReflectUtils {

    public static <T> List<Class<? extends T>> getReflects(String packagePath, Class<T> clazz) { //TODO: rename to getReflects

        return new Reflections(packagePath).getSubTypesOf(clazz).stream().filter(aClass -> aClass.getDeclaredAnnotation(NotUsable.class) == null).collect(Collectors.toList());
    }

    public @interface NotUsable {
    }

}
