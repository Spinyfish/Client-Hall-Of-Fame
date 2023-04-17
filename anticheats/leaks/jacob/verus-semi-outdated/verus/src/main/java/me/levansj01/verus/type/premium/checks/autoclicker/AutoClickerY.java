package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity.EntityUseAction;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "Y",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_8
)

public class AutoClickerY extends Check implements PacketHandler {
    private int attacks;
    private int interacts;

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.attacks == 1 && this.interacts > 0) {
            if (this.interacts == 2) {
                this.handleViolation();
            } else {
                if (this.interacts == 1) {
                    this.decreaseVL(0.05D);
                }
            }
        }
        this.interacts = this.attacks = 0;
    }

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getAction() == EntityUseAction.ATTACK) {
            this.attacks += 1;
        } else if (vPacket.getAction() == EntityUseAction.INTERACT) {
            if (this.attacks == 1) {
                this.interacts += 1;
            }
        }
    }
}