package org.crystalpvp.anticheat.api.event.player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class PlayerTakeVelocityEvent {

    public double deltaH, deltaV;

    public PlayerTakeVelocityEvent(double deltaH, double deltaV) {
        this.deltaH = deltaH;
        this.deltaV = deltaV;
    }
}
