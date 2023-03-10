/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import java.util.List;

public class ParticleMapping {
    private static final ParticleData[] particles;

    public static ParticleData getMapping(int id) {
        return particles[id];
    }

    private static ParticleData rewrite(int replacementId) {
        return new ParticleData(replacementId);
    }

    private static ParticleData rewrite(int replacementId, ParticleHandler handler) {
        return new ParticleData(replacementId, handler);
    }

    static {
        ParticleHandler blockHandler = new ParticleHandler(){

            @Override
            public int[] rewrite(Protocol1_12_2To1_13 protocol, PacketWrapper wrapper) throws Exception {
                return this.rewrite(wrapper.read(Type.VAR_INT));
            }

            @Override
            public int[] rewrite(Protocol1_12_2To1_13 protocol, List<Particle.ParticleData> data) {
                return this.rewrite((Integer)data.get(0).getValue());
            }

            private int[] rewrite(int newType) {
                int blockType = Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(newType);
                int type = blockType >> 4;
                int meta = blockType & 0xF;
                return new int[]{type + (meta << 12)};
            }

            @Override
            public boolean isBlockHandler() {
                return true;
            }
        };
        particles = new ParticleData[]{ParticleMapping.rewrite(16), ParticleMapping.rewrite(20), ParticleMapping.rewrite(35), ParticleMapping.rewrite(37, blockHandler), ParticleMapping.rewrite(4), ParticleMapping.rewrite(29), ParticleMapping.rewrite(9), ParticleMapping.rewrite(44), ParticleMapping.rewrite(42), ParticleMapping.rewrite(19), ParticleMapping.rewrite(18), ParticleMapping.rewrite(30, new ParticleHandler(){

            @Override
            public int[] rewrite(Protocol1_12_2To1_13 protocol, PacketWrapper wrapper) throws Exception {
                float r = wrapper.read(Type.FLOAT).floatValue();
                float g = wrapper.read(Type.FLOAT).floatValue();
                float b = wrapper.read(Type.FLOAT).floatValue();
                float scale = wrapper.read(Type.FLOAT).floatValue();
                wrapper.set(Type.FLOAT, 3, Float.valueOf(r));
                wrapper.set(Type.FLOAT, 4, Float.valueOf(g));
                wrapper.set(Type.FLOAT, 5, Float.valueOf(b));
                wrapper.set(Type.FLOAT, 6, Float.valueOf(scale));
                wrapper.set(Type.INT, 1, 0);
                return null;
            }

            @Override
            public int[] rewrite(Protocol1_12_2To1_13 protocol, List<Particle.ParticleData> data) {
                return null;
            }
        }), ParticleMapping.rewrite(13), ParticleMapping.rewrite(41), ParticleMapping.rewrite(10), ParticleMapping.rewrite(25), ParticleMapping.rewrite(43), ParticleMapping.rewrite(15), ParticleMapping.rewrite(2), ParticleMapping.rewrite(1), ParticleMapping.rewrite(46, blockHandler), ParticleMapping.rewrite(3), ParticleMapping.rewrite(6), ParticleMapping.rewrite(26), ParticleMapping.rewrite(21), ParticleMapping.rewrite(34), ParticleMapping.rewrite(14), ParticleMapping.rewrite(36, new ParticleHandler(){

            @Override
            public int[] rewrite(Protocol1_12_2To1_13 protocol, PacketWrapper wrapper) throws Exception {
                return this.rewrite(protocol, wrapper.read(Type.FLAT_ITEM));
            }

            @Override
            public int[] rewrite(Protocol1_12_2To1_13 protocol, List<Particle.ParticleData> data) {
                return this.rewrite(protocol, (Item)data.get(0).getValue());
            }

            private int[] rewrite(Protocol1_12_2To1_13 protocol, Item newItem) {
                Item item = protocol.getItemRewriter().handleItemToClient(newItem);
                return new int[]{item.identifier(), item.data()};
            }
        }), ParticleMapping.rewrite(33), ParticleMapping.rewrite(31), ParticleMapping.rewrite(12), ParticleMapping.rewrite(27), ParticleMapping.rewrite(22), ParticleMapping.rewrite(23), ParticleMapping.rewrite(0), ParticleMapping.rewrite(24), ParticleMapping.rewrite(39), ParticleMapping.rewrite(11), ParticleMapping.rewrite(48), ParticleMapping.rewrite(12), ParticleMapping.rewrite(45), ParticleMapping.rewrite(47), ParticleMapping.rewrite(7), ParticleMapping.rewrite(5), ParticleMapping.rewrite(17), ParticleMapping.rewrite(4), ParticleMapping.rewrite(4), ParticleMapping.rewrite(4), ParticleMapping.rewrite(18), ParticleMapping.rewrite(18)};
    }

    public static final class ParticleData {
        private final int historyId;
        private final ParticleHandler handler;

        private ParticleData(int historyId, ParticleHandler handler) {
            this.historyId = historyId;
            this.handler = handler;
        }

        private ParticleData(int historyId) {
            this(historyId, (ParticleHandler)null);
        }

        public int[] rewriteData(Protocol1_12_2To1_13 protocol, PacketWrapper wrapper) throws Exception {
            if (this.handler == null) {
                return null;
            }
            return this.handler.rewrite(protocol, wrapper);
        }

        public int[] rewriteMeta(Protocol1_12_2To1_13 protocol, List<Particle.ParticleData> data) {
            if (this.handler == null) {
                return null;
            }
            return this.handler.rewrite(protocol, data);
        }

        public int getHistoryId() {
            return this.historyId;
        }

        public ParticleHandler getHandler() {
            return this.handler;
        }
    }

    public static interface ParticleHandler {
        public int[] rewrite(Protocol1_12_2To1_13 var1, PacketWrapper var2) throws Exception;

        public int[] rewrite(Protocol1_12_2To1_13 var1, List<Particle.ParticleData> var2);

        default public boolean isBlockHandler() {
            return false;
        }
    }
}

