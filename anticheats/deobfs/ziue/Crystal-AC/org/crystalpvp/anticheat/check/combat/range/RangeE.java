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

public class RangeE  extends PacketCheck {

    private boolean sameTick;

    public RangeE(PlayerData playerData) {
        super(playerData, "Range (E)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("RangeE")) {
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

                if (distance > 6.5D) {
                    return;
                }

                double reach;

                if (playerData.getPing() > 320) {
                    reach = 3.57D;
                } else if (playerData.getPing() > 250) {
                    reach = 3.5D;
                } else if (playerData.getPing() > 200) {
                    reach = 3.4D;
                } else if (playerData.getPing() > 150) {
                    reach = 3.35D;
                } else if (playerData.getPing() > 100) {
                    reach = 3.28D;
                } else if (playerData.getPing () > 70) {
                    reach = 3.185D;
                } else if (playerData.getPing() > 50) {
                    reach = 3.14D;
                } else if (playerData.getPing() > 35) {
                    reach = 3.13D;
                } else if (playerData.getPing() > 20) {
                    reach = 3.12D;
                } else if (playerData.getPing() < 20) {
                    reach = 3.095D;
                } else {
                    reach = 3.14D;
                }

                if (!tData.isSprinting() || MathUtil.getDistanceBetweenAngles(playerCrystalLocation.getYaw(), targetCrystalLocation.getYaw()) < 90.0D) {
                    reach = 3.35D;
                }

                if (player.getMaximumNoDamageTicks() != 19 || player.getMaximumNoDamageTicks() != 20) {
                    reach += 0.4D;
                }

                if (distance > reach) {
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
                                        distance - reach + 3.0, distance, reach, calculation, vl), true)) {


                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("RangeE") && vl >= ConfigurationManager.banVL("RangeE")) {
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