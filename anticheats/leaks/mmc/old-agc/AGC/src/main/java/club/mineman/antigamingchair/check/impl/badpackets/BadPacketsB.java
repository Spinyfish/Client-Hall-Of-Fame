package club.mineman.antigamingchair.check.impl.badpackets;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class BadPacketsB extends PacketCheck {
    public BadPacketsB(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInFlying && Math.abs(((PacketPlayInFlying) packet).e()) > 90.0f && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Bad Packets Check B.") && !this.playerData.isBanning()) {
            this.ban(player, "Bad Packets Check B");
        }
    }
}
