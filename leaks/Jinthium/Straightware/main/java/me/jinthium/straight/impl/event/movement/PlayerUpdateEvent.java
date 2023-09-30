package me.jinthium.straight.impl.event.movement;

import me.jinthium.straight.api.event.Event;

public class PlayerUpdateEvent extends Event {
    private State eventState;
    private double posX, posY, posZ;
    private float yaw, pitch, prevYaw, prevPitch;
    private boolean onGround;

    public PlayerUpdateEvent(State eventState, double posX, double posY, double posZ, float yaw, float pitch, float prevYaw, float prevPitch, boolean onGround) {
        this.eventState = eventState;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
        this.onGround = onGround;
    }

    public State getEventState() {
        return eventState;
    }

    public void setEventState(State eventState) {
        this.eventState = eventState;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPrevYaw() {
        return prevYaw;
    }

    public void setPrevYaw(float prevYaw) {
        this.prevYaw = prevYaw;
    }

    public float getPrevPitch() {
        return prevPitch;
    }

    public void setPrevPitch(float prevPitch) {
        this.prevPitch = prevPitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isPre(){
        return eventState == State.PRE;
    }

    public boolean isUpdate() {return eventState == State.UPDATE;}

    public boolean isPost(){
        return eventState == State.POST;
    }


    public enum State {
        UPDATE("Update"),
        PRE("Pre"),
        POST("Post");

        private final String stateString;

        State(String stateString) {
            this.stateString = stateString;
        }

        @Override
        public String toString() {
            return stateString;
        }
    }
}
