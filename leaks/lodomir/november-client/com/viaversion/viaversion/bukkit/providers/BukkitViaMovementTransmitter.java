/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package com.viaversion.viaversion.bukkit.providers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.bukkit.util.NMSUtil;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitViaMovementTransmitter
extends MovementTransmitterProvider {
    private static boolean USE_NMS = true;
    private Object idlePacket;
    private Object idlePacket2;
    private Method getHandle;
    private Field connection;
    private Method handleFlying;

    public BukkitViaMovementTransmitter() {
        Class<?> idlePacketClass;
        USE_NMS = Via.getConfig().isNMSPlayerTicking();
        try {
            idlePacketClass = NMSUtil.nms("PacketPlayInFlying");
        }
        catch (ClassNotFoundException e) {
            return;
        }
        try {
            this.idlePacket = idlePacketClass.newInstance();
            this.idlePacket2 = idlePacketClass.newInstance();
            Field flying = idlePacketClass.getDeclaredField("f");
            flying.setAccessible(true);
            flying.set(this.idlePacket2, true);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException e) {
            throw new RuntimeException("Couldn't make player idle packet, help!", e);
        }
        if (USE_NMS) {
            try {
                this.getHandle = NMSUtil.obc("entity.CraftPlayer").getDeclaredMethod("getHandle", new Class[0]);
            }
            catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException("Couldn't find CraftPlayer", e);
            }
            try {
                this.connection = NMSUtil.nms("EntityPlayer").getDeclaredField("playerConnection");
            }
            catch (ClassNotFoundException | NoSuchFieldException e) {
                throw new RuntimeException("Couldn't find Player Connection", e);
            }
            try {
                this.handleFlying = NMSUtil.nms("PlayerConnection").getDeclaredMethod("a", idlePacketClass);
            }
            catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException("Couldn't find CraftPlayer", e);
            }
        }
    }

    @Override
    public Object getFlyingPacket() {
        if (this.idlePacket == null) {
            throw new NullPointerException("Could not locate flying packet");
        }
        return this.idlePacket2;
    }

    @Override
    public Object getGroundPacket() {
        if (this.idlePacket == null) {
            throw new NullPointerException("Could not locate flying packet");
        }
        return this.idlePacket;
    }

    @Override
    public void sendPlayer(UserConnection info) {
        if (USE_NMS) {
            Player player = Bukkit.getPlayer((UUID)info.getProtocolInfo().getUuid());
            if (player != null) {
                try {
                    Object entityPlayer = this.getHandle.invoke((Object)player, new Object[0]);
                    Object pc = this.connection.get(entityPlayer);
                    if (pc != null) {
                        this.handleFlying.invoke(pc, info.get(MovementTracker.class).isGround() ? this.idlePacket2 : this.idlePacket);
                        info.get(MovementTracker.class).incrementIdlePacket();
                    }
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.sendPlayer(info);
        }
    }
}

