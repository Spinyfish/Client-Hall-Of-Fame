package me.kansio.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import javax.vecmath.Vector2f;

public class Location {
    public float yaw, pitch;
    public double x, y, z;

    public boolean ignoreF3Rotations, can3DRotate;

    public Location(Entity entity) {
        this.yaw = entity.rotationYaw;
        this.pitch = entity.rotationPitch;
        this.x = entity.posX;
        this.y = entity.posY;
        this.z = entity.posZ;
    }

    public Location(float yaw, float pitch, double x, double y, double z) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location clone() {
        return new Location(yaw, pitch, x, y, z);
    }

    public Location normalize() {
        normalizeX();
        normalizeY();
        normalizeZ();
        return this;
    }

    public boolean isCan3DRotate() {
        return can3DRotate;
    }

    public void setCan3DRotate(boolean can3DRotate) {
        this.can3DRotate = can3DRotate;
    }

    public boolean isIgnoreF3Rotations() {
        return ignoreF3Rotations;
    }

    public void setIgnoreF3Rotations(boolean ignoreF3Rotations) {
        this.ignoreF3Rotations = ignoreF3Rotations;
    }


    public Location normalizeX() {
        Minecraft.getMinecraft().thePlayer.motionX = x;
        return this;
    }

    public Location normalizeY() {
        Minecraft.getMinecraft().thePlayer.motionY = y;
        return this;
    }

    public Location normalizeZ() {
        Minecraft.getMinecraft().thePlayer.motionZ = z;
        return this;
    }

    public Location add(Location location) {
        x += location.x;
        y += location.y;
        z += location.z;
        return this;
    }

    public Location subtract(Location location) {
        x -= location.x;
        y -= location.y;
        z -= location.z;
        return this;
    }

    public Location multiply(Location location) {
        x *= location.x;
        y *= location.y;
        z *= location.z;
        return this;
    }

    public Location divide(Location location) {
        x /= location.x;
        y /= location.y;
        z /= location.z;
        return this;
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Location multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Location divide(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public Location setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public Location setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public Location setX(double x) {
        this.x = x;
        return this;
    }

    public Location setY(double y) {
        this.y = y;
        return this;
    }

    public Location setZ(double z) {
        this.z = z;
        return this;
    }

    public Vector2f getRotations() {
        return new Vector2f(yaw, pitch);
    }

    public Location setRotation(Vector2f rotation) {
        if (rotation == null) return this;
        this.yaw = rotation.x;
        this.pitch = Math.min(Math.max(rotation.y, -90), 90);
        return this;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}