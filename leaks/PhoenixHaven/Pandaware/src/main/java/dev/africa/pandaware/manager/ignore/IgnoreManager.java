package dev.africa.pandaware.manager.ignore;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class IgnoreManager {
    private final List<EntityPlayer> ignoreList = new CopyOnWriteArrayList<>();
    private final List<EntityPlayer> friendList = new CopyOnWriteArrayList<>();

    public boolean isIgnore(EntityPlayer entityPlayer, boolean friend) {
        return (friend ? this.friendList.contains(entityPlayer) : this.ignoreList.contains(entityPlayer));
    }

    public boolean isIgnoreBoth(EntityPlayer entityPlayer) {
        return this.friendList.contains(entityPlayer) || this.ignoreList.contains(entityPlayer);
    }

    public void remove(EntityPlayer entityPlayer, boolean friend) {
        if (friend) {
            this.friendList.remove(entityPlayer);
        } else {
            this.ignoreList.remove(entityPlayer);
        }
    }

    public void add(EntityPlayer entityPlayer, boolean friend) {
        if (friend) {
            if (!this.friendList.contains(entityPlayer)) {
                this.friendList.add(entityPlayer);
            }
        } else {
            if (!this.ignoreList.contains(entityPlayer)) {
                this.ignoreList.add(entityPlayer);
            }
        }
    }
}