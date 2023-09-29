package juul.inject;

import juul.Juul;

public class Wrapper {

    private final String path;
    private final Object classInstance;

    public Wrapper(String path, Object classInstance) {
        this.path = path;
        this.classInstance = classInstance;
    }

    public Wrapper(String path) {
        this.path = path;
        this.classInstance = null;
    }

    public String getClassNameWithPath() { return getLookupTable().getClassName(path); }

    public Object getClassInstance() { return classInstance; }

    public boolean isInstance(Object instance) { return Juul.INSTANCE.getReflectionHelper().isInstance(getClassNameWithPath(),instance); }

    //Sets the field to the value
    public void setPrivateField(String fieldName, Object value) {
        Juul.INSTANCE.getReflectionHelper().setPrivateValue(Juul.INSTANCE.getLookupTable().getFieldName(path, fieldName), getClassInstance(), value);
    }

    // Get a private field in the class
    public Object getPrivateField(String path, String fieldName) {
        return Juul.INSTANCE.getReflectionHelper().getPrivateValue(getFieldName(path, fieldName), classInstance);
    }

    public Object getPrivateField(String fieldName) {
        return getField(path, fieldName);
    }


    //Get a field in the class
    public Object getField(String path, String fieldName) {
        return getReflectionHelper().getField(getFieldName(path, fieldName), getClassNameWithPath(), classInstance);
    }

    public Object getField(String fieldName) {
        return getField(path, fieldName);
    }

    //Sets the field to the value
    public void setField(String fieldName, Object value) {
        Juul.INSTANCE.getReflectionHelper().setField(Juul.INSTANCE.getLookupTable().getFieldName(path, fieldName), getClassNameWithPath(), getClassInstance(), value);
    }

    public Object callMethodWithTypes(String methodName, Object[] objects, String... objectTypeNames) {
        return getReflectionHelper().callMethodWithTypes(Juul.INSTANCE.getLookupTable().getMethodName(path, methodName), getClassInstance(), objects, objectTypeNames);
    }

    //Invoke a method with only primitive parameters
    public Object getMethodPrimitive(String path, String methodName, Object... params) {
        return getReflectionHelper().getOnlyPrimitives(Juul.INSTANCE.getLookupTable().getMethodName(path, methodName), classInstance, params);
    }

    public Object getMethodPrimitive(String methodName, Object... params) { return getMethodPrimitive(path, methodName, params); }

    //Used to get method with path and name of the class (Should be used for utility classes like GlStateManager which don't have an instance)
    public Object getWithClassMethod(String methodName, Object... params) {
        return getReflectionHelper().getWithClass(getMethodName(path, methodName), getClassNameWithPath(), null, params);
    }

    public String getMethodName(String clazz, String name) { return getLookupTable().getMethodName(clazz, name); }

    public String getFieldName(String clazz, String name) { return getLookupTable().getFieldName(clazz, name); }

    public ReflectionHelper getReflectionHelper() { return Juul.INSTANCE.getReflectionHelper(); }

    public static LUT getLookupTable() { return Juul.INSTANCE.getLookupTable(); }

    public String getPath() { return path; }

}
