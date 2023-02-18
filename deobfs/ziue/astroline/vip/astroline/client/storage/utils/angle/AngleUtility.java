/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.storage.utils.angle.Angle
 *  vip.astroline.client.storage.utils.angle.Mafs
 *  vip.astroline.client.storage.utils.vector.impl.Vector3
 */
package vip.astroline.client.storage.utils.angle;

import java.util.Random;
import vip.astroline.client.storage.utils.angle.Angle;
import vip.astroline.client.storage.utils.angle.Mafs;
import vip.astroline.client.storage.utils.vector.impl.Vector3;

public class AngleUtility {
    private float minYawSmoothing;
    private float maxYawSmoothing;
    private float minPitchSmoothing;
    private float maxPitchSmoothing;
    private Vector3<Float> delta;
    private Angle smoothedAngle;
    private final Random random;
    private float height = Mafs.getRandomInRange((float)1.1f, (float)1.8f);

    public AngleUtility(float minYawSmoothing, float maxYawSmoothing, float minPitchSmoothing, float maxPitchSmoothing) {
        this.minYawSmoothing = minYawSmoothing;
        this.maxYawSmoothing = maxYawSmoothing;
        this.minPitchSmoothing = minPitchSmoothing;
        this.maxPitchSmoothing = maxPitchSmoothing;
        this.random = new Random();
        this.delta = new Vector3((Number)Float.valueOf(0.0f), (Number)Float.valueOf(0.0f), (Number)Float.valueOf(0.0f));
        this.smoothedAngle = new Angle(Float.valueOf(0.0f), Float.valueOf(0.0f));
    }

    public float randomFloat(float min, float max) {
        return min + this.random.nextFloat() * (max - min);
    }

    public Angle calculateAngle(Vector3<Double> destination, Vector3<Double> source) {
        Angle angles = new Angle(Float.valueOf(0.0f), Float.valueOf(0.0f));
        this.delta.setX((Number)Float.valueOf(destination.getX().floatValue() - source.getX().floatValue())).setY((Number)Float.valueOf(destination.getY().floatValue() + this.height - (source.getY().floatValue() + this.height))).setZ((Number)Float.valueOf(destination.getZ().floatValue() - source.getZ().floatValue()));
        double hypotenuse = Math.hypot(this.delta.getX().doubleValue(), this.delta.getZ().doubleValue());
        float yawAtan = (float)Math.atan2(this.delta.getZ().floatValue(), this.delta.getX().floatValue());
        float pitchAtan = (float)Math.atan2(this.delta.getY().floatValue(), hypotenuse);
        float deg = 57.29578f;
        float yaw = yawAtan * deg - 90.0f;
        float pitch = -(pitchAtan * deg);
        return angles.setYaw(Float.valueOf(yaw)).setPitch(Float.valueOf(pitch)).constrantAngle();
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Angle smoothAngle(Angle destination, Angle source, float pitch, float yaw) {
        return this.smoothedAngle.setYaw(Float.valueOf(source.getYaw().floatValue() - destination.getYaw().floatValue() - (Math.abs(source.getYaw().floatValue() - destination.getYaw().floatValue()) > 5.0f ? Math.abs(source.getYaw().floatValue() - destination.getYaw().floatValue()) / Math.abs(source.getYaw().floatValue() - destination.getYaw().floatValue()) * 2.0f / 2.0f : 0.0f))).setPitch(Float.valueOf(source.getPitch().floatValue() - destination.getPitch().floatValue())).constrantAngle().setYaw(Float.valueOf(source.getYaw().floatValue() - this.smoothedAngle.getYaw().floatValue() / yaw * this.randomFloat(this.minYawSmoothing, this.maxYawSmoothing))).constrantAngle().setPitch(Float.valueOf(source.getPitch().floatValue() - this.smoothedAngle.getPitch().floatValue() / pitch * this.randomFloat(this.minPitchSmoothing, this.maxPitchSmoothing))).constrantAngle();
    }
}
