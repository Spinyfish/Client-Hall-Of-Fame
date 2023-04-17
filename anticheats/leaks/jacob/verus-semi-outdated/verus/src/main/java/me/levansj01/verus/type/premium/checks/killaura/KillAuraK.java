package me.levansj01.verus.type.premium.checks.killaura;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.compat.packets.*;

@CheckInfo(
        type = CheckType.KILL_AURA,
        subType = "K",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9
)
public class KillAuraK extends Check implements PacketHandler {
    private boolean attack;

    @Override
    public void handle(VPacketPlayInBlockDig vPacket) {
        if ((vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.DROP_ITEM || vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.DROP_ALL_ITEMS) && this.attack) {
            this.handleViolation("", 1.0, true);
        }
    }

    @Override
    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getAction() == VPacketPlayInUseEntity.EntityUseAction.ATTACK) {
            this.attack = true;
        }
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        this.attack = false;
    }
}
