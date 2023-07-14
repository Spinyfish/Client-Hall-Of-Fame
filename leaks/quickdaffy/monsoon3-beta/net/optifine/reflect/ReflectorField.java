/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.reflect;

import java.lang.reflect.Field;
import net.optifine.reflect.FieldLocatorFixed;
import net.optifine.reflect.FieldLocatorName;
import net.optifine.reflect.FieldLocatorType;
import net.optifine.reflect.IFieldLocator;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;

public class ReflectorField {
    private IFieldLocator fieldLocator = null;
    private boolean checked = false;
    private Field targetField = null;

    public ReflectorField(ReflectorClass reflectorClass, String targetFieldName) {
        this(new FieldLocatorName(reflectorClass, targetFieldName));
    }

    public ReflectorField(ReflectorClass reflectorClass, String targetFieldName, boolean lazyResolve) {
        this(new FieldLocatorName(reflectorClass, targetFieldName), lazyResolve);
    }

    public ReflectorField(ReflectorClass reflectorClass, Class targetFieldType) {
        this(reflectorClass, targetFieldType, 0);
    }

    public ReflectorField(ReflectorClass reflectorClass, Class targetFieldType, int targetFieldIndex) {
        this(new FieldLocatorType(reflectorClass, targetFieldType, targetFieldIndex));
    }

    public ReflectorField(Field field) {
        this(new FieldLocatorFixed(field));
    }

    public ReflectorField(IFieldLocator fieldLocator) {
        this(fieldLocator, false);
    }

    public ReflectorField(IFieldLocator fieldLocator, boolean lazyResolve) {
        this.fieldLocator = fieldLocator;
        if (!lazyResolve) {
            this.getTargetField();
        }
    }

    public Field getTargetField() {
        if (this.checked) {
            return this.targetField;
        }
        this.checked = true;
        this.targetField = this.fieldLocator.getField();
        if (this.targetField != null) {
            this.targetField.setAccessible(true);
        }
        return this.targetField;
    }

    public Object getValue() {
        return Reflector.getFieldValue(null, this);
    }

    public void setValue(Object value) {
        Reflector.setFieldValue(null, this, value);
    }

    public void setValue(Object obj, Object value) {
        Reflector.setFieldValue(obj, this, value);
    }

    public boolean exists() {
        return this.getTargetField() != null;
    }
}

