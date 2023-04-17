package club.mineman.antigamingchair.check.impl.badpackets;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class BadPacketsC extends PacketCheck {
    private boolean sent;

    public BadPacketsC(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInEntityAction) {
            final PacketPlayInEntityAction.EnumPlayerAction playerAction = ((PacketPlayInEntityAction) packet).b();
            if (playerAction == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING || playerAction == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
                if (this.sent) {
                    if (this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Bad Packets Check C.")) {
                        final int violations = this.playerData.getViolations(this, 60000L);
                        if (!this.playerData.isBanning() && violations > 2) {
                            this.ban(player, "Bad Packets Check C");
                        }
                    }
                } else {
                    this.sent = true;
                }
            }
        } else if (packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
