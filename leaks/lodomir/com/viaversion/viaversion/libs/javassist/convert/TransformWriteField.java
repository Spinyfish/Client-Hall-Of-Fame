/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.convert;

import com.viaversion.viaversion.libs.javassist.CtClass;
import com.viaversion.viaversion.libs.javassist.CtField;
import com.viaversion.viaversion.libs.javassist.bytecode.BadBytecode;
import com.viaversion.viaversion.libs.javassist.bytecode.CodeAttribute;
import com.viaversion.viaversion.libs.javassist.bytecode.CodeIterator;
import com.viaversion.viaversion.libs.javassist.bytecode.ConstPool;
import com.viaversion.viaversion.libs.javassist.convert.TransformReadField;
import com.viaversion.viaversion.libs.javassist.convert.Transformer;

public final class TransformWriteField
extends TransformReadField {
    public TransformWriteField(Transformer next, CtField field, String methodClassname, String methodName) {
        super(next, field, methodClassname, methodName);
    }

    @Override
    public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp) throws BadBytecode {
        int c = iterator.byteAt(pos);
        if (c == 181 || c == 179) {
            int index = iterator.u16bitAt(pos + 1);
            String typedesc = TransformWriteField.isField(tclazz.getClassPool(), cp, this.fieldClass, this.fieldname, this.isPrivate, index);
            if (typedesc != null) {
                if (c == 179) {
                    CodeAttribute ca = iterator.get();
                    iterator.move(pos);
                    char c0 = typedesc.charAt(0);
                    if (c0 == 'J' || c0 == 'D') {
                        pos = iterator.insertGap(3);
                        iterator.writeByte(1, pos);
                        iterator.writeByte(91, pos + 1);
                        iterator.writeByte(87, pos + 2);
                        ca.setMaxStack(ca.getMaxStack() + 2);
                    } else {
                        pos = iterator.insertGap(2);
                        iterator.writeByte(1, pos);
                        iterator.writeByte(95, pos + 1);
                        ca.setMaxStack(ca.getMaxStack() + 1);
                    }
                    pos = iterator.next();
                }
                int mi = cp.addClassInfo(this.methodClassname);
                String type = "(Ljava/lang/Object;" + typedesc + ")V";
                int methodref = cp.addMethodrefInfo(mi, this.methodName, type);
                iterator.writeByte(184, pos);
                iterator.write16bit(methodref, pos + 1);
            }
        }
        return pos;
    }
}

