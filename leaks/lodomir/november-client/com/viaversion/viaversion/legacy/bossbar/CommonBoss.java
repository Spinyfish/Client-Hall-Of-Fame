/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.MapMaker
 */
package com.viaversion.viaversion.legacy.bossbar;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.bossbar.BossBar;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossFlag;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonBoss
implements BossBar {
    private final UUID uuid;
    private final Map<UUID, UserConnection> connections;
    private final Set<BossFlag> flags;
    private String title;
    private float health;
    private BossColor color;
    private BossStyle style;
    private boolean visible;

    public CommonBoss(String title, float health, BossColor color, BossStyle style) {
        Preconditions.checkNotNull((Object)title, (Object)"Title cannot be null");
        Preconditions.checkArgument((health >= 0.0f && health <= 1.0f ? 1 : 0) != 0, (Object)("Health must be between 0 and 1. Input: " + health));
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.health = health;
        this.color = color == null ? BossColor.PURPLE : color;
        this.style = style == null ? BossStyle.SOLID : style;
        this.connections = new MapMaker().weakValues().makeMap();
        this.flags = new HashSet<BossFlag>();
        this.visible = true;
    }

    @Override
    public BossBar setTitle(String title) {
        Preconditions.checkNotNull((Object)title);
        this.title = title;
        this.sendPacket(UpdateAction.UPDATE_TITLE);
        return this;
    }

    @Override
    public BossBar setHealth(float health) {
        Preconditions.checkArgument((health >= 0.0f && health <= 1.0f ? 1 : 0) != 0, (Object)("Health must be between 0 and 1. Input: " + health));
        this.health = health;
        this.sendPacket(UpdateAction.UPDATE_HEALTH);
        return this;
    }

    @Override
    public BossColor getColor() {
        return this.color;
    }

    @Override
    public BossBar setColor(BossColor color) {
        Preconditions.checkNotNull((Object)((Object)color));
        this.color = color;
        this.sendPacket(UpdateAction.UPDATE_STYLE);
        return this;
    }

    @Override
    public BossBar setStyle(BossStyle style) {
        Preconditions.checkNotNull((Object)((Object)style));
        this.style = style;
        this.sendPacket(UpdateAction.UPDATE_STYLE);
        return this;
    }

    @Override
    public BossBar addPlayer(UUID player) {
        UserConnection client = Via.getManager().getConnectionManager().getConnectedClient(player);
        if (client != null) {
            this.addConnection(client);
        }
        return this;
    }

    @Override
    public BossBar addConnection(UserConnection conn) {
        if (this.connections.put(conn.getProtocolInfo().getUuid(), conn) == null && this.visible) {
            this.sendPacketConnection(conn, this.getPacket(UpdateAction.ADD, conn));
        }
        return this;
    }

    @Override
    public BossBar removePlayer(UUID uuid) {
        UserConnection client = this.connections.remove(uuid);
        if (client != null) {
            this.sendPacketConnection(client, this.getPacket(UpdateAction.REMOVE, client));
        }
        return this;
    }

    @Override
    public BossBar removeConnection(UserConnection conn) {
        this.removePlayer(conn.getProtocolInfo().getUuid());
        return this;
    }

    @Override
    public BossBar addFlag(BossFlag flag) {
        Preconditions.checkNotNull((Object)((Object)flag));
        if (!this.hasFlag(flag)) {
            this.flags.add(flag);
        }
        this.sendPacket(UpdateAction.UPDATE_FLAGS);
        return this;
    }

    @Override
    public BossBar removeFlag(BossFlag flag) {
        Preconditions.checkNotNull((Object)((Object)flag));
        if (this.hasFlag(flag)) {
            this.flags.remove((Object)flag);
        }
        this.sendPacket(UpdateAction.UPDATE_FLAGS);
        return this;
    }

    @Override
    public boolean hasFlag(BossFlag flag) {
        Preconditions.checkNotNull((Object)((Object)flag));
        return this.flags.contains((Object)flag);
    }

    @Override
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(this.connections.keySet());
    }

    @Override
    public Set<UserConnection> getConnections() {
        return Collections.unmodifiableSet(new HashSet<UserConnection>(this.connections.values()));
    }

    @Override
    public BossBar show() {
        this.setVisible(true);
        return this;
    }

    @Override
    public BossBar hide() {
        this.setVisible(false);
        return this;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    private void setVisible(boolean value) {
        if (this.visible != value) {
            this.visible = value;
            this.sendPacket(value ? UpdateAction.ADD : UpdateAction.REMOVE);
        }
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public BossStyle getStyle() {
        return this.style;
    }

    public Set<BossFlag> getFlags() {
        return this.flags;
    }

    private void sendPacket(UpdateAction action) {
        for (UserConnection conn : new ArrayList<UserConnection>(this.connections.values())) {
            PacketWrapper wrapper = this.getPacket(action, conn);
            this.sendPacketConnection(conn, wrapper);
        }
    }

    private void sendPacketConnection(UserConnection conn, PacketWrapper wrapper) {
        if (conn.getProtocolInfo() == null || !conn.getProtocolInfo().getPipeline().contains(Protocol1_9To1_8.class)) {
            this.connections.remove(conn.getProtocolInfo().getUuid());
            return;
        }
        try {
            wrapper.scheduleSend(Protocol1_9To1_8.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PacketWrapper getPacket(UpdateAction action, UserConnection connection) {
        try {
            PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.BOSSBAR, null, connection);
            wrapper.write(Type.UUID, this.uuid);
            wrapper.write(Type.VAR_INT, action.getId());
            switch (action) {
                case ADD: {
                    Protocol1_9To1_8.FIX_JSON.write(wrapper, this.title);
                    wrapper.write(Type.FLOAT, Float.valueOf(this.health));
                    wrapper.write(Type.VAR_INT, this.color.getId());
                    wrapper.write(Type.VAR_INT, this.style.getId());
                    wrapper.write(Type.BYTE, (byte)this.flagToBytes());
                    break;
                }
                case REMOVE: {
                    break;
                }
                case UPDATE_HEALTH: {
                    wrapper.write(Type.FLOAT, Float.valueOf(this.health));
                    break;
                }
                case UPDATE_TITLE: {
                    Protocol1_9To1_8.FIX_JSON.write(wrapper, this.title);
                    break;
                }
                case UPDATE_STYLE: {
                    wrapper.write(Type.VAR_INT, this.color.getId());
                    wrapper.write(Type.VAR_INT, this.style.getId());
                    break;
                }
                case UPDATE_FLAGS: {
                    wrapper.write(Type.BYTE, (byte)this.flagToBytes());
                }
            }
            return wrapper;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int flagToBytes() {
        int bitmask = 0;
        for (BossFlag flag : this.flags) {
            bitmask |= flag.getId();
        }
        return bitmask;
    }

    private static enum UpdateAction {
        ADD(0),
        REMOVE(1),
        UPDATE_HEALTH(2),
        UPDATE_TITLE(3),
        UPDATE_STYLE(4),
        UPDATE_FLAGS(5);

        private final int id;

        private UpdateAction(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }
}

