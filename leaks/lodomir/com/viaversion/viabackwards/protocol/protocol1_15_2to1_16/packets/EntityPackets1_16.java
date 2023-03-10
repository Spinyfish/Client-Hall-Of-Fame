/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.packets;

import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.Protocol1_15_2To1_16;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data.WorldNameTracker;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.storage.WolfDataMaskStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class EntityPackets1_16
extends EntityRewriter<Protocol1_15_2To1_16> {
    private final ValueTransformer<String, Integer> dimensionTransformer = new ValueTransformer<String, Integer>(Type.STRING, (Type)Type.INT){

        @Override
        public Integer transform(PacketWrapper wrapper, String input) {
            switch (input) {
                case "minecraft:the_nether": {
                    return -1;
                }
                default: {
                    return 0;
                }
                case "minecraft:the_end": 
            }
            return 1;
        }
    };

    public EntityPackets1_16(Protocol1_15_2To1_16 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.SPAWN_ENTITY, new PacketRemapper(){

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
                this.map(Type.INT);
                this.handler(wrapper -> {
                    EntityType entityType = EntityPackets1_16.this.typeFromId(wrapper.get(Type.VAR_INT, 1));
                    if (entityType == Entity1_16Types.LIGHTNING_BOLT) {
                        wrapper.cancel();
                        PacketWrapper spawnLightningPacket = wrapper.create(ClientboundPackets1_15.SPAWN_GLOBAL_ENTITY);
                        spawnLightningPacket.write(Type.VAR_INT, wrapper.get(Type.VAR_INT, 0));
                        spawnLightningPacket.write(Type.BYTE, (byte)1);
                        spawnLightningPacket.write(Type.DOUBLE, wrapper.get(Type.DOUBLE, 0));
                        spawnLightningPacket.write(Type.DOUBLE, wrapper.get(Type.DOUBLE, 1));
                        spawnLightningPacket.write(Type.DOUBLE, wrapper.get(Type.DOUBLE, 2));
                        spawnLightningPacket.send(Protocol1_15_2To1_16.class);
                    }
                });
                this.handler(EntityPackets1_16.this.getSpawnTrackerWithDataHandler(Entity1_16Types.FALLING_BLOCK));
            }
        });
        this.registerSpawnTracker(ClientboundPackets1_16.SPAWN_MOB);
        ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(EntityPackets1_16.this.dimensionTransformer);
                this.handler(wrapper -> {
                    WorldNameTracker worldNameTracker = wrapper.user().get(WorldNameTracker.class);
                    String nextWorldName = wrapper.read(Type.STRING);
                    wrapper.passthrough(Type.LONG);
                    wrapper.passthrough(Type.UNSIGNED_BYTE);
                    wrapper.read(Type.BYTE);
                    ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    int dimension = wrapper.get(Type.INT, 0);
                    if (clientWorld.getEnvironment() != null && dimension == clientWorld.getEnvironment().getId() && (wrapper.user().isClientSide() || Via.getPlatform().isProxy() || wrapper.user().getProtocolInfo().getProtocolVersion() <= ProtocolVersion.v1_12_2.getVersion() || !nextWorldName.equals(worldNameTracker.getWorldName()))) {
                        PacketWrapper packet = wrapper.create(ClientboundPackets1_15.RESPAWN);
                        packet.write(Type.INT, dimension == 0 ? -1 : 0);
                        packet.write(Type.LONG, 0L);
                        packet.write(Type.UNSIGNED_BYTE, (short)0);
                        packet.write(Type.STRING, "default");
                        packet.send(Protocol1_15_2To1_16.class);
                    }
                    clientWorld.setEnvironment(dimension);
                    wrapper.write(Type.STRING, "default");
                    wrapper.read(Type.BOOLEAN);
                    if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                        wrapper.set(Type.STRING, 0, "flat");
                    }
                    wrapper.read(Type.BOOLEAN);
                    worldNameTracker.setWorldName(nextWorldName);
                });
            }
        });
        ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map((Type)Type.BYTE, Type.NOTHING);
                this.map(Type.STRING_ARRAY, Type.NOTHING);
                this.map(Type.NBT, Type.NOTHING);
                this.map(EntityPackets1_16.this.dimensionTransformer);
                this.handler(wrapper -> {
                    WorldNameTracker worldNameTracker = wrapper.user().get(WorldNameTracker.class);
                    worldNameTracker.setWorldName(wrapper.read(Type.STRING));
                });
                this.map(Type.LONG);
                this.map(Type.UNSIGNED_BYTE);
                this.handler(wrapper -> {
                    ClientWorld clientChunks = wrapper.user().get(ClientWorld.class);
                    clientChunks.setEnvironment(wrapper.get(Type.INT, 1));
                    EntityPackets1_16.this.tracker(wrapper.user()).addEntity(wrapper.get(Type.INT, 0), Entity1_16Types.PLAYER);
                    wrapper.write(Type.STRING, "default");
                    wrapper.passthrough(Type.VAR_INT);
                    wrapper.passthrough(Type.BOOLEAN);
                    wrapper.passthrough(Type.BOOLEAN);
                    wrapper.read(Type.BOOLEAN);
                    if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                        wrapper.set(Type.STRING, 0, "flat");
                    }
                });
            }
        });
        this.registerTracker(ClientboundPackets1_16.SPAWN_EXPERIENCE_ORB, Entity1_16Types.EXPERIENCE_ORB);
        this.registerTracker(ClientboundPackets1_16.SPAWN_PAINTING, Entity1_16Types.PAINTING);
        this.registerTracker(ClientboundPackets1_16.SPAWN_PLAYER, Entity1_16Types.PLAYER);
        this.registerRemoveEntities(ClientboundPackets1_16.DESTROY_ENTITIES);
        this.registerMetadataRewriter(ClientboundPackets1_16.ENTITY_METADATA, Types1_16.METADATA_LIST, Types1_14.METADATA_LIST);
        ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.ENTITY_PROPERTIES, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT);
                    int size = wrapper.passthrough(Type.INT);
                    for (int i = 0; i < size; ++i) {
                        String attributeIdentifier = wrapper.read(Type.STRING);
                        String oldKey = ((Protocol1_15_2To1_16)EntityPackets1_16.this.protocol).getMappingData().getAttributeMappings().get(attributeIdentifier);
                        wrapper.write(Type.STRING, oldKey != null ? oldKey : attributeIdentifier.replace("minecraft:", ""));
                        wrapper.passthrough(Type.DOUBLE);
                        int modifierSize = wrapper.passthrough(Type.VAR_INT);
                        for (int j = 0; j < modifierSize; ++j) {
                            wrapper.passthrough(Type.UUID);
                            wrapper.passthrough(Type.DOUBLE);
                            wrapper.passthrough(Type.BYTE);
                        }
                    }
                });
            }
        });
        ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.PLAYER_INFO, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    int action = packetWrapper.passthrough(Type.VAR_INT);
                    int playerCount = packetWrapper.passthrough(Type.VAR_INT);
                    for (int i = 0; i < playerCount; ++i) {
                        packetWrapper.passthrough(Type.UUID);
                        if (action == 0) {
                            packetWrapper.passthrough(Type.STRING);
                            int properties = packetWrapper.passthrough(Type.VAR_INT);
                            for (int j = 0; j < properties; ++j) {
                                packetWrapper.passthrough(Type.STRING);
                                packetWrapper.passthrough(Type.STRING);
                                if (!packetWrapper.passthrough(Type.BOOLEAN).booleanValue()) continue;
                                packetWrapper.passthrough(Type.STRING);
                            }
                            packetWrapper.passthrough(Type.VAR_INT);
                            packetWrapper.passthrough(Type.VAR_INT);
                            if (!packetWrapper.passthrough(Type.BOOLEAN).booleanValue()) continue;
                            ((Protocol1_15_2To1_16)EntityPackets1_16.this.protocol).getTranslatableRewriter().processText(packetWrapper.passthrough(Type.COMPONENT));
                            continue;
                        }
                        if (action == 1) {
                            packetWrapper.passthrough(Type.VAR_INT);
                            continue;
                        }
                        if (action == 2) {
                            packetWrapper.passthrough(Type.VAR_INT);
                            continue;
                        }
                        if (action != 3 || !packetWrapper.passthrough(Type.BOOLEAN).booleanValue()) continue;
                        ((Protocol1_15_2To1_16)EntityPackets1_16.this.protocol).getTranslatableRewriter().processText(packetWrapper.passthrough(Type.COMPONENT));
                    }
                });
            }
        });
    }

    @Override
    protected void registerRewrites() {
        this.filter().handler((event, meta) -> {
            JsonElement text;
            meta.setMetaType(Types1_14.META_TYPES.byId(meta.metaType().typeId()));
            MetaType type = meta.metaType();
            if (type == Types1_14.META_TYPES.itemType) {
                meta.setValue(((Protocol1_15_2To1_16)this.protocol).getItemRewriter().handleItemToClient((Item)meta.getValue()));
            } else if (type == Types1_14.META_TYPES.blockStateType) {
                meta.setValue(((Protocol1_15_2To1_16)this.protocol).getMappingData().getNewBlockStateId((Integer)meta.getValue()));
            } else if (type == Types1_14.META_TYPES.particleType) {
                this.rewriteParticle((Particle)meta.getValue());
            } else if (type == Types1_14.META_TYPES.optionalComponentType && (text = (JsonElement)meta.value()) != null) {
                ((Protocol1_15_2To1_16)this.protocol).getTranslatableRewriter().processText(text);
            }
        });
        this.mapEntityType(Entity1_16Types.ZOMBIFIED_PIGLIN, Entity1_15Types.ZOMBIE_PIGMAN);
        this.mapTypes(Entity1_16Types.values(), Entity1_15Types.class);
        this.mapEntityTypeWithData(Entity1_16Types.HOGLIN, Entity1_16Types.COW).jsonName();
        this.mapEntityTypeWithData(Entity1_16Types.ZOGLIN, Entity1_16Types.COW).jsonName();
        this.mapEntityTypeWithData(Entity1_16Types.PIGLIN, Entity1_16Types.ZOMBIFIED_PIGLIN).jsonName();
        this.mapEntityTypeWithData(Entity1_16Types.STRIDER, Entity1_16Types.MAGMA_CUBE).jsonName();
        this.filter().type(Entity1_16Types.ZOGLIN).cancel(16);
        this.filter().type(Entity1_16Types.HOGLIN).cancel(15);
        this.filter().type(Entity1_16Types.PIGLIN).cancel(16);
        this.filter().type(Entity1_16Types.PIGLIN).cancel(17);
        this.filter().type(Entity1_16Types.PIGLIN).cancel(18);
        this.filter().type(Entity1_16Types.STRIDER).index(15).handler((event, meta) -> {
            boolean baby = (Boolean)meta.value();
            meta.setTypeAndValue(Types1_14.META_TYPES.varIntType, baby ? 1 : 3);
        });
        this.filter().type(Entity1_16Types.STRIDER).cancel(16);
        this.filter().type(Entity1_16Types.STRIDER).cancel(17);
        this.filter().type(Entity1_16Types.STRIDER).cancel(18);
        this.filter().type(Entity1_16Types.FISHING_BOBBER).cancel(8);
        this.filter().filterFamily(Entity1_16Types.ABSTRACT_ARROW).cancel(8);
        this.filter().filterFamily(Entity1_16Types.ABSTRACT_ARROW).handler((event, meta) -> {
            if (event.index() >= 8) {
                event.setIndex(event.index() + 1);
            }
        });
        this.filter().type(Entity1_16Types.WOLF).index(16).handler((event, meta) -> {
            byte mask = (Byte)meta.value();
            StoredEntityData data = this.tracker(event.user()).entityData(event.entityId());
            data.put(new WolfDataMaskStorage(mask));
        });
        this.filter().type(Entity1_16Types.WOLF).index(20).handler((event, meta) -> {
            int angerTime;
            WolfDataMaskStorage wolfData;
            StoredEntityData data = this.tracker(event.user()).entityDataIfPresent(event.entityId());
            byte previousMask = 0;
            if (data != null && (wolfData = data.get(WolfDataMaskStorage.class)) != null) {
                previousMask = wolfData.tameableMask();
            }
            byte tameableMask = (byte)((angerTime = ((Integer)meta.value()).intValue()) > 0 ? previousMask | 2 : previousMask & 0xFFFFFFFD);
            event.createExtraMeta(new Metadata(16, Types1_14.META_TYPES.byteType, tameableMask));
            event.cancel();
        });
    }

    @Override
    public EntityType typeFromId(int typeId) {
        return Entity1_16Types.getTypeFromId(typeId);
    }
}

