package me.levansj01.verus.type.custom.checks.inventory;

import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.packets.VPacketPlayInWindowClick;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.type.custom.checks.badpackets.TransactionCheck;

@CheckInfo(
        type = CheckType.INVENTORY,
        subType = "A2",
        friendlyName = "Inventory",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9
)
public class InventoryA2 extends TransactionCheck {
    public InventoryA2() {
        super((packet) -> {
            if (!(packet instanceof VPacketPlayInWindowClick)) {
                return false;
            } else {
                VPacketPlayInWindowClick vPacket = (VPacketPlayInWindowClick)packet;
                return vPacket.isShiftClick() && vPacket.isItem();
            }
        });
    }
}
