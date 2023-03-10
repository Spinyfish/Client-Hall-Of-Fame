/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.bytecode.annotation;

import com.viaversion.viaversion.libs.javassist.ClassPool;
import com.viaversion.viaversion.libs.javassist.bytecode.ConstPool;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.AnnotationsWriter;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.MemberValue;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.MemberValueVisitor;
import java.io.IOException;
import java.lang.reflect.Method;

public class BooleanMemberValue
extends MemberValue {
    int valueIndex;

    public BooleanMemberValue(int index, ConstPool cp) {
        super('Z', cp);
        this.valueIndex = index;
    }

    public BooleanMemberValue(boolean b, ConstPool cp) {
        super('Z', cp);
        this.setValue(b);
    }

    public BooleanMemberValue(ConstPool cp) {
        super('Z', cp);
        this.setValue(false);
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) {
        return this.getValue();
    }

    @Override
    Class<?> getType(ClassLoader cl) {
        return Boolean.TYPE;
    }

    public boolean getValue() {
        return this.cp.getIntegerInfo(this.valueIndex) != 0;
    }

    public void setValue(boolean newValue) {
        this.valueIndex = this.cp.addIntegerInfo(newValue ? 1 : 0);
    }

    public String toString() {
        return this.getValue() ? "true" : "false";
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitBooleanMemberValue(this);
    }
}

