package com.darkmagician6.eventapi.events.callables;

import com.darkmagician6.eventapi.events.Event;

public class EventTest implements Event{
    
    float moveForward, moveStrafe, yaw, shouldYaw;
    boolean silentMoveFix, fixYaw;
    
    
    public EventTest(float moveForward, float moveStrafe, float yaw, float shouldyaw, boolean silent, boolean fix) {
	this.moveForward = moveForward;
	this.moveStrafe = moveStrafe;
	this.yaw = yaw;
	this.shouldYaw = shouldyaw;
	this.silentMoveFix = silent;
	this.fixYaw = fix;
    }
    
    public float getMoveForward() {
        return moveForward;
    }
    public void setMoveForward(float moveForward) {
        this.moveForward = moveForward;
    }
    public float getMoveStrafe() {
        return moveStrafe;
    }
    public void setMoveStrafe(float moveStrafe) {
        this.moveStrafe = moveStrafe;
    }
    public float getYaw() {
        return yaw;
    }
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
    public float getShouldYaw() {
        return shouldYaw;
    }
    public void setShouldYaw(float shouldYaw) {
        this.shouldYaw = shouldYaw;
    }
    public boolean isSilentMoveFix() {
        return silentMoveFix;
    }
    public void setSilentMoveFix(boolean silentMoveFix) {
        this.silentMoveFix = silentMoveFix;
    }
    public boolean isFixYaw() {
        return fixYaw;
    }
    public void setFixYaw(boolean fixYaw) {
        this.fixYaw = fixYaw;
    }
    
}
