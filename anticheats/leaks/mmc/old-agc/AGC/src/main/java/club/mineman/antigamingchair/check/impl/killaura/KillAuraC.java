package club.mineman.antigamingchair.check.impl.killaura;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.location.CustomLocation;
import club.mineman.antigamingchair.util.MathUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KillAuraC extends PacketCheck {
    private UUID lastTarget;

    public KillAuraC(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInFlying && !this.playerData.isAllowTeleport()) {
            final PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            if (flying.h() && flying.g() && this.lastTarget != null) {
                final CustomLocation playerLocation = this.playerData.getLastMovePacket();
                final CustomLocation targetLocation = this.playerData.getLastPlayerPacket(this.lastTarget, MathUtil.pingFormula(this.playerData.getPing()) + 2);
                if (targetLocation == null) {
                    return;
                }
                final double bodyYaw = MathUtil.getDistanceBetweenAngles(playerLocation.getYaw(), MathUtil.getRotationFromPosition(this.playerData.getLastMovePacket(), targetLocation)[0]);
                final double bodyPitch = MathUtil.getDistanceBetweenAngles(playerLocation.getPitch(), MathUtil.getRotationFromPosition(this.playerData.getLastMovePacket(), targetLocation)[1]);
                if ((bodyYaw == 0.0 || bodyPitch == 0.0) && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("failed Kill Aura Check C. BY %.2f. BP %.2f.", bodyYaw, bodyPitch))) {
                    final int violations = this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > 6) {
                        this.ban(player, "Kill Aura Check C");
                    }
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity targetEntity = useEntity.a(((CraftPlayer) player).getHandle().getWorld());
                if (targetEntity instanceof EntityPlayer) {
                    final Player target = (Player) targetEntity.getBukkitEntity();
                    this.lastTarget = target.getUniqueId();
                }
            }
        }
    }
}
