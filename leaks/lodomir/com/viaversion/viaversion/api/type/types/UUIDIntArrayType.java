/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import java.util.UUID;

public class UUIDIntArrayType
extends Type<UUID> {
    public UUIDIntArrayType() {
        super(UUID.class);
    }

    @Override
    public UUID read(ByteBuf buffer) {
        int[] ints = new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()};
        return UUIDIntArrayType.uuidFromIntArray(ints);
    }

    @Override
    public void write(ByteBuf buffer, UUID object) {
        int[] ints = UUIDIntArrayType.uuidToIntArray(object);
        buffer.writeInt(ints[0]);
        buffer.writeInt(ints[1]);
        buffer.writeInt(ints[2]);
        buffer.writeInt(ints[3]);
    }

    public static UUID uuidFromIntArray(int[] ints) {
        return new UUID((long)ints[0] << 32 | (long)ints[1] & 0xFFFFFFFFL, (long)ints[2] << 32 | (long)ints[3] & 0xFFFFFFFFL);
    }

    public static int[] uuidToIntArray(UUID uuid) {
        return UUIDIntArrayType.bitsToIntArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public static int[] bitsToIntArray(long long1, long long2) {
        return new int[]{(int)(long1 >> 32), (int)long1, (int)(long2 >> 32), (int)long2};
    }
}

