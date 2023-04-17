package me.levansj01.verus.type.premium.checks.scaffold;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.util.item.MaterialList;
import org.bukkit.Material;

@CheckInfo(
        friendlyName = "Scaffold",
        type = CheckType.SCAFFOLD,
        subType = "A",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        maxViolations = 5,
        schem = true
)
public class ScaffoldA extends Check implements PacketHandler {
    public void handle(VPacketPlayInBlockPlace vPacket) {
        if (vPacket.getFace() == 1 && (double)vPacket.getBlockY() == 0.0D) {
            this.run(() -> {
                Material material = NMSManager.getInstance().getType(this.player, this.player.getWorld(), vPacket.getPosition());
                if (!MaterialList.FLAT.contains(material)) {
                    this.handleViolation(material.toString());
                }
            });
        } else {
            this.decreaseVL(0.05D);
        }
    }
}
