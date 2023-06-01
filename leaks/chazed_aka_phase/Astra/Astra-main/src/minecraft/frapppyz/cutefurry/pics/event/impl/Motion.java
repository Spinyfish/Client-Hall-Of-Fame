package frapppyz.cutefurry.pics.event.impl;

import frapppyz.cutefurry.pics.event.Event;

public class Motion extends Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    public Motion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getZ(){
        return z;
    }

    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
    public void setZ(double z){
        this.z = z;
    }

    public float getYaw(){
        return yaw;
    }

    public float getPitch(){
        return pitch;
    }

    public void setYaw(float yaw){
        this.yaw = yaw;
    }
    public void setPitch(float pitch){
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround){
        this.onGround = onGround;
    }

    public boolean isOnGround(){
        return onGround;
    }
}
