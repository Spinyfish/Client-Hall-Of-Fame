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

public class RangeG extends PacketCheck {

    private boolean sameTick;
    private boolean realAlert;

    public RangeG(PlayerData playerData) {
        super(playerData, "Range (G)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("RangeG")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInUseEntity && !player.getGameMode().equals((Object) GameMode.CREATIVE) && System.currentTimeMillis() - playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp() < 110L && !sameTick) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity targetEntity = useEntity.a(((CraftPlayer) player).getHandle().getWorld());

                if (targetEntity instanceof EntityPlayer) {
                    final Player target = (Player) targetEntity.getBukkitEntity();

                    CrystalLocation playerLocation = playerData.getLastMovePacket();

                    PlayerData targetData = getPlugin().getPlayerDataManager().getPlayerData(target);
                    if (targetData == null) {
                        return;
                    }
                    CrystalLocation targetLocation = targetData.getLastMovePacket();
                    if (targetLocation == null) {
                        return;
                    }


                    long diff = System.currentTimeMillis() - targetLocation.getTimestamp();
                    long estimate = MathUtil.pingFormula(playerData.getPing()) * 50L;
                    long diffEstimate = diff - estimate;

                    if (diffEstimate >= 500L) {
                        return;
                    }


                    if (playerData.getPing() > 120 || targetData.getPing() > 120) {
                        return;
                    }


                    double rangeRemove = 0.0;


                    double xzTarget = targetData.getLastDistanceXZ();

                    if (xzTarget > 0.4) {
                        return;
                    }

                    if (playerData.isSprinting()) {
                        rangeRemove += playerData.getPing() * 0.006 + targetData.getPing() * 0.006;
                        rangeRemove += playerData.getLastDistanceXZ() * 3.5;
                    } else {
                        rangeRemove += playerData.getPing() * 0.005 + targetData.getPing() * 0.005;
                        rangeRemove += playerData.getLastDistanceXZ() * 2;
                    }

                    if (target.isSprinting()) {
                        rangeRemove += targetData.getLastDistanceXZ() * 3.5;
                    } else {
                        rangeRemove += targetData.getLastDistanceXZ() * 2;
                    }

                    if (MathUtil.getDistanceBetweenAngles(playerLocation.getYaw(), targetLocation.getYaw()) < 90.0) {
                        rangeRemove += 0.5;
                    }

                    double range = Math.hypot(playerLocation.getX() - targetLocation.getX(), playerLocation.getZ() - targetLocation.getZ()) - 0.55 - rangeRemove;

                    //Bukkit.broadcastMessage(String.format("R %.2f.", range));

                    if (range > 3.01 && range < 3.8) {

                        if (realAlert) {

                            if (++vl >= 5) {
                                alert(AlertType.RELEASE, player, String.format("Range %.2f. D %s. VL %.1f/%s.", range, diffEstimate, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("RangeG") && vl >= ConfigurationManager.banVL("RangeG")) {
                                    punish(player);
                                }
                            }


                        } else {
                            realAlert = true;
                        }


                    } else if (range >= 2.6) {
                        vl -= 0.02;
                        realAlert = false;
                    } else if (range < 2.6) {
                        vl -= 0.01;
                        realAlert = false;
                    }

                    sameTick = true;
                }
            }
        } else if (packet instanceof PacketPlayInFlying) {
            sameTick = false;
        }
        playerData.setCheckVl(vl, this);
    }
}