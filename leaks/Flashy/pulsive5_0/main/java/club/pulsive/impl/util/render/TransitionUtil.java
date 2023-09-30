package club.pulsive.impl.util.render;

import club.pulsive.impl.util.math.apache.ApacheMath;
import net.minecraft.client.Minecraft;
import net.optifine.util.MathUtils;

import java.math.BigDecimal;
import java.math.MathContext;

public class TransitionUtil {
    public static double roundToDecimalPlace(double value, double inc) {
        final double halfOfInc = inc / 2.0D;
        final double floored = ApacheMath.floor(value / inc) * inc;
        if (value >= floored + halfOfInc)
            return new BigDecimal(ApacheMath.ceil(value / inc) * inc, MathContext.DECIMAL64).
                    stripTrailingZeros()
                    .doubleValue();
        else
            return new BigDecimal(floored, MathContext.DECIMAL64)
                    .stripTrailingZeros()
                    .doubleValue();
    }
    public static double transition(double now, double desired, double speed) {
        final double dif = ApacheMath.abs(now - desired);

        final int fps = Minecraft.getDebugFPS();

        if (dif > 0) {
            double animationSpeed = roundToDecimalPlace(ApacheMath.min(
                    10.0D, ApacheMath.max(0.0625D, (144.0D / fps) * (dif / 10) * speed)), 0.0625D);

            if (dif != 0 && dif < animationSpeed)
                animationSpeed = dif;

            if (now < desired)
                return now + animationSpeed;
            else if (now > desired)
                return now - animationSpeed;
        }

        return now;
    }
}
