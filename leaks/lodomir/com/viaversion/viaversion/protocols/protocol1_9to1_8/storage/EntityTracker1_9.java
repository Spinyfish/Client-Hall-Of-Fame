/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.storage;

import com.google.common.cache.CacheBuilder;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.bossbar.BossBar;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.flare.fastutil.Int2ObjectSyncMap;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.GameMode;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.BossBarProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.EntityIdProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.InventoryTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EntityTracker1_9
extends EntityTrackerBase {
    public static final String WITHER_TRANSLATABLE = "{\"translate\":\"entity.WitherBoss.name\"}";
    public static final String DRAGON_TRANSLATABLE = "{\"translate\":\"entity.EnderDragon.name\"}";
    private final Int2ObjectMap<UUID> uuidMap = Int2ObjectSyncMap.hashmap();
    private final Int2ObjectMap<List<Metadata>> metadataBuffer = Int2ObjectSyncMap.hashmap();
    private final Int2ObjectMap<Integer> vehicleMap = Int2ObjectSyncMap.hashmap();
    private final Int2ObjectMap<BossBar> bossBarMap = Int2ObjectSyncMap.hashmap();
    private final IntSet validBlocking = Int2ObjectSyncMap.hashset();
    private final Set<Integer> knownHolograms = Int2ObjectSyncMap.hashset();
    private final Set<Position> blockInteractions = Collections.newSetFromMap(CacheBuilder.newBuilder().maximumSize(1000L).expireAfterAccess(250L, TimeUnit.MILLISECONDS).build().asMap());
    private boolean blocking = false;
    private boolean autoTeam = false;
    private Position currentlyDigging = null;
    private boolean teamExists = false;
    private GameMode gameMode;
    private String currentTeam;
    private int heldItemSlot;
    private Item itemInSecondHand = null;

    public EntityTracker1_9(UserConnection user) {
        super(user, Entity1_10Types.EntityType.PLAYER);
    }

    public UUID getEntityUUID(int id) {
        UUID uuid = (UUID)this.uuidMap.get(id);
        if (uuid == null) {
            uuid = UUID.randomUUID();
            this.uuidMap.put(id, uuid);
        }
        return uuid;
    }

    public void setSecondHand(Item item) {
        this.setSecondHand(this.clientEntityId(), item);
    }

    public void setSecondHand(int entityID, Item item) {
        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.ENTITY_EQUIPMENT, null, this.user());
        wrapper.write(Type.VAR_INT, entityID);
        wrapper.write(Type.VAR_INT, 1);
        this.itemInSecondHand = item;
        wrapper.write(Type.ITEM, this.itemInSecondHand);
        try {
            wrapper.scheduleSend(Protocol1_9To1_8.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Item getItemInSecondHand() {
        return this.itemInSecondHand;
    }

    public void syncShieldWithSword() {
        boolean swordInHand = this.hasSwordInHand();
        if (!swordInHand || this.itemInSecondHand == null) {
            this.setSecondHand(swordInHand ? new DataItem(442, 1, 0, null) : null);
        }
    }

    public boolean hasSwordInHand() {
        InventoryTracker inventoryTracker = this.user().get(InventoryTracker.class);
        int inventorySlot = this.heldItemSlot + 36;
        int itemIdentifier = inventoryTracker.getItemId((short)0, (short)inventorySlot);
        return Protocol1_9To1_8.isSword(itemIdentifier);
    }

    @Override
    public void removeEntity(int entityId) {
        super.removeEntity(entityId);
        this.vehicleMap.remove(entityId);
        this.uuidMap.remove(entityId);
        this.validBlocking.remove(entityId);
        this.knownHolograms.remove(entityId);
        this.metadataBuffer.remove(entityId);
        BossBar bar = (BossBar)this.bossBarMap.remove(entityId);
        if (bar != null) {
            bar.hide();
            Via.getManager().getProviders().get(BossBarProvider.class).handleRemove(this.user(), bar.getId());
        }
    }

    public boolean interactedBlockRecently(int x, int y, int z) {
        return this.blockInteractions.contains(new Position(x, (short)y, z));
    }

    public void addBlockInteraction(Position p) {
        this.blockInteractions.add(p);
    }

    public void handleMetadata(int entityId, List<Metadata> metadataList) {
        EntityType type = this.entityType(entityId);
        if (type == null) {
            return;
        }
        for (Metadata metadata : new ArrayList<Metadata>(metadataList)) {
            if (type == Entity1_10Types.EntityType.WITHER && metadata.id() == 10) {
                metadataList.remove(metadata);
            }
            if (type == Entity1_10Types.EntityType.ENDER_DRAGON && metadata.id() == 11) {
                metadataList.remove(metadata);
            }
            if (type == Entity1_10Types.EntityType.SKELETON && this.getMetaByIndex(metadataList, 12) == null) {
                metadataList.add(new Metadata(12, MetaType1_9.Boolean, true));
            }
            if (type == Entity1_10Types.EntityType.HORSE && metadata.id() == 16 && (Integer)metadata.getValue() == Integer.MIN_VALUE) {
                metadata.setValue(0);
            }
            if (type == Entity1_10Types.EntityType.PLAYER) {
                if (metadata.id() == 0) {
                    byte data = (Byte)metadata.getValue();
                    if (entityId != this.getProvidedEntityId() && Via.getConfig().isShieldBlocking()) {
                        if ((data & 0x10) == 16) {
                            if (this.validBlocking.contains(entityId)) {
                                DataItem shield = new DataItem(442, 1, 0, null);
                                this.setSecondHand(entityId, shield);
                            } else {
                                this.setSecondHand(entityId, null);
                            }
                        } else {
                            this.setSecondHand(entityId, null);
                        }
                    }
                }
                if (metadata.id() == 12 && Via.getConfig().isLeftHandedHandling()) {
                    metadataList.add(new Metadata(13, MetaType1_9.Byte, (byte)(((Byte)metadata.getValue() & 0x80) == 0 ? 1 : 0)));
                }
            }
            if (type == Entity1_10Types.EntityType.ARMOR_STAND && Via.getConfig().isHologramPatch() && metadata.id() == 0 && this.getMetaByIndex(metadataList, 10) != null) {
                Metadata displayNameVisible;
                Metadata displayName;
                Metadata meta = this.getMetaByIndex(metadataList, 10);
                byte data = (Byte)metadata.getValue();
                if ((data & 0x20) == 32 && ((Byte)meta.getValue() & 1) == 1 && (displayName = this.getMetaByIndex(metadataList, 2)) != null && !((String)displayName.getValue()).isEmpty() && (displayNameVisible = this.getMetaByIndex(metadataList, 3)) != null && ((Boolean)displayNameVisible.getValue()).booleanValue() && !this.knownHolograms.contains(entityId)) {
                    this.knownHolograms.add(entityId);
                    try {
                        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.ENTITY_POSITION, null, this.user());
                        wrapper.write(Type.VAR_INT, entityId);
                        wrapper.write(Type.SHORT, (short)0);
                        wrapper.write(Type.SHORT, (short)(128.0 * (Via.getConfig().getHologramYOffset() * 32.0)));
                        wrapper.write(Type.SHORT, (short)0);
                        wrapper.write(Type.BOOLEAN, true);
                        wrapper.scheduleSend(Protocol1_9To1_8.class);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            if (!Via.getConfig().isBossbarPatch() || type != Entity1_10Types.EntityType.ENDER_DRAGON && type != Entity1_10Types.EntityType.WITHER) continue;
            if (metadata.id() == 2) {
                BossBar bar = (BossBar)this.bossBarMap.get(entityId);
                String title = (String)metadata.getValue();
                String string = title.isEmpty() ? (type == Entity1_10Types.EntityType.ENDER_DRAGON ? DRAGON_TRANSLATABLE : WITHER_TRANSLATABLE) : (title = title);
                if (bar == null) {
                    bar = Via.getAPI().legacyAPI().createLegacyBossBar(title, BossColor.PINK, BossStyle.SOLID);
                    this.bossBarMap.put(entityId, bar);
                    bar.addConnection(this.user());
                    bar.show();
                    Via.getManager().getProviders().get(BossBarProvider.class).handleAdd(this.user(), bar.getId());
                    continue;
                }
                bar.setTitle(title);
                continue;
            }
            if (metadata.id() != 6 || Via.getConfig().isBossbarAntiflicker()) continue;
            BossBar bar = (BossBar)this.bossBarMap.get(entityId);
            float maxHealth = type == Entity1_10Types.EntityType.ENDER_DRAGON ? 200.0f : 300.0f;
            float health = Math.max(0.0f, Math.min(((Float)metadata.getValue()).floatValue() / maxHealth, 1.0f));
            if (bar == null) {
                String title = type == Entity1_10Types.EntityType.ENDER_DRAGON ? DRAGON_TRANSLATABLE : WITHER_TRANSLATABLE;
                bar = Via.getAPI().legacyAPI().createLegacyBossBar(title, health, BossColor.PINK, BossStyle.SOLID);
                this.bossBarMap.put(entityId, bar);
                bar.addConnection(this.user());
                bar.show();
                Via.getManager().getProviders().get(BossBarProvider.class).handleAdd(this.user(), bar.getId());
                continue;
            }
            bar.setHealth(health);
        }
    }

    public Metadata getMetaByIndex(List<Metadata> list, int index) {
        for (Metadata meta : list) {
            if (index != meta.id()) continue;
            return meta;
        }
        return null;
    }

    public void sendTeamPacket(boolean add, boolean now) {
        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.TEAMS, null, this.user());
        wrapper.write(Type.STRING, "viaversion");
        if (add) {
            if (!this.teamExists) {
                wrapper.write(Type.BYTE, (byte)0);
                wrapper.write(Type.STRING, "viaversion");
                wrapper.write(Type.STRING, "\u00a7f");
                wrapper.write(Type.STRING, "");
                wrapper.write(Type.BYTE, (byte)0);
                wrapper.write(Type.STRING, "");
                wrapper.write(Type.STRING, "never");
                wrapper.write(Type.BYTE, (byte)15);
            } else {
                wrapper.write(Type.BYTE, (byte)3);
            }
            wrapper.write(Type.STRING_ARRAY, new String[]{this.user().getProtocolInfo().getUsername()});
        } else {
            wrapper.write(Type.BYTE, (byte)1);
        }
        this.teamExists = add;
        try {
            if (now) {
                wrapper.send(Protocol1_9To1_8.class);
            } else {
                wrapper.scheduleSend(Protocol1_9To1_8.class);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMetadataToBuffer(int entityID, List<Metadata> metadataList) {
        List metadata = (List)this.metadataBuffer.get(entityID);
        if (metadata != null) {
            metadata.addAll(metadataList);
        } else {
            this.metadataBuffer.put(entityID, metadataList);
        }
    }

    public void sendMetadataBuffer(int entityId) {
        List metadataList = (List)this.metadataBuffer.get(entityId);
        if (metadataList != null) {
            PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.ENTITY_METADATA, null, this.user());
            wrapper.write(Type.VAR_INT, entityId);
            wrapper.write(Types1_9.METADATA_LIST, metadataList);
            Via.getManager().getProtocolManager().getProtocol(Protocol1_9To1_8.class).get(MetadataRewriter1_9To1_8.class).handleMetadata(entityId, metadataList, this.user());
            this.handleMetadata(entityId, metadataList);
            if (!metadataList.isEmpty()) {
                try {
                    wrapper.scheduleSend(Protocol1_9To1_8.class);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.metadataBuffer.remove(entityId);
        }
    }

    public int getProvidedEntityId() {
        try {
            return Via.getManager().getProviders().get(EntityIdProvider.class).getEntityId(this.user());
        }
        catch (Exception e) {
            return this.clientEntityId();
        }
    }

    public Map<Integer, UUID> getUuidMap() {
        return this.uuidMap;
    }

    public Map<Integer, List<Metadata>> getMetadataBuffer() {
        return this.metadataBuffer;
    }

    public Map<Integer, Integer> getVehicleMap() {
        return this.vehicleMap;
    }

    public Map<Integer, BossBar> getBossBarMap() {
        return this.bossBarMap;
    }

    public Set<Integer> getValidBlocking() {
        return this.validBlocking;
    }

    public Set<Integer> getKnownHolograms() {
        return this.knownHolograms;
    }

    public Set<Position> getBlockInteractions() {
        return this.blockInteractions;
    }

    public boolean isBlocking() {
        return this.blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean isAutoTeam() {
        return this.autoTeam;
    }

    public void setAutoTeam(boolean autoTeam) {
        this.autoTeam = autoTeam;
    }

    public Position getCurrentlyDigging() {
        return this.currentlyDigging;
    }

    public void setCurrentlyDigging(Position currentlyDigging) {
        this.currentlyDigging = currentlyDigging;
    }

    public boolean isTeamExists() {
        return this.teamExists;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public String getCurrentTeam() {
        return this.currentTeam;
    }

    public void setCurrentTeam(String currentTeam) {
        this.currentTeam = currentTeam;
    }

    public void setHeldItemSlot(int heldItemSlot) {
        this.heldItemSlot = heldItemSlot;
    }
}

