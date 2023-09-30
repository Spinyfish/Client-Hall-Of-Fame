package de.lirium.util.math;


import java.util.concurrent.ThreadLocalRandom;

public class Vec {

    public double x, y, z;

    void init(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec() {
    }

    public Vec(double y) {
        init(0, y, 0);
    }

    public Vec(double x, double y) {
        init(x, y, 0);
    }

    public Vec(double x, double y, double z) {
        init(x, y, z);
    }

    public Vec(Vec vec) {
        init(vec.x, vec.y, vec.z);
    }

    public static Vec random() {
        final double rand_number = Math.random() * (Math.PI * 2);
        return new Vec(Math.cos(rand_number), Math.sin(rand_number));
    }

    public static Vec random(double xMin, double xMax) {
        final double x = ThreadLocalRandom.current().nextDouble(xMin, xMax);
        return new Vec(x, 0);
    }

    public static Vec random(double xMin, double xMax, double yMin, double yMax) {
        final double x = ThreadLocalRandom.current().nextDouble(xMin, xMax);
        final double y = ThreadLocalRandom.current().nextDouble(yMin, yMax);
        return new Vec(x, y);
    }

    public static Vec random(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        final double x = ThreadLocalRandom.current().nextDouble(xMin, xMax);
        final double y = ThreadLocalRandom.current().nextDouble(yMin, yMax);
        final double z = ThreadLocalRandom.current().nextDouble(zMin, zMax);
        return new Vec(x, y, z);
    }


    public Vec set(double x) {
        this.x = x;
        return this;
    }

    public Vec set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec set(Vec vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }

    public Vec add(double x) {
        this.x += x;
        return this;
    }

    public Vec add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec add(Vec vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public Vec sub(double x) {
        this.x -= x;
        return this;
    }

    public Vec sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vec sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vec sub(Vec vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public Vec mul(double x) {
        this.x *= x;
        return this;
    }

    public Vec mul(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vec mul(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vec mul(Vec vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;
        return this;
    }


    public void div(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
    }


    public double dot(double x) {
        return this.x * x;
    }

    public double dot(double x, double y) {
        return this.x * x + this.y * y;
    }

    public double dot(double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public double dot(Vec vec) {
        return dot(vec.x, vec.y, vec.z);
    }

    public double cross(double x, double y) {
        return this.x * y - this.y * x;
    }

    public Vec cross(double x, double y, double z) {
        final double xCross = this.y * z - this.z * y;
        final double yCross = this.z * x - this.x * z;
        final double zCross = this.x * y - this.y * x;
        return new Vec(xCross, yCross, zCross);
    }

    public Vec cross(Vec vec) {
        return cross(vec.x, vec.y, vec.z);
    }

    public Vec normalize() {
        final double magnitude = getMagnitude();
        if (magnitude != 0 && magnitude != 1) {
            div(magnitude, magnitude, magnitude);
        }
        return this;
    }

    public Vec limit(double limit) {
        if (getMagnitude() > limit * limit) {
            normalize();
            mul(limit, limit, limit);
        }
        return this;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }


    public Vec clamp(double xMin, double xMax) {
        this.x = (x < xMin ? xMin : Math.min(x, xMax));
        return this;
    }

    public Vec clamp(double xMin, double xMax, double yMin, double yMax) {
        this.x = (x < xMin ? xMin : Math.min(x, xMax));
        this.y = (y < yMin ? yMin : Math.min(y, yMax));
        return this;
    }

    public Vec clamp(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        this.x = (x < xMin ? xMin : Math.min(x, xMax));
        this.y = (y < yMin ? yMin : Math.min(y, yMax));
        this.z = (z < zMin ? zMin : Math.min(z, zMax));
        return this;
    }

    public Vec clamp(Vec min, Vec max) {
        this.x = (x < min.x ? min.x : Math.min(x, max.x));
        this.y = (y < min.y ? min.y : Math.min(y, max.y));
        this.z = (z < min.z ? min.z : Math.min(z, max.z));
        return this;
    }

    public Vec scale(double n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
        return this;
    }

    @Override
    public Vec clone() throws CloneNotSupportedException {
        Vec clone = (Vec) super.clone();
        return new Vec(this);
    }

    public Vec mirror() {
        return new Vec(x * -1, y * -1, z * -1);
    }

    public Vec pos() {
        return new Vec(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public Vec center() {
        return new Vec(x / 2, y / 2, z / 2);
    }

    public Vec zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }

    public double direction() {
        return Math.atan2(y, x);
    }

    public double distance(double x) {
        final double xDiff = this.x - x;
        return Math.sqrt(xDiff * xDiff);
    }

    public double distance(double x, double y) {
        final double xDiff = this.x - x;
        final double yDiff = this.y - y;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public double distance(double x, double y, double z) {
        final double xDiff = this.x - x;
        final double yDiff = this.y - y;
        final double zDiff = this.z - z;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff + zDiff);
    }

    public double distance(Vec vec) {
        return distance(vec.x, vec.y, vec.z);
    }

    public boolean equal(double x) {
        return this.x == x;
    }

    public boolean equal(double x, double y) {
        return this.x == x && this.y == y;
    }

    public boolean equal(double x, double y, double z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public boolean equal(Vec vec) {
        return equal(vec.x, vec.y, vec.z);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, %s}", x, y, z);
    }

}
