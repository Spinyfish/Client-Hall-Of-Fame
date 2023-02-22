package net.optifine.reflect;

import net.minecraft.src.Config;

import java.lang.reflect.Constructor;

public class ReflectorConstructor {
    private ReflectorClass reflectorClass = null;
    private Class[] parameterTypes = null;
    private boolean checked = false;
    private Constructor targetConstructor = null;

    public ReflectorConstructor(final ReflectorClass reflectorClass, final Class[] parameterTypes) {
        this.reflectorClass = reflectorClass;
        this.parameterTypes = parameterTypes;
        final Constructor constructor = this.getTargetConstructor();
    }

    public Constructor getTargetConstructor() {
        if (this.checked) {
            return this.targetConstructor;
        } else {
            this.checked = true;
            final Class oclass = this.reflectorClass.getTargetClass();

            if (oclass == null) {
                return null;
            } else {
                try {
                    this.targetConstructor = findConstructor(oclass, this.parameterTypes);

                    if (this.targetConstructor == null) {
                        Config.dbg("(Reflector) Constructor not present: " + oclass.getName() + ", params: " + Config.arrayToString(this.parameterTypes));
                    }

                    if (this.targetConstructor != null) {
                        this.targetConstructor.setAccessible(true);
                    }
                } catch (final Throwable throwable) {
                    throwable.printStackTrace();
                }

                return this.targetConstructor;
            }
        }
    }

    private static Constructor findConstructor(final Class cls, final Class[] paramTypes) {
        final Constructor[] aconstructor = cls.getDeclaredConstructors();

        for (int i = 0; i < aconstructor.length; ++i) {
            final Constructor constructor = aconstructor[i];
            final Class[] aclass = constructor.getParameterTypes();

            if (Reflector.matchesTypes(paramTypes, aclass)) {
                return constructor;
            }
        }

        return null;
    }

    public boolean exists() {
        return this.checked ? this.targetConstructor != null : this.getTargetConstructor() != null;
    }

    public void deactivate() {
        this.checked = true;
        this.targetConstructor = null;
    }
}
