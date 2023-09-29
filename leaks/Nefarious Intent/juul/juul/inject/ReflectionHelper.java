package juul.inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionHelper {

    public Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
    public Class<?>[] primitives = {boolean.class, char.class, int.class, short.class, long.class, byte.class, float.class, double.class};
    public List<String> primitiveNames = Arrays.asList("boolean", "char", "int", "short", "long", "byte", "float", "double");
    public Class<?>[] objects = {Boolean.class, Character.class, Integer.class, Short.class, Long.class, Byte.class, Float.class, Double.class};

    public Class getPrimitiveClass(Object instance) {
        int index = 0;
        for (Class<?> c : objects) {
            if (c == instance.getClass()) {
                return primitives[index];
            }
            index++;
        }

        return null;
    }

    public boolean isPrimitive(Object instance) {
        for (Class<?> c : objects)
            if (c == instance.getClass())
                return true;

        return false;
    }

    public void setFinalStatic(String name, Object instance, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPrivateValue(String name, Object instance, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(name); //NoSuchFieldException
            f.setAccessible(true);
            f.set(instance, value); //IllegalAccessException
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Object getPrivateValue(String name, Object instance) {
        try {
            Field f = instance.getClass().getDeclaredField(name); //NoSuchFieldException
            f.setAccessible(true);
            return f.get(instance); //IllegalAccessException
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getField(String name, String clazz, Object instance) {
        try {
            if (!classCache.containsKey(clazz))
                classCache.put(clazz, Class.forName(clazz));

            if(name == "thePlayer") {
                System.out.println(clazz);
                System.out.println("instance: " + instance.getClass().getName());
            }
            Field f = classCache.get(clazz).getField(name); //NoSuchFieldException
            return f.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setField(String name, String clazz, Object instance, Object value) {
        try {
            if (!classCache.containsKey(clazz))
                classCache.put(clazz, Class.forName(clazz));

            Field f = classCache.get(clazz).getField(name); //NoSuchFieldException
            f.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isInstance(String name, Object instance) {
        try {
            if (!classCache.containsKey(name))
                classCache.put(name, Class.forName(name));


            return classCache.get(name).isInstance(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Object construct(String name, Object[] params, Class[] types) {
        try {
            if (!classCache.containsKey(name))
                classCache.put(name, Class.forName(name));

            Constructor<?> c = classCache.get(name).getDeclaredConstructor(types); //NoSuchFieldException
            return c.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //The following are the three ways to call methods

    /**
     * This is like callMethodWithTypes but you do not need to specify the type classes because it is for only primitive types.
     * @param name     Method name
     * @param instance Instance of the class to reflect
     * @param params   The parameters to pass to the method
     * @return returns the result of the method call
     */
    public Object getOnlyPrimitives(String name, Object instance, Object... params) {
        try {
            Class<?>[] types = params == null ? null : new Class<?>[params.length];
            int index = 0;
            for (Object param : params) {
                if (isPrimitive(param))
                    types[index] = getPrimitiveClass(param);

                index++;
            }
            if (instance == null) return null;

            Method m = instance.getClass().getMethod(name, types); //NoSuchFieldException
            return m.invoke(instance, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param name            Method name
     * @param instance        Instance of the class to reflect
     * @param objects         The actual parameters of the method
     * @param objectTypeNames The class name of the parameters
     * @return result of method call
     */
    public Object callMethodWithTypes(String name, Object instance, Object[] objects, String[] objectTypeNames) {
        try {
            Class<?>[] types = objects == null ? null : new Class<?>[objectTypeNames.length];

            int index = 0;
            for (String type : objectTypeNames) {
                if (primitiveNames.contains(type)) {
                    types[index] = primitives[primitiveNames.indexOf(type)];
                } else {
                    if (!classCache.containsKey(type))
                        classCache.put(type, Class.forName(type));

                    types[index] = classCache.get(type);
                }

                index++;
            }
            Method m = instance
                    .getClass()
                    .getMethod(name, types); //NoSuchFieldException
            return m.invoke(instance, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This calls a method with a string to reference the class instead of an instance.
     *
     * @param name   Method name
     * @param clazz  Class name
     * @param onNull Returns this if the method is not found
     * @param params The parameters to pass to the method
     * @return Returns the result of the method call
     */
    public Object getWithClass(String name, String clazz, Object onNull, Object... params) {
        try {
            if (!classCache.containsKey(clazz))
                classCache.put(clazz, Class.forName(clazz));

            Class<?>[] types = params == null ? null : new Class<?>[params.length];
            int index = 0;
            for (Object param : params) {
                if (isPrimitive(param))
                    types[index] = getPrimitiveClass(param);

                index++;
            }

            Method m = classCache.get(clazz).getMethod(name, types); //NoSuchFieldException
            return m.invoke(null, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return onNull;
    }

}
