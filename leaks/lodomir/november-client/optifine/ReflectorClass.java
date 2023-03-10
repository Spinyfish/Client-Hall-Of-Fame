/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import optifine.Config;
import optifine.ReflectorField;
import optifine.ReflectorMethod;

public class ReflectorClass {
    private String targetClassName = null;
    private boolean checked = false;
    private Class targetClass = null;

    public ReflectorClass(String p_i81_1_) {
        this(p_i81_1_, false);
    }

    public ReflectorClass(String p_i82_1_, boolean p_i82_2_) {
        this.targetClassName = p_i82_1_;
        if (!p_i82_2_) {
            Class clazz = this.getTargetClass();
        }
    }

    public ReflectorClass(Class p_i83_1_) {
        this.targetClass = p_i83_1_;
        this.targetClassName = p_i83_1_.getName();
        this.checked = true;
    }

    public Class getTargetClass() {
        if (this.checked) {
            return this.targetClass;
        }
        this.checked = true;
        try {
            this.targetClass = Class.forName(this.targetClassName);
        }
        catch (ClassNotFoundException var2) {
            Config.log("(Reflector) Class not present: " + this.targetClassName);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return this.targetClass;
    }

    public boolean exists() {
        return this.getTargetClass() != null;
    }

    public String getTargetClassName() {
        return this.targetClassName;
    }

    public boolean isInstance(Object p_isInstance_1_) {
        return this.getTargetClass() == null ? false : this.getTargetClass().isInstance(p_isInstance_1_);
    }

    public ReflectorField makeField(String p_makeField_1_) {
        return new ReflectorField(this, p_makeField_1_);
    }

    public ReflectorMethod makeMethod(String p_makeMethod_1_) {
        return new ReflectorMethod(this, p_makeMethod_1_);
    }

    public ReflectorMethod makeMethod(String p_makeMethod_1_, Class[] p_makeMethod_2_) {
        return new ReflectorMethod(this, p_makeMethod_1_, p_makeMethod_2_);
    }

    public ReflectorMethod makeMethod(String p_makeMethod_1_, Class[] p_makeMethod_2_, boolean p_makeMethod_3_) {
        return new ReflectorMethod(this, p_makeMethod_1_, p_makeMethod_2_, p_makeMethod_3_);
    }
}

