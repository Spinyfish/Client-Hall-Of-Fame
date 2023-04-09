package tech.dort.dortware.api.util;

import tech.dort.dortware.impl.utils.movement.ACType;

public abstract class PlayerFriction implements Util {

    public abstract double[] update(ACType antiCheatType, float jumpMotion, double hDistSlowMul, double hDistBaseMul, double hDistDiv, double hDistLastTick, boolean hasMoved, boolean groundState);

}
