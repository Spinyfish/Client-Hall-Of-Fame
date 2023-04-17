package me.levansj01.verus.check;

import me.levansj01.verus.compat.VPacket;

public abstract class PacketCheck extends Check {
    public abstract void handle(VPacket<?> var1, long var2);
}
