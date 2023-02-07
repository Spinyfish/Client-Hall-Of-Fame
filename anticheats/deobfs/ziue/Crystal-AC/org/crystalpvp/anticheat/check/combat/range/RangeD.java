package org.crystalpvp.anticheat.check.combat.range;

import org.crystalpvp.anticheat.CrystalAC;
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

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class RangeD  extends PacketCheck {

    private boolean sameTick;

    public RangeD(PlayerData playerData) {
        super(playerData, "Range (D)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("RangeD")) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (packet instanceof PacketPlayInUseEntity
                && !player.getGameMode().equals(GameMode.CREATIVE)
                && (System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket()) > 220L
                && (System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp()) < 110L
                && !this.sameTick) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity)packet;
            final Entity targetEntity;

            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK && (targetEntity = useEntity.a(((CraftPlayer) player).getHandle().getWorld())) instanceof EntityPlayer) {
                final Player target = (Player) targetEntity.getBukkitEntity();
                final CrystalLocation targetCrystalLocation = this.playerData.getLastPlayerPacket(target.getUniqueId(), MathUtil.pingFormula(this.playerData.getPing()));
                if (targetCrystalLocation == null) {
                    return;
                }
                long difference = System.currentTimeMillis() - targetCrystalLocation.getTimestamp();
                long estimate = (long) (Math.ceil((double) this.playerData.getPing() / 50.0D) * 50.0D);

                long calculation = difference - estimate;

                if (calculation >= 300L) {
                    return;
                }

                CrystalLocation playerCrystalLocation = this.playerData.getLastMovePacket();
                PlayerData tData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(target);

                if (tData == null) {
                    return;
                }

                double distance = Math.hypot(playerCrystalLocation.getX() - targetCrystalLocation.getX(), playerCrystalLocation.getZ() - targetCrystalLocation.getZ());



                double maxReach = 3.2;
                maxReach += Math.abs(player.getVelocity().length() + target.getVelocity().length()) * 0.9;
                maxReach += playerData.getPing() * 0.0087;
                if (maxReach < 3.200000047683716) {
                    maxReach = 3.200000047683716;
                }


                if (distance > maxReach) {
                    int alertvl;

                    if (playerData.getPing() > 100) {
                        alertvl = 25;
                    } else if (playerData.getPing() > 50) {
                        alertvl = 22;
                    } else {
                        alertvl = 17;
                    }

                    if ((vl += 1.0D) >= alertvl) {
                        if (this.alert(AlertType.RELEASE, player,
                                String.format("P %.1f. R %.3f. T %.2f. D %s. VL %.2f.",
                                        distance - maxReach + 3.0, distance, maxReach, calculation, vl), true)) {


                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("RangeD") && vl >= ConfigurationManager.banVL("RangeD")) {
                                punish(player);
                            }
                        } else {
                            vl = 0.0D;
                        }
                    }
                } else if (distance >= 2.0D) {


                    if (playerData.getPing() > 150) {
                        vl -= 0.3D;
                    } else if (playerData.getPing() > 100) {
                        vl -= 0.275D;
                    } else {
                        vl -= 0.25;
                    }
                }
                this.sameTick = true;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            this.sameTick = false;
        }
        playerData.setCheckVl(vl, this);
    }
}