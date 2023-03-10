/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_8to1_9.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.ClientEntityIdChangeListener;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.metadata.MetadataRewriter;
import de.gerrygames.viarewind.replacement.EntityReplacement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker
extends StoredObject
implements ClientEntityIdChangeListener {
    private final Map<Integer, List<Integer>> vehicleMap = new ConcurrentHashMap<Integer, List<Integer>>();
    private final Map<Integer, Entity1_10Types.EntityType> clientEntityTypes = new ConcurrentHashMap<Integer, Entity1_10Types.EntityType>();
    private final Map<Integer, List<Metadata>> metadataBuffer = new ConcurrentHashMap<Integer, List<Metadata>>();
    private final Map<Integer, EntityReplacement> entityReplacements = new ConcurrentHashMap<Integer, EntityReplacement>();
    private final Map<Integer, Vector> entityOffsets = new ConcurrentHashMap<Integer, Vector>();
    private int playerId;
    private int playerGamemode = 0;

    public EntityTracker(UserConnection user) {
        super(user);
    }

    public void setPlayerId(int entityId) {
        this.playerId = entityId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getPlayerGamemode() {
        return this.playerGamemode;
    }

    public void setPlayerGamemode(int playerGamemode) {
        this.playerGamemode = playerGamemode;
    }

    public void removeEntity(int entityId) {
        this.vehicleMap.remove(entityId);
        this.vehicleMap.forEach((vehicle, passengers) -> passengers.remove((Object)entityId));
        this.vehicleMap.entrySet().removeIf(entry -> ((List)entry.getValue()).isEmpty());
        this.clientEntityTypes.remove(entityId);
        this.entityOffsets.remove(entityId);
        if (this.entityReplacements.containsKey(entityId)) {
            this.entityReplacements.remove(entityId).despawn();
        }
    }

    public void resetEntityOffset(int entityId) {
        this.entityOffsets.remove(entityId);
    }

    public Vector getEntityOffset(int entityId) {
        return this.entityOffsets.computeIfAbsent(entityId, key -> new Vector(0, 0, 0));
    }

    public void addToEntityOffset(int entityId, short relX, short relY, short relZ) {
        this.entityOffsets.compute(entityId, (key, offset) -> {
            if (offset == null) {
                return new Vector(relX, relY, relZ);
            }
            offset.setBlockX(offset.getBlockX() + relX);
            offset.setBlockY(offset.getBlockY() + relY);
            offset.setBlockZ(offset.getBlockZ() + relZ);
            return offset;
        });
    }

    public void setEntityOffset(int entityId, short relX, short relY, short relZ) {
        this.entityOffsets.compute(entityId, (key, offset) -> {
            if (offset == null) {
                return new Vector(relX, relY, relZ);
            }
            offset.setBlockX(relX);
            offset.setBlockY(relY);
            offset.setBlockZ(relZ);
            return offset;
        });
    }

    public void setEntityOffset(int entityId, Vector offset) {
        this.entityOffsets.put(entityId, offset);
    }

    public List<Integer> getPassengers(int entityId) {
        return this.vehicleMap.getOrDefault(entityId, new ArrayList());
    }

    public void setPassengers(int entityId, List<Integer> passengers) {
        this.vehicleMap.put(entityId, passengers);
    }

    public void addEntityReplacement(EntityReplacement entityReplacement) {
        this.entityReplacements.put(entityReplacement.getEntityId(), entityReplacement);
    }

    public EntityReplacement getEntityReplacement(int entityId) {
        return this.entityReplacements.get(entityId);
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

    public List<Metadata> getBufferedMetadata(int entityId) {
        return this.metadataBuffer.get(entityId);
    }

    public boolean isInsideVehicle(int entityId) {
        for (List<Integer> vehicle : this.vehicleMap.values()) {
            if (!vehicle.contains(entityId)) continue;
            return true;
        }
        return false;
    }

    public int getVehicle(int passenger) {
        for (Map.Entry<Integer, List<Integer>> vehicle : this.vehicleMap.entrySet()) {
            if (!vehicle.getValue().contains(passenger)) continue;
            return vehicle.getKey();
        }
        return -1;
    }

    public boolean isPassenger(int vehicle, int passenger) {
        return this.vehicleMap.containsKey(vehicle) && this.vehicleMap.get(vehicle).contains(passenger);
    }

    public void sendMetadataBuffer(int entityId) {
        if (!this.metadataBuffer.containsKey(entityId)) {
            return;
        }
        if (this.entityReplacements.containsKey(entityId)) {
            this.entityReplacements.get(entityId).updateMetadata(this.metadataBuffer.remove(entityId));
        } else {
            PacketWrapper wrapper = PacketWrapper.create(28, null, this.getUser());
            wrapper.write(Type.VAR_INT, entityId);
            wrapper.write(Types1_8.METADATA_LIST, this.metadataBuffer.get(entityId));
            MetadataRewriter.transform(this.getClientEntityTypes().get(entityId), this.metadataBuffer.get(entityId));
            if (!this.metadataBuffer.get(entityId).isEmpty()) {
                try {
                    wrapper.send(Protocol1_8TO1_9.class);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            this.metadataBuffer.remove(entityId);
        }
    }

    @Override
    public void setClientEntityId(int playerEntityId) {
        this.clientEntityTypes.remove(this.playerId);
        this.playerId = playerEntityId;
        this.clientEntityTypes.put(this.playerId, Entity1_10Types.EntityType.ENTITY_HUMAN);
    }
}

