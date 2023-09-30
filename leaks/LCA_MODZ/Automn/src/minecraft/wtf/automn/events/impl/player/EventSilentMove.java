package wtf.automn.events.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import wtf.automn.events.events.Event;

import java.util.ArrayList;

public class EventSilentMove implements Event {
    private boolean silent;
    private float yaw;

    public EventSilentMove(float yaw) {
        this.yaw = yaw;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float[] moveSilent(float strafe, float forward) {
        Minecraft mc = Minecraft.getMinecraft();
        float diff = MathHelper.wrapAngleTo180_float(this.yaw - mc.thePlayer.rotationYaw);
        float newForward = 0.0F;
        float newStrafe = 0.0F;

        if (diff >= 22.5 && diff < 67.5) {
            newStrafe += strafe;
            newForward += forward;
            newStrafe -= forward;
            newForward += strafe;
        } else if (diff >= 67.5 && diff < 112.5) {
            newStrafe -= forward;
            newForward += strafe;
        } else if (diff >= 112.5 && diff < 157.5) {
            newStrafe -= strafe;
            newForward -= forward;
            newStrafe -= forward;
            newForward += strafe;
        } else if (diff >= 157.5 || diff <= -157.5) {
            newStrafe -= strafe;
            newForward -= forward;
        } else if (diff > -157.5 && diff <= -112.5) {
            newStrafe -= strafe;
            newForward -= forward;
            newStrafe += forward;
            newForward -= strafe;
        } else if (diff > -112.5 && diff <= -67.5) {
            newStrafe += forward;
            newForward -= strafe;
        } else if (diff > -67.5 && diff <= -22.5) {
            newStrafe += strafe;
            newForward += forward;
            newStrafe += forward;
            newForward -= strafe;
        } else {
            newStrafe += strafe;
            newForward += forward;
        }

        return new float[] {newStrafe, newForward};
    }


}
