/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.manager.friend;

import net.minecraft.entity.player.EntityPlayer;

public class Friend {
    private String name;
    private EntityPlayer player;

    public Friend(String name) {
        this.name = name;
    }

    public Friend setPlayer(EntityPlayer player) {
        this.player = player;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}

