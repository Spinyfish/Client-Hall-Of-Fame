/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.ClientEntityIdChangeListener;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.metadata.MetadataRewriter;
import de.gerrygames.viarewind.replacement.EntityReplacement;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker
extends StoredObject
implements ClientEntityIdChangeListener {
    private final Map<Integer, Entity1_10Types.EntityType> clientEntityTypes = new ConcurrentHashMap<Integer, Entity1_10Types.EntityType>();
    private final Map<Integer, List<Metadata>> metadataBuffer = new ConcurrentHashMap<Integer, List<Metadata>>();
    private final Map<Integer, Integer> vehicles = new ConcurrentHashMap<Integer, Integer>();
    private final Map<Integer, EntityReplacement> entityReplacements = new ConcurrentHashMap<Integer, EntityReplacement>();
    private final Map<Integer, UUID> playersByEntityId = new HashMap<Integer, UUID>();
    private final Map<UUID, Integer> playersByUniqueId = new HashMap<UUID, Integer>();
    private final Map<UUID, Item[]> playerEquipment = new HashMap<UUID, Item[]>();
    private int gamemode = 0;
    private int playerId = -1;
    private int spectating = -1;
    private int dimension = 0;

    public EntityTracker(UserConnection user) {
        super(user);
    }

    public void removeEntity(int entityId) {
        this.clientEntityTypes.remove(entityId);
        if (this.entityReplacements.containsKey(entityId)) {
            this.entityReplacements.remove(entityId).despawn();
        }
        if (this.playersByEntityId.containsKey(entityId)) {
            this.playersByUniqueId.remove(this.playersByEntityId.remove(entityId));
        }
    }

    public void addPlayer(Integer entityId, UUID uuid) {
        this.playersByUniqueId.put(uuid, entityId);
        this.playersByEntityId.put(entityId, uuid);
    }

    public UUID getPlayerUUID(int entityId) {
        return this.playersByEntityId.get(entityId);
    }

    public int getPlayerEntityId(UUID uuid) {
        return this.playersByUniqueId.getOrDefault(uuid, -1);
    }

    public Item[] getPlayerEquipment(UUID uuid) {
        return this.playerEquipment.get(uuid);
    }

    public void setPlayerEquipment(UUID uuid, Item[] equipment) {
        this.playerEquipment.put(uuid, equipment);
    }

    public Map<Integer, Entity1_10Types.EntityType> getClientEntityTypes() {
        return this.clientEntityTypes;
    }

    public void addMetadataToBuffer(int entityID, List<Metadata> metadataList) {
        if (this.metadataBuffer.containsKey(entityID)) {
            this.metadataBuffer.get(entityID).addAll(metadataList);
        } else if (!metadataList.isEmpty()) {
            this.metadataBuffer.put(entityID, metadataList);
        }
    }

    public void addEntityReplacement(EntityReplacement entityReplacement) {
        this.entityReplacements.put(entityReplacement.getEntityId(), entityReplacement);
    }

    public EntityReplacement getEntityReplacement(int entityId) {
        return this.entityReplacements.get(entityId);
    }

    public List<Metadata> getBufferedMetadata(int entityId) {
        return this.metadataBuffer.get(entityId);
    }

    public void sendMetadataBuffer(int entityId) {
        if (!this.metadataBuffer.containsKey(entityId)) {
            return;
        }
        if (this.entityReplacements.containsKey(entityId)) {
            this.entityReplacements.get(entityId).updateMetadata(this.metadataBuffer.remove(entityId));
        } else {
            Entity1_10Types.EntityType type = this.getClientEntityTypes().get(entityId);
            PacketWrapper wrapper = PacketWrapper.create(28, null, this.getUser());
            wrapper.write(Type.VAR_INT, entityId);
            wrapper.write(Types1_8.METADATA_LIST, this.metadataBuffer.get(entityId));
            MetadataRewriter.transform(type, this.metadataBuffer.get(entityId));
            if (!this.metadataBuffer.get(entityId).isEmpty()) {
                PacketUtil.sendPacket(wrapper, Protocol1_7_6_10TO1_8.class);
            }
            this.metadataBuffer.remove(entityId);
        }
    }

    public int getVehicle(int passengerId) {
        for (Map.Entry<Integer, Integer> vehicle : this.vehicles.entrySet()) {
            if (vehicle.getValue() != passengerId) continue;
            return vehicle.getValue();
        }
        return -1;
    }

    public int getPassenger(int vehicleId) {
        return this.vehicles.getOrDefault(vehicleId, -1);
    }

    public void setPassenger(int vehicleId, int passengerId) {
        if (vehicleId == this.spectating && this.spectating != this.playerId) {
            try {
                PacketWrapper sneakPacket = PacketWrapper.create(11, null, this.getUser());
                sneakPacket.write(Type.VAR_INT, this.playerId);
                sneakPacket.write(Type.VAR_INT, 0);
                sneakPacket.write(Type.VAR_INT, 0);
                PacketWrapper unsneakPacket = PacketWrapper.create(11, null, this.getUser());
                unsneakPacket.write(Type.VAR_INT, this.playerId);
                unsneakPacket.write(Type.VAR_INT, 1);
                unsneakPacket.write(Type.VAR_INT, 0);
                PacketUtil.sendToServer(sneakPacket, Protocol1_7_6_10TO1_8.class, true, true);
                this.setSpectating(this.playerId);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (vehicleId == -1) {
            int oldVehicleId = this.getVehicle(passengerId);
            this.vehicles.remove(oldVehicleId);
        } else if (passengerId == -1) {
            this.vehicles.remove(vehicleId);
        } else {
            this.vehicles.put(vehicleId, passengerId);
        }
    }

    public int getSpectating() {
        return this.spectating;
    }

    public boolean setSpectating(int spectating) {
        if (spectating != this.playerId && this.getPassenger(spectating) != -1) {
            PacketWrapper sneakPacket = PacketWrapper.create(11, null, this.getUser());
            sneakPacket.write(Type.VAR_INT, this.playerId);
            sneakPacket.write(Type.VAR_INT, 0);
            sneakPacket.write(Type.VAR_INT, 0);
            PacketWrapper unsneakPacket = PacketWrapper.create(11, null, this.getUser());
            unsneakPacket.write(Type.VAR_INT, this.playerId);
            unsneakPacket.write(Type.VAR_INT, 1);
            unsneakPacket.write(Type.VAR_INT, 0);
            PacketUtil.sendToServer(sneakPacket, Protocol1_7_6_10TO1_8.class, true, true);
            this.setSpectating(this.playerId);
            return false;
        }
        if (this.spectating != spectating && this.spectating != this.playerId) {
            PacketWrapper unmount = PacketWrapper.create(27, null, this.getUser());
            unmount.write(Type.INT, this.playerId);
            unmount.write(Type.INT, -1);
            unmount.write(Type.BOOLEAN, false);
            PacketUtil.sendPacket(unmount, Protocol1_7_6_10TO1_8.class);
        }
        this.spectating = spectating;
        if (spectating != this.playerId) {
            PacketWrapper mount = PacketWrapper.create(27, null, this.getUser());
            mount.write(Type.INT, this.playerId);
            mount.write(Type.INT, this.spectating);
            mount.write(Type.BOOLEAN, false);
            PacketUtil.sendPacket(mount, Protocol1_7_6_10TO1_8.class);
        }
        return true;
    }

    public int getGamemode() {
        return this.gamemode;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = this.spectating = playerId;
    }

    public void clearEntities() {
        this.clientEntityTypes.clear();
        this.entityReplacements.clear();
        this.vehicles.clear();
        this.metadataBuffer.clear();
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public void setClientEntityId(int playerEntityId) {
        if (this.spectating == this.playerId) {
            this.spectating = playerEntityId;
        }
        this.clientEntityTypes.remove(this.playerId);
        this.playerId = playerEntityId;
        this.clientEntityTypes.put(this.playerId, Entity1_10Types.EntityType.ENTITY_HUMAN);
    }
}

