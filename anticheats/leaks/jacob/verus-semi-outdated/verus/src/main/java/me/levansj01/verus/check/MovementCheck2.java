package me.levansj01.verus.check;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.util.location.PacketLocation;

public abstract class MovementCheck2
        extends Check {
    public abstract void handle(PacketLocation var1, PacketLocation var2, long var3);
}
