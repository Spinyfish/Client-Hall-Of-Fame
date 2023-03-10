/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.EntityPositionHandler;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.ChunkLightStorage;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.EntityPositionStorage1_14;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.VillagerData;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_13_2;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.meta.MetaHandler;

public class EntityPackets1_14
extends LegacyEntityRewriter<Protocol1_13_2To1_14> {
    private EntityPositionHandler positionHandler;

    public EntityPackets1_14(Protocol1_13_2To1_14 protocol) {
        super(protocol, Types1_13_2.META_TYPES.optionalComponentType, Types1_13_2.META_TYPES.booleanType);
    }

    @Override
    protected void addTrackedEntity(PacketWrapper wrapper, int entityId, EntityType type) throws Exception {
        super.addTrackedEntity(wrapper, entityId, type);
        if (type == Entity1_14Types.PAINTING) {
            Position position = wrapper.get(Type.POSITION, 0);
            this.positionHandler.cacheEntityPosition(wrapper, position.getX(), position.getY(), position.getZ(), true, false);
        } else if (wrapper.getId() != ClientboundPackets1_14.JOIN_GAME.getId()) {
            this.positionHandler.cacheEntityPosition(wrapper, true, false);
        }
    }

    @Override
    protected void registerPackets() {
        this.positionHandler = new EntityPositionHandler(this, EntityPositionStorage1_14.class, EntityPositionStorage1_14::new);
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.ENTITY_STATUS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int entityId = wrapper.passthrough(Type.INT);
                    byte status = wrapper.passthrough(Type.BYTE);
                    if (status != 3) {
                        return;
                    }
                    Object tracker = EntityPackets1_14.this.tracker(wrapper.user());
                    EntityType entityType = tracker.entityType(entityId);
                    if (entityType != Entity1_14Types.PLAYER) {
                        return;
                    }
                    for (int i = 0; i <= 5; ++i) {
                        PacketWrapper equipmentPacket = wrapper.create(ClientboundPackets1_13.ENTITY_EQUIPMENT);
                        equipmentPacket.write(Type.VAR_INT, entityId);
                        equipmentPacket.write(Type.VAR_INT, i);
                        equipmentPacket.write(Type.FLAT_VAR_INT_ITEM, null);
                        equipmentPacket.send(Protocol1_13_2To1_14.class);
                    }
                });
            }
        });
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.ENTITY_TELEPORT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.handler(wrapper -> EntityPackets1_14.this.positionHandler.cacheEntityPosition(wrapper, false, false));
            }
        });
        PacketRemapper relativeMoveHandler = new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        double x = (double)wrapper.get(Type.SHORT, 0).shortValue() / 4096.0;
                        double y = (double)wrapper.get(Type.SHORT, 1).shortValue() / 4096.0;
                        double z = (double)wrapper.get(Type.SHORT, 2).shortValue() / 4096.0;
                        EntityPackets1_14.this.positionHandler.cacheEntityPosition(wrapper, x, y, z, false, true);
                    }
                });
            }
        };
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.ENTITY_POSITION, relativeMoveHandler);
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.ENTITY_POSITION_AND_ROTATION, relativeMoveHandler);
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.SPAWN_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map((Type)Type.VAR_INT, Type.BYTE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.INT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.handler(EntityPackets1_14.this.getObjectTrackerHandler());
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int data;
                        Entity1_13Types.ObjectType objectType;
                        byte id = wrapper.get(Type.BYTE, 0);
                        int mappedId = EntityPackets1_14.this.newEntityId(id);
                        Entity1_13Types.EntityType entityType = Entity1_13Types.getTypeFromId(mappedId, false);
                        if (entityType.isOrHasParent(Entity1_13Types.EntityType.MINECART_ABSTRACT)) {
                            objectType = Entity1_13Types.ObjectType.MINECART;
                            data = 0;
                            switch (entityType) {
                                case CHEST_MINECART: {
                                    data = 1;
                                    break;
                                }
                                case FURNACE_MINECART: {
                                    data = 2;
                                    break;
                                }
                                case TNT_MINECART: {
                                    data = 3;
                                    break;
                                }
                                case SPAWNER_MINECART: {
                                    data = 4;
                                    break;
                                }
                                case HOPPER_MINECART: {
                                    data = 5;
                                    break;
                                }
                                case COMMAND_BLOCK_MINECART: {
                                    data = 6;
                                }
                            }
                            if (data != 0) {
                                wrapper.set(Type.INT, 0, data);
                            }
                        } else {
                            objectType = Entity1_13Types.ObjectType.fromEntityType(entityType).orElse(null);
                        }
                        if (objectType == null) {
                            return;
                        }
                        wrapper.set(Type.BYTE, 0, (byte)objectType.getId());
                        data = wrapper.get(Type.INT, 0);
                        if (objectType == Entity1_13Types.ObjectType.FALLING_BLOCK) {
                            int blockState = wrapper.get(Type.INT, 0);
                            int combined = ((Protocol1_13_2To1_14)EntityPackets1_14.this.protocol).getMappingData().getNewBlockStateId(blockState);
                            wrapper.set(Type.INT, 0, combined);
                        } else if (entityType.isOrHasParent(Entity1_13Types.EntityType.ABSTRACT_ARROW)) {
                            wrapper.set(Type.INT, 0, data + 1);
                        }
                    }
                });
            }
        });
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.SPAWN_MOB, new PacketRemapper(){

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
                this.map(Types1_14.METADATA_LIST, Types1_13_2.METADATA_LIST);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int type = wrapper.get(Type.VAR_INT, 1);
                        EntityType entityType = Entity1_14Types.getTypeFromId(type);
                        EntityPackets1_14.this.addTrackedEntity(wrapper, wrapper.get(Type.VAR_INT, 0), entityType);
                        int oldId = EntityPackets1_14.this.newEntityId(type);
                        if (oldId == -1) {
                            EntityData entityData = EntityPackets1_14.this.entityDataForType(entityType);
                            if (entityData == null) {
                                ViaBackwards.getPlatform().getLogger().warning("Could not find 1.13.2 entity type for 1.14 entity type " + type + "/" + entityType);
                                wrapper.cancel();
                            } else {
                                wrapper.set(Type.VAR_INT, 1, entityData.replacementId());
                            }
                        } else {
                            wrapper.set(Type.VAR_INT, 1, oldId);
                        }
                    }
                });
                this.handler(EntityPackets1_14.this.getMobSpawnRewriter(Types1_13_2.METADATA_LIST));
            }
        });
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.SPAWN_EXPERIENCE_ORB, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.handler(wrapper -> EntityPackets1_14.this.addTrackedEntity(wrapper, wrapper.get(Type.VAR_INT, 0), Entity1_14Types.EXPERIENCE_ORB));
            }
        });
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.SPAWN_GLOBAL_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.handler(wrapper -> EntityPackets1_14.this.addTrackedEntity(wrapper, wrapper.get(Type.VAR_INT, 0), Entity1_14Types.LIGHTNING_BOLT));
            }
        });
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.SPAWN_PAINTING, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.VAR_INT);
                this.map(Type.POSITION1_14, Type.POSITION);
                this.map(Type.BYTE);
                this.handler(wrapper -> EntityPackets1_14.this.addTrackedEntity(wrapper, wrapper.get(Type.VAR_INT, 0), Entity1_14Types.PAINTING));
            }
        });
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.SPAWN_PLAYER, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Types1_14.METADATA_LIST, Types1_13_2.METADATA_LIST);
                this.handler(EntityPackets1_14.this.getTrackerAndMetaHandler(Types1_13_2.METADATA_LIST, Entity1_14Types.PLAYER));
                this.handler(wrapper -> EntityPackets1_14.this.positionHandler.cacheEntityPosition(wrapper, true, false));
            }
        });
        this.registerRemoveEntities(ClientboundPackets1_14.DESTROY_ENTITIES);
        this.registerMetadataRewriter(ClientboundPackets1_14.ENTITY_METADATA, Types1_14.METADATA_LIST, Types1_13_2.METADATA_LIST);
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.INT);
                this.handler(EntityPackets1_14.this.getTrackerHandler(Entity1_14Types.PLAYER, Type.INT));
                this.handler(EntityPackets1_14.this.getDimensionHandler(1));
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.UNSIGNED_BYTE, (short)0);
                        wrapper.passthrough(Type.UNSIGNED_BYTE);
                        wrapper.passthrough(Type.STRING);
                        wrapper.read(Type.VAR_INT);
                    }
                });
            }
        });
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 0);
                        clientWorld.setEnvironment(dimensionId);
                        wrapper.write(Type.UNSIGNED_BYTE, (short)0);
                        wrapper.user().get(ChunkLightStorage.class).clear();
                    }
                });
            }
        });
    }

    @Override
    protected void registerRewrites() {
        this.mapTypes(Entity1_14Types.values(), Entity1_13Types.EntityType.class);
        this.mapEntityTypeWithData(Entity1_14Types.CAT, Entity1_14Types.OCELOT).jsonName();
        this.mapEntityTypeWithData(Entity1_14Types.TRADER_LLAMA, Entity1_14Types.LLAMA).jsonName();
        this.mapEntityTypeWithData(Entity1_14Types.FOX, Entity1_14Types.WOLF).jsonName();
        this.mapEntityTypeWithData(Entity1_14Types.PANDA, Entity1_14Types.POLAR_BEAR).jsonName();
        this.mapEntityTypeWithData(Entity1_14Types.PILLAGER, Entity1_14Types.VILLAGER).jsonName();
        this.mapEntityTypeWithData(Entity1_14Types.WANDERING_TRADER, Entity1_14Types.VILLAGER).jsonName();
        this.mapEntityTypeWithData(Entity1_14Types.RAVAGER, Entity1_14Types.COW).jsonName();
        this.filter().handler((event, meta) -> {
            MetaType type;
            int typeId = meta.metaType().typeId();
            if (typeId <= 15) {
                meta.setMetaType(Types1_13_2.META_TYPES.byId(typeId));
            }
            if ((type = meta.metaType()) == Types1_13_2.META_TYPES.itemType) {
                Item item = (Item)meta.getValue();
                meta.setValue(((Protocol1_13_2To1_14)this.protocol).getItemRewriter().handleItemToClient(item));
            } else if (type == Types1_13_2.META_TYPES.blockStateType) {
                int blockstate = (Integer)meta.value();
                meta.setValue(((Protocol1_13_2To1_14)this.protocol).getMappingData().getNewBlockStateId(blockstate));
            }
        });
        this.filter().type(Entity1_14Types.PILLAGER).cancel(15);
        this.filter().type(Entity1_14Types.FOX).cancel(15);
        this.filter().type(Entity1_14Types.FOX).cancel(16);
        this.filter().type(Entity1_14Types.FOX).cancel(17);
        this.filter().type(Entity1_14Types.FOX).cancel(18);
        this.filter().type(Entity1_14Types.PANDA).cancel(15);
        this.filter().type(Entity1_14Types.PANDA).cancel(16);
        this.filter().type(Entity1_14Types.PANDA).cancel(17);
        this.filter().type(Entity1_14Types.PANDA).cancel(18);
        this.filter().type(Entity1_14Types.PANDA).cancel(19);
        this.filter().type(Entity1_14Types.PANDA).cancel(20);
        this.filter().type(Entity1_14Types.CAT).cancel(18);
        this.filter().type(Entity1_14Types.CAT).cancel(19);
        this.filter().type(Entity1_14Types.CAT).cancel(20);
        this.filter().handler((event, meta) -> {
            EntityType type = event.entityType();
            if (type == null) {
                return;
            }
            if (type.isOrHasParent(Entity1_14Types.ABSTRACT_ILLAGER_BASE) || type == Entity1_14Types.RAVAGER || type == Entity1_14Types.WITCH) {
                int index = event.index();
                if (index == 14) {
                    event.cancel();
                } else if (index > 14) {
                    event.setIndex(index - 1);
                }
            }
        });
        this.filter().type(Entity1_14Types.AREA_EFFECT_CLOUD).index(10).handler((event, meta) -> this.rewriteParticle((Particle)meta.getValue()));
        this.filter().type(Entity1_14Types.FIREWORK_ROCKET).index(8).handler((event, meta) -> {
            meta.setMetaType(Types1_13_2.META_TYPES.varIntType);
            Integer value = (Integer)meta.getValue();
            if (value == null) {
                meta.setValue(0);
            }
        });
        this.filter().filterFamily(Entity1_14Types.ABSTRACT_ARROW).removeIndex(9);
        this.filter().type(Entity1_14Types.VILLAGER).cancel(15);
        MetaHandler villagerDataHandler = (event, meta) -> {
            VillagerData villagerData = (VillagerData)meta.getValue();
            meta.setTypeAndValue(Types1_13_2.META_TYPES.varIntType, this.villagerDataToProfession(villagerData));
            if (meta.id() == 16) {
                event.setIndex(15);
            }
        };
        this.filter().type(Entity1_14Types.ZOMBIE_VILLAGER).index(18).handler(villagerDataHandler);
        this.filter().type(Entity1_14Types.VILLAGER).index(16).handler(villagerDataHandler);
        this.filter().filterFamily(Entity1_14Types.ABSTRACT_SKELETON).index(13).handler((event, meta) -> {
            byte value = (Byte)meta.getValue();
            if ((value & 4) != 0) {
                event.createExtraMeta(new Metadata(14, Types1_13_2.META_TYPES.booleanType, true));
            }
        });
        this.filter().filterFamily(Entity1_14Types.ZOMBIE).index(13).handler((event, meta) -> {
            byte value = (Byte)meta.getValue();
            if ((value & 4) != 0) {
                event.createExtraMeta(new Metadata(16, Types1_13_2.META_TYPES.booleanType, true));
            }
        });
        this.filter().filterFamily(Entity1_14Types.ZOMBIE).addIndex(16);
        this.filter().filterFamily(Entity1_14Types.LIVINGENTITY).handler((event, meta) -> {
            int index = event.index();
            if (index == 12) {
                Position position = (Position)meta.getValue();
                if (position != null) {
                    PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_13.USE_BED, null, event.user());
                    wrapper.write(Type.VAR_INT, event.entityId());
                    wrapper.write(Type.POSITION, position);
                    try {
                        wrapper.scheduleSend(Protocol1_13_2To1_14.class);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                event.cancel();
            } else if (index > 12) {
                event.setIndex(index - 1);
            }
        });
        this.filter().removeIndex(6);
        this.filter().type(Entity1_14Types.OCELOT).index(13).handler((event, meta) -> {
            event.setIndex(15);
            meta.setTypeAndValue(Types1_13_2.META_TYPES.varIntType, 0);
        });
        this.filter().type(Entity1_14Types.CAT).handler((event, meta) -> {
            if (event.index() == 15) {
                meta.setValue(1);
            } else if (event.index() == 13) {
                meta.setValue((byte)((Byte)meta.getValue() & 4));
            }
        });
        this.filter().handler((event, meta) -> {
            if (meta.metaType().typeId() > 15) {
                throw new IllegalArgumentException("Unhandled metadata: " + meta);
            }
        });
    }

    public int villagerDataToProfession(VillagerData data) {
        switch (data.getProfession()) {
            case 1: 
            case 10: 
            case 13: 
            case 14: {
                return 3;
            }
            case 2: 
            case 8: {
                return 4;
            }
            case 3: 
            case 9: {
                return 1;
            }
            case 4: {
                return 2;
            }
            case 5: 
            case 6: 
            case 7: 
            case 12: {
                return 0;
            }
        }
        return 5;
    }

    @Override
    public EntityType typeFromId(int typeId) {
        return Entity1_14Types.getTypeFromId(typeId);
    }
}

