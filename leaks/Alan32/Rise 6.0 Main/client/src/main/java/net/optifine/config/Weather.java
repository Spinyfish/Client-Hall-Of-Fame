package net.optifine.config;

import net.minecraft.world.World;

public enum Weather {
    CLEAR,
    RAIN,
    THUNDER;

    public static Weather getWeather(final World world, final float partialTicks) {
        final float f = world.getThunderStrength(partialTicks);

        if (f > 0.5F) {
            return THUNDER;
        } else {
            final float f1 = world.getRainStrength(partialTicks);
            return f1 > 0.5F ? RAIN : CLEAR;
        }
    }
}
