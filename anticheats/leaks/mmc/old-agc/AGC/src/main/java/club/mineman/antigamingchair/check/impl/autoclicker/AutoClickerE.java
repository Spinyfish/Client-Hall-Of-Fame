package club.mineman.antigamingchair.check.impl.autoclicker;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class AutoClickerE extends PacketCheck {
    private boolean failed;
    private boolean sent;

    public AutoClickerE(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
            if (this.sent) {
                if (!this.failed) {
                    int vl = (int) this.playerData.getCheckVl(this);
                    if (++vl >= 5) {
                        vl = 0;
                        this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "failed Auto Clicker Check E (Experimental).");
                    }
                    this.playerData.setCheckVl(vl, this);
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
