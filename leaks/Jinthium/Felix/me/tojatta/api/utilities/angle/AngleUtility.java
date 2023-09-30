package me.tojatta.api.utilities.angle;

import me.tojatta.api.utilities.vector.impl.Vector3;

import java.util.Random;

/**
 * Created by Tojatta on 12/17/2016.
 */
public class AngleUtility {

    private static float minYawSmoothing;
	private static float maxYawSmoothing;
	private static float minPitchSmoothing;
	private static float maxPitchSmoothing;
    private static Vector3<Float> delta;
    private static Angle smoothedAngle;
    private static Random random;

    public AngleUtility(float minYawSmoothing, float maxYawSmoothing, float minPitchSmoothing, float maxPitchSmoothing) {
        this.minYawSmoothing = minYawSmoothing;
        this.maxYawSmoothing = maxYawSmoothing;
        this.minPitchSmoothing = minPitchSmoothing;
        this.maxPitchSmoothing = maxPitchSmoothing;
        this.random = new Random();
        this.delta = new Vector3<>(0F, 0F, 0F);
        this.smoothedAngle = new Angle(0F, 0F);
    }

    public static float randomFloat(float min, float max) {
        return min + (random.nextFloat() * (max - min));
    }

    public static Angle calculateAngle(Vector3<Double> destination, Vector3<Double> source) {

        Angle angles = new Angle(0F, 0F);

        //Height of where you want to aim at on the entity.
        float height = 1.5F;

        delta
                .setX(destination.getX().floatValue() - source.getX().floatValue())
                .setY((destination.getY().floatValue() + height) - (source.getY().floatValue() + height))
                .setZ(destination.getZ().floatValue() - source.getZ().floatValue());

        double hypotenuse = Math.hypot(delta.getX().doubleValue(), delta.getZ().doubleValue());

        float yawAtan = ((float) Math.atan2(delta.getZ().floatValue(), delta.getX().floatValue()));
        float pitchAtan = ((float) Math.atan2(delta.getY().floatValue(), hypotenuse));

        float deg = ((float) (180 / Math.PI));

        float yaw = ((yawAtan * deg) - 90F);
        float pitch = -(pitchAtan * deg);

        return angles.setYaw(yaw).setPitch(pitch).constrantAngle();
    }

    public static Angle smoothAngle(Angle destination, Angle source) {
        return smoothedAngle
                .setYaw(source.getYaw() - destination.getYaw())
                .setPitch(source.getPitch() - destination.getPitch())
                .constrantAngle()
                .setYaw(source.getYaw() - smoothedAngle.getYaw() / 100 * randomFloat(minYawSmoothing, maxYawSmoothing))
                .setPitch(source.getPitch() - smoothedAngle.getPitch() / 100 * randomFloat(minPitchSmoothing, maxPitchSmoothing))
                .constrantAngle();
    }
}
