/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import java.lang.reflect.Field;

public class StructureWriteContext
extends ToNativeContext {
    private Structure struct;
    private Field field;

    StructureWriteContext(Structure struct, Field field) {
        this.struct = struct;
        this.field = field;
    }

    public Structure getStructure() {
        return this.struct;
    }

    public Field getField() {
        return this.field;
    }
}

