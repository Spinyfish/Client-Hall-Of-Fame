package me.levansj01.verus.check;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.util.location.PlayerLocation;

public abstract class MovementCheck
        extends Check {
    public abstract void handle(PlayerLocation var1, PlayerLocation var2, long var3);
}
