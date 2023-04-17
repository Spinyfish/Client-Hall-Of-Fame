package club.mineman.antigamingchair.check.impl.range;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.location.CustomLocation;
import club.mineman.antigamingchair.util.MathUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class RangeA extends PacketCheck {
    private boolean sameTick;

    public RangeA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInUseEntity && !player.getGameMode().equals(GameMode.CREATIVE) && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L && !this.sameTick) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity targetEntity = useEntity.a(((CraftPlayer) player).getHandle().getWorld());
                if (targetEntity instanceof EntityPlayer) {
                    final Player target = (Player) targetEntity.getBukkitEntity();
                    final CustomLocation latestLocation = this.playerData.getLastPlayerPacket(target.getUniqueId(), 1);
                    if (latestLocation == null || System.currentTimeMillis() - latestLocation.getTimestamp() > 100L) {
                        return;
                    }
                    final CustomLocation targetLocation = this.playerData.getLastPlayerPacket(target.getUniqueId(), MathUtil.pingFormula(this.playerData.getPing()) + 2);
                    if (targetLocation == null) {
                        return;
                    }
                    final CustomLocation playerLocation = this.playerData.getLastMovePacket();
                    final PlayerData targetData = this.plugin.getPlayerDataManager().getPlayerData(target);
                    if (targetData == null) {
                        return;
                    }
                    final double range = Math.hypot(playerLocation.getX() - targetLocation.getX(), playerLocation.getZ() - targetLocation.getZ());
                    if (range > 6.5) {
                        return;
                    }
                    double threshold = 3.3;
                    if (!targetData.isSprinting() || MathUtil.getDistanceBetweenAngles(playerLocation.getYaw(), targetLocation.getYaw()) < 90.0) {
                        threshold += 0.5;
                    }
                    double vl = this.playerData.getCheckVl(this);
                    if (range > threshold && ++vl >= 12.5) {
                        if (this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("failed Range Check A. E %.2f. R %.3f. T %.2f. VL %.2f.", range - threshold + 3.0, range, threshold, vl))) {
                            if (!this.playerData.isBanning() && vl >= this.plugin.getRangeVl()) {
                                this.ban(player, "Range Check A");
                            }
                        } else {
                            --vl;
                        }
                    } else if (range >= 2.0) {
                        vl -= 0.225;
                    }
                    this.playerData.setCheckVl(vl, this);
                    this.sameTick = true;
                }
            }
        } else if (packet instanceof PacketPlayInFlying) {
            this.sameTick = false;
        }
    }
}
