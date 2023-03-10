/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api.entities.storage;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.entities.storage.EntityPositionStorage;
import com.viaversion.viabackwards.api.rewriters.EntityRewriterBase;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import java.util.function.Supplier;

public class EntityPositionHandler {
    public static final double RELATIVE_MOVE_FACTOR = 4096.0;
    private final EntityRewriterBase entityRewriter;
    private final Class<? extends EntityPositionStorage> storageClass;
    private final Supplier<? extends EntityPositionStorage> storageSupplier;
    private boolean warnedForMissingEntity;

    public EntityPositionHandler(EntityRewriterBase entityRewriter, Class<? extends EntityPositionStorage> storageClass, Supplier<? extends EntityPositionStorage> storageSupplier) {
        this.entityRewriter = entityRewriter;
        this.storageClass = storageClass;
        this.storageSupplier = storageSupplier;
    }

    public void cacheEntityPosition(PacketWrapper wrapper, boolean create, boolean relative) throws Exception {
        this.cacheEntityPosition(wrapper, wrapper.get(Type.DOUBLE, 0), wrapper.get(Type.DOUBLE, 1), wrapper.get(Type.DOUBLE, 2), create, relative);
    }

    public void cacheEntityPosition(PacketWrapper wrapper, double x, double y, double z, boolean create, boolean relative) throws Exception {
        EntityPositionStorage positionStorage;
        int entityId = wrapper.get(Type.VAR_INT, 0);
        StoredEntityData storedEntity = this.entityRewriter.tracker(wrapper.user()).entityData(entityId);
        if (storedEntity == null) {
            if (Via.getManager().isDebug()) {
                ViaBackwards.getPlatform().getLogger().warning("Stored entity with id " + entityId + " missing at position: " + x + " - " + y + " - " + z + " in " + this.storageClass.getSimpleName());
                if (entityId == -1 && x == 0.0 && y == 0.0 && z == 0.0) {
                    ViaBackwards.getPlatform().getLogger().warning("DO NOT REPORT THIS TO VIA, THIS IS A PLUGIN ISSUE");
                } else if (!this.warnedForMissingEntity) {
                    this.warnedForMissingEntity = true;
                    ViaBackwards.getPlatform().getLogger().warning("This is very likely caused by a plugin sending a teleport packet for an entity outside of the player's range.");
                }
            }
            return;
        }
        if (create) {
            positionStorage = this.storageSupplier.get();
            storedEntity.put(positionStorage);
        } else {
            positionStorage = storedEntity.get(this.storageClass);
            if (positionStorage == null) {
                ViaBackwards.getPlatform().getLogger().warning("Stored entity with id " + entityId + " missing " + this.storageClass.getSimpleName());
                return;
            }
        }
        positionStorage.setCoordinates(x, y, z, relative);
    }

    public EntityPositionStorage getStorage(UserConnection user, int entityId) {
        EntityPositionStorage entityStorage;
        StoredEntityData storedEntity = this.entityRewriter.tracker(user).entityData(entityId);
        if (storedEntity == null || (entityStorage = storedEntity.get(EntityPositionStorage.class)) == null) {
            ViaBackwards.getPlatform().getLogger().warning("Untracked entity with id " + entityId + " in " + this.storageClass.getSimpleName());
            return null;
        }
        return entityStorage;
    }

    public static void writeFacingAngles(PacketWrapper wrapper, double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dX = targetX - x;
        double dY = targetY - y;
        double dZ = targetZ - z;
        double r = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        double yaw = -Math.atan2(dX, dZ) / Math.PI * 180.0;
        if (yaw < 0.0) {
            yaw = 360.0 + yaw;
        }
        double pitch = -Math.asin(dY / r) / Math.PI * 180.0;
        wrapper.write(Type.BYTE, (byte)(yaw * 256.0 / 360.0));
        wrapper.write(Type.BYTE, (byte)(pitch * 256.0 / 360.0));
    }

    public static void writeFacingDegrees(PacketWrapper wrapper, double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dX = targetX - x;
        double dY = targetY - y;
        double dZ = targetZ - z;
        double r = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        double yaw = -Math.atan2(dX, dZ) / Math.PI * 180.0;
        if (yaw < 0.0) {
            yaw = 360.0 + yaw;
        }
        double pitch = -Math.asin(dY / r) / Math.PI * 180.0;
        wrapper.write(Type.FLOAT, Float.valueOf((float)yaw));
        wrapper.write(Type.FLOAT, Float.valueOf((float)pitch));
    }
}

