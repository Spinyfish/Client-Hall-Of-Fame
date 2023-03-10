/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Union
extends Structure {
    private Structure.StructField activeField;

    protected Union() {
    }

    protected Union(Pointer p) {
        super(p);
    }

    protected Union(Pointer p, int alignType) {
        super(p, alignType);
    }

    protected Union(TypeMapper mapper) {
        super(mapper);
    }

    protected Union(Pointer p, int alignType, TypeMapper mapper) {
        super(p, alignType, mapper);
    }

    @Override
    protected List<String> getFieldOrder() {
        List<Field> flist = this.getFieldList();
        ArrayList<String> list = new ArrayList<String>(flist.size());
        for (Field f : flist) {
            list.add(f.getName());
        }
        return list;
    }

    public void setType(Class<?> type) {
        this.ensureAllocated();
        for (Structure.StructField f : this.fields().values()) {
            if (f.type != type) continue;
            this.activeField = f;
            return;
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }

    public void setType(String fieldName) {
        this.ensureAllocated();
        Structure.StructField f = this.fields().get(fieldName);
        if (f == null) {
            throw new IllegalArgumentException("No field named " + fieldName + " in " + this);
        }
        this.activeField = f;
    }

    @Override
    public Object readField(String fieldName) {
        this.ensureAllocated();
        this.setType(fieldName);
        return super.readField(fieldName);
    }

    @Override
    public void writeField(String fieldName) {
        this.ensureAllocated();
        this.setType(fieldName);
        super.writeField(fieldName);
    }

    @Override
    public void writeField(String fieldName, Object value) {
        this.ensureAllocated();
        this.setType(fieldName);
        super.writeField(fieldName, value);
    }

    public Object getTypedValue(Class<?> type) {
        this.ensureAllocated();
        for (Structure.StructField f : this.fields().values()) {
            if (f.type != type) continue;
            this.activeField = f;
            this.read();
            return this.getFieldValue(this.activeField.field);
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }

    public Object setTypedValue(Object object) {
        Structure.StructField f = this.findField(object.getClass());
        if (f != null) {
            this.activeField = f;
            this.setFieldValue(f.field, object);
            return this;
        }
        throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
    }

    private Structure.StructField findField(Class<?> type) {
        this.ensureAllocated();
        for (Structure.StructField f : this.fields().values()) {
            if (!f.type.isAssignableFrom(type)) continue;
            return f;
        }
        return null;
    }

    @Override
    protected void writeField(Structure.StructField field) {
        if (field == this.activeField) {
            super.writeField(field);
        }
    }

    @Override
    protected Object readField(Structure.StructField field) {
        if (field == this.activeField || !Structure.class.isAssignableFrom(field.type) && !String.class.isAssignableFrom(field.type) && !WString.class.isAssignableFrom(field.type)) {
            return super.readField(field);
        }
        return null;
    }

    @Override
    protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
        return super.getNativeAlignment(type, value, true);
    }
}

