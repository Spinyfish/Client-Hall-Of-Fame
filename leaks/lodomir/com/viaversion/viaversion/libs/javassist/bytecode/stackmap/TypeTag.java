/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.bytecode.stackmap;

import com.viaversion.viaversion.libs.javassist.bytecode.stackmap.TypeData;

public interface TypeTag {
    public static final String TOP_TYPE = "*top*";
    public static final TypeData.BasicType TOP = new TypeData.BasicType("*top*", 0, ' ');
    public static final TypeData.BasicType INTEGER = new TypeData.BasicType("int", 1, 'I');
    public static final TypeData.BasicType FLOAT = new TypeData.BasicType("float", 2, 'F');
    public static final TypeData.BasicType DOUBLE = new TypeData.BasicType("double", 3, 'D');
    public static final TypeData.BasicType LONG = new TypeData.BasicType("long", 4, 'J');
}

