package club.mineman.antigamingchair.check.impl.autoclicker;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class AutoClickerG extends PacketCheck {
    private boolean failed;
    private boolean sent;

    public AutoClickerG(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInBlockPlace && ((PacketPlayInBlockPlace) packet).getFace() == 255 && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L) {
            if (this.sent) {
                if (!this.failed) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "failed Auto Clicker Check G (Experimental).");
                    this.failed = true;
                }
            } else {
                this.sent = true;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            final boolean b = false;
            this.failed = false;
            this.sent = false;
        }
    }
}
