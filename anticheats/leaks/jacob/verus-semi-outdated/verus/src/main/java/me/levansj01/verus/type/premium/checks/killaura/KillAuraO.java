package me.levansj01.verus.type.premium.checks.killaura;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.compat.packets.*;

import java.util.*;

@CheckInfo(
        type = CheckType.KILL_AURA,
        subType = "O",
        friendlyName = "MultiAura",
        version = CheckVersion.RELEASE,
        maxViolations = 5,
        unsupportedAtleast = ClientVersion.V1_9
)
public class KillAuraO extends Check implements PacketHandler {
    private boolean ticked;
    private int entityId;

    @Override
    public void handle(VPacketPlayInFlying vPacketPlayInFlying) {
        this.ticked = true;
        this.decreaseVL(0.025);
    }

    @Override
    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getAction().equals(VPacketPlayInUseEntity.EntityUseAction.ATTACK) && this.playerData.isSurvival() && this.playerData.getTotalTicks() > 200) {
            if (!this.ticked && !Objects.equals(this.entityId, vPacket.getId())) {
                this.handleViolation();

            } else {
                this.decreaseVL(0.25);
            }
            this.entityId = vPacket.getId();
            this.ticked = false;
        }
    }
}
