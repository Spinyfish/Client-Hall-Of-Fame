/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagScope;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ByteArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ByteArrayBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ByteBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.DoubleBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.EndBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.FloatBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntArrayBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ListBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongArrayBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ShortBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.StringBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.TrackingDataInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class BinaryTagTypes {
    public static final BinaryTagType<EndBinaryTag> END = BinaryTagType.register(EndBinaryTag.class, (byte)0, input -> EndBinaryTag.get(), null);
    public static final BinaryTagType<ByteBinaryTag> BYTE = BinaryTagType.registerNumeric(ByteBinaryTag.class, (byte)1, input -> ByteBinaryTag.of(input.readByte()), (tag, output) -> output.writeByte(tag.value()));
    public static final BinaryTagType<ShortBinaryTag> SHORT = BinaryTagType.registerNumeric(ShortBinaryTag.class, (byte)2, input -> ShortBinaryTag.of(input.readShort()), (tag, output) -> output.writeShort(tag.value()));
    public static final BinaryTagType<IntBinaryTag> INT = BinaryTagType.registerNumeric(IntBinaryTag.class, (byte)3, input -> IntBinaryTag.of(input.readInt()), (tag, output) -> output.writeInt(tag.value()));
    public static final BinaryTagType<LongBinaryTag> LONG = BinaryTagType.registerNumeric(LongBinaryTag.class, (byte)4, input -> LongBinaryTag.of(input.readLong()), (tag, output) -> output.writeLong(tag.value()));
    public static final BinaryTagType<FloatBinaryTag> FLOAT = BinaryTagType.registerNumeric(FloatBinaryTag.class, (byte)5, input -> FloatBinaryTag.of(input.readFloat()), (tag, output) -> output.writeFloat(tag.value()));
    public static final BinaryTagType<DoubleBinaryTag> DOUBLE = BinaryTagType.registerNumeric(DoubleBinaryTag.class, (byte)6, input -> DoubleBinaryTag.of(input.readDouble()), (tag, output) -> output.writeDouble(tag.value()));
    public static final BinaryTagType<ByteArrayBinaryTag> BYTE_ARRAY = BinaryTagType.register(ByteArrayBinaryTag.class, (byte)7, input -> {
        int length = input.readInt();
        try (BinaryTagScope ignored = TrackingDataInput.enter(input, length);){
            byte[] value = new byte[length];
            input.readFully(value);
            ByteArrayBinaryTag byteArrayBinaryTag = ByteArrayBinaryTag.of(value);
            return byteArrayBinaryTag;
        }
    }, (tag, output) -> {
        byte[] value = ByteArrayBinaryTagImpl.value(tag);
        output.writeInt(value.length);
        output.write(value);
    });
    public static final BinaryTagType<StringBinaryTag> STRING = BinaryTagType.register(StringBinaryTag.class, (byte)8, input -> StringBinaryTag.of(input.readUTF()), (tag, output) -> output.writeUTF(tag.value()));
    public static final BinaryTagType<ListBinaryTag> LIST = BinaryTagType.register(ListBinaryTag.class, (byte)9, input -> {
        BinaryTagType<BinaryTag> type = BinaryTagType.of(input.readByte());
        int length = input.readInt();
        try (BinaryTagScope ignored = TrackingDataInput.enter(input, (long)length * 8L);){
            ArrayList<BinaryTag> tags = new ArrayList<BinaryTag>(length);
            for (int i = 0; i < length; ++i) {
                tags.add(type.read(input));
            }
            ListBinaryTag listBinaryTag = ListBinaryTag.of(type, tags);
            return listBinaryTag;
        }
    }, (tag, output) -> {
        output.writeByte(tag.elementType().id());
        int size = tag.size();
        output.writeInt(size);
        for (BinaryTag item : tag) {
            BinaryTagType.write(item.type(), item, output);
        }
    });
    public static final BinaryTagType<CompoundBinaryTag> COMPOUND = BinaryTagType.register(CompoundBinaryTag.class, (byte)10, input -> {
        try (BinaryTagScope ignored = TrackingDataInput.enter(input);){
            BinaryTagType<BinaryTag> type;
            HashMap<String, BinaryTag> tags = new HashMap<String, BinaryTag>();
            while ((type = BinaryTagType.of(input.readByte())) != END) {
                String key = input.readUTF();
                BinaryTag tag = type.read(input);
                tags.put(key, tag);
            }
            CompoundBinaryTagImpl compoundBinaryTagImpl = new CompoundBinaryTagImpl(tags);
            return compoundBinaryTagImpl;
        }
    }, (tag, output) -> {
        for (Map.Entry entry : tag) {
            BinaryTag value = (BinaryTag)entry.getValue();
            if (value == null) continue;
            BinaryTagType<? extends BinaryTag> type = value.type();
            output.writeByte(type.id());
            if (type == END) continue;
            output.writeUTF((String)entry.getKey());
            BinaryTagType.write(type, value, output);
        }
        output.writeByte(END.id());
    });
    public static final BinaryTagType<IntArrayBinaryTag> INT_ARRAY = BinaryTagType.register(IntArrayBinaryTag.class, (byte)11, input -> {
        int length = input.readInt();
        try (BinaryTagScope ignored = TrackingDataInput.enter(input, (long)length * 4L);){
            int[] value = new int[length];
            for (int i = 0; i < length; ++i) {
                value[i] = input.readInt();
            }
            IntArrayBinaryTag intArrayBinaryTag = IntArrayBinaryTag.of(value);
            return intArrayBinaryTag;
        }
    }, (tag, output) -> {
        int[] value = IntArrayBinaryTagImpl.value(tag);
        int length = value.length;
        output.writeInt(length);
        for (int i = 0; i < length; ++i) {
            output.writeInt(value[i]);
        }
    });
    public static final BinaryTagType<LongArrayBinaryTag> LONG_ARRAY = BinaryTagType.register(LongArrayBinaryTag.class, (byte)12, input -> {
        int length = input.readInt();
        try (BinaryTagScope ignored = TrackingDataInput.enter(input, (long)length * 8L);){
            long[] value = new long[length];
            for (int i = 0; i < length; ++i) {
                value[i] = input.readLong();
            }
            LongArrayBinaryTag longArrayBinaryTag = LongArrayBinaryTag.of(value);
            return longArrayBinaryTag;
        }
    }, (tag, output) -> {
        long[] value = LongArrayBinaryTagImpl.value(tag);
        int length = value.length;
        output.writeInt(length);
        for (int i = 0; i < length; ++i) {
            output.writeLong(value[i]);
        }
    });

    private BinaryTagTypes() {
    }
}

