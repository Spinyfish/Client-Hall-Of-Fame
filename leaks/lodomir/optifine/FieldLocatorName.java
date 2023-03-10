/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.lang.reflect.Field;
import optifine.Config;
import optifine.IFieldLocator;
import optifine.ReflectorClass;

public class FieldLocatorName
implements IFieldLocator {
    private ReflectorClass reflectorClass = null;
    private String targetFieldName = null;

    public FieldLocatorName(ReflectorClass p_i38_1_, String p_i38_2_) {
        this.reflectorClass = p_i38_1_;
        this.targetFieldName = p_i38_2_;
    }

    @Override
    public Field getField() {
        Class oclass = this.reflectorClass.getTargetClass();
        if (oclass == null) {
            return null;
        }
        try {
            Field field = oclass.getDeclaredField(this.targetFieldName);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException var3) {
            Config.log("(Reflector) Field not present: " + oclass.getName() + "." + this.targetFieldName);
            return null;
        }
        catch (SecurityException securityexception) {
            securityexception.printStackTrace();
            return null;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}

