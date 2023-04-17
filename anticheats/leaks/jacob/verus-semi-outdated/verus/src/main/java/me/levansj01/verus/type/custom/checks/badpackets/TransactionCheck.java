package me.levansj01.verus.type.custom.checks.badpackets;

import java.util.function.Function;
import me.levansj01.verus.check.PacketCheck;
import me.levansj01.verus.compat.VPacket;
import me.levansj01.verus.compat.api.Transactionable;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.verus2.data.player.TickerType;

public abstract class TransactionCheck extends PacketCheck {
    private final Function<VPacket<?>, Boolean> packetFunction;
    private boolean sent, reduce;

    public void handle(VPacket<?> vPacket, long var2) {
        if (vPacket instanceof VPacketPlayInFlying) {
            if (this.reduce && this.sent) {
                this.decreaseVL(0.1D);
            }
            this.sent = this.reduce = false;
        } else {
            if (vPacket instanceof Transactionable) {
                if (this.sent && !this.player.isDead() && this.playerData.isSurvival()) {
                    if (this.playerData.getTickerMap().get(TickerType.TELEPORT) > 1) {
                        this.handleViolation();
                    }
                }
                this.reduce = true;
            } else if (this.packetFunction.apply(vPacket)) {
                this.sent = true;
            }
        }
    }

    public TransactionCheck(Function<VPacket<?>, Boolean> vPacket) {
        this.packetFunction = vPacket;
    }
}
