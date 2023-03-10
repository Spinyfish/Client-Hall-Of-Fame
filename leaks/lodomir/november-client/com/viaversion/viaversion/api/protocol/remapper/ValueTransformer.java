/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.protocol.remapper;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueWriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.InformativeException;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ValueTransformer<T1, T2>
implements ValueWriter<T1> {
    private final Type<T1> inputType;
    private final Type<T2> outputType;

    protected ValueTransformer(@Nullable Type<T1> inputType, Type<T2> outputType) {
        this.inputType = inputType;
        this.outputType = outputType;
    }

    protected ValueTransformer(Type<T2> outputType) {
        this(null, outputType);
    }

    public abstract T2 transform(PacketWrapper var1, T1 var2) throws Exception;

    @Override
    public void write(PacketWrapper writer, T1 inputValue) throws Exception {
        try {
            writer.write(this.outputType, this.transform(writer, inputValue));
        }
        catch (InformativeException e) {
            e.addSource(this.getClass());
            throw e;
        }
    }

    public @Nullable Type<T1> getInputType() {
        return this.inputType;
    }

    public Type<T2> getOutputType() {
        return this.outputType;
    }
}

