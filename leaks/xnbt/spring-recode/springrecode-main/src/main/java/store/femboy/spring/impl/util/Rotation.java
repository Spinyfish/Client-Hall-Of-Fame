package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;

public class Rotation {

    Minecraft mc = Minecraft.getMinecraft();

    private float rotationPitch;
    private float rotationYaw;

    private double posX;

    private double posY;

    private double posZ;

    public Rotation(double posX, double posY, double posZ, float rotationYaw, float rotationPitch) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }

    public Rotation(float rotationYaw, float rotationPitch){
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
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
}
