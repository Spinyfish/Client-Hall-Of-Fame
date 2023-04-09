package tech.dort.dortware.impl.utils.world;

import net.minecraft.entity.Entity;

public class EntityData {

    private final Entity entity;
    private double x, y, z, lastX, lastY, lastZ;

    private boolean ground, lastGround;

    public EntityData(Entity entity) {
        this.entity = entity;
        this.x = entity.posX;
        this.y = entity.posY;
        this.z = entity.posZ;
        this.ground = entity.onGround;
    }

    public double getLastX() {
        return lastX;
    }

    public void setLastX(double lastX) {
        this.lastX = lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public void setLastY(double lastY) {
        this.lastY = lastY;
    }

    public double getLastZ() {
        return lastZ;
    }

    public void setLastZ(double lastZ) {
        this.lastZ = lastZ;
    }

    public boolean isGround() {
        return ground;
    }

    public boolean isLastGround() {
        return lastGround;
    }

    public void setLastGround(boolean lastGround) {
        this.lastGround = lastGround;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        lastX = this.x;
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        lastY = this.y;
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        lastZ = this.z;
        this.z = z;
    }

    public boolean getGround() {
        return ground;
    }

    public void setGround(boolean ground) {
        lastGround = this.ground;
        this.ground = ground;
    }

    public boolean isCurrentMoveInvalid() {
        double y = this.y;
        return y - (int) y == 0.015625 && ground && !lastGround;
    }

    public void update() {
        this.setX(entity.posX);
        this.setY(entity.posY);
        this.setZ(entity.posZ);
        this.setGround(entity.onGround);
    }
}
