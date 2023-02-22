package net.optifine.reflect;

import net.minecraft.src.Config;

import java.lang.reflect.Field;

public class FieldLocatorName implements IFieldLocator {
    private ReflectorClass reflectorClass = null;
    private String targetFieldName = null;

    public FieldLocatorName(final ReflectorClass reflectorClass, final String targetFieldName) {
        this.reflectorClass = reflectorClass;
        this.targetFieldName = targetFieldName;
    }

    public Field getField() {
        final Class oclass = this.reflectorClass.getTargetClass();

        if (oclass == null) {
            return null;
        } else {
            try {
                final Field field = this.getDeclaredField(oclass, this.targetFieldName);
                field.setAccessible(true);
                return field;
            } catch (final NoSuchFieldException var3) {
                Config.log("(Reflector) Field not present: " + oclass.getName() + "." + this.targetFieldName);
                return null;
            } catch (final SecurityException securityexception) {
                securityexception.printStackTrace();
                return null;
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        }
    }

    private Field getDeclaredField(final Class cls, final String name) throws NoSuchFieldException {
        final Field[] afield = cls.getDeclaredFields();

        for (int i = 0; i < afield.length; ++i) {
            final Field field = afield[i];

            if (field.getName().equals(name)) {
                return field;
            }
        }

        if (cls == Object.class) {
            throw new NoSuchFieldException(name);
        } else {
            return this.getDeclaredField(cls.getSuperclass(), name);
        }
    }
}
