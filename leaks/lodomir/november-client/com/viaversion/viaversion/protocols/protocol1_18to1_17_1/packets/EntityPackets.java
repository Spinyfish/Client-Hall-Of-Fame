/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_18to1_17_1.packets;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_17;
import com.viaversion.viaversion.api.type.types.version.Types1_18;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.Protocol1_18To1_17_1;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.storage.ChunkLightStorage;
import com.viaversion.viaversion.rewriter.EntityRewriter;

public final class EntityPackets
extends EntityRewriter<Protocol1_18To1_17_1> {
    public EntityPackets(Protocol1_18To1_17_1 protocol) {
        super(protocol);
    }

    @Override
    public void registerPackets() {
        this.registerMetadataRewriter(ClientboundPackets1_17_1.ENTITY_METADATA, Types1_17.METADATA_LIST, Types1_18.METADATA_LIST);
        ((Protocol1_18To1_17_1)this.protocol).registerClientbound(ClientboundPackets1_17_1.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.BYTE);
                this.map(Type.STRING_ARRAY);
                this.map(Type.NBT);
                this.map(Type.NBT);
                this.map(Type.STRING);
                this.map(Type.LONG);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int chunkRadius = wrapper.passthrough(Type.VAR_INT);
                    wrapper.write(Type.VAR_INT, chunkRadius);
                });
                this.handler(EntityPackets.this.worldDataTrackerHandler(1));
                this.handler(EntityPackets.this.biomeSizeTracker());
            }
        });
        ((Protocol1_18To1_17_1)this.protocol).registerClientbound(ClientboundPackets1_17_1.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.NBT);
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    Object tracker;
                    String world = wrapper.get(Type.STRING, 0);
                    if (!world.equals((tracker = EntityPackets.this.tracker(wrapper.user())).currentWorld())) {
                        wrapper.user().get(ChunkLightStorage.class).clear();
                    }
                });
                this.handler(EntityPackets.this.worldDataTrackerHandler(0));
            }
        });
    }

    @Override
    protected void registerRewrites() {
        this.filter().handler((event, meta) -> {
            meta.setMetaType(Types1_18.META_TYPES.byId(meta.metaType().typeId()));
            if (meta.metaType() == Types1_18.META_TYPES.particleType) {
                Particle particle = (Particle)meta.getValue();
                if (particle.getId() == 2) {
                    particle.setId(3);
                    particle.getArguments().add(new Particle.ParticleData(Type.VAR_INT, 7754));
                } else if (particle.getId() == 3) {
                    particle.getArguments().add(new Particle.ParticleData(Type.VAR_INT, 7786));
                } else {
                    this.rewriteParticle(particle);
                }
            }
        });
        this.registerMetaTypeHandler(Types1_18.META_TYPES.itemType, null, null);
    }

    @Override
    public EntityType typeFromId(int type) {
        return Entity1_17Types.getTypeFromId(type);
    }
}

