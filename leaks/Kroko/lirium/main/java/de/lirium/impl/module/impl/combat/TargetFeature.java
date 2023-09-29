package de.lirium.impl.module.impl.combat;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.impl.events.TickEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.impl.module.ModuleManager;
import me.felix.friends.FriendData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

@ModuleFeature.Info(name = "Target", description = "Modify the target settings", category = ModuleFeature.Category.COMBAT)
public class TargetFeature extends ModuleFeature {

    /* Entity types */
    @Value(name = "Players")
    private final CheckBox players = new CheckBox(true);

    @Value(name = "Animals")
    private final CheckBox animals = new CheckBox(false);

    @Value(name = "Mobs")
    private final CheckBox mobs = new CheckBox(false);

    @Value(name = "Villager")
    private final CheckBox villager = new CheckBox(false);

    /* Misc */
    @Value(name = "Bots")
    private final CheckBox bots = new CheckBox(false);

    @Value(name = "Teams")
    private final CheckBox teams = new CheckBox(false);

    @Value(name = "Invisible")
    private final CheckBox invisible = new CheckBox(false);

    //TODO: Wenn settings bindbar Friends als setting

    private boolean ignoreFriends = false;

    public boolean canAttack(EntityLivingBase entity) {
        if(!invisible.getValue() && entity.isInvisible()) return false;
        if (ModuleManager.getFriend().isEnabled() && FriendData.isAlreadyFriend(entity) && !ignoreFriends) return false;
        if (!teams.getValue() && isTeam(getPlayer(), entity))
            return false;
        if (!bots.getValue() && ModuleManager.getAntiBot().isBot(entity))
            return false;
        if (players.getValue() && entity instanceof EntityPlayer)
            return true;
        if (animals.getValue() && entity instanceof EntityAnimal)
            return true;
        if (mobs.getValue() && entity instanceof EntityMob)
            return true;
        if (villager.getValue() && entity instanceof EntityVillager)
            return true;
        return false;
    }

    @EventHandler
    public Listener<TickEvent> tickEventListener = e -> {
        if (getWorld() == null || getPlayer() == null) return;
        ignoreFriends = ignoreFriends();
    };

    private boolean ignoreFriends() {
        if (!ModuleManager.getFriend().intelligent.getValue()) return false;
        int validSize = 0;
        int validPlayers = 0;
        for (NetworkPlayerInfo info : getPlayer().connection.getPlayerInfoMap()) {
            if (info.getGameProfile().getId().equals(getPlayer().getUniqueID())) continue;
            if (FriendData.friends.containsKey(info.getGameProfile().getName()))
                validSize++;
            validPlayers++;
        }
        if (validPlayers <= validSize)
            return true;
        return false;
    }

    @Override
    public void onEnable() {
        enabled = false;
    }
}
