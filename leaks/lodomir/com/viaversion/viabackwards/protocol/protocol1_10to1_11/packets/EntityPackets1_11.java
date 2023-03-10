/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets;

import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.PotionSplashHandler;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.Protocol1_10To1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.ChestedHorseStorage;
import com.viaversion.viabackwards.utils.Block;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_12Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import java.util.List;
import java.util.Optional;

public class EntityPackets1_11
extends LegacyEntityRewriter<Protocol1_10To1_11> {
    public EntityPackets1_11(Protocol1_10To1_11 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.EFFECT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.POSITION);
                this.map(Type.INT);
                this.handler(wrapper -> {
                    int type = wrapper.get(Type.INT, 0);
                    if (type == 2002 || type == 2007) {
                        int mappedData;
                        if (type == 2007) {
                            wrapper.set(Type.INT, 0, 2002);
                        }
                        if ((mappedData = PotionSplashHandler.getOldData(wrapper.get(Type.INT, 1))) != -1) {
                            wrapper.set(Type.INT, 1, mappedData);
                        }
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.BYTE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.INT);
                this.handler(EntityPackets1_11.this.getObjectTrackerHandler());
                this.handler(EntityPackets1_11.this.getObjectRewriter(id -> Entity1_11Types.ObjectType.findById(id.byteValue()).orElse(null)));
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Optional<Entity1_12Types.ObjectType> type = Entity1_12Types.ObjectType.findById(wrapper.get(Type.BYTE, 0).byteValue());
                        if (type.isPresent() && type.get() == Entity1_12Types.ObjectType.FALLING_BLOCK) {
                            int objectData = wrapper.get(Type.INT, 0);
                            int objType = objectData & 0xFFF;
                            int data = objectData >> 12 & 0xF;
                            Block block = ((Protocol1_10To1_11)EntityPackets1_11.this.protocol).getItemRewriter().handleBlock(objType, data);
                            if (block == null) {
                                return;
                            }
                            wrapper.set(Type.INT, 0, block.getId() | block.getData() << 12);
                        }
                    }
                });
            }
        });
        this.registerTracker(ClientboundPackets1_9_3.SPAWN_EXPERIENCE_ORB, Entity1_11Types.EntityType.EXPERIENCE_ORB);
        this.registerTracker(ClientboundPackets1_9_3.SPAWN_GLOBAL_ENTITY, Entity1_11Types.EntityType.WEATHER);
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map((Type)Type.VAR_INT, Type.UNSIGNED_BYTE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Types1_9.METADATA_LIST);
                this.handler(EntityPackets1_11.this.getTrackerHandler(Type.UNSIGNED_BYTE, 0));
                this.handler(wrapper -> {
                    int entityId = wrapper.get(Type.VAR_INT, 0);
                    EntityType type = EntityPackets1_11.this.tracker(wrapper.user()).entityType(entityId);
                    List<Metadata> list = wrapper.get(Types1_9.METADATA_LIST, 0);
                    EntityPackets1_11.this.handleMetadata(wrapper.get(Type.VAR_INT, 0), list, wrapper.user());
                    EntityData entityData = EntityPackets1_11.this.entityDataForType(type);
                    if (entityData != null) {
                        wrapper.set(Type.UNSIGNED_BYTE, 0, (short)entityData.replacementId());
                        if (entityData.hasBaseMeta()) {
                            entityData.defaultMeta().createMeta(new WrappedMetadata(list));
                        }
                    }
                    if (list.isEmpty()) {
                        list.add(new Metadata(0, MetaType1_9.Byte, (byte)0));
                    }
                });
            }
        });
        this.registerTracker(ClientboundPackets1_9_3.SPAWN_PAINTING, Entity1_11Types.EntityType.PAINTING);
        this.registerJoinGame(ClientboundPackets1_9_3.JOIN_GAME, Entity1_11Types.EntityType.PLAYER);
        this.registerRespawn(ClientboundPackets1_9_3.RESPAWN);
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.SPAWN_PLAYER, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Types1_9.METADATA_LIST);
                this.handler(EntityPackets1_11.this.getTrackerAndMetaHandler(Types1_9.METADATA_LIST, Entity1_11Types.EntityType.PLAYER));
                this.handler(wrapper -> {
                    List<Metadata> metadata = wrapper.get(Types1_9.METADATA_LIST, 0);
                    if (metadata.isEmpty()) {
                        metadata.add(new Metadata(0, MetaType1_9.Byte, (byte)0));
                    }
                });
            }
        });
        this.registerRemoveEntities(ClientboundPackets1_9_3.DESTROY_ENTITIES);
        this.registerMetadataRewriter(ClientboundPackets1_9_3.ENTITY_METADATA, Types1_9.METADATA_LIST);
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.ENTITY_STATUS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.BYTE);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        byte b = wrapper.get(Type.BYTE, 0);
                        if (b == 35) {
                            wrapper.clearPacket();
                            wrapper.setId(30);
                            wrapper.write(Type.UNSIGNED_BYTE, (short)10);
                            wrapper.write(Type.FLOAT, Float.valueOf(0.0f));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void registerRewrites() {
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.ELDER_GUARDIAN, Entity1_11Types.EntityType.GUARDIAN);
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.WITHER_SKELETON, Entity1_11Types.EntityType.SKELETON).plainName().spawnMetadata(storage -> storage.add(this.getSkeletonTypeMeta(1)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.STRAY, Entity1_11Types.EntityType.SKELETON).plainName().spawnMetadata(storage -> storage.add(this.getSkeletonTypeMeta(2)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.HUSK, Entity1_11Types.EntityType.ZOMBIE).plainName().spawnMetadata(storage -> this.handleZombieType(storage, 6));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.ZOMBIE_VILLAGER, Entity1_11Types.EntityType.ZOMBIE).spawnMetadata(storage -> this.handleZombieType(storage, 1));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.HORSE, Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(this.getHorseMetaType(0)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.DONKEY, Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(this.getHorseMetaType(1)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.MULE, Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(this.getHorseMetaType(2)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.SKELETON_HORSE, Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(this.getHorseMetaType(4)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.ZOMBIE_HORSE, Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(this.getHorseMetaType(3)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.EVOCATION_FANGS, Entity1_11Types.EntityType.SHULKER);
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.EVOCATION_ILLAGER, Entity1_11Types.EntityType.VILLAGER).plainName();
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.VEX, Entity1_11Types.EntityType.BAT).plainName();
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.VINDICATION_ILLAGER, Entity1_11Types.EntityType.VILLAGER).plainName().spawnMetadata(storage -> storage.add(new Metadata(13, MetaType1_9.VarInt, 4)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.LIAMA, Entity1_11Types.EntityType.HORSE).plainName().spawnMetadata(storage -> storage.add(this.getHorseMetaType(1)));
        this.mapEntityTypeWithData(Entity1_11Types.EntityType.LIAMA_SPIT, Entity1_11Types.EntityType.SNOWBALL);
        this.mapObjectType(Entity1_11Types.ObjectType.LIAMA_SPIT, Entity1_11Types.ObjectType.SNOWBALL, -1);
        this.mapObjectType(Entity1_11Types.ObjectType.EVOCATION_FANGS, Entity1_11Types.ObjectType.FALLING_BLOCK, 4294);
        this.filter().filterFamily(Entity1_11Types.EntityType.GUARDIAN).index(12).handler((event, meta) -> {
            int bitmask;
            boolean b = (Boolean)meta.getValue();
            int n = bitmask = b ? 2 : 0;
            if (event.entityType() == Entity1_11Types.EntityType.ELDER_GUARDIAN) {
                bitmask |= 4;
            }
            meta.setTypeAndValue(MetaType1_9.Byte, (byte)bitmask);
        });
        this.filter().filterFamily(Entity1_11Types.EntityType.ABSTRACT_SKELETON).index(12).toIndex(13);
        this.filter().filterFamily(Entity1_11Types.EntityType.ZOMBIE).handler((event, meta) -> {
            switch (meta.id()) {
                case 13: {
                    event.cancel();
                    return;
                }
                case 14: {
                    event.setIndex(15);
                    break;
                }
                case 15: {
                    event.setIndex(14);
                    break;
                }
                case 16: {
                    event.setIndex(13);
                    meta.setValue(1 + (Integer)meta.getValue());
                }
            }
        });
        this.filter().type(Entity1_11Types.EntityType.EVOCATION_ILLAGER).index(12).handler((event, meta) -> {
            event.setIndex(13);
            meta.setTypeAndValue(MetaType1_9.VarInt, ((Byte)meta.getValue()).intValue());
        });
        this.filter().type(Entity1_11Types.EntityType.VEX).index(12).handler((event, meta) -> meta.setValue((byte)0));
        this.filter().type(Entity1_11Types.EntityType.VINDICATION_ILLAGER).index(12).handler((event, meta) -> {
            event.setIndex(13);
            meta.setTypeAndValue(MetaType1_9.VarInt, ((Number)meta.getValue()).intValue() == 1 ? 2 : 4);
        });
        this.filter().filterFamily(Entity1_11Types.EntityType.ABSTRACT_HORSE).index(13).handler((event, meta) -> {
            StoredEntityData data = this.storedEntityData(event);
            byte b = (Byte)meta.getValue();
            if (data.has(ChestedHorseStorage.class) && data.get(ChestedHorseStorage.class).isChested()) {
                b = (byte)(b | 8);
                meta.setValue(b);
            }
        });
        this.filter().filterFamily(Entity1_11Types.EntityType.CHESTED_HORSE).handler((event, meta) -> {
            StoredEntityData data = this.storedEntityData(event);
            if (!data.has(ChestedHorseStorage.class)) {
                data.put(new ChestedHorseStorage());
            }
        });
        this.filter().type(Entity1_11Types.EntityType.HORSE).index(16).toIndex(17);
        this.filter().filterFamily(Entity1_11Types.EntityType.CHESTED_HORSE).index(15).handler((event, meta) -> {
            StoredEntityData data = this.storedEntityData(event);
            ChestedHorseStorage storage = data.get(ChestedHorseStorage.class);
            boolean b = (Boolean)meta.getValue();
            storage.setChested(b);
            event.cancel();
        });
        this.filter().type(Entity1_11Types.EntityType.LIAMA).handler((event, meta) -> {
            StoredEntityData data = this.storedEntityData(event);
            ChestedHorseStorage storage = data.get(ChestedHorseStorage.class);
            int index = event.index();
            switch (index) {
                case 16: {
                    storage.setLiamaStrength((Integer)meta.getValue());
                    event.cancel();
                    break;
                }
                case 17: {
                    storage.setLiamaCarpetColor((Integer)meta.getValue());
                    event.cancel();
                    break;
                }
                case 18: {
                    storage.setLiamaVariant((Integer)meta.getValue());
                    event.cancel();
                }
            }
        });
        this.filter().filterFamily(Entity1_11Types.EntityType.ABSTRACT_HORSE).index(14).toIndex(16);
        this.filter().type(Entity1_11Types.EntityType.VILLAGER).index(13).handler((event, meta) -> {
            if ((Integer)meta.getValue() == 5) {
                meta.setValue(0);
            }
        });
        this.filter().type(Entity1_11Types.EntityType.SHULKER).cancel(15);
    }

    private Metadata getSkeletonTypeMeta(int type) {
        return new Metadata(12, MetaType1_9.VarInt, type);
    }

    private Metadata getZombieTypeMeta(int type) {
        return new Metadata(13, MetaType1_9.VarInt, type);
    }

    private void handleZombieType(WrappedMetadata storage, int type) {
        Metadata meta = storage.get(13);
        if (meta == null) {
            storage.add(this.getZombieTypeMeta(type));
        }
    }

    private Metadata getHorseMetaType(int type) {
        return new Metadata(14, MetaType1_9.VarInt, type);
    }

    @Override
    public EntityType typeFromId(int typeId) {
        return Entity1_11Types.getTypeFromId(typeId, false);
    }

    @Override
    protected EntityType getObjectTypeFromId(int typeId) {
        return Entity1_11Types.getTypeFromId(typeId, true);
    }
}

