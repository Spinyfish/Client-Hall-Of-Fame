package frapppyz.cutefurry.pics.event.impl;

import frapppyz.cutefurry.pics.event.Event;

public class Move extends Event {
    private double x, y, z;
    public Move(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

}
