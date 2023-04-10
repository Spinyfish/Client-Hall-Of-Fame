package crispy.util.math;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Vec4f {

    public float x;
    public float y;
    public float z;
    public float w;

    public Vec4f() {
        this(0, 0, 0, 0);
    }

    public Vec4f sub(Vec4f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        this.w -= vec.w;
        return this;
    }

    public Vec4f add(Vec4f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        this.w += vec.w;
        return this;
    }

    public Vec4f mul(Vec4f vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;
        this.w *= vec.w;
        return this;
    }

    public Vec4f mul(float scale) {
        return mul(new Vec4f(scale, scale, scale, scale));
    }

    public Vec4f clone() {
        return new Vec4f(x, y, z, w);
    }

}