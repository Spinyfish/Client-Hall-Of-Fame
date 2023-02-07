package org.crystalpvp.anticheat.check.combat.range;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class RangeF  extends PacketCheck {

    private boolean sameTick;

    public RangeF(PlayerData playerData) {
        super(playerData, "Range (F)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("RangeF")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInUseEntity && !player.getGameMode().equals((Object)GameMode.CREATIVE) && System.currentTimeMillis() - playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp() < 110L && !sameTick) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity)packet;
            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity targetEntity = useEntity.a(((CraftPlayer)player).getHandle().getWorld());

                if (targetEntity instanceof EntityPlayer) {
                    final Player target = (Player) targetEntity.getBukkitEntity();


                    CrystalLocation playerCrystalLocation = this.playerData.getLastPlayerPacket(player.getUniqueId(), MathUtil.pingFormula(playerData.getPing()) + 2);

                    if (playerCrystalLocation == null) {
                        return;
                    }


                    PlayerData targetData = getPlugin().getPlayerDataManager().getPlayerData(target);
                    if (targetData == null) {
                        return;
                    }

                    CrystalLocation targetCrystalLocation = targetData.getLastMovePacket();
                    if (targetCrystalLocation == null) {
                        return;
                    }


                    long diff = System.currentTimeMillis() - targetCrystalLocation.getTimestamp();
                    long estimate = MathUtil.pingFormula(playerData.getPing()) * 50L;
                    long diffEstimate = diff - estimate;
                    if (diffEstimate >= 500L) {
                        return;
                    }


                    final double range = MathUtil.doubleDecimal(Math.hypot(playerCrystalLocation.getX() - targetCrystalLocation.getX(), playerCrystalLocation.getZ() - targetCrystalLocation.getZ()), 3);
                    Bukkit.broadcastMessage(String.format("%s", range));

                    double threshold = 3.3;
                    if (!targetData.isSprinting() || MathUtil.getDistanceBetweenAngles(playerCrystalLocation.getYaw(), targetCrystalLocation.getYaw()) < 90.0) {
                        threshold += 0.5;
                    }
                    if (range > threshold) {
                        if (++vl >= 12.5) {
                            alert(AlertType.RELEASE, player, String.format("Range %.2f. Threshold %.2f. D %s. VL %.1f/%s.", range - 0.4, threshold, diffEstimate, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);


                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("RangeF") && vl >= ConfigurationManager.banVL("RangeF")) {
                                punish(player);
                            }

                        }
                    } else if (range >= 2.0) {
                        vl -= 0.25;
                    }
                    else if (range < 2.0) {
                        vl -= 0.225;
                    }
                    sameTick = true;
                }
            }
        }
        else if (packet instanceof PacketPlayInFlying) {
            sameTick = false;
        }
        playerData.setCheckVl(vl, this);
    }
}