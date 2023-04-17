package club.mineman.antigamingchair.check.impl.badpackets;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.location.CustomLocation;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class BadPacketsF extends PacketCheck {
    private CustomLocation lastPosition;

    public BadPacketsF(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInFlying.PacketPlayInPosition) {
            if (this.lastPosition != null) {
                final CustomLocation currentPosition = this.playerData.getLastMovePacket();
                if (this.lastPosition.getX() == currentPosition.getX() && this.lastPosition.getY() == currentPosition.getY() && this.lastPosition.getZ() == currentPosition.getZ()) {
                    final long delay = currentPosition.getTimestamp() - this.lastPosition.getTimestamp();
                    final long diff = Math.abs(50L - delay);
                    if (diff < 4L) {
                        final int violations = this.playerData.getViolations(this, 60000L);
                        if (!this.playerData.isBanWave() && violations > 5) {
                            this.banWave(player, "Bad Packets Check F");
                        } else if (!this.playerData.isBanWave()) {
                            this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Bad Packets Check F.");
                        }
                    }
                }
                this.lastPosition = this.playerData.getLastMovePacket();
            } else {
                this.lastPosition = this.playerData.getLastMovePacket();
            }
        }
    }
}
