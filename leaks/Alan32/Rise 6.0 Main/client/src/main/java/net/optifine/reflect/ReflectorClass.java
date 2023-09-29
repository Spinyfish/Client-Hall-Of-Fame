package net.optifine.reflect;

import net.minecraft.src.Config;

public class ReflectorClass {
    private String targetClassName;
    private boolean checked;
    private Class targetClass;

    public ReflectorClass(final String targetClassName) {
        this(targetClassName, false);
    }

    public ReflectorClass(final String targetClassName, final boolean lazyResolve) {
        this.targetClassName = null;
        this.checked = false;
        this.targetClass = null;
        this.targetClassName = targetClassName;

        if (!lazyResolve) {
            final Class oclass = this.getTargetClass();
        }
    }

    public ReflectorClass(final Class targetClass) {
        this.targetClassName = null;
        this.checked = false;
        this.targetClass = null;
        this.targetClass = targetClass;
        this.targetClassName = targetClass.getName();
        this.checked = true;
    }

    public Class getTargetClass() {
        if (this.checked) {
            return this.targetClass;
        } else {
            this.checked = true;

            try {
                this.targetClass = Class.forName(this.targetClassName);
            } catch (final ClassNotFoundException var2) {
                Config.log("(Reflector) Class not present: " + this.targetClassName);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }

            return this.targetClass;
        }
    }

    public boolean exists() {
        return this.getTargetClass() != null;
    }

    public String getTargetClassName() {
        return this.targetClassName;
    }

    public boolean isInstance(final Object obj) {
        return this.getTargetClass() != null && this.getTargetClass().isInstance(obj);
    }

    public ReflectorField makeField(final String name) {
        return new ReflectorField(this, name);
    }

    public ReflectorMethod makeMethod(final String name) {
        return new ReflectorMethod(this, name);
    }

    public ReflectorMethod makeMethod(final String name, final Class[] paramTypes) {
        return new ReflectorMethod(this, name, paramTypes);
    }

    public ReflectorMethod makeMethod(final String name, final Class[] paramTypes, final boolean lazyResolve) {
        return new ReflectorMethod(this, name, paramTypes, lazyResolve);
    }
}
