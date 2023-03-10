/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.protocol.remapper;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.TypeRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueReader;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.protocol.remapper.ValueWriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.exception.InformativeException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class PacketRemapper {
    private final List<PacketHandler> valueRemappers = new ArrayList<PacketHandler>();

    protected PacketRemapper() {
        this.registerMap();
    }

    public void map(Type type) {
        this.handler(wrapper -> wrapper.write(type, wrapper.read(type)));
    }

    public void map(Type oldType, Type newType) {
        this.handler(wrapper -> wrapper.write(newType, wrapper.read(oldType)));
    }

    public <T1, T2> void map(Type<T1> oldType, Type<T2> newType, final Function<T1, T2> transformer) {
        this.map(oldType, new ValueTransformer<T1, T2>(newType){

            @Override
            public T2 transform(PacketWrapper wrapper, T1 inputValue) throws Exception {
                return transformer.apply(inputValue);
            }
        });
    }

    public <T1, T2> void map(ValueTransformer<T1, T2> transformer) {
        if (transformer.getInputType() == null) {
            throw new IllegalArgumentException("Use map(Type<T1>, ValueTransformer<T1, T2>) for value transformers without specified input type!");
        }
        this.map(transformer.getInputType(), transformer);
    }

    public <T1, T2> void map(Type<T1> oldType, ValueTransformer<T1, T2> transformer) {
        this.map(new TypeRemapper<T1>(oldType), transformer);
    }

    public <T> void map(ValueReader<T> inputReader, ValueWriter<T> outputWriter) {
        this.handler(wrapper -> outputWriter.write(wrapper, inputReader.read(wrapper)));
    }

    public void handler(PacketHandler handler) {
        this.valueRemappers.add(handler);
    }

    public <T> void create(Type<T> type, T value) {
        this.handler(wrapper -> wrapper.write(type, value));
    }

    public void read(Type type) {
        this.handler(wrapper -> wrapper.read(type));
    }

    public abstract void registerMap();

    public void remap(PacketWrapper packetWrapper) throws Exception {
        try {
            for (PacketHandler handler : this.valueRemappers) {
                handler.handle(packetWrapper);
            }
        }
        catch (CancelException e) {
            throw e;
        }
        catch (InformativeException e) {
            e.addSource(this.getClass());
            throw e;
        }
        catch (Exception e) {
            InformativeException ex = new InformativeException(e);
            ex.addSource(this.getClass());
            throw ex;
        }
    }
}

