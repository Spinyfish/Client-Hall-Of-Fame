package club.mineman.antigamingchair.check.impl.killaura;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class KillAuraB extends PacketCheck {
    private boolean sent;
    private boolean failed;
    private int movements;

    public KillAuraB(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (this.playerData.isDigging() && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L) {
            int vl = (int) this.playerData.getCheckVl(this);
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                this.movements = 0;
                vl = 0;
            } else if (packet instanceof PacketPlayInArmAnimation && this.movements >= 2) {
                if (this.sent) {
                    if (!this.failed) {
                        if (++vl >= 5) {
                            this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "failed Kill Aura Check B (Experimental). VL " + vl + ".");
                        }
                        this.failed = true;
                    }
                } else {
                    this.sent = true;
                }
            } else if (packet instanceof PacketPlayInFlying) {
                final boolean b = false;
                this.failed = false;
                this.sent = false;
                ++this.movements;
            }
            this.playerData.setCheckVl(vl, this);
        }
    }
}
