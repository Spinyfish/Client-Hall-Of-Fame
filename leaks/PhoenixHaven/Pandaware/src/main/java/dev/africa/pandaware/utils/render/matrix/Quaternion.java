package dev.africa.pandaware.utils.render.matrix;

public final class Quaternion {
    private float i;

    private float j;

    private float k;

    private float r;

    public Quaternion(float f, float g, float h, float i) {
        this.i = f;
        this.j = g;
        this.k = h;
        this.r = i;
    }

    public Quaternion(Vector3f vector3f, float f, boolean bl) {
        if (bl)
            f *= 0.017453292F;
        float g = sin(f / 2.0F);
        this.i = vector3f.x() * g;
        this.j = vector3f.y() * g;
        this.k = vector3f.z() * g;
        this.r = cos(f / 2.0F);
    }

    public Quaternion(Quaternion quaternion) {
        this.i = quaternion.i;
        this.j = quaternion.j;
        this.k = quaternion.k;
        this.r = quaternion.r;
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Quaternion quaternion = (Quaternion) object;
        if (Float.compare(quaternion.i, this.i) != 0)
            return false;
        if (Float.compare(quaternion.j, this.j) != 0)
            return false;
        if (Float.compare(quaternion.k, this.k) != 0)
            return false;
        return (Float.compare(quaternion.r, this.r) == 0);
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.i);
        i = 31 * i + Float.floatToIntBits(this.j);
        i = 31 * i + Float.floatToIntBits(this.k);
        i = 31 * i + Float.floatToIntBits(this.r);
        return i;
    }

    public float i() {
        return this.i;
    }

    public float j() {
        return this.j;
    }

    public float k() {
        return this.k;
    }

    public float r() {
        return this.r;
    }

    public void mul(Quaternion quaternion) {
        float f = i();
        float g = j();
        float h = k();
        float i = r();
        float j = quaternion.i();
        float k = quaternion.j();
        float l = quaternion.k();
        float m = quaternion.r();
        this.i = i * j + f * m + g * l - h * k;
        this.j = i * k - f * l + g * m + h * j;
        this.k = i * l + f * k - g * j + h * m;
        this.r = i * m - f * j - g * k - h * l;
    }

    public void conj() {
        this.i = -this.i;
        this.j = -this.j;
        this.k = -this.k;
    }

    public void set(float f, float g, float h, float i) {
        this.i = f;
        this.j = g;
        this.k = h;
        this.r = i;
    }

    private static float cos(float f) {
        return (float) Math.cos(f);
    }

    private static float sin(float f) {
        return (float) Math.sin(f);
    }

    public void normalize() {
        float f = i() * i() + j() * j() + k() * k() + r() * r();
        if (f > 1.0E-6F) {
            float g = Mth.fastInvSqrt(f);
            this.i *= g;
            this.j *= g;
            this.k *= g;
            this.r *= g;
        } else {
            this.i = 0.0F;
            this.j = 0.0F;
            this.k = 0.0F;
            this.r = 0.0F;
        }
    }

    public Quaternion copy() {
        return new Quaternion(this);
    }
}