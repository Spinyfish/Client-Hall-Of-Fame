/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.rewriter;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.rewriter.meta.MetaFilter;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEventImpl;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class EntityRewriter<T extends Protocol>
extends RewriterBase<T>
implements com.viaversion.viaversion.api.rewriter.EntityRewriter<T> {
    private static final Metadata[] EMPTY_ARRAY = new Metadata[0];
    protected final List<MetaFilter> metadataFilters = new ArrayList<MetaFilter>();
    protected final boolean trackMappedType;
    protected Int2IntMap typeMappings;

    protected EntityRewriter(T protocol) {
        this(protocol, true);
    }

    protected EntityRewriter(T protocol, boolean trackMappedType) {
        super(protocol);
        this.trackMappedType = trackMappedType;
        protocol.put(this);
    }

    public MetaFilter.Builder filter() {
        return new MetaFilter.Builder(this);
    }

    public void registerFilter(MetaFilter filter) {
        Preconditions.checkArgument((!this.metadataFilters.contains(filter) ? 1 : 0) != 0);
        this.metadataFilters.add(filter);
    }

    @Override
    public void handleMetadata(int entityId, List<Metadata> metadataList, UserConnection connection) {
        EntityType type = this.tracker(connection).entityType(entityId);
        int i = 0;
        for (Metadata metadata : metadataList.toArray(EMPTY_ARRAY)) {
            if (!this.callOldMetaHandler(entityId, type, metadata, metadataList, connection)) {
                metadataList.remove(i--);
                continue;
            }
            MetaHandlerEvent event = null;
            for (MetaFilter filter : this.metadataFilters) {
                if (!filter.isFiltered(type, metadata)) continue;
                if (event == null) {
                    event = new MetaHandlerEventImpl(connection, type, entityId, metadata, metadataList);
                }
                try {
                    filter.handler().handle(event, metadata);
                }
                catch (Exception e) {
                    this.logException(e, type, metadataList, metadata);
                    metadataList.remove(i--);
                    break;
                }
                if (!event.cancelled()) continue;
                metadataList.remove(i--);
                break;
            }
            if (event != null && event.extraMeta() != null) {
                metadataList.addAll(event.extraMeta());
            }
            ++i;
        }
    }

    @Deprecated
    private boolean callOldMetaHandler(int entityId, @Nullable EntityType type, Metadata metadata, List<Metadata> metadataList, UserConnection connection) {
        try {
            this.handleMetadata(entityId, type, metadata, metadataList, connection);
            return true;
        }
        catch (Exception e) {
            this.logException(e, type, metadataList, metadata);
            return false;
        }
    }

    @Deprecated
    protected void handleMetadata(int entityId, @Nullable EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    }

    @Override
    public int newEntityId(int id) {
        return this.typeMappings != null ? this.typeMappings.getOrDefault(id, id) : id;
    }

    public void mapEntityType(EntityType type, EntityType mappedType) {
        Preconditions.checkArgument((type.getClass() != mappedType.getClass() ? 1 : 0) != 0, (Object)"EntityTypes should not be of the same class/enum");
        this.mapEntityType(type.getId(), mappedType.getId());
    }

    protected void mapEntityType(int id, int mappedId) {
        if (this.typeMappings == null) {
            this.typeMappings = new Int2IntOpenHashMap();
            this.typeMappings.defaultReturnValue(-1);
        }
        this.typeMappings.put(id, mappedId);
    }

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
            catch (IllegalArgumentException notFound) {
                if (this.typeMappings.containsKey(oldType.getId())) continue;
                Via.getPlatform().getLogger().warning("Could not find new entity type for " + oldType + "! Old type: " + oldType.getClass().getEnclosingClass().getSimpleName() + ", new type: " + newTypeClass.getEnclosingClass().getSimpleName());
            }
        }
    }

    public void registerMetaTypeHandler(@Nullable MetaType itemType, @Nullable MetaType blockType, @Nullable MetaType particleType) {
        this.filter().handler((event, meta) -> {
            if (itemType != null && meta.metaType() == itemType) {
                this.protocol.getItemRewriter().handleItemToClient((Item)meta.value());
            } else if (blockType != null && meta.metaType() == blockType) {
                int data = (Integer)meta.value();
                meta.setValue(this.protocol.getMappingData().getNewBlockStateId(data));
            } else if (particleType != null && meta.metaType() == particleType) {
                this.rewriteParticle((Particle)meta.value());
            }
        });
    }

    public void registerTracker(ClientboundPacketType packetType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.VAR_INT);
                this.handler(EntityRewriter.this.trackerHandler());
            }
        });
    }

    public void registerTrackerWithData(ClientboundPacketType packetType, final EntityType fallingBlockType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

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
                this.handler(EntityRewriter.this.trackerHandler());
                this.handler(wrapper -> {
                    int entityId = wrapper.get(Type.VAR_INT, 0);
                    EntityType entityType = EntityRewriter.this.tracker(wrapper.user()).entityType(entityId);
                    if (entityType == fallingBlockType) {
                        wrapper.set(Type.INT, 0, EntityRewriter.this.protocol.getMappingData().getNewBlockStateId(wrapper.get(Type.INT, 0)));
                    }
                });
            }
        });
    }

    public void registerTracker(ClientboundPacketType packetType, final EntityType entityType, final Type<Integer> intType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int entityId = (Integer)wrapper.passthrough(intType);
                    EntityRewriter.this.tracker(wrapper.user()).addEntity(entityId, entityType);
                });
            }
        });
    }

    public void registerTracker(ClientboundPacketType packetType, EntityType entityType) {
        this.registerTracker(packetType, entityType, Type.VAR_INT);
    }

    public void registerRemoveEntities(ClientboundPacketType packetType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int[] entityIds = wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                    Object entityTracker = EntityRewriter.this.tracker(wrapper.user());
                    for (int entity : entityIds) {
                        entityTracker.removeEntity(entity);
                    }
                });
            }
        });
    }

    public void registerRemoveEntity(ClientboundPacketType packetType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int entityId = wrapper.passthrough(Type.VAR_INT);
                    EntityRewriter.this.tracker(wrapper.user()).removeEntity(entityId);
                });
            }
        });
    }

    public void registerMetadataRewriter(ClientboundPacketType packetType, final @Nullable Type<List<Metadata>> oldMetaType, final Type<List<Metadata>> newMetaType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                if (oldMetaType != null) {
                    this.map(oldMetaType, newMetaType);
                } else {
                    this.map(newMetaType);
                }
                this.handler(wrapper -> {
                    int entityId = wrapper.get(Type.VAR_INT, 0);
                    List metadata = (List)wrapper.get(newMetaType, 0);
                    EntityRewriter.this.handleMetadata(entityId, metadata, wrapper.user());
                });
            }
        });
    }

    public void registerMetadataRewriter(ClientboundPacketType packetType, Type<List<Metadata>> metaType) {
        this.registerMetadataRewriter(packetType, null, metaType);
    }

    public PacketHandler trackerHandler() {
        return this.trackerAndRewriterHandler(null);
    }

    public PacketHandler worldDataTrackerHandler(int nbtIndex) {
        return wrapper -> {
            Object tracker = this.tracker(wrapper.user());
            CompoundTag registryData = wrapper.get(Type.NBT, nbtIndex);
            Object height = registryData.get("height");
            if (height instanceof IntTag) {
                int blockHeight = ((IntTag)height).asInt();
                tracker.setCurrentWorldSectionHeight(blockHeight >> 4);
            } else {
                Via.getPlatform().getLogger().warning("Height missing in dimension data: " + registryData);
            }
            Object minY = registryData.get("min_y");
            if (minY instanceof IntTag) {
                tracker.setCurrentMinY(((IntTag)minY).asInt());
            } else {
                Via.getPlatform().getLogger().warning("Min Y missing in dimension data: " + registryData);
            }
            String world = wrapper.get(Type.STRING, 0);
            if (tracker.currentWorld() != null && !tracker.currentWorld().equals(world)) {
                tracker.clearEntities();
            }
            tracker.setCurrentWorld(world);
        };
    }

    public PacketHandler biomeSizeTracker() {
        return wrapper -> {
            CompoundTag registry = wrapper.get(Type.NBT, 0);
            CompoundTag biomeRegistry = (CompoundTag)registry.get("minecraft:worldgen/biome");
            ListTag biomes = (ListTag)biomeRegistry.get("value");
            this.tracker(wrapper.user()).setBiomesSent(biomes.size());
        };
    }

    public PacketHandler trackerAndRewriterHandler(@Nullable Type<List<Metadata>> metaType) {
        return wrapper -> {
            int entityId = wrapper.get(Type.VAR_INT, 0);
            int type = wrapper.get(Type.VAR_INT, 1);
            int newType = this.newEntityId(type);
            if (newType != type) {
                wrapper.set(Type.VAR_INT, 1, newType);
            }
            EntityType entType = this.typeFromId(this.trackMappedType ? newType : type);
            this.tracker(wrapper.user()).addEntity(entityId, entType);
            if (metaType != null) {
                this.handleMetadata(entityId, (List)wrapper.get(metaType, 0), wrapper.user());
            }
        };
    }

    public PacketHandler trackerAndRewriterHandler(@Nullable Type<List<Metadata>> metaType, EntityType entityType) {
        return wrapper -> {
            int entityId = wrapper.get(Type.VAR_INT, 0);
            this.tracker(wrapper.user()).addEntity(entityId, entityType);
            if (metaType != null) {
                this.handleMetadata(entityId, (List)wrapper.get(metaType, 0), wrapper.user());
            }
        };
    }

    public PacketHandler objectTrackerHandler() {
        return wrapper -> {
            int entityId = wrapper.get(Type.VAR_INT, 0);
            byte type = wrapper.get(Type.BYTE, 0);
            EntityType entType = this.objectTypeFromId(type);
            this.tracker(wrapper.user()).addEntity(entityId, entType);
        };
    }

    @Deprecated
    protected @Nullable Metadata metaByIndex(int index, List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            if (metadata.id() != index) continue;
            return metadata;
        }
        return null;
    }

    protected void rewriteParticle(Particle particle) {
        int id;
        ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
        if (mappings.isBlockParticle(id = particle.getId())) {
            Particle.ParticleData data = particle.getArguments().get(0);
            data.setValue(this.protocol.getMappingData().getNewBlockStateId((Integer)data.get()));
        } else if (mappings.isItemParticle(id) && this.protocol.getItemRewriter() != null) {
            Particle.ParticleData data = particle.getArguments().get(0);
            Item item = (Item)data.get();
            this.protocol.getItemRewriter().handleItemToClient(item);
        }
        particle.setId(this.protocol.getMappingData().getNewParticleId(id));
    }

    private void logException(Exception e, @Nullable EntityType type, List<Metadata> metadataList, Metadata metadata) {
        if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
            Logger logger = Via.getPlatform().getLogger();
            logger.severe("An error occurred in metadata handler " + this.getClass().getSimpleName() + " for " + (type != null ? type.name() : "untracked") + " entity type: " + metadata);
            logger.severe(metadataList.stream().sorted(Comparator.comparingInt(Metadata::id)).map(Metadata::toString).collect(Collectors.joining("\n", "Full metadata: ", "")));
            e.printStackTrace();
        }
    }
}

