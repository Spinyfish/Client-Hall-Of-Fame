package me.levansj01.verus.type.custom.checks.killaura;

import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity.EntityUseAction;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.type.custom.checks.badpackets.TransactionCheck;

@CheckInfo(
        type = CheckType.KILL_AURA,
        subType = "H3",
        friendlyName = "KillAura",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9,
        unsupportedServerAtleast = ServerVersion.v1_11_R1,
        maxViolations = 10
)
public class KillAuraH3 extends TransactionCheck {
    public KillAuraH3() {
        super((packet) -> packet instanceof VPacketPlayInUseEntity && ((VPacketPlayInUseEntity) packet).getAction() == EntityUseAction.ATTACK);
    }
}
