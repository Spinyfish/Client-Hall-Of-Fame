package dev.africa.pandaware.utils.math;

import dev.africa.pandaware.utils.math.vector.Vec3d;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;

import java.time.LocalTime;
import java.util.List;

@UtilityClass
public class MathUtils {
    public double getAverage(List<Number> numbers) {
        double sum = 0;

        for (Number number : numbers) {
            sum += number.doubleValue();
        }

        return sum / numbers.size();
    }

    public double roundToDecimal(double number, double places) {
        return Math.round(number * Math.pow(10, places)) / Math.pow(10, places);
    }

    public double roundToIncrement(double value, double increment) {
        return increment * (Math.round(value / increment));
    }

    public String formatSeconds(long seconds) {
        LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);

        long hh = seconds / 3600;
        long mm = (seconds % 3600) / 60;
        long ss = seconds % 60;

        String format = ss + "s";

        if (mm > 0) {
            format = mm + "m " + format;
        }

        if (hh > 0) {
            format = hh + "h " + format;
        }

        return format;
    }

    public float interpolate(float before, float current, float offset) {
        return (float) interpolate(before, current, (double) offset);
    }

    public double interpolate(double before, double current, double offset) {
        return before + (current - before) * offset;
    }

    public Vec3d interpolate(Entity entity, float offset) {
        return new Vec3d(
                interpolate(entity.prevPosX, entity.posX, offset),
                interpolate(entity.prevPosY, entity.posY, offset),
                interpolate(entity.prevPosZ, entity.posZ, offset)
        );
    }

    public float calculateGaussianOffset(float x, float sigma) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }
}
