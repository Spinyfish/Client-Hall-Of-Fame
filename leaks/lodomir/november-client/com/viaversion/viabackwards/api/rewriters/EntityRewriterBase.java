/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viabackwards.api.rewriters;

import com.google.common.base.Preconditions;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class EntityRewriterBase<T extends BackwardsProtocol>
extends EntityRewriter<T> {
    private final Int2ObjectMap<EntityData> entityDataMappings = new Int2ObjectOpenHashMap<EntityData>();
    private final MetaType displayNameMetaType;
    private final MetaType displayVisibilityMetaType;
    private final int displayNameIndex;
    private final int displayVisibilityIndex;

    EntityRewriterBase(T protocol, MetaType displayNameMetaType, int displayNameIndex, MetaType displayVisibilityMetaType, int displayVisibilityIndex) {
        super(protocol, false);
        this.displayNameMetaType = displayNameMetaType;
        this.displayNameIndex = displayNameIndex;
        this.displayVisibilityMetaType = displayVisibilityMetaType;
        this.displayVisibilityIndex = displayVisibilityIndex;
    }

    @Override
    public void handleMetadata(int entityId, List<Metadata> metadataList, UserConnection connection) {
        super.handleMetadata(entityId, metadataList, connection);
        EntityType type = this.tracker(connection).entityType(entityId);
        if (type == null) {
            return;
        }
        EntityData entityData = this.entityDataForType(type);
        Metadata meta = this.getMeta(this.displayNameIndex, metadataList);
        if (meta != null && entityData != null && entityData.mobName() != null && (meta.getValue() == null || meta.getValue().toString().isEmpty()) && meta.metaType().typeId() == this.displayNameMetaType.typeId()) {
            meta.setValue(entityData.mobName());
            if (ViaBackwards.getConfig().alwaysShowOriginalMobName()) {
                this.removeMeta(this.displayVisibilityIndex, metadataList);
                metadataList.add(new Metadata(this.displayVisibilityIndex, this.displayVisibilityMetaType, true));
            }
        }
        if (entityData != null && entityData.hasBaseMeta()) {
            entityData.defaultMeta().createMeta(new WrappedMetadata(metadataList));
        }
    }

    protected @Nullable Metadata getMeta(int metaIndex, List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            if (metadata.id() != metaIndex) continue;
            return metadata;
        }
        return null;
    }

    protected void removeMeta(int metaIndex, List<Metadata> metadataList) {
        metadataList.removeIf(meta -> meta.id() == metaIndex);
    }

    protected boolean hasData(EntityType type) {
        return this.entityDataMappings.containsKey(type.getId());
    }

    protected @Nullable EntityData entityDataForType(EntityType type) {
        return (EntityData)this.entityDataMappings.get(type.getId());
    }

    protected @Nullable StoredEntityData storedEntityData(MetaHandlerEvent event) {
        return this.tracker(event.user()).entityData(event.entityId());
    }

    protected EntityData mapEntityTypeWithData(EntityType type, EntityType mappedType) {
        Preconditions.checkArgument((type.getClass() == mappedType.getClass() ? 1 : 0) != 0);
        int mappedReplacementId = this.newEntityId(mappedType.getId());
        EntityData data = new EntityData((BackwardsProtocol)this.protocol, type, mappedReplacementId);
        this.mapEntityType(type.getId(), mappedReplacementId);
        this.entityDataMappings.put(type.getId(), data);
        return data;
    }

    @Override
    public <E extends Enum<E>> void mapTypes(EntityType[] oldTypes, Class<E> newTypeClass) {
        if (this.typeMappings == null) {
            this.typeMappings = new Int2IntOpenHashMap(oldTypes.length, 0.99f);
            this.typeMappings.defaultReturnValue(-1);
        }
        for (EntityType oldType : oldTypes) {
            try {
                E newType = Enum.valueOf(newTypeClass, oldType.name());
                this.typeMappings.put(oldType.getId(), ((EntityType)newType).getId());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
    }

    public void registerMetaTypeHandler(@Nullable MetaType itemType, @Nullable MetaType blockType, @Nullable MetaType particleType, @Nullable MetaType optChatType) {
        this.filter().handler((event, meta) -> {
            JsonElement text;
            MetaType type = meta.metaType();
            if (itemType != null && type == itemType) {
                ((BackwardsProtocol)this.protocol).getItemRewriter().handleItemToClient((Item)meta.value());
            } else if (blockType != null && type == blockType) {
                int data = (Integer)meta.value();
                meta.setValue(((BackwardsProtocol)this.protocol).getMappingData().getNewBlockStateId(data));
            } else if (particleType != null && type == particleType) {
                this.rewriteParticle((Particle)meta.value());
            } else if (optChatType != null && type == optChatType && (text = (JsonElement)meta.value()) != null) {
                ((BackwardsProtocol)this.protocol).getTranslatableRewriter().processText(text);
            }
        });
    }

    protected PacketHandler getTrackerHandler(Type<? extends Number> intType, int typeIndex) {
        return wrapper -> {
            Number id = (Number)wrapper.get(intType, typeIndex);
            this.tracker(wrapper.user()).addEntity(wrapper.get(Type.VAR_INT, 0), this.typeFromId(id.intValue()));
        };
    }

    protected PacketHandler getTrackerHandler() {
        return this.getTrackerHandler(Type.VAR_INT, 1);
    }

    protected PacketHandler getTrackerHandler(EntityType entityType, Type<? extends Number> intType) {
        return wrapper -> this.tracker(wrapper.user()).addEntity((Integer)((Number)wrapper.get(intType, 0)), entityType);
    }

    protected PacketHandler getDimensionHandler(int index) {
        return wrapper -> {
            ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
            int dimensionId = wrapper.get(Type.INT, index);
            clientWorld.setEnvironment(dimensionId);
        };
    }
}

