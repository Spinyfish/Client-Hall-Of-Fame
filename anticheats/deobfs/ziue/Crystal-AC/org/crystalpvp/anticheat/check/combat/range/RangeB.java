package org.crystalpvp.anticheat.check.combat.range;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class RangeB  extends PacketCheck {

    /**
     *  Coded by 4Remi#8652
     *   DO NOT REMOVE
     */

    private boolean sameTick;

    public RangeB(PlayerData playerData) {
        super(playerData, "Range (B)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (ConfigurationManager.isEnabled("RangeB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInUseEntity && !player.getGameMode().equals((Object)GameMode.CREATIVE) && System.currentTimeMillis() - playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp() < 110L && !sameTick) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity)packet;
            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity targetEntity = useEntity.a(((CraftPlayer)player).getHandle().getWorld());

                if (targetEntity instanceof EntityPlayer) {
                    final Player target = (Player) targetEntity.getBukkitEntity();

                    CrystalLocation playerCrystalLocation = playerData.getLastMovePacket();

                    PlayerData targetData = getPlugin().getPlayerDataManager().getPlayerData(target);
                    if (targetData == null) {
                        return;
                    }
                    final CrystalLocation targetCrystalLocation = targetData.getLastMovePacket();
                    if (targetCrystalLocation == null) {
                        return;
                    }

                    long diff = System.currentTimeMillis() - targetCrystalLocation.getTimestamp();
                    long estimate = MathUtil.pingFormula(playerData.getPing()) * 50L;
                    long diffEstimate = diff - estimate;

                    if (diffEstimate >= 500L) {
                        return;
                    }


                    if (playerData.getLastDistanceXZ() > 0.01 || targetData.getLastDistanceXZ() > 0.01) {
                        return;
                    }

                    double range = Math.hypot(playerCrystalLocation.getX() - targetCrystalLocation.getX(), playerCrystalLocation.getZ() - targetCrystalLocation.getZ()) - 0.42;

                    if (range > 3.0 && ++vl >= 5) {
                        alert(AlertType.RELEASE, player, String.format("Range %.2f. D %s. VL %.1f/%s.", range, diffEstimate, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);


                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("RangeB") && vl >= ConfigurationManager.banVL("RangeB")) {
                            punish(player);
                        }

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
