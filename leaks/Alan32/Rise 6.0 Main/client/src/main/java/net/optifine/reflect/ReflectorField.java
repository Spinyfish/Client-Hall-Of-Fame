package net.optifine.reflect;

import java.lang.reflect.Field;

public class ReflectorField {
    private IFieldLocator fieldLocator;
    private boolean checked;
    private Field targetField;

    public ReflectorField(final ReflectorClass reflectorClass, final String targetFieldName) {
        this(new FieldLocatorName(reflectorClass, targetFieldName));
    }

    public ReflectorField(final ReflectorClass reflectorClass, final String targetFieldName, final boolean lazyResolve) {
        this(new FieldLocatorName(reflectorClass, targetFieldName), lazyResolve);
    }

    public ReflectorField(final ReflectorClass reflectorClass, final Class targetFieldType) {
        this(reflectorClass, targetFieldType, 0);
    }

    public ReflectorField(final ReflectorClass reflectorClass, final Class targetFieldType, final int targetFieldIndex) {
        this(new FieldLocatorType(reflectorClass, targetFieldType, targetFieldIndex));
    }

    public ReflectorField(final Field field) {
        this(new FieldLocatorFixed(field));
    }

    public ReflectorField(final IFieldLocator fieldLocator) {
        this(fieldLocator, false);
    }

    public ReflectorField(final IFieldLocator fieldLocator, final boolean lazyResolve) {
        this.fieldLocator = null;
        this.checked = false;
        this.targetField = null;
        this.fieldLocator = fieldLocator;

        if (!lazyResolve) {
            this.getTargetField();
        }
    }

    public Field getTargetField() {
        if (this.checked) {
            return this.targetField;
        } else {
            this.checked = true;
            this.targetField = this.fieldLocator.getField();

            if (this.targetField != null) {
                this.targetField.setAccessible(true);
            }

            return this.targetField;
        }
    }

    public Object getValue() {
        return Reflector.getFieldValue(null, this);
    }

    public void setValue(final Object value) {
        Reflector.setFieldValue(null, this, value);
    }

    public void setValue(final Object obj, final Object value) {
        Reflector.setFieldValue(obj, this, value);
    }

    public boolean exists() {
        return this.getTargetField() != null;
    }
}
