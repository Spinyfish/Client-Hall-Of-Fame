package net.minecraft.util;

public class Vec3
{
    /** X coordinate of Vec3D */
    public final double x;

    /** Y coordinate of Vec3D */
    public final double y;

    /** Z coordinate of Vec3D */
    public final double z;

    public Vec3(double x, double y, double z)
    {
        if (x == -0.0D)
        {
            x = 0.0D;
        }

        if (y == -0.0D)
        {
            y = 0.0D;
        }

        if (z == -0.0D)
        {
            z = 0.0D;
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public Vec3 subtractReverse(Vec3 vec)
    {
        return new Vec3(vec.x - this.x, vec.y - this.y, vec.z - this.z);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vec3 normalize()
    {
        double var1 = (double)MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
        return var1 < 1.0E-4D ? new Vec3(0.0D, 0.0D, 0.0D) : new Vec3(this.x / var1, this.y / var1, this.z / var1);
    }

    public double dotProduct(Vec3 vec)
    {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public Vec3 crossProduct(Vec3 vec)
    {
        return new Vec3(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public Vec3 subtract(Vec3 p_178788_1_)
    {
        return this.subtract(p_178788_1_.x, p_178788_1_.y, p_178788_1_.z);
    }

    public Vec3 subtract(double p_178786_1_, double p_178786_3_, double p_178786_5_)
    {
        return this.addVector(-p_178786_1_, -p_178786_3_, -p_178786_5_);
    }

    public Vec3 add(Vec3 p_178787_1_)
    {
        return this.addVector(p_178787_1_.x, p_178787_1_.y, p_178787_1_.z);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public Vec3 addVector(double x, double y, double z)
    {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(Vec3 vec)
    {
        double var2 = vec.x - this.x;
        double var4 = vec.y - this.y;
        double var6 = vec.z - this.z;
        return (double)MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(Vec3 vec)
    {
        double var2 = vec.x - this.x;
        double var4 = vec.y - this.y;
        double var6 = vec.z - this.z;
        return var2 * var2 + var4 * var4 + var6 * var6;
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector()
    {
        return (double)MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithXValue(Vec3 vec, double x)
    {
        double var4 = vec.x - this.x;
        double var6 = vec.y - this.y;
        double var8 = vec.z - this.z;

        if (var4 * var4 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double var10 = (x - this.x) / var4;
            return var10 >= 0.0D && var10 <= 1.0D ? new Vec3(this.x + var4 * var10, this.y + var6 * var10, this.z + var8 * var10) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithYValue(Vec3 vec, double y)
    {
        double var4 = vec.x - this.x;
        double var6 = vec.y - this.y;
        double var8 = vec.z - this.z;

        if (var6 * var6 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double var10 = (y - this.y) / var6;
            return var10 >= 0.0D && var10 <= 1.0D ? new Vec3(this.x + var4 * var10, this.y + var6 * var10, this.z + var8 * var10) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithZValue(Vec3 vec, double z)
    {
        double var4 = vec.x - this.x;
        double var6 = vec.y - this.y;
        double var8 = vec.z - this.z;

        if (var8 * var8 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double var10 = (z - this.z) / var8;
            return var10 >= 0.0D && var10 <= 1.0D ? new Vec3(this.x + var4 * var10, this.y + var6 * var10, this.z + var8 * var10) : null;
        }
    }

    public String toString()
    {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3 rotatePitch(float p_178789_1_)
    {
        float var2 = MathHelper.cos(p_178789_1_);
        float var3 = MathHelper.sin(p_178789_1_);
        double var4 = this.x;
        double var6 = this.y * (double)var2 + this.z * (double)var3;
        double var8 = this.z * (double)var2 - this.y * (double)var3;
        return new Vec3(var4, var6, var8);
    }

    public Vec3 rotateYaw(float p_178785_1_)
    {
        float var2 = MathHelper.cos(p_178785_1_);
        float var3 = MathHelper.sin(p_178785_1_);
        double var4 = this.x * (double)var2 + this.z * (double)var3;
        double var6 = this.y;
        double var8 = this.z * (double)var2 - this.x * (double)var3;
        return new Vec3(var4, var6, var8);
    }
}
