package dev.africa.pandaware.utils.client;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
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
}