/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.packets;

import com.google.common.collect.Sets;
import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.Protocol1_16_1To1_16_2;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.EntityPackets;
import java.util.Set;

public class EntityPackets1_16_2
extends EntityRewriter<Protocol1_16_1To1_16_2> {
    private final Set<String> oldDimensions = Sets.newHashSet((Object[])new String[]{"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"});

    public EntityPackets1_16_2(Protocol1_16_1To1_16_2 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        this.registerTrackerWithData(ClientboundPackets1_16_2.SPAWN_ENTITY, Entity1_16_2Types.FALLING_BLOCK);
        this.registerSpawnTracker(ClientboundPackets1_16_2.SPAWN_MOB);
        this.registerTracker(ClientboundPackets1_16_2.SPAWN_EXPERIENCE_ORB, Entity1_16_2Types.EXPERIENCE_ORB);
        this.registerTracker(ClientboundPackets1_16_2.SPAWN_PAINTING, Entity1_16_2Types.PAINTING);
        this.registerTracker(ClientboundPackets1_16_2.SPAWN_PLAYER, Entity1_16_2Types.PLAYER);
        this.registerRemoveEntities(ClientboundPackets1_16_2.DESTROY_ENTITIES);
        this.registerMetadataRewriter(ClientboundPackets1_16_2.ENTITY_METADATA, Types1_16.METADATA_LIST);
        ((Protocol1_16_1To1_16_2)this.protocol).registerClientbound(ClientboundPackets1_16_2.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.handler(wrapper -> {
                    boolean hardcore = wrapper.read(Type.BOOLEAN);
                    short gamemode = wrapper.read(Type.UNSIGNED_BYTE);
                    if (hardcore) {
                        gamemode = (short)(gamemode | 8);
                    }
                    wrapper.write(Type.UNSIGNED_BYTE, gamemode);
                });
                this.map(Type.BYTE);
                this.map(Type.STRING_ARRAY);
                this.handler(wrapper -> {
                    wrapper.read(Type.NBT);
                    wrapper.write(Type.NBT, EntityPackets.DIMENSIONS_TAG);
                    CompoundTag dimensionData = wrapper.read(Type.NBT);
                    wrapper.write(Type.STRING, EntityPackets1_16_2.this.getDimensionFromData(dimensionData));
                });
                this.map(Type.STRING);
                this.map(Type.LONG);
                this.handler(wrapper -> {
                    int maxPlayers = wrapper.read(Type.VAR_INT);
                    wrapper.write(Type.UNSIGNED_BYTE, (short)Math.max(maxPlayers, 255));
                });
                this.handler(EntityPackets1_16_2.this.getTrackerHandler(Entity1_16_2Types.PLAYER, Type.INT));
            }
        });
        ((Protocol1_16_1To1_16_2)this.protocol).registerClientbound(ClientboundPackets1_16_2.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    CompoundTag dimensionData = wrapper.read(Type.NBT);
                    wrapper.write(Type.STRING, EntityPackets1_16_2.this.getDimensionFromData(dimensionData));
                });
            }
        });
    }

    private String getDimensionFromData(CompoundTag dimensionData) {
        StringTag effectsLocation = (StringTag)dimensionData.get("effects");
        return effectsLocation != null && this.oldDimensions.contains(effectsLocation.getValue()) ? effectsLocation.getValue() : "minecraft:overworld";
    }

    @Override
    protected void registerRewrites() {
        this.registerMetaTypeHandler(Types1_16.META_TYPES.itemType, Types1_16.META_TYPES.blockStateType, Types1_16.META_TYPES.particleType, Types1_16.META_TYPES.optionalComponentType);
        this.mapTypes(Entity1_16_2Types.values(), Entity1_16Types.class);
        this.mapEntityTypeWithData(Entity1_16_2Types.PIGLIN_BRUTE, Entity1_16_2Types.PIGLIN).jsonName();
        this.filter().filterFamily(Entity1_16_2Types.ABSTRACT_PIGLIN).index(15).toIndex(16);
        this.filter().filterFamily(Entity1_16_2Types.ABSTRACT_PIGLIN).index(16).toIndex(15);
    }

    @Override
    public EntityType typeFromId(int typeId) {
        return Entity1_16_2Types.getTypeFromId(typeId);
    }
}

