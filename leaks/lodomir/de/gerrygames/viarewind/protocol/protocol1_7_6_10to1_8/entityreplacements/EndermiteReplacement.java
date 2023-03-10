/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.metadata.MetadataRewriter;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import de.gerrygames.viarewind.replacement.EntityReplacement;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.ArrayList;
import java.util.List;

public class EndermiteReplacement
implements EntityReplacement {
    private int entityId;
    private List<Metadata> datawatcher = new ArrayList<Metadata>();
    private double locX;
    private double locY;
    private double locZ;
    private float yaw;
    private float pitch;
    private float headYaw;
    private UserConnection user;

    public EndermiteReplacement(int entityId, UserConnection user) {
        this.entityId = entityId;
        this.user = user;
        this.spawn();
    }

    @Override
    public void setLocation(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.updateLocation();
    }

    @Override
    public void relMove(double x, double y, double z) {
        this.locX += x;
        this.locY += y;
        this.locZ += z;
        this.updateLocation();
    }

    @Override
    public void setYawPitch(float yaw, float pitch) {
        if (this.yaw != yaw || this.pitch != pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.updateLocation();
        }
    }

    @Override
    public void setHeadYaw(float yaw) {
        if (this.headYaw != yaw) {
            this.headYaw = yaw;
            this.updateLocation();
        }
    }

    @Override
    public void updateMetadata(List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            this.datawatcher.removeIf(m -> m.id() == metadata.id());
            this.datawatcher.add(metadata);
        }
        this.updateMetadata();
    }

    public void updateLocation() {
        PacketWrapper teleport = PacketWrapper.create(24, null, this.user);
        teleport.write(Type.INT, this.entityId);
        teleport.write(Type.INT, (int)(this.locX * 32.0));
        teleport.write(Type.INT, (int)(this.locY * 32.0));
        teleport.write(Type.INT, (int)(this.locZ * 32.0));
        teleport.write(Type.BYTE, (byte)(this.yaw / 360.0f * 256.0f));
        teleport.write(Type.BYTE, (byte)(this.pitch / 360.0f * 256.0f));
        PacketWrapper head = PacketWrapper.create(25, null, this.user);
        head.write(Type.INT, this.entityId);
        head.write(Type.BYTE, (byte)(this.headYaw / 360.0f * 256.0f));
        PacketUtil.sendPacket(teleport, Protocol1_7_6_10TO1_8.class, true, true);
        PacketUtil.sendPacket(head, Protocol1_7_6_10TO1_8.class, true, true);
    }

    public void updateMetadata() {
        PacketWrapper metadataPacket = PacketWrapper.create(28, null, this.user);
        metadataPacket.write(Type.INT, this.entityId);
        ArrayList<Metadata> metadataList = new ArrayList<Metadata>();
        for (Metadata metadata : this.datawatcher) {
            metadataList.add(new Metadata(metadata.id(), metadata.metaType(), metadata.getValue()));
        }
        MetadataRewriter.transform(Entity1_10Types.EntityType.SQUID, metadataList);
        metadataPacket.write(Types1_7_6_10.METADATA_LIST, metadataList);
        PacketUtil.sendPacket(metadataPacket, Protocol1_7_6_10TO1_8.class);
    }

    @Override
    public void spawn() {
        PacketWrapper spawn = PacketWrapper.create(15, null, this.user);
        spawn.write(Type.VAR_INT, this.entityId);
        spawn.write(Type.UNSIGNED_BYTE, (short)60);
        spawn.write(Type.INT, 0);
        spawn.write(Type.INT, 0);
        spawn.write(Type.INT, 0);
        spawn.write(Type.BYTE, (byte)0);
        spawn.write(Type.BYTE, (byte)0);
        spawn.write(Type.BYTE, (byte)0);
        spawn.write(Type.SHORT, (short)0);
        spawn.write(Type.SHORT, (short)0);
        spawn.write(Type.SHORT, (short)0);
        spawn.write(Types1_7_6_10.METADATA_LIST, new ArrayList());
        PacketUtil.sendPacket(spawn, Protocol1_7_6_10TO1_8.class, true, true);
    }

    @Override
    public void despawn() {
        PacketWrapper despawn = PacketWrapper.create(19, null, this.user);
        despawn.write(Types1_7_6_10.INT_ARRAY, new int[]{this.entityId});
        PacketUtil.sendPacket(despawn, Protocol1_7_6_10TO1_8.class, true, true);
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }
}

