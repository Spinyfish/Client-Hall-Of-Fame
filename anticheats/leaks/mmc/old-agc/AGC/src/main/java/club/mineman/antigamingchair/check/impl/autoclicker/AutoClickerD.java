package club.mineman.antigamingchair.check.impl.autoclicker;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class AutoClickerD extends PacketCheck {
    private int stage;
    private int other;

    public AutoClickerD(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        int vl = (int) this.playerData.getCheckVl(this);
        if (this.stage == 0 && packet instanceof PacketPlayInArmAnimation) {
            ++this.stage;
        } else if (this.stage == 1) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 2) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                if (++vl >= 5) {
                    try {
                        if (this.other > 10) {
                            this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "failed Auto Clicker Check D (Experimental). " + this.other + ".");
                        }
                    } finally {
                        final boolean other = false;
                        this.other = 0;
                        vl = 0;
                    }
                    final boolean other = false;
                    this.other = 0;
                    vl = 0;
                }
                this.stage = 0;
            } else if (packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            } else {
                final boolean b = false;
                this.other = 0;
                vl = 0;
                this.stage = 0;
            }
        } else if (this.stage == 3) {
            if (packet instanceof PacketPlayInFlying) {
                ++this.stage;
            } else {
                final boolean b2 = false;
                this.other = 0;
                vl = 0;
                this.stage = 0;
            }
        } else if (this.stage == 4) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                ++this.other;
                this.stage = 0;
            } else {
                final boolean b3 = false;
                this.other = 0;
                vl = 0;
                this.stage = 0;
            }
        }
        this.playerData.setCheckVl(vl, this);
    }
}
