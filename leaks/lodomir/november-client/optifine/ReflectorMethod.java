/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import optifine.Config;
import optifine.Reflector;
import optifine.ReflectorClass;

public class ReflectorMethod {
    private ReflectorClass reflectorClass = null;
    private String targetMethodName = null;
    private Class[] targetMethodParameterTypes = null;
    private boolean checked = false;
    private Method targetMethod = null;

    public ReflectorMethod(ReflectorClass p_i91_1_, String p_i91_2_) {
        this(p_i91_1_, p_i91_2_, null, false);
    }

    public ReflectorMethod(ReflectorClass p_i92_1_, String p_i92_2_, Class[] p_i92_3_) {
        this(p_i92_1_, p_i92_2_, p_i92_3_, false);
    }

    public ReflectorMethod(ReflectorClass p_i93_1_, String p_i93_2_, Class[] p_i93_3_, boolean p_i93_4_) {
        this.reflectorClass = p_i93_1_;
        this.targetMethodName = p_i93_2_;
        this.targetMethodParameterTypes = p_i93_3_;
        if (!p_i93_4_) {
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

    public static Method getMethod(Class p_getMethod_0_, String p_getMethod_1_, Class[] p_getMethod_2_) {
        Method[] amethod = p_getMethod_0_.getDeclaredMethods();
        for (int i = 0; i < amethod.length; ++i) {
            Class[] aclass;
            Method method = amethod[i];
            if (!method.getName().equals(p_getMethod_1_) || !Reflector.matchesTypes(p_getMethod_2_, aclass = method.getParameterTypes())) continue;
            return method;
        }
        return null;
    }

    public static Method[] getMethods(Class p_getMethods_0_, String p_getMethods_1_) {
        ArrayList<Method> list = new ArrayList<Method>();
        Method[] amethod = p_getMethods_0_.getDeclaredMethods();
        for (int i = 0; i < amethod.length; ++i) {
            Method method = amethod[i];
            if (!method.getName().equals(p_getMethods_1_)) continue;
            list.add(method);
        }
        Method[] amethod1 = list.toArray(new Method[list.size()]);
        return amethod1;
    }
}

