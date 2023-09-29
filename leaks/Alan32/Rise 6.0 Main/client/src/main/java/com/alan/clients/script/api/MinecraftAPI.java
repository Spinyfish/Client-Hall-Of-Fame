package com.alan.clients.script.api;

/**
 * @author Strikeless
 * @since 11.06.2022
 */
public class MinecraftAPI extends API {

    public int getDisplayWidth() {
        return MC.displayWidth;
    }

    public int getDisplayHeight() {
        return MC.displayHeight;
    }

    public float getTimerSpeed() {
        return MC.getTimer().timerSpeed;
    }

    public void setTimerSpeed(final float timerSpeed) {
        MC.getTimer().timerSpeed = timerSpeed;
    }

    public float getPartialTicks() {
        return MC.timer.elapsedPartialTicks;
    }

    public float getRenderPartialTicks() {
        return MC.timer.renderPartialTicks;
    }
}
