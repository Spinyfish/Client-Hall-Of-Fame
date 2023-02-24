package me.kansio.client.utils.rotations;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public final class Rotation {

    private final float rotationYaw;

    private final float rotationPitch;

    /**
     * Constructs a {@code Rotation} instance.
     *
     * @param rotationYaw   - The yaw
     * @param rotationPitch - The pitch
     */
    public Rotation(float rotationYaw, float rotationPitch) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }

    public static Rotation fromFacing(EntityLivingBase entity) {
        Rotation rotation = AimUtil.getRotationsRandom(entity);
        return new Rotation(MathHelper.floor_double(rotation.getRotationYaw()), rotation.getRotationPitch());
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }
}