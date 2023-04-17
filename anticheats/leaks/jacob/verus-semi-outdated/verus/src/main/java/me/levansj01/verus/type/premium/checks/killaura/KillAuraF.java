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
        subType = "F",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        maxViolations = 10,
        unsupportedAtleast = ClientVersion.V1_9
)
public class KillAuraF extends Check implements PacketHandler {
    private boolean place;
    private boolean dig;

    @Override
    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.place = true;
    }

    @Override
    public void handle(VPacketPlayInUseEntity vPacket) {
        if (!this.place && !this.playerData.isVehicle() && this.dig && vPacket.getAction() == VPacketPlayInUseEntity.EntityUseAction.ATTACK) {
            this.handleViolation("", 1.0);
        }
    }

    @Override
    public void handle(VPacketPlayInBlockDig vPacket) {
        if (vPacket.getType() != VPacketPlayInBlockDig.PlayerDigType.DROP_ITEM && vPacket.getType() != VPacketPlayInBlockDig.PlayerDigType.DROP_ALL_ITEMS) {
            this.dig = true;
        }
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        this.place = false;
        this.dig = false;
    }
}
