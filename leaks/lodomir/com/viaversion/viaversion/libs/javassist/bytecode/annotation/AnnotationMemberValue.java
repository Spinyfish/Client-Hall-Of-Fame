/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.bytecode.annotation;

import com.viaversion.viaversion.libs.javassist.ClassPool;
import com.viaversion.viaversion.libs.javassist.bytecode.ConstPool;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.Annotation;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.AnnotationImpl;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.AnnotationsWriter;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.MemberValue;
import com.viaversion.viaversion.libs.javassist.bytecode.annotation.MemberValueVisitor;
import java.io.IOException;
import java.lang.reflect.Method;

public class AnnotationMemberValue
extends MemberValue {
    Annotation value;

    public AnnotationMemberValue(ConstPool cp) {
        this(null, cp);
    }

    public AnnotationMemberValue(Annotation a, ConstPool cp) {
        super('@', cp);
        this.value = a;
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) throws ClassNotFoundException {
        return AnnotationImpl.make(cl, this.getType(cl), cp, this.value);
    }

    @Override
    Class<?> getType(ClassLoader cl) throws ClassNotFoundException {
        if (this.value == null) {
            throw new ClassNotFoundException("no type specified");
        }
        return AnnotationMemberValue.loadClass(cl, this.value.getTypeName());
    }

    public Annotation getValue() {
        return this.value;
    }

    public void setValue(Annotation newValue) {
        this.value = newValue;
    }

    public String toString() {
        return this.value.toString();
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.annotationValue();
        this.value.write(writer);
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitAnnotationMemberValue(this);
    }
}

