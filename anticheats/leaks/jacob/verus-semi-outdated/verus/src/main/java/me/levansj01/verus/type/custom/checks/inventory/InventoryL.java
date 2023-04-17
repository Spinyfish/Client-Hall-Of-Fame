package me.levansj01.verus.type.custom.checks.inventory;

import java.util.Objects;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInWindowClick;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.INVENTORY,
        subType = "L",
        friendlyName = "Inventory",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9
)
public class InventoryL extends Check implements PacketHandler {
    private boolean pressed = false;
    private int slot = -1, button = -1;

    public void handle(VPacketPlayInWindowClick vPacket) {
        int slot = vPacket.getSlot(), button = vPacket.getButton();
        if (vPacket.getMode() == 2 && vPacket.getWindow() != 0) {
            if (this.pressed && this.slot != slot && this.button == button) {
                this.handleViolation();
                PlayerData playerData = this.playerData;
                Objects.requireNonNull(playerData);
                this.run(playerData::closeInventory);
            } else {
                this.decreaseVL(0.025D);
            }
            this.pressed = true;
        }
        this.slot = slot;
        this.button = button;
    }

    public void handle(VPacketPlayInFlying vPacket) {
        this.pressed = false;
    }
}
