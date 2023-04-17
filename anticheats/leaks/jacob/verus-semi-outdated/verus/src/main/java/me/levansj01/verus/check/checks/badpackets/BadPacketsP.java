package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "P",
        friendlyName = "KillAura",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9
)
public class BadPacketsP extends Check implements PacketHandler {

    private boolean attack;
    private boolean interactAt;
    private boolean interact;

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getAction().isAttack()) {
            if (!this.attack && (this.interact || this.interactAt)) {
                StringBuilder builder = new StringBuilder().append("Attack [");
                String add;
                if (this.interactAt) {
                    add = "Interact At ";
                } else {
                    add = "";
                }

                builder.append(add);
                if (this.interact) {
                    add = "Interact";
                } else {
                    add = "";
                }

                String s = builder.append(add).append("]").toString();
                this.handleViolation(s, 1.0, true);
                this.interact = this.interactAt = false;
            }

            this.attack = true;
        } else if (vPacket.getAction().isInteract()) {
            this.interact = true;
        } else if (vPacket.getAction().isInteractAt()) {
            if (!this.interactAt && this.interact) {
                this.handleViolation("Interact", 1.0, true);
                this.interact = false;
            }

            this.interactAt = true;
        }

    }

    public BadPacketsP() {
    }

    public void handle(VPacketPlayInFlying vPacket) {
        this.attack = this.interactAt = this.interact = false;
    }
}