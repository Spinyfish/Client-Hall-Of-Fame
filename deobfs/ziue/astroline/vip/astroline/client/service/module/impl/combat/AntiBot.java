/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.module.impl.combat;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;

public class AntiBot
extends Module {
    public AntiBot() {
        super("AntiBot", Category.Combat, 0, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        List playerEntities = AntiBot.mc.theWorld.playerEntities;
        int i = 0;
        int playerEntitiesSize = playerEntities.size();
        while (i < playerEntitiesSize) {
            EntityPlayer player = (EntityPlayer)playerEntities.get(i);
            if (player == null) {
                return;
            }
            if (player.getName().startsWith("\u00a7") && player.getName().contains("\u00a7c") || this.isEntityBot((Entity)player) && !player.getDisplayName().getFormattedText().contains("NPC")) {
                AntiBot.mc.theWorld.removeEntity((Entity)player);
            }
            ++i;
        }
    }

    private boolean isEntityBot(Entity entity) {
        double distance = entity.getDistanceSqToEntity((Entity)AntiBot.mc.thePlayer);
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        if (mc.getCurrentServerData() != null) return AntiBot.mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel") && entity.getDisplayName().getFormattedText().startsWith("&") || !this.isOnTab(entity) && AntiBot.mc.thePlayer.ticksExisted > 100;
        return false;
    }

    private boolean isOnTab(Entity entity) {
        NetworkPlayerInfo info;
        Iterator var2 = mc.getNetHandler().getPlayerInfoMap().iterator();
        do {
            if (var2.hasNext()) continue;
            return false;
        } while (!(info = (NetworkPlayerInfo)var2.next()).getGameProfile().getName().equals(entity.getName()));
        return true;
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
