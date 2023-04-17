package club.mineman.antigamingchair.check.impl.autoclicker;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;

public class AutoClickerB extends PacketCheck {
    private long clickTime;
    private int outliers;
    private int failed;
    private int passed;
    private int clicks;
    private int zeros;
    private boolean block;

    public AutoClickerB(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
            final long clickDelay = System.currentTimeMillis() - this.clickTime;
            ++this.clicks;
            final int cps = this.playerData.getCps();
            if (cps < 8) {
                return;
            }
            if (this.block) {
                this.block = false;
                return;
            }
            final long expectedOutlier = 1000L / (this.playerData.getCps() - 5);
            if (clickDelay == 0L) {
                ++this.zeros;
            } else if (clickDelay > expectedOutlier && clickDelay < 1000L) {
                ++this.outliers;
            }
            if (this.clicks % 150 == 0) {
                double vl = this.playerData.getCheckVl(this);
                if (this.outliers <= 7) {
                    ++vl;
                    ++this.failed;
                    final double total = (double) (this.passed + this.failed);
                    if (total / ((this.passed == 0) ? 1 : this.passed) > 2.0) {
                        this.alert(PlayerAlertEvent.AlertType.DEVELOPMENT, player, "failed Auto Clicker Check B (Development). O " + this.outliers + ". Z " + this.zeros + ". RAT " + this.passed + "/" + (this.passed + this.failed) + ". VL " + vl + ".");
                    }
                } else {
                    ++this.passed;
                    vl -= 0.75;
                }
                this.playerData.setCheckVl(vl, this);
                if (this.clicks == 300) {
                    this.clicks = 0;
                }
                final boolean b = false;
                this.outliers = 0;
                this.zeros = 0;
            }
            this.clickTime = System.currentTimeMillis();
        } else if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) {
            this.block = true;
        }
    }
}
