package me.levansj01.verus.util.location;

public class Vector3d {
    private double x;
    private double z;
    private double y;

    public Vector3d copy() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public Vector3d getIntermediateWithZValue(Vector3d vector3d, double d) {
        double d2 = vector3d.x - this.x;
        double d3 = vector3d.y - this.y;
        double d4 = vector3d.z - this.z;
        if (d4 * d4 < (double)1.0E-7f) {
            return null;
        }
        double d5 = (d - this.z) / d4;
        return d5 >= 0.0 && d5 <= 1.0 ? new Vector3d(this.x + d2 * d5, this.y + d3 * d5, this.z + d4 * d5) : null;
    }

    public Vector3d multiply(double d) {
        this.x *= d;
        this.y *= d;
        this.z *= d;
        return this;
    }

    public Vector3d getIntermediateWithXValue(Vector3d vector3d, double d) {
        double d2 = vector3d.x - this.x;
        double d3 = vector3d.y - this.y;
        double d4 = vector3d.z - this.z;
        if (d2 * d2 < (double)1.0E-7f) {
            return null;
        }
        double d5 = (d - this.x) / d2;
        return d5 >= 0.0 && d5 <= 1.0 ? new Vector3d(this.x + d2 * d5, this.y + d3 * d5, this.z + d4 * d5) : null;
    }

    public double getY() {
        return this.y;
    }

    public Vector3d add(Vector3d vector3d) {
        this.x += vector3d.x;
        this.y += vector3d.y;
        this.z += vector3d.z;
        return this;
    }

    public double distanceSquared(Vector3d vector3d) {
        return (this.x - vector3d.x) * (this.x - vector3d.x) + (this.y - vector3d.y) * (this.y - vector3d.y) + (this.z - vector3d.z) * (this.z - vector3d.z);
    }

    public Vector3d add(double d) {
        this.x += d;
        this.y += d;
        this.z += d;
        return this;
    }

    public double getX() {
        return this.x;
    }

    public Vector3d(double d, double d2, double d3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    public double getZ() {
        return this.z;
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public void setY(double d) {
        this.y = d;
    }

    public void setZ(double d) {
        this.z = d;
    }

    public Vector3d subtract(Vector3d vector3d) {
        this.x -= vector3d.x;
        this.y -= vector3d.y;
        this.z -= vector3d.z;
        return this;
    }

    public String toString() {
        return "Vector3d(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
    }

    public Vector3d divide(double d) {
        this.x /= d;
        this.y /= d;
        this.z /= d;
        return this;
    }

    public Vector3d getIntermediateWithYValue(Vector3d vector3d, double d) {
        double d2 = vector3d.x - this.x;
        double d3 = vector3d.y - this.y;
        double d4 = vector3d.z - this.z;
        if (d3 * d3 < (double)1.0E-7f) {
            return null;
        }
        double d5 = (d - this.y) / d3;
        return d5 >= 0.0 && d5 <= 1.0 ? new Vector3d(this.x + d2 * d5, this.y + d3 * d5, this.z + d4 * d5) : null;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public void setX(double d) {
        this.x = d;
    }
}