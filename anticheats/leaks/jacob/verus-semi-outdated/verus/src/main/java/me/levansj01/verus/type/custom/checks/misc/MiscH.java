package me.levansj01.verus.type.custom.checks.misc;

import java.util.Objects;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.packets.VPacketPlayInArmAnimation;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.item.MaterialList;

@CheckInfo(
        type = CheckType.MISC,
        subType = "H",
        friendlyName = "FastBreak",
        minViolations = -1.0D,
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9,
        maxViolations = 20
)
public class MiscH extends Check implements PacketHandler {
    private boolean sentarm;
    private float damage;
    private int digging;
    private BlockPosition position;

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.playerData.isDigging()) {
            this.digging += 1;
        } else {
            this.digging = 0;
        }
        this.sentarm = false;
    }

    public void handle(VPacketPlayInBlockDig vPacket) {
        BlockPosition blockPosition;
        switch(vPacket.getType().ordinal()) {
            case 1:
                if (this.sentarm) {
                    this.digging += 1;
                }
                blockPosition = vPacket.getBlockPosition();
                this.run(() -> {
                    NMSManager nmsManager = NMSManager.getInstance();
                    this.damage = nmsManager.getDamage(this.player, this.player.getWorld(), blockPosition);
                    nmsManager.setOnGround(this.player, nmsManager.setOnGround(this.player, true));
                    nmsManager.setInWater(this.player, nmsManager.setInWater(this.player, false));
                    this.position = blockPosition;
                });
            break;
            case 2:
                if (this.playerData.isSurvival() && this.digging > 0) {
                    blockPosition = vPacket.getBlockPosition();
                    if (Objects.equals(blockPosition, this.position)) {
                        float damage = this.damage;
                        int digging = this.digging,
                                round = Math.round(1.0F / damage),
                                i = round - digging;
                        if (i > 0) {
                            this.run(() -> {
                                if (this.playerData.getVersion() == ClientVersion.V1_7 && NMSManager.getInstance().getServerVersion().after(ServerVersion.v1_7_R4)) {
                                    if (NMSManager.getInstance().getTypeWithAPI(this.player, this.player.getWorld(), blockPosition) == MaterialList.BARRIER) {
                                        return;
                                    }
                                }
                                this.player.updateInventory();
                                this.handleViolation(String.format("D: %.4f T: %s, %s ", damage, digging, round), Math.min((double)i / 2.0D, 2.0D));
                            });
                        } else {
                            this.decreaseVL(0.1D);
                        }
                    }
                }
            case 3:
                this.digging = 0;
        }
    }

    public void handle(VPacketPlayInArmAnimation vPacket) {
        this.sentarm = true;
    }
}
