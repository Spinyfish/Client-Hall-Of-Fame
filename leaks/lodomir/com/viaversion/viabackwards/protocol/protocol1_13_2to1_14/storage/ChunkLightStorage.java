/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChunkLightStorage
extends StoredObject {
    public static final byte[] FULL_LIGHT = new byte[2048];
    public static final byte[] EMPTY_LIGHT = new byte[2048];
    private static Constructor<?> fastUtilLongObjectHashMap;
    private final Map<Long, ChunkLight> storedLight = this.createLongObjectMap();

    public ChunkLightStorage(UserConnection user) {
        super(user);
    }

    public void setStoredLight(byte[][] skyLight, byte[][] blockLight, int x, int z) {
        this.storedLight.put(this.getChunkSectionIndex(x, z), new ChunkLight(skyLight, blockLight));
    }

    public ChunkLight getStoredLight(int x, int z) {
        return this.storedLight.get(this.getChunkSectionIndex(x, z));
    }

    public void clear() {
        this.storedLight.clear();
    }

    public void unloadChunk(int x, int z) {
        this.storedLight.remove(this.getChunkSectionIndex(x, z));
    }

    private long getChunkSectionIndex(int x, int z) {
        return ((long)x & 0x3FFFFFFL) << 38 | (long)z & 0x3FFFFFFL;
    }

    private Map<Long, ChunkLight> createLongObjectMap() {
        if (fastUtilLongObjectHashMap != null) {
            try {
                return (Map)fastUtilLongObjectHashMap.newInstance(new Object[0]);
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<Long, ChunkLight>();
    }

    static {
        Arrays.fill(FULL_LIGHT, (byte)-1);
        Arrays.fill(EMPTY_LIGHT, (byte)0);
        try {
            fastUtilLongObjectHashMap = Class.forName("com.viaversion.viaversion.libs.fastutil.longs.Long2ObjectOpenHashMap").getConstructor(new Class[0]);
        }
        catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
            // empty catch block
        }
    }

    public static class ChunkLight {
        private final byte[][] skyLight;
        private final byte[][] blockLight;

        public ChunkLight(byte[][] skyLight, byte[][] blockLight) {
            this.skyLight = skyLight;
            this.blockLight = blockLight;
        }

        public byte[][] getSkyLight() {
            return this.skyLight;
        }

        public byte[][] getBlockLight() {
            return this.blockLight;
        }
    }
}

