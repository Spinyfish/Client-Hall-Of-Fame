package crispy.util.rotation;

import net.minecraft.util.Vec3;

public class VecRotation {
    private final Vec3 vec3;
    private final Rotation rotation;

    public VecRotation(Vec3 vec3, Rotation rotation) {
        this.vec3 = vec3;
        this.rotation = rotation;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vec3 getVec3() {
        return vec3;
    }
}
