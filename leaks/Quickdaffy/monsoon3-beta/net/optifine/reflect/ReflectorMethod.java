/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;

public class ReflectorMethod {
    private ReflectorClass reflectorClass = null;
    private String targetMethodName = null;
    private Class[] targetMethodParameterTypes = null;
    private boolean checked = false;
    private Method targetMethod = null;

    public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName) {
        this(reflectorClass, targetMethodName, null, false);
    }

    public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes) {
        this(reflectorClass, targetMethodName, targetMethodParameterTypes, false);
    }

    public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes, boolean lazyResolve) {
        this.reflectorClass = reflectorClass;
        this.targetMethodName = targetMethodName;
        this.targetMethodParameterTypes = targetMethodParameterTypes;
        if (!lazyResolve) {
            Method method = this.getTargetMethod();
        }
    }

    public Method getTargetMethod() {
        if (this.checked) {
            return this.targetMethod;
        }
        this.checked = true;
        Class oclass = this.reflectorClass.getTargetClass();
        if (oclass == null) {
            return null;
        }
        try {
            if (this.targetMethodParameterTypes == null) {
                Method[] amethod = ReflectorMethod.getMethods(oclass, this.targetMethodName);
                if (amethod.length <= 0) {
                    Config.log("(Reflector) Method not present: " + oclass.getName() + "." + this.targetMethodName);
                    return null;
                }
                if (amethod.length > 1) {
                    Config.warn("(Reflector) More than one method found: " + oclass.getName() + "." + this.targetMethodName);
                    for (int i = 0; i < amethod.length; ++i) {
                        Method method = amethod[i];
                        Config.warn("(Reflector)  - " + method);
                    }
                    return null;
                }
                this.targetMethod = amethod[0];
            } else {
                this.targetMethod = ReflectorMethod.getMethod(oclass, this.targetMethodName, this.targetMethodParameterTypes);
            }
            if (this.targetMethod == null) {
                Config.log("(Reflector) Method not present: " + oclass.getName() + "." + this.targetMethodName);
                return null;
            }
            this.targetMethod.setAccessible(true);
            return this.targetMethod;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public boolean exists() {
        return this.checked ? this.targetMethod != null : this.getTargetMethod() != null;
    }

    public Class getReturnType() {
        Method method = this.getTargetMethod();
        return method == null ? null : method.getReturnType();
    }

    public void deactivate() {
        this.checked = true;
        this.targetMethod = null;
    }

    public static Method getMethod(Class cls, String methodName, Class[] paramTypes) {
        Method[] amethod = cls.getDeclaredMethods();
        for (int i = 0; i < amethod.length; ++i) {
            Class[] aclass;
            Method method = amethod[i];
            if (!method.getName().equals(methodName) || !Reflector.matchesTypes(paramTypes, aclass = method.getParameterTypes())) continue;
            return method;
        }
        return null;
    }

    public static Method[] getMethods(Class cls, String methodName) {
        ArrayList<Method> list = new ArrayList<Method>();
        Method[] amethod = cls.getDeclaredMethods();
        for (int i = 0; i < amethod.length; ++i) {
            Method method = amethod[i];
            if (!method.getName().equals(methodName)) continue;
            list.add(method);
        }
        Method[] amethod1 = list.toArray(new Method[list.size()]);
        return amethod1;
    }
}

