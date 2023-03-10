/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.WorldPackets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleRewriter {
    private static final List<NewParticle> particles = new ArrayList<NewParticle>();

    public static Particle rewriteParticle(int particleId, Integer[] data) {
        if (particleId >= particles.size()) {
            Via.getPlatform().getLogger().severe("Failed to transform particles with id " + particleId + " and data " + Arrays.toString((Object[])data));
            return null;
        }
        NewParticle rewrite = particles.get(particleId);
        return rewrite.handle(new Particle(rewrite.getId()), data);
    }

    private static void add(int newId) {
        particles.add(new NewParticle(newId, null));
    }

    private static void add(int newId, ParticleDataHandler dataHandler) {
        particles.add(new NewParticle(newId, dataHandler));
    }

    private static ParticleDataHandler reddustHandler() {
        return new ParticleDataHandler(){

            @Override
            public Particle handler(Particle particle, Integer[] data) {
                particle.getArguments().add(new Particle.ParticleData(Type.FLOAT, Float.valueOf(ParticleRewriter.randomBool() ? 1.0f : 0.0f)));
                particle.getArguments().add(new Particle.ParticleData(Type.FLOAT, Float.valueOf(0.0f)));
                particle.getArguments().add(new Particle.ParticleData(Type.FLOAT, Float.valueOf(ParticleRewriter.randomBool() ? 1.0f : 0.0f)));
                particle.getArguments().add(new Particle.ParticleData(Type.FLOAT, Float.valueOf(1.0f)));
                return particle;
            }
        };
    }

    private static boolean randomBool() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    private static ParticleDataHandler iconcrackHandler() {
        return new ParticleDataHandler(){

            @Override
            public Particle handler(Particle particle, Integer[] data) {
                DataItem item;
                if (data.length == 1) {
                    item = new DataItem(data[0], 1, 0, null);
                } else if (data.length == 2) {
                    item = new DataItem(data[0], 1, data[1].shortValue(), null);
                } else {
                    return particle;
                }
                Via.getManager().getProtocolManager().getProtocol(Protocol1_13To1_12_2.class).getItemRewriter().handleItemToClient(item);
                particle.getArguments().add(new Particle.ParticleData(Type.FLAT_ITEM, item));
                return particle;
            }
        };
    }

    private static ParticleDataHandler blockHandler() {
        return new ParticleDataHandler(){

            @Override
            public Particle handler(Particle particle, Integer[] data) {
                int value = data[0];
                int combined = (value & 0xFFF) << 4 | value >> 12 & 0xF;
                int newId = WorldPackets.toNewId(combined);
                particle.getArguments().add(new Particle.ParticleData(Type.VAR_INT, newId));
                return particle;
            }
        };
    }

    static {
        ParticleRewriter.add(34);
        ParticleRewriter.add(19);
        ParticleRewriter.add(18);
        ParticleRewriter.add(21);
        ParticleRewriter.add(4);
        ParticleRewriter.add(43);
        ParticleRewriter.add(22);
        ParticleRewriter.add(42);
        ParticleRewriter.add(42);
        ParticleRewriter.add(6);
        ParticleRewriter.add(14);
        ParticleRewriter.add(37);
        ParticleRewriter.add(30);
        ParticleRewriter.add(12);
        ParticleRewriter.add(26);
        ParticleRewriter.add(17);
        ParticleRewriter.add(0);
        ParticleRewriter.add(44);
        ParticleRewriter.add(10);
        ParticleRewriter.add(9);
        ParticleRewriter.add(1);
        ParticleRewriter.add(24);
        ParticleRewriter.add(32);
        ParticleRewriter.add(33);
        ParticleRewriter.add(35);
        ParticleRewriter.add(15);
        ParticleRewriter.add(23);
        ParticleRewriter.add(31);
        ParticleRewriter.add(-1);
        ParticleRewriter.add(5);
        ParticleRewriter.add(11, ParticleRewriter.reddustHandler());
        ParticleRewriter.add(29);
        ParticleRewriter.add(34);
        ParticleRewriter.add(28);
        ParticleRewriter.add(25);
        ParticleRewriter.add(2);
        ParticleRewriter.add(27, ParticleRewriter.iconcrackHandler());
        ParticleRewriter.add(3, ParticleRewriter.blockHandler());
        ParticleRewriter.add(3, ParticleRewriter.blockHandler());
        ParticleRewriter.add(36);
        ParticleRewriter.add(-1);
        ParticleRewriter.add(13);
        ParticleRewriter.add(8);
        ParticleRewriter.add(16);
        ParticleRewriter.add(7);
        ParticleRewriter.add(40);
        ParticleRewriter.add(20, ParticleRewriter.blockHandler());
        ParticleRewriter.add(41);
        ParticleRewriter.add(38);
    }

    private static class NewParticle {
        private final int id;
        private final ParticleDataHandler handler;

        public NewParticle(int id, ParticleDataHandler handler) {
            this.id = id;
            this.handler = handler;
        }

        public Particle handle(Particle particle, Integer[] data) {
            if (this.handler != null) {
                return this.handler.handler(particle, data);
            }
            return particle;
        }

        public int getId() {
            return this.id;
        }

        public ParticleDataHandler getHandler() {
            return this.handler;
        }
    }

    static interface ParticleDataHandler {
        public Particle handler(Particle var1, Integer[] var2);
    }
}

