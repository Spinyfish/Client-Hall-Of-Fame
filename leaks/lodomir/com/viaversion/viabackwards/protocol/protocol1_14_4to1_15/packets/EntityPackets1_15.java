/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets;

import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.Protocol1_14_4To1_15;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.EntityTypeMapping;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.ImmediateRespawn;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import java.util.ArrayList;

public class EntityPackets1_15
extends EntityRewriter<Protocol1_14_4To1_15> {
    public EntityPackets1_15(Protocol1_14_4To1_15 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.UPDATE_HEALTH, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    float health = wrapper.passthrough(Type.FLOAT).floatValue();
                    if (health > 0.0f) {
                        return;
                    }
                    if (!wrapper.user().get(ImmediateRespawn.class).isImmediateRespawn()) {
                        return;
                    }
                    PacketWrapper statusPacket = wrapper.create(ServerboundPackets1_14.CLIENT_STATUS);
                    statusPacket.write(Type.VAR_INT, 0);
                    statusPacket.sendToServer(Protocol1_14_4To1_15.class);
                });
            }
        });
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.GAME_EVENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.FLOAT);
                this.handler(wrapper -> {
                    if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 11) {
                        wrapper.user().get(ImmediateRespawn.class).setImmediateRespawn(wrapper.get(Type.FLOAT, 0).floatValue() == 1.0f);
                    }
                });
            }
        });
        this.registerTrackerWithData(ClientboundPackets1_15.SPAWN_ENTITY, Entity1_15Types.FALLING_BLOCK);
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.SPAWN_MOB, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.VAR_INT);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.handler(wrapper -> wrapper.write(Types1_14.METADATA_LIST, new ArrayList()));
                this.handler(wrapper -> {
                    int type = wrapper.get(Type.VAR_INT, 1);
                    EntityType entityType = Entity1_15Types.getTypeFromId(type);
                    EntityPackets1_15.this.tracker(wrapper.user()).addEntity(wrapper.get(Type.VAR_INT, 0), entityType);
                    wrapper.set(Type.VAR_INT, 1, EntityTypeMapping.getOldEntityId(type));
                });
            }
        });
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map((Type)Type.LONG, Type.NOTHING);
            }
        });
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.INT);
                this.map((Type)Type.LONG, Type.NOTHING);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.map(Type.VAR_INT);
                this.map(Type.BOOLEAN);
                this.handler(EntityPackets1_15.this.getTrackerHandler(Entity1_15Types.PLAYER, Type.INT));
                this.handler(wrapper -> {
                    boolean immediateRespawn = wrapper.read(Type.BOOLEAN) == false;
                    wrapper.user().get(ImmediateRespawn.class).setImmediateRespawn(immediateRespawn);
                });
            }
        });
        this.registerTracker(ClientboundPackets1_15.SPAWN_EXPERIENCE_ORB, Entity1_15Types.EXPERIENCE_ORB);
        this.registerTracker(ClientboundPackets1_15.SPAWN_GLOBAL_ENTITY, Entity1_15Types.LIGHTNING_BOLT);
        this.registerTracker(ClientboundPackets1_15.SPAWN_PAINTING, Entity1_15Types.PAINTING);
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.SPAWN_PLAYER, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.handler(wrapper -> wrapper.write(Types1_14.METADATA_LIST, new ArrayList()));
                this.handler(EntityPackets1_15.this.getTrackerHandler(Entity1_15Types.PLAYER, Type.VAR_INT));
            }
        });
        this.registerRemoveEntities(ClientboundPackets1_15.DESTROY_ENTITIES);
        this.registerMetadataRewriter(ClientboundPackets1_15.ENTITY_METADATA, Types1_14.METADATA_LIST);
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.ENTITY_PROPERTIES, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.INT);
                this.handler(wrapper -> {
                    int size;
                    int entityId = wrapper.get(Type.VAR_INT, 0);
                    EntityType entityType = EntityPackets1_15.this.tracker(wrapper.user()).entityType(entityId);
                    if (entityType != Entity1_15Types.BEE) {
                        return;
                    }
                    int newSize = size = wrapper.get(Type.INT, 0).intValue();
                    for (int i = 0; i < size; ++i) {
                        int j;
                        int modSize;
                        String key = wrapper.read(Type.STRING);
                        if (key.equals("generic.flyingSpeed")) {
                            --newSize;
                            wrapper.read(Type.DOUBLE);
                            modSize = wrapper.read(Type.VAR_INT);
                            for (j = 0; j < modSize; ++j) {
                                wrapper.read(Type.UUID);
                                wrapper.read(Type.DOUBLE);
                                wrapper.read(Type.BYTE);
                            }
                            continue;
                        }
                        wrapper.write(Type.STRING, key);
                        wrapper.passthrough(Type.DOUBLE);
                        modSize = wrapper.passthrough(Type.VAR_INT);
                        for (j = 0; j < modSize; ++j) {
                            wrapper.passthrough(Type.UUID);
                            wrapper.passthrough(Type.DOUBLE);
                            wrapper.passthrough(Type.BYTE);
                        }
                    }
                    if (newSize != size) {
                        wrapper.set(Type.INT, 0, newSize);
                    }
                });
            }
        });
    }

    @Override
    protected void registerRewrites() {
        this.registerMetaTypeHandler(Types1_14.META_TYPES.itemType, Types1_14.META_TYPES.blockStateType, Types1_14.META_TYPES.particleType, null);
        this.filter().filterFamily(Entity1_15Types.LIVINGENTITY).removeIndex(12);
        this.filter().type(Entity1_15Types.BEE).cancel(15);
        this.filter().type(Entity1_15Types.BEE).cancel(16);
        this.mapEntityTypeWithData(Entity1_15Types.BEE, Entity1_15Types.PUFFERFISH).jsonName().spawnMetadata(storage -> {
            storage.add(new Metadata(14, Types1_14.META_TYPES.booleanType, false));
            storage.add(new Metadata(15, Types1_14.META_TYPES.varIntType, 2));
        });
        this.filter().type(Entity1_15Types.ENDERMAN).cancel(16);
        this.filter().type(Entity1_15Types.TRIDENT).cancel(10);
        this.filter().type(Entity1_15Types.WOLF).addIndex(17);
        this.filter().type(Entity1_15Types.WOLF).index(8).handler((event, meta) -> event.createExtraMeta(new Metadata(17, Types1_14.META_TYPES.floatType, event.meta().value())));
    }

    @Override
    public EntityType typeFromId(int typeId) {
        return Entity1_15Types.getTypeFromId(typeId);
    }

    @Override
    public int newEntityId(int newId) {
        return EntityTypeMapping.getOldEntityId(newId);
    }
}

