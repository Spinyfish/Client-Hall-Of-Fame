package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;

import java.lang.ref.*;

import me.levansj01.verus.data.manager.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import me.levansj01.verus.data.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInUseEntity extends VPacket {
    protected double bodyZ;
    protected double bodyY;
    protected EntityUseAction action;
    private static final int count = VPacket.count();
    protected double bodyX;
    protected int id;
    protected WeakReference entity;

    public double getBodyX() {
        return this.bodyX;
    }

    public boolean isPlayer() {
        DataManager instance = DataManager.getInstance();
        return instance != null && instance.isPlayer(this.id);
    }

    public abstract Entity getEntity(World world);

    @Override
    public int ordinal() {
        return VPacketPlayInUseEntity.count;
    }

    public double getBodyY() {
        return this.bodyY;
    }

    public EntityUseAction getAction() {
        return this.action;
    }

    public PlayerData getPlayerData() {
        DataManager instance = DataManager.getInstance();
        if (instance == null) {
            return null;
        }
        return instance.getPlayer(this.id);
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public double getBodyZ() {
        return this.bodyZ;
    }

    public enum EntityUseAction {
        INTERACT_AT("INTERACT_AT", 2),
        INTERACT("INTERACT", 0),
        ATTACK("ATTACK", 1);

        EntityUseAction(String name, int i) {
        }

        public boolean isAttack() {
            return this == EntityUseAction.ATTACK;
        }

        public boolean isInteractAt() {
            return this == EntityUseAction.INTERACT_AT;
        }

        public boolean isInteract() {
            return this == EntityUseAction.INTERACT;
        }
    }
}
