/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.DataPaletteImpl;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.CompactArrayUtil;
import com.viaversion.viaversion.util.MathUtil;
import io.netty.buffer.ByteBuf;

public final class PaletteType1_18
extends Type<DataPalette> {
    private final int globalPaletteBits;
    private final PaletteType type;

    public PaletteType1_18(PaletteType type, int globalPaletteBits) {
        super(DataPalette.class);
        this.globalPaletteBits = globalPaletteBits;
        this.type = type;
    }

    @Override
    public DataPalette read(ByteBuf buffer) throws Exception {
        long[] values;
        DataPaletteImpl palette;
        int bitsPerValue;
        int originalBitsPerValue = bitsPerValue = buffer.readByte();
        if (bitsPerValue > this.type.highestBitsPerValue()) {
            bitsPerValue = this.globalPaletteBits;
        }
        if (bitsPerValue == 0) {
            DataPaletteImpl palette2 = new DataPaletteImpl(this.type.size(), 1);
            palette2.addId(Type.VAR_INT.readPrimitive(buffer));
            Type.VAR_INT.readPrimitive(buffer);
            return palette2;
        }
        if (bitsPerValue != this.globalPaletteBits) {
            int paletteLength = Type.VAR_INT.readPrimitive(buffer);
            palette = new DataPaletteImpl(this.type.size(), paletteLength);
            for (int i = 0; i < paletteLength; ++i) {
                palette.addId(Type.VAR_INT.readPrimitive(buffer));
            }
        } else {
            palette = new DataPaletteImpl(this.type.size());
        }
        if ((values = new long[Type.VAR_INT.readPrimitive(buffer)]).length > 0) {
            char valuesPerLong = (char)(64 / bitsPerValue);
            int expectedLength = (this.type.size() + valuesPerLong - 1) / valuesPerLong;
            if (values.length != expectedLength) {
                throw new IllegalStateException("Palette data length (" + values.length + ") does not match expected length (" + expectedLength + ")! bitsPerValue=" + bitsPerValue + ", originalBitsPerValue=" + originalBitsPerValue);
            }
            for (int i = 0; i < values.length; ++i) {
                values[i] = buffer.readLong();
            }
            CompactArrayUtil.iterateCompactArrayWithPadding(bitsPerValue, this.type.size(), values, bitsPerValue == this.globalPaletteBits ? palette::setIdAt : palette::setPaletteIndexAt);
        }
        return palette;
    }

    @Override
    public void write(ByteBuf buffer, DataPalette palette) throws Exception {
        if (palette.size() == 1) {
            buffer.writeByte(0);
            Type.VAR_INT.writePrimitive(buffer, palette.idByIndex(0));
            Type.VAR_INT.writePrimitive(buffer, 0);
            return;
        }
        int min = this.type == PaletteType.BLOCKS ? 4 : 1;
        int bitsPerValue = MathUtil.clamp(MathUtil.ceilLog2(palette.size()), min, this.type.highestBitsPerValue());
        buffer.writeByte(bitsPerValue);
        if (bitsPerValue != this.globalPaletteBits) {
            Type.VAR_INT.writePrimitive(buffer, palette.size());
            for (int i = 0; i < palette.size(); ++i) {
                Type.VAR_INT.writePrimitive(buffer, palette.idByIndex(i));
            }
        }
        long[] data = CompactArrayUtil.createCompactArrayWithPadding(bitsPerValue, this.type.size(), bitsPerValue == this.globalPaletteBits ? palette::idAt : palette::paletteIndexAt);
        Type.VAR_INT.writePrimitive(buffer, data.length);
        for (long l : data) {
            buffer.writeLong(l);
        }
    }
}

