package me.levansj01.verus.check.checks.scaffold;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.util.item.MaterialList;
import org.bukkit.inventory.ItemStack;

@CheckInfo(
        type = CheckType.SCAFFOLD,
        subType = "E",
        friendlyName = "Scaffold",
        version = CheckVersion.RELEASE,
        schem = true,
        maxViolations = 1
)
public class ScaffoldE extends Check implements PacketHandler {
    public void handle(VPacketPlayInBlockPlace vPacket) {
        int face = vPacket.getFace();
        if (face != 255) {
            ItemStack itemStack = vPacket.getItemStack();
            if (itemStack != null && itemStack.getType() != MaterialList.AIR) {
                float x = vPacket.getBlockX(),
                        y = vPacket.getBlockY(),
                        z = vPacket.getBlockZ();
                if (x > 1.0F || y > 1.0F || z > 1.0F) {
                    this.handleViolation(String.format("X: %.2f Y: %.2f Z: %.2f", x, y, z));
                }
            }
        }
    }
}
