/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.StructureReadContext;
import com.sun.jna.StructureWriteContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.Union;
import com.sun.jna.WString;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class Structure {
    public static final int ALIGN_DEFAULT = 0;
    public static final int ALIGN_NONE = 1;
    public static final int ALIGN_GNUC = 2;
    public static final int ALIGN_MSVC = 3;
    protected static final int CALCULATE_SIZE = -1;
    static final Map<Class<?>, LayoutInfo> layoutInfo = new WeakHashMap();
    static final Map<Class<?>, List<String>> fieldOrder = new WeakHashMap();
    private Pointer memory;
    private int size = -1;
    private int alignType;
    private String encoding;
    private int actualAlignType;
    private int structAlignment;
    private Map<String, StructField> structFields;
    private final Map<String, Object> nativeStrings = new HashMap<String, Object>();
    private TypeMapper typeMapper;
    private long typeInfo;
    private boolean autoRead = true;
    private boolean autoWrite = true;
    private Structure[] array;
    private boolean readCalled;
    private static final ThreadLocal<Map<Pointer, Structure>> reads = new ThreadLocal<Map<Pointer, Structure>>(){

        @Override
        protected synchronized Map<Pointer, Structure> initialValue() {
            return new HashMap<Pointer, Structure>();
        }
    };
    private static final ThreadLocal<Set<Structure>> busy = new ThreadLocal<Set<Structure>>(){

        @Override
        protected synchronized Set<Structure> initialValue() {
            return new StructureSet();
        }
    };
    private static final Pointer PLACEHOLDER_MEMORY = new Pointer(0L){

        @Override
        public Pointer share(long offset, long sz) {
            return this;
        }
    };

    protected Structure() {
        this(0);
    }

    protected Structure(TypeMapper mapper) {
        this(null, 0, mapper);
    }

    protected Structure(int alignType) {
        this(null, alignType);
    }

    protected Structure(int alignType, TypeMapper mapper) {
        this(null, alignType, mapper);
    }

    protected Structure(Pointer p) {
        this(p, 0);
    }

    protected Structure(Pointer p, int alignType) {
        this(p, alignType, null);
    }

    protected Structure(Pointer p, int alignType, TypeMapper mapper) {
        this.setAlignType(alignType);
        this.setStringEncoding(Native.getStringEncoding(this.getClass()));
        this.initializeTypeMapper(mapper);
        this.validateFields();
        if (p != null) {
            this.useMemory(p, 0, true);
        } else {
            this.allocateMemory(-1);
        }
        this.initializeFields();
    }

    Map<String, StructField> fields() {
        return this.structFields;
    }

    TypeMapper getTypeMapper() {
        return this.typeMapper;
    }

    private void initializeTypeMapper(TypeMapper mapper) {
        if (mapper == null) {
            mapper = Native.getTypeMapper(this.getClass());
        }
        this.typeMapper = mapper;
        this.layoutChanged();
    }

    private void layoutChanged() {
        if (this.size != -1) {
            this.size = -1;
            if (this.memory instanceof AutoAllocated) {
                this.memory = null;
            }
            this.ensureAllocated();
        }
    }

    protected void setStringEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected String getStringEncoding() {
        return this.encoding;
    }

    protected void setAlignType(int alignType) {
        this.alignType = alignType;
        if (alignType == 0 && (alignType = Native.getStructureAlignment(this.getClass())) == 0) {
            alignType = Platform.isWindows() ? 3 : 2;
        }
        this.actualAlignType = alignType;
        this.layoutChanged();
    }

    protected Memory autoAllocate(int size) {
        return new AutoAllocated(size);
    }

    protected void useMemory(Pointer m) {
        this.useMemory(m, 0);
    }

    protected void useMemory(Pointer m, int offset) {
        this.useMemory(m, offset, false);
    }

    void useMemory(Pointer m, int offset, boolean force) {
        try {
            this.nativeStrings.clear();
            if (this instanceof ByValue && !force) {
                byte[] buf = new byte[this.size()];
                m.read(0L, buf, 0, buf.length);
                this.memory.write(0L, buf, 0, buf.length);
            } else {
                this.memory = m.share(offset);
                if (this.size == -1) {
                    this.size = this.calculateSize(false);
                }
                if (this.size != -1) {
                    this.memory = m.share(offset, this.size);
                }
            }
            this.array = null;
            this.readCalled = false;
        }
        catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Structure exceeds provided memory bounds", e);
        }
    }

    protected void ensureAllocated() {
        this.ensureAllocated(false);
    }

    private void ensureAllocated(boolean avoidFFIType) {
        if (this.memory == null) {
            this.allocateMemory(avoidFFIType);
        } else if (this.size == -1) {
            this.size = this.calculateSize(true, avoidFFIType);
            if (!(this.memory instanceof AutoAllocated)) {
                try {
                    this.memory = this.memory.share(0L, this.size);
                }
                catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Structure exceeds provided memory bounds", e);
                }
            }
        }
    }

    protected void allocateMemory() {
        this.allocateMemory(false);
    }

    private void allocateMemory(boolean avoidFFIType) {
        this.allocateMemory(this.calculateSize(true, avoidFFIType));
    }

    protected void allocateMemory(int size) {
        if (size == -1) {
            size = this.calculateSize(false);
        } else if (size <= 0) {
            throw new IllegalArgumentException("Structure size must be greater than zero: " + size);
        }
        if (size != -1) {
            if (this.memory == null || this.memory instanceof AutoAllocated) {
                this.memory = this.autoAllocate(size);
            }
            this.size = size;
        }
    }

    public int size() {
        this.ensureAllocated();
        return this.size;
    }

    public void clear() {
        this.ensureAllocated();
        this.memory.clear(this.size());
    }

    public Pointer getPointer() {
        this.ensureAllocated();
        return this.memory;
    }

    static Set<Structure> busy() {
        return busy.get();
    }

    static Map<Pointer, Structure> reading() {
        return reads.get();
    }

    void conditionalAutoRead() {
        if (!this.readCalled) {
            this.autoRead();
        }
    }

    public void read() {
        if (this.memory == PLACEHOLDER_MEMORY) {
            return;
        }
        this.readCalled = true;
        this.ensureAllocated();
        if (Structure.busy().contains(this)) {
            return;
        }
        Structure.busy().add(this);
        if (this instanceof ByReference) {
            Structure.reading().put(this.getPointer(), this);
        }
        try {
            for (StructField structField : this.fields().values()) {
                this.readField(structField);
            }
        }
        finally {
            Structure.busy().remove(this);
            if (Structure.reading().get(this.getPointer()) == this) {
                Structure.reading().remove(this.getPointer());
            }
        }
    }

    protected int fieldOffset(String name) {
        this.ensureAllocated();
        StructField f = this.fields().get(name);
        if (f == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        return f.offset;
    }

    public Object readField(String name) {
        this.ensureAllocated();
        StructField f = this.fields().get(name);
        if (f == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        return this.readField(f);
    }

    Object getFieldValue(Field field) {
        try {
            return field.get(this);
        }
        catch (Exception e) {
            throw new Error("Exception reading field '" + field.getName() + "' in " + this.getClass(), e);
        }
    }

    void setFieldValue(Field field, Object value) {
        this.setFieldValue(field, value, false);
    }

    private void setFieldValue(Field field, Object value, boolean overrideFinal) {
        try {
            field.set(this, value);
        }
        catch (IllegalAccessException e) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers)) {
                if (overrideFinal) {
                    throw new UnsupportedOperationException("This VM does not support Structures with final fields (field '" + field.getName() + "' within " + this.getClass() + ")", e);
                }
                throw new UnsupportedOperationException("Attempt to write to read-only field '" + field.getName() + "' within " + this.getClass(), e);
            }
            throw new Error("Unexpectedly unable to write to field '" + field.getName() + "' within " + this.getClass(), e);
        }
    }

    static Structure updateStructureByReference(Class<?> type, Structure s, Pointer address) {
        if (address == null) {
            s = null;
        } else if (s == null || !address.equals(s.getPointer())) {
            Structure s1 = Structure.reading().get(address);
            if (s1 != null && type.equals(s1.getClass())) {
                s = s1;
                s.autoRead();
            } else {
                s = Structure.newInstance(type, address);
                s.conditionalAutoRead();
            }
        } else {
            s.autoRead();
        }
        return s;
    }

    protected Object readField(StructField structField) {
        Pointer p;
        Object currentValue;
        int offset = structField.offset;
        Class<?> fieldType = structField.type;
        FromNativeConverter readConverter = structField.readConverter;
        if (readConverter != null) {
            fieldType = readConverter.nativeType();
        }
        Object object = currentValue = Structure.class.isAssignableFrom(fieldType) || Callback.class.isAssignableFrom(fieldType) || Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(fieldType) || Pointer.class.isAssignableFrom(fieldType) || NativeMapped.class.isAssignableFrom(fieldType) || fieldType.isArray() ? this.getFieldValue(structField.field) : null;
        Object result = fieldType == String.class ? ((p = this.memory.getPointer(offset)) == null ? null : p.getString(0L, this.encoding)) : this.memory.getValue(offset, fieldType, currentValue);
        if (readConverter != null) {
            result = readConverter.fromNative(result, structField.context);
            if (currentValue != null && currentValue.equals(result)) {
                result = currentValue;
            }
        }
        if (fieldType.equals(String.class) || fieldType.equals(WString.class)) {
            this.nativeStrings.put(structField.name + ".ptr", this.memory.getPointer(offset));
            this.nativeStrings.put(structField.name + ".val", result);
        }
        this.setFieldValue(structField.field, result, true);
        return result;
    }

    public void write() {
        if (this.memory == PLACEHOLDER_MEMORY) {
            return;
        }
        this.ensureAllocated();
        if (this instanceof ByValue) {
            this.getTypeInfo();
        }
        if (Structure.busy().contains(this)) {
            return;
        }
        Structure.busy().add(this);
        try {
            for (StructField sf : this.fields().values()) {
                if (sf.isVolatile) continue;
                this.writeField(sf);
            }
        }
        finally {
            Structure.busy().remove(this);
        }
    }

    public void writeField(String name) {
        this.ensureAllocated();
        StructField f = this.fields().get(name);
        if (f == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        this.writeField(f);
    }

    public void writeField(String name, Object value) {
        this.ensureAllocated();
        StructField structField = this.fields().get(name);
        if (structField == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        this.setFieldValue(structField.field, value);
        this.writeField(structField);
    }

    protected void writeField(StructField structField) {
        if (structField.isReadOnly) {
            return;
        }
        int offset = structField.offset;
        Object value = this.getFieldValue(structField.field);
        Class<?> fieldType = structField.type;
        ToNativeConverter converter = structField.writeConverter;
        if (converter != null) {
            value = converter.toNative(value, new StructureWriteContext(this, structField.field));
            fieldType = converter.nativeType();
        }
        if (String.class == fieldType || WString.class == fieldType) {
            boolean wide;
            boolean bl = wide = fieldType == WString.class;
            if (value != null) {
                if (this.nativeStrings.containsKey(structField.name + ".ptr") && value.equals(this.nativeStrings.get(structField.name + ".val"))) {
                    return;
                }
                NativeString nativeString = wide ? new NativeString(value.toString(), true) : new NativeString(value.toString(), this.encoding);
                this.nativeStrings.put(structField.name, nativeString);
                value = nativeString.getPointer();
            } else {
                this.nativeStrings.remove(structField.name);
            }
            this.nativeStrings.remove(structField.name + ".ptr");
            this.nativeStrings.remove(structField.name + ".val");
        }
        try {
            this.memory.setValue(offset, value, fieldType);
        }
        catch (IllegalArgumentException e) {
            String msg = "Structure field \"" + structField.name + "\" was declared as " + structField.type + (structField.type == fieldType ? "" : " (native type " + fieldType + ")") + ", which is not supported within a Structure";
            throw new IllegalArgumentException(msg, e);
        }
    }

    protected abstract List<String> getFieldOrder();

    @Deprecated
    protected final void setFieldOrder(String[] fields) {
        throw new Error("This method is obsolete, use getFieldOrder() instead");
    }

    protected void sortFields(List<Field> fields, List<String> names) {
        block0: for (int i = 0; i < names.size(); ++i) {
            String name = names.get(i);
            for (int f = 0; f < fields.size(); ++f) {
                Field field = fields.get(f);
                if (!name.equals(field.getName())) continue;
                Collections.swap(fields, i, f);
                continue block0;
            }
        }
    }

    protected List<Field> getFieldList() {
        ArrayList<Field> flist = new ArrayList<Field>();
        Class<?> cls = this.getClass();
        while (!cls.equals(Structure.class)) {
            ArrayList<Field> classFields = new ArrayList<Field>();
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                int modifiers = fields[i].getModifiers();
                if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) continue;
                classFields.add(fields[i]);
            }
            flist.addAll(0, classFields);
            cls = cls.getSuperclass();
        }
        return flist;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<String> fieldOrder() {
        Class<?> clazz = this.getClass();
        Map<Class<?>, List<String>> map = fieldOrder;
        synchronized (map) {
            List<String> list = fieldOrder.get(clazz);
            if (list == null) {
                list = this.getFieldOrder();
                fieldOrder.put(clazz, list);
            }
            return list;
        }
    }

    public static List<String> createFieldsOrder(List<String> baseFields, String ... extraFields) {
        return Structure.createFieldsOrder(baseFields, Arrays.asList(extraFields));
    }

    public static List<String> createFieldsOrder(List<String> baseFields, List<String> extraFields) {
        ArrayList<String> fields = new ArrayList<String>(baseFields.size() + extraFields.size());
        fields.addAll(baseFields);
        fields.addAll(extraFields);
        return Collections.unmodifiableList(fields);
    }

    public static List<String> createFieldsOrder(String field) {
        return Collections.unmodifiableList(Collections.singletonList(field));
    }

    public static List<String> createFieldsOrder(String ... fields) {
        return Collections.unmodifiableList(Arrays.asList(fields));
    }

    private static <T extends Comparable<T>> List<T> sort(Collection<? extends T> c) {
        ArrayList<? extends T> list = new ArrayList<T>(c);
        Collections.sort(list);
        return list;
    }

    protected List<Field> getFields(boolean force) {
        List<Field> flist = this.getFieldList();
        HashSet<String> names = new HashSet<String>();
        for (Field f : flist) {
            names.add(f.getName());
        }
        List<String> fieldOrder = this.fieldOrder();
        if (fieldOrder.size() != flist.size() && flist.size() > 1) {
            if (force) {
                throw new Error("Structure.getFieldOrder() on " + this.getClass() + " does not provide enough names [" + fieldOrder.size() + "] (" + Structure.sort(fieldOrder) + ") to match declared fields [" + flist.size() + "] (" + Structure.sort(names) + ")");
            }
            return null;
        }
        HashSet<String> orderedNames = new HashSet<String>(fieldOrder);
        if (!orderedNames.equals(names)) {
            throw new Error("Structure.getFieldOrder() on " + this.getClass() + " returns names (" + Structure.sort(fieldOrder) + ") which do not match declared field names (" + Structure.sort(names) + ")");
        }
        this.sortFields(flist, fieldOrder);
        return flist;
    }

    protected int calculateSize(boolean force) {
        return this.calculateSize(force, false);
    }

    static int size(Class<?> type) {
        return Structure.size(type, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int size(Class<?> type, Structure value) {
        int sz;
        LayoutInfo info;
        Map<Class<?>, LayoutInfo> map = layoutInfo;
        synchronized (map) {
            info = layoutInfo.get(type);
        }
        int n = sz = info != null && !info.variable ? info.size : -1;
        if (sz == -1) {
            if (value == null) {
                value = Structure.newInstance(type, PLACEHOLDER_MEMORY);
            }
            sz = value.size();
        }
        return sz;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int calculateSize(boolean force, boolean avoidFFIType) {
        LayoutInfo info;
        int size = -1;
        Class<?> clazz = this.getClass();
        Map<Class<?>, LayoutInfo> map = layoutInfo;
        synchronized (map) {
            info = layoutInfo.get(clazz);
        }
        if (info == null || this.alignType != info.alignType || this.typeMapper != info.typeMapper) {
            info = this.deriveLayout(force, avoidFFIType);
        }
        if (info != null) {
            this.structAlignment = info.alignment;
            this.structFields = info.fields;
            if (!info.variable) {
                map = layoutInfo;
                synchronized (map) {
                    if (!layoutInfo.containsKey(clazz) || this.alignType != 0 || this.typeMapper != null) {
                        layoutInfo.put(clazz, info);
                    }
                }
            }
            size = info.size;
        }
        return size;
    }

    private void validateField(String name, Class<?> type) {
        ToNativeConverter toNative;
        if (this.typeMapper != null && (toNative = this.typeMapper.getToNativeConverter(type)) != null) {
            this.validateField(name, toNative.nativeType());
            return;
        }
        if (type.isArray()) {
            this.validateField(name, type.getComponentType());
        } else {
            try {
                this.getNativeSize(type);
            }
            catch (IllegalArgumentException e) {
                String msg = "Invalid Structure field in " + this.getClass() + ", field name '" + name + "' (" + type + "): " + e.getMessage();
                throw new IllegalArgumentException(msg, e);
            }
        }
    }

    private void validateFields() {
        List<Field> fields = this.getFieldList();
        for (Field f : fields) {
            this.validateField(f.getName(), f.getType());
        }
    }

    private LayoutInfo deriveLayout(boolean force, boolean avoidFFIType) {
        int calculatedSize = 0;
        List<Field> fields = this.getFields(force);
        if (fields == null) {
            return null;
        }
        LayoutInfo info = new LayoutInfo();
        info.alignType = this.alignType;
        info.typeMapper = this.typeMapper;
        boolean firstField = true;
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            Class<?> type = field.getType();
            if (type.isArray()) {
                info.variable = true;
            }
            StructField structField = new StructField();
            structField.isVolatile = Modifier.isVolatile(modifiers);
            structField.isReadOnly = Modifier.isFinal(modifiers);
            if (structField.isReadOnly) {
                if (!Platform.RO_FIELDS) {
                    throw new IllegalArgumentException("This VM does not support read-only fields (field '" + field.getName() + "' within " + this.getClass() + ")");
                }
                field.setAccessible(true);
            }
            structField.field = field;
            structField.name = field.getName();
            structField.type = type;
            if (Callback.class.isAssignableFrom(type) && !type.isInterface()) {
                throw new IllegalArgumentException("Structure Callback field '" + field.getName() + "' must be an interface");
            }
            if (type.isArray() && Structure.class.equals(type.getComponentType())) {
                String msg = "Nested Structure arrays must use a derived Structure type so that the size of the elements can be determined";
                throw new IllegalArgumentException(msg);
            }
            int fieldAlignment = 1;
            if (Modifier.isPublic(field.getModifiers())) {
                Object value = this.getFieldValue(structField.field);
                if (value == null && type.isArray()) {
                    if (force) {
                        throw new IllegalStateException("Array fields must be initialized");
                    }
                    return null;
                }
                Class<Object> nativeType = type;
                if (NativeMapped.class.isAssignableFrom(type)) {
                    NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
                    nativeType = tc.nativeType();
                    structField.writeConverter = tc;
                    structField.readConverter = tc;
                    structField.context = new StructureReadContext(this, field);
                } else if (this.typeMapper != null) {
                    ToNativeConverter writeConverter = this.typeMapper.getToNativeConverter(type);
                    FromNativeConverter readConverter = this.typeMapper.getFromNativeConverter(type);
                    if (writeConverter != null && readConverter != null) {
                        nativeType = (value = writeConverter.toNative(value, new StructureWriteContext(this, structField.field))) != null ? value.getClass() : Pointer.class;
                        structField.writeConverter = writeConverter;
                        structField.readConverter = readConverter;
                        structField.context = new StructureReadContext(this, field);
                    } else if (writeConverter != null || readConverter != null) {
                        String msg = "Structures require bidirectional type conversion for " + type;
                        throw new IllegalArgumentException(msg);
                    }
                }
                if (value == null) {
                    value = this.initializeField(structField.field, type);
                }
                try {
                    structField.size = this.getNativeSize(nativeType, value);
                    fieldAlignment = this.getNativeAlignment(nativeType, value, firstField);
                }
                catch (IllegalArgumentException e) {
                    if (!force && this.typeMapper == null) {
                        return null;
                    }
                    String msg = "Invalid Structure field in " + this.getClass() + ", field name '" + structField.name + "' (" + structField.type + "): " + e.getMessage();
                    throw new IllegalArgumentException(msg, e);
                }
                if (fieldAlignment == 0) {
                    throw new Error("Field alignment is zero for field '" + structField.name + "' within " + this.getClass());
                }
                info.alignment = Math.max(info.alignment, fieldAlignment);
                if (calculatedSize % fieldAlignment != 0) {
                    calculatedSize += fieldAlignment - calculatedSize % fieldAlignment;
                }
                if (this instanceof Union) {
                    structField.offset = 0;
                    calculatedSize = Math.max(calculatedSize, structField.size);
                } else {
                    structField.offset = calculatedSize;
                    calculatedSize += structField.size;
                }
                info.fields.put(structField.name, structField);
                if (info.typeInfoField == null || ((LayoutInfo)info).typeInfoField.size < structField.size || ((LayoutInfo)info).typeInfoField.size == structField.size && Structure.class.isAssignableFrom(structField.type)) {
                    info.typeInfoField = structField;
                }
            }
            firstField = false;
        }
        if (calculatedSize > 0) {
            int size = this.addPadding(calculatedSize, info.alignment);
            if (this instanceof ByValue && !avoidFFIType) {
                this.getTypeInfo();
            }
            info.size = size;
            return info;
        }
        throw new IllegalArgumentException("Structure " + this.getClass() + " has unknown or zero size (ensure all fields are public)");
    }

    private void initializeFields() {
        List<Field> flist = this.getFieldList();
        for (Field f : flist) {
            try {
                Object o = f.get(this);
                if (o != null) continue;
                this.initializeField(f, f.getType());
            }
            catch (Exception e) {
                throw new Error("Exception reading field '" + f.getName() + "' in " + this.getClass(), e);
            }
        }
    }

    private Object initializeField(Field field, Class<?> type) {
        Object value = null;
        if (Structure.class.isAssignableFrom(type) && !ByReference.class.isAssignableFrom(type)) {
            try {
                value = Structure.newInstance(type, PLACEHOLDER_MEMORY);
                this.setFieldValue(field, value);
            }
            catch (IllegalArgumentException e) {
                String msg = "Can't determine size of nested structure";
                throw new IllegalArgumentException(msg, e);
            }
        } else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
            value = tc.defaultValue();
            this.setFieldValue(field, value);
        }
        return value;
    }

    private int addPadding(int calculatedSize) {
        return this.addPadding(calculatedSize, this.structAlignment);
    }

    private int addPadding(int calculatedSize, int alignment) {
        if (this.actualAlignType != 1 && calculatedSize % alignment != 0) {
            calculatedSize += alignment - calculatedSize % alignment;
        }
        return calculatedSize;
    }

    protected int getStructAlignment() {
        if (this.size == -1) {
            this.calculateSize(true);
        }
        return this.structAlignment;
    }

    protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
        int alignment = 1;
        if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
            type = tc.nativeType();
            value = tc.toNative(value, new ToNativeContext());
        }
        int size = Native.getNativeSize(type, value);
        if (type.isPrimitive() || Long.class == type || Integer.class == type || Short.class == type || Character.class == type || Byte.class == type || Boolean.class == type || Float.class == type || Double.class == type) {
            alignment = size;
        } else if (Pointer.class.isAssignableFrom(type) && !Function.class.isAssignableFrom(type) || Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type) || Callback.class.isAssignableFrom(type) || WString.class == type || String.class == type) {
            alignment = Pointer.SIZE;
        } else if (Structure.class.isAssignableFrom(type)) {
            if (ByReference.class.isAssignableFrom(type)) {
                alignment = Pointer.SIZE;
            } else {
                if (value == null) {
                    value = Structure.newInstance(type, PLACEHOLDER_MEMORY);
                }
                alignment = ((Structure)value).getStructAlignment();
            }
        } else if (type.isArray()) {
            alignment = this.getNativeAlignment(type.getComponentType(), null, isFirstElement);
        } else {
            throw new IllegalArgumentException("Type " + type + " has unknown native alignment");
        }
        if (this.actualAlignType == 1) {
            alignment = 1;
        } else if (this.actualAlignType == 3) {
            alignment = Math.min(8, alignment);
        } else if (this.actualAlignType == 2) {
            if (!(isFirstElement && Platform.isMac() && Platform.isPPC())) {
                alignment = Math.min(Native.MAX_ALIGNMENT, alignment);
            }
            if (!isFirstElement && Platform.isAIX() && (type == Double.TYPE || type == Double.class)) {
                alignment = 4;
            }
        }
        return alignment;
    }

    public String toString() {
        return this.toString(Boolean.getBoolean("jna.dump_memory"));
    }

    public String toString(boolean debug) {
        return this.toString(0, true, debug);
    }

    private String format(Class<?> type) {
        String s = type.getName();
        int dot = s.lastIndexOf(".");
        return s.substring(dot + 1);
    }

    private String toString(int indent, boolean showContents, boolean dumpMemory) {
        this.ensureAllocated();
        String LS = System.getProperty("line.separator");
        String name = this.format(this.getClass()) + "(" + this.getPointer() + ")";
        if (!(this.getPointer() instanceof Memory)) {
            name = name + " (" + this.size() + " bytes)";
        }
        String prefix = "";
        for (int idx = 0; idx < indent; ++idx) {
            prefix = prefix + "  ";
        }
        String contents = LS;
        if (!showContents) {
            contents = "...}";
        } else {
            Iterator<StructField> i = this.fields().values().iterator();
            while (i.hasNext()) {
                StructField sf = i.next();
                Object value = this.getFieldValue(sf.field);
                String type = this.format(sf.type);
                String index = "";
                contents = contents + prefix;
                if (sf.type.isArray() && value != null) {
                    type = this.format(sf.type.getComponentType());
                    index = "[" + Array.getLength(value) + "]";
                }
                contents = contents + String.format("  %s %s%s@0x%X", type, sf.name, index, sf.offset);
                if (value instanceof Structure) {
                    value = ((Structure)value).toString(indent + 1, !(value instanceof ByReference), dumpMemory);
                }
                contents = contents + "=";
                contents = value instanceof Long ? contents + String.format("0x%08X", (Long)value) : (value instanceof Integer ? contents + String.format("0x%04X", (Integer)value) : (value instanceof Short ? contents + String.format("0x%02X", (Short)value) : (value instanceof Byte ? contents + String.format("0x%01X", (Byte)value) : contents + String.valueOf(value).trim())));
                contents = contents + LS;
                if (i.hasNext()) continue;
                contents = contents + prefix + "}";
            }
        }
        if (indent == 0 && dumpMemory) {
            int BYTES_PER_ROW = 4;
            contents = contents + LS + "memory dump" + LS;
            byte[] buf = this.getPointer().getByteArray(0L, this.size());
            for (int i = 0; i < buf.length; ++i) {
                if (i % 4 == 0) {
                    contents = contents + "[";
                }
                if (buf[i] >= 0 && buf[i] < 16) {
                    contents = contents + "0";
                }
                contents = contents + Integer.toHexString(buf[i] & 0xFF);
                if (i % 4 != 3 || i >= buf.length - 1) continue;
                contents = contents + "]" + LS;
            }
            contents = contents + "]";
        }
        return name + " {" + contents;
    }

    public Structure[] toArray(Structure[] array) {
        this.ensureAllocated();
        if (this.memory instanceof AutoAllocated) {
            Memory m = (Memory)this.memory;
            int requiredSize = array.length * this.size();
            if (m.size() < (long)requiredSize) {
                this.useMemory(this.autoAllocate(requiredSize));
            }
        }
        array[0] = this;
        int size = this.size();
        for (int i = 1; i < array.length; ++i) {
            array[i] = Structure.newInstance(this.getClass(), this.memory.share(i * size, size));
            array[i].conditionalAutoRead();
        }
        if (!(this instanceof ByValue)) {
            this.array = array;
        }
        return array;
    }

    public Structure[] toArray(int size) {
        return this.toArray((Structure[])Array.newInstance(this.getClass(), size));
    }

    private Class<?> baseClass() {
        if ((this instanceof ByReference || this instanceof ByValue) && Structure.class.isAssignableFrom(this.getClass().getSuperclass())) {
            return this.getClass().getSuperclass();
        }
        return this.getClass();
    }

    public boolean dataEquals(Structure s) {
        return this.dataEquals(s, false);
    }

    public boolean dataEquals(Structure s, boolean clear) {
        byte[] ref;
        byte[] data;
        if (clear) {
            s.getPointer().clear(s.size());
            s.write();
            this.getPointer().clear(this.size());
            this.write();
        }
        if ((data = s.getPointer().getByteArray(0L, s.size())).length == (ref = this.getPointer().getByteArray(0L, this.size())).length) {
            for (int i = 0; i < data.length; ++i) {
                if (data[i] == ref[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        return o instanceof Structure && o.getClass() == this.getClass() && ((Structure)o).getPointer().equals(this.getPointer());
    }

    public int hashCode() {
        Pointer p = this.getPointer();
        if (p != null) {
            return this.getPointer().hashCode();
        }
        return this.getClass().hashCode();
    }

    protected void cacheTypeInfo(Pointer p) {
        this.typeInfo = p.peer;
    }

    Pointer getFieldTypeInfo(StructField f) {
        ToNativeConverter nc;
        Class<?> type = f.type;
        Object value = this.getFieldValue(f.field);
        if (this.typeMapper != null && (nc = this.typeMapper.getToNativeConverter(type)) != null) {
            type = nc.nativeType();
            value = nc.toNative(value, new ToNativeContext());
        }
        return FFIType.get(value, type);
    }

    Pointer getTypeInfo() {
        Pointer p = Structure.getTypeInfo(this);
        this.cacheTypeInfo(p);
        return p;
    }

    public void setAutoSynch(boolean auto) {
        this.setAutoRead(auto);
        this.setAutoWrite(auto);
    }

    public void setAutoRead(boolean auto) {
        this.autoRead = auto;
    }

    public boolean getAutoRead() {
        return this.autoRead;
    }

    public void setAutoWrite(boolean auto) {
        this.autoWrite = auto;
    }

    public boolean getAutoWrite() {
        return this.autoWrite;
    }

    static Pointer getTypeInfo(Object obj) {
        return FFIType.get(obj);
    }

    private static Structure newInstance(Class<?> type, long init) {
        try {
            Structure s = Structure.newInstance(type, init == 0L ? PLACEHOLDER_MEMORY : new Pointer(init));
            if (init != 0L) {
                s.conditionalAutoRead();
            }
            return s;
        }
        catch (Throwable e) {
            System.err.println("JNA: Error creating structure: " + e);
            return null;
        }
    }

    public static Structure newInstance(Class<?> type, Pointer init) throws IllegalArgumentException {
        try {
            Constructor<?> ctor = type.getConstructor(Pointer.class);
            return (Structure)ctor.newInstance(init);
        }
        catch (NoSuchMethodException ctor) {
        }
        catch (SecurityException ctor) {
        }
        catch (InstantiationException e) {
            String msg = "Can't instantiate " + type;
            throw new IllegalArgumentException(msg, e);
        }
        catch (IllegalAccessException e) {
            String msg = "Instantiation of " + type + " (Pointer) not allowed, is it public?";
            throw new IllegalArgumentException(msg, e);
        }
        catch (InvocationTargetException e) {
            String msg = "Exception thrown while instantiating an instance of " + type;
            e.printStackTrace();
            throw new IllegalArgumentException(msg, e);
        }
        Structure s = Structure.newInstance(type);
        if (init != PLACEHOLDER_MEMORY) {
            s.useMemory(init);
        }
        return s;
    }

    public static Structure newInstance(Class<?> type) throws IllegalArgumentException {
        try {
            Structure s = (Structure)type.newInstance();
            if (s instanceof ByValue) {
                s.allocateMemory();
            }
            return s;
        }
        catch (InstantiationException e) {
            String msg = "Can't instantiate " + type;
            throw new IllegalArgumentException(msg, e);
        }
        catch (IllegalAccessException e) {
            String msg = "Instantiation of " + type + " not allowed, is it public?";
            throw new IllegalArgumentException(msg, e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    StructField typeInfoField() {
        LayoutInfo info;
        Map<Class<?>, LayoutInfo> map = layoutInfo;
        synchronized (map) {
            info = layoutInfo.get(this.getClass());
        }
        if (info != null) {
            return info.typeInfoField;
        }
        return null;
    }

    private static void structureArrayCheck(Structure[] ss) {
        if (ByReference[].class.isAssignableFrom(ss.getClass())) {
            return;
        }
        Pointer base = ss[0].getPointer();
        int size = ss[0].size();
        for (int si = 1; si < ss.length; ++si) {
            if (ss[si].getPointer().peer == base.peer + (long)(size * si)) continue;
            String msg = "Structure array elements must use contiguous memory (bad backing address at Structure array index " + si + ")";
            throw new IllegalArgumentException(msg);
        }
    }

    public static void autoRead(Structure[] ss) {
        Structure.structureArrayCheck(ss);
        if (ss[0].array == ss) {
            ss[0].autoRead();
        } else {
            for (int si = 0; si < ss.length; ++si) {
                if (ss[si] == null) continue;
                ss[si].autoRead();
            }
        }
    }

    public void autoRead() {
        if (this.getAutoRead()) {
            this.read();
            if (this.array != null) {
                for (int i = 1; i < this.array.length; ++i) {
                    this.array[i].autoRead();
                }
            }
        }
    }

    public static void autoWrite(Structure[] ss) {
        Structure.structureArrayCheck(ss);
        if (ss[0].array == ss) {
            ss[0].autoWrite();
        } else {
            for (int si = 0; si < ss.length; ++si) {
                if (ss[si] == null) continue;
                ss[si].autoWrite();
            }
        }
    }

    public void autoWrite() {
        if (this.getAutoWrite()) {
            this.write();
            if (this.array != null) {
                for (int i = 1; i < this.array.length; ++i) {
                    this.array[i].autoWrite();
                }
            }
        }
    }

    protected int getNativeSize(Class<?> nativeType) {
        return this.getNativeSize(nativeType, null);
    }

    protected int getNativeSize(Class<?> nativeType, Object value) {
        return Native.getNativeSize(nativeType, value);
    }

    static void validate(Class<?> cls) {
        Structure.newInstance(cls, PLACEHOLDER_MEMORY);
    }

    private static class AutoAllocated
    extends Memory {
        public AutoAllocated(int size) {
            super(size);
            super.clear();
        }

        @Override
        public String toString() {
            return "auto-" + super.toString();
        }
    }

    static class FFIType
    extends Structure {
        private static final Map<Object, Object> typeInfoMap = new WeakHashMap<Object, Object>();
        private static final int FFI_TYPE_STRUCT = 13;
        public size_t size;
        public short alignment;
        public short type = (short)13;
        public Pointer elements;

        private FFIType(Structure ref) {
            Pointer[] els;
            ref.ensureAllocated(true);
            if (ref instanceof Union) {
                StructField sf = ((Union)ref).typeInfoField();
                els = new Pointer[]{FFIType.get(ref.getFieldValue(sf.field), sf.type), null};
            } else {
                els = new Pointer[ref.fields().size() + 1];
                int idx = 0;
                for (StructField sf : ref.fields().values()) {
                    els[idx++] = ref.getFieldTypeInfo(sf);
                }
            }
            this.init(els);
        }

        private FFIType(Object array, Class<?> type) {
            int length = Array.getLength(array);
            Pointer[] els = new Pointer[length + 1];
            Pointer p = FFIType.get(null, type.getComponentType());
            for (int i = 0; i < length; ++i) {
                els[i] = p;
            }
            this.init(els);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("size", "alignment", "type", "elements");
        }

        private void init(Pointer[] els) {
            this.elements = new Memory(Pointer.SIZE * els.length);
            this.elements.write(0L, els, 0, els.length);
            this.write();
        }

        static Pointer get(Object obj) {
            if (obj == null) {
                return FFITypes.ffi_type_pointer;
            }
            if (obj instanceof Class) {
                return FFIType.get(null, (Class)obj);
            }
            return FFIType.get(obj, obj.getClass());
        }

        private static Pointer get(Object obj, Class<?> cls) {
            ToNativeConverter nc;
            TypeMapper mapper = Native.getTypeMapper(cls);
            if (mapper != null && (nc = mapper.getToNativeConverter(cls)) != null) {
                cls = nc.nativeType();
            }
            Map<Object, Object> map = typeInfoMap;
            synchronized (map) {
                Object o = typeInfoMap.get(cls);
                if (o instanceof Pointer) {
                    return (Pointer)o;
                }
                if (o instanceof FFIType) {
                    return ((FFIType)o).getPointer();
                }
                if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(cls) || Callback.class.isAssignableFrom(cls)) {
                    typeInfoMap.put(cls, FFITypes.ffi_type_pointer);
                    return FFITypes.ffi_type_pointer;
                }
                if (Structure.class.isAssignableFrom(cls)) {
                    if (obj == null) {
                        obj = FFIType.newInstance(cls, PLACEHOLDER_MEMORY);
                    }
                    if (ByReference.class.isAssignableFrom(cls)) {
                        typeInfoMap.put(cls, FFITypes.ffi_type_pointer);
                        return FFITypes.ffi_type_pointer;
                    }
                    FFIType type = new FFIType((Structure)obj);
                    typeInfoMap.put(cls, type);
                    return type.getPointer();
                }
                if (NativeMapped.class.isAssignableFrom(cls)) {
                    NativeMappedConverter c = NativeMappedConverter.getInstance(cls);
                    return FFIType.get(c.toNative(obj, new ToNativeContext()), c.nativeType());
                }
                if (cls.isArray()) {
                    FFIType type = new FFIType(obj, cls);
                    typeInfoMap.put(obj, type);
                    return type.getPointer();
                }
                throw new IllegalArgumentException("Unsupported type " + cls);
            }
        }

        static {
            if (Native.POINTER_SIZE == 0) {
                throw new Error("Native library not initialized");
            }
            if (FFITypes.ffi_type_void == null) {
                throw new Error("FFI types not initialized");
            }
            typeInfoMap.put(Void.TYPE, FFITypes.ffi_type_void);
            typeInfoMap.put(Void.class, FFITypes.ffi_type_void);
            typeInfoMap.put(Float.TYPE, FFITypes.ffi_type_float);
            typeInfoMap.put(Float.class, FFITypes.ffi_type_float);
            typeInfoMap.put(Double.TYPE, FFITypes.ffi_type_double);
            typeInfoMap.put(Double.class, FFITypes.ffi_type_double);
            typeInfoMap.put(Long.TYPE, FFITypes.ffi_type_sint64);
            typeInfoMap.put(Long.class, FFITypes.ffi_type_sint64);
            typeInfoMap.put(Integer.TYPE, FFITypes.ffi_type_sint32);
            typeInfoMap.put(Integer.class, FFITypes.ffi_type_sint32);
            typeInfoMap.put(Short.TYPE, FFITypes.ffi_type_sint16);
            typeInfoMap.put(Short.class, FFITypes.ffi_type_sint16);
            Pointer ctype = Native.WCHAR_SIZE == 2 ? FFITypes.ffi_type_uint16 : FFITypes.ffi_type_uint32;
            typeInfoMap.put(Character.TYPE, ctype);
            typeInfoMap.put(Character.class, ctype);
            typeInfoMap.put(Byte.TYPE, FFITypes.ffi_type_sint8);
            typeInfoMap.put(Byte.class, FFITypes.ffi_type_sint8);
            typeInfoMap.put(Pointer.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(String.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(WString.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(Boolean.TYPE, FFITypes.ffi_type_uint32);
            typeInfoMap.put(Boolean.class, FFITypes.ffi_type_uint32);
        }

        private static class FFITypes {
            private static Pointer ffi_type_void;
            private static Pointer ffi_type_float;
            private static Pointer ffi_type_double;
            private static Pointer ffi_type_longdouble;
            private static Pointer ffi_type_uint8;
            private static Pointer ffi_type_sint8;
            private static Pointer ffi_type_uint16;
            private static Pointer ffi_type_sint16;
            private static Pointer ffi_type_uint32;
            private static Pointer ffi_type_sint32;
            private static Pointer ffi_type_uint64;
            private static Pointer ffi_type_sint64;
            private static Pointer ffi_type_pointer;

            private FFITypes() {
            }
        }

        public static class size_t
        extends IntegerType {
            private static final long serialVersionUID = 1L;

            public size_t() {
                this(0L);
            }

            public size_t(long value) {
                super(Native.SIZE_T_SIZE, value);
            }
        }
    }

    protected static class StructField {
        public String name;
        public Class<?> type;
        public Field field;
        public int size = -1;
        public int offset = -1;
        public boolean isVolatile;
        public boolean isReadOnly;
        public FromNativeConverter readConverter;
        public ToNativeConverter writeConverter;
        public FromNativeContext context;

        protected StructField() {
        }

        public String toString() {
            return this.name + "@" + this.offset + "[" + this.size + "] (" + this.type + ")";
        }
    }

    private static class LayoutInfo {
        private int size = -1;
        private int alignment = 1;
        private final Map<String, StructField> fields = Collections.synchronizedMap(new LinkedHashMap());
        private int alignType = 0;
        private TypeMapper typeMapper;
        private boolean variable;
        private StructField typeInfoField;

        private LayoutInfo() {
        }
    }

    static class StructureSet
    extends AbstractCollection<Structure>
    implements Set<Structure> {
        Structure[] elements;
        private int count;

        StructureSet() {
        }

        private void ensureCapacity(int size) {
            if (this.elements == null) {
                this.elements = new Structure[size * 3 / 2];
            } else if (this.elements.length < size) {
                Structure[] e = new Structure[size * 3 / 2];
                System.arraycopy(this.elements, 0, e, 0, this.elements.length);
                this.elements = e;
            }
        }

        public Structure[] getElements() {
            return this.elements;
        }

        @Override
        public int size() {
            return this.count;
        }

        @Override
        public boolean contains(Object o) {
            return this.indexOf((Structure)o) != -1;
        }

        @Override
        public boolean add(Structure o) {
            if (!this.contains(o)) {
                this.ensureCapacity(this.count + 1);
                this.elements[this.count++] = o;
            }
            return true;
        }

        private int indexOf(Structure s1) {
            for (int i = 0; i < this.count; ++i) {
                Structure s2 = this.elements[i];
                if (s1 != s2 && (s1.getClass() != s2.getClass() || s1.size() != s2.size() || !s1.getPointer().equals(s2.getPointer()))) continue;
                return i;
            }
            return -1;
        }

        @Override
        public boolean remove(Object o) {
            int idx = this.indexOf((Structure)o);
            if (idx != -1) {
                if (--this.count >= 0) {
                    this.elements[idx] = this.elements[this.count];
                    this.elements[this.count] = null;
                }
                return true;
            }
            return false;
        }

        @Override
        public Iterator<Structure> iterator() {
            Structure[] e = new Structure[this.count];
            if (this.count > 0) {
                System.arraycopy(this.elements, 0, e, 0, this.count);
            }
            return Arrays.asList(e).iterator();
        }
    }

    public static interface ByReference {
    }

    public static interface ByValue {
    }
}

