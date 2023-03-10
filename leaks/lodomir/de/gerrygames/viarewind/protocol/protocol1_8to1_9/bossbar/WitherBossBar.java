/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_8to1_9.bossbar;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.bossbar.BossBar;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossFlag;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class WitherBossBar
implements BossBar {
    private static int highestId = 2147473647;
    private final UUID uuid;
    private String title;
    private float health;
    private boolean visible = false;
    private UserConnection connection;
    private final int entityId = highestId++;
    private double locX;
    private double locY;
    private double locZ;

    public WitherBossBar(UserConnection connection, UUID uuid, String title, float health) {
        this.connection = connection;
        this.uuid = uuid;
        this.title = title;
        this.health = health;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public BossBar setTitle(String title) {
        this.title = title;
        if (this.visible) {
            this.updateMetadata();
        }
        return this;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public BossBar setHealth(float health) {
        this.health = health;
        if (this.health <= 0.0f) {
            this.health = 1.0E-4f;
        }
        if (this.visible) {
            this.updateMetadata();
        }
        return this;
    }

    @Override
    public BossColor getColor() {
        return null;
    }

    @Override
    public BossBar setColor(BossColor bossColor) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support color");
    }

    @Override
    public BossStyle getStyle() {
        return null;
    }

    @Override
    public BossBar setStyle(BossStyle bossStyle) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support styles");
    }

    @Override
    public BossBar addPlayer(UUID uuid) {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only for one UserConnection!");
    }

    @Override
    public BossBar addConnection(UserConnection userConnection) {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only for one UserConnection!");
    }

    @Override
    public BossBar removePlayer(UUID uuid) {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only for one UserConnection!");
    }

    @Override
    public BossBar removeConnection(UserConnection userConnection) {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only for one UserConnection!");
    }

    @Override
    public BossBar addFlag(BossFlag bossFlag) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support flags");
    }

    @Override
    public BossBar removeFlag(BossFlag bossFlag) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support flags");
    }

    @Override
    public boolean hasFlag(BossFlag bossFlag) {
        return false;
    }

    @Override
    public Set<UUID> getPlayers() {
        return Collections.singleton(this.connection.getProtocolInfo().getUuid());
    }

    @Override
    public Set<UserConnection> getConnections() {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only for one UserConnection!");
    }

    @Override
    public BossBar show() {
        if (!this.visible) {
            this.visible = true;
            this.spawnWither();
        }
        return this;
    }

    @Override
    public BossBar hide() {
        if (this.visible) {
            this.visible = false;
            this.despawnWither();
        }
        return this;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    public void setLocation(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.updateLocation();
    }

    private void spawnWither() {
        PacketWrapper packetWrapper = PacketWrapper.create(15, null, this.connection);
        packetWrapper.write(Type.VAR_INT, this.entityId);
        packetWrapper.write(Type.UNSIGNED_BYTE, (short)64);
        packetWrapper.write(Type.INT, (int)(this.locX * 32.0));
        packetWrapper.write(Type.INT, (int)(this.locY * 32.0));
        packetWrapper.write(Type.INT, (int)(this.locZ * 32.0));
        packetWrapper.write(Type.BYTE, (byte)0);
        packetWrapper.write(Type.BYTE, (byte)0);
        packetWrapper.write(Type.BYTE, (byte)0);
        packetWrapper.write(Type.SHORT, (short)0);
        packetWrapper.write(Type.SHORT, (short)0);
        packetWrapper.write(Type.SHORT, (short)0);
        ArrayList<Metadata> metadata = new ArrayList<Metadata>();
        metadata.add(new Metadata(0, MetaType1_8.Byte, (byte)32));
        metadata.add(new Metadata(2, MetaType1_8.String, this.title));
        metadata.add(new Metadata(3, MetaType1_8.Byte, (byte)1));
        metadata.add(new Metadata(6, MetaType1_8.Float, Float.valueOf(this.health * 300.0f)));
        packetWrapper.write(Types1_8.METADATA_LIST, metadata);
        PacketUtil.sendPacket(packetWrapper, Protocol1_8TO1_9.class, true, false);
    }

    private void updateLocation() {
        PacketWrapper packetWrapper = PacketWrapper.create(24, null, this.connection);
        packetWrapper.write(Type.VAR_INT, this.entityId);
        packetWrapper.write(Type.INT, (int)(this.locX * 32.0));
        packetWrapper.write(Type.INT, (int)(this.locY * 32.0));
        packetWrapper.write(Type.INT, (int)(this.locZ * 32.0));
        packetWrapper.write(Type.BYTE, (byte)0);
        packetWrapper.write(Type.BYTE, (byte)0);
        packetWrapper.write(Type.BOOLEAN, false);
        PacketUtil.sendPacket(packetWrapper, Protocol1_8TO1_9.class, true, false);
    }

    private void updateMetadata() {
        PacketWrapper packetWrapper = PacketWrapper.create(28, null, this.connection);
        packetWrapper.write(Type.VAR_INT, this.entityId);
        ArrayList<Metadata> metadata = new ArrayList<Metadata>();
        metadata.add(new Metadata(2, MetaType1_8.String, this.title));
        metadata.add(new Metadata(6, MetaType1_8.Float, Float.valueOf(this.health * 300.0f)));
        packetWrapper.write(Types1_8.METADATA_LIST, metadata);
        PacketUtil.sendPacket(packetWrapper, Protocol1_8TO1_9.class, true, false);
    }

    private void despawnWither() {
        PacketWrapper packetWrapper = PacketWrapper.create(19, null, this.connection);
        packetWrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{this.entityId});
        PacketUtil.sendPacket(packetWrapper, Protocol1_8TO1_9.class, true, false);
    }

    public void setPlayerLocation(double posX, double posY, double posZ, float yaw, float pitch) {
        double yawR = Math.toRadians(yaw);
        double pitchR = Math.toRadians(pitch);
        this.setLocation(posX -= Math.cos(pitchR) * Math.sin(yawR) * 48.0, posY -= Math.sin(pitchR) * 48.0, posZ += Math.cos(pitchR) * Math.cos(yawR) * 48.0);
    }
}

