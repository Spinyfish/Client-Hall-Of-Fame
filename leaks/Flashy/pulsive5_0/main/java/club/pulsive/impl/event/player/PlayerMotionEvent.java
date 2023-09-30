package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

@Getter
@Setter
public class PlayerMotionEvent extends Event {

    private EventState state;
    private float prevYaw;
    private float prevPitch;
    private double prevPosX, prevPosY, prevPosZ;
    private double posX, posY, posZ;
    private float yaw, pitch;
    private boolean ground;
    private boolean rotating;

    public PlayerMotionEvent(EventState state, float prevYaw, float prevPitch, double prevPosX, double prevPosY, double prevPosZ, double posX, double posY, double posZ, float yaw, float pitch, boolean ground) {
        this.state = state;
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
        this.prevPosX = prevPosX;
        this.prevPosY = prevPosY;
        this.prevPosZ = prevPosZ;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
    }

    public PlayerMotionEvent(EventState state, double posX, double posY, double posZ, float yaw, float pitch, boolean ground) {
        this.state = state;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
    }

    public void setYaw(float yaw) {
        rotating = this.yaw != yaw;
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        rotating = this.pitch != pitch;
        this.pitch = pitch;
    }

    public boolean isPre() {
        return getState() == EventState.PRE;
    }
    public boolean isUpdate() {
        return getState() == EventState.UPDATE;
    }
    public boolean isPost() {
        return getState() == EventState.POST;
    }
    
    public enum EventState {
        UPDATE, PRE, POST
    }
}
