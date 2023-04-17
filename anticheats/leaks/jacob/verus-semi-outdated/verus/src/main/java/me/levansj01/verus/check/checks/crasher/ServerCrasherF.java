package me.levansj01.verus.check.checks.crasher;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInCustomPayload;
import me.levansj01.verus.util.item.MaterialList;
import org.bukkit.inventory.ItemStack;

@CheckInfo(
        type = CheckType.SERVER_CRASHER,
        subType = "F",
        friendlyName = "Server Crasher",
        version = CheckVersion.DEVELOPMENT,
        maxViolations = 1
)
public class ServerCrasherF extends Check implements PacketHandler {

    public void handle(VPacketPlayInCustomPayload vPacket) {
        String channel = vPacket.getChannel();
        if (channel.equals("MC|BOpen") || channel.equals("MC|BEdit") || channel.equals("MC|BSign")) {
            ItemStack itemStack = this.player.getItemInHand();
            if (itemStack != null && !MaterialList.BOOKS.contains(itemStack.getType())) {
                this.handleViolation();
                this.playerData.fuckOff();
            }
        }
    }

}