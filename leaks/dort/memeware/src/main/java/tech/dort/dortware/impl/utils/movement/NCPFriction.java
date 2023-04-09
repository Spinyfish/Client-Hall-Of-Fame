package tech.dort.dortware.impl.utils.movement;

import tech.dort.dortware.api.util.PlayerFriction;

public class NCPFriction extends PlayerFriction {

    private double hDistCurrent;
    private boolean hDistSlow;

    @Override
    public double[] update(ACType antiCheatType, float jumpMotion, double hDistSlowMul, double hDistBaseMul, double hDistDiv, double hDistLastTick, boolean hasMoved, boolean groundState) {
        double hDistBase = MotionUtils.getBaseSpeed(antiCheatType);
        double vDist = mc.thePlayer.motionY;
        if (groundState && hasMoved) {
            vDist = MotionUtils.getMotion(jumpMotion);
            hDistCurrent *= /*hDistBase */ hDistBaseMul;
            hDistSlow = true;
        } else if (hDistSlow) {
            hDistCurrent = hDistLastTick - (hDistSlowMul * (hDistLastTick - hDistBase));
            hDistSlow = false;
        } else {
            hDistCurrent = hDistLastTick - hDistLastTick / hDistDiv;
        }
        return new double[]{hDistCurrent, vDist};
    }

//    public void setNextHorizontalDistance(double hDistCurrent) {
//        this.hDistCurrent = hDistCurrent;
//    }
}
