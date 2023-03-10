/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;

public class DifficultyInstance {
    private final EnumDifficulty worldDifficulty;
    private final float additionalDifficulty;

    public DifficultyInstance(EnumDifficulty worldDifficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor) {
        this.worldDifficulty = worldDifficulty;
        this.additionalDifficulty = this.calculateAdditionalDifficulty(worldDifficulty, worldTime, chunkInhabitedTime, moonPhaseFactor);
    }

    public float getAdditionalDifficulty() {
        return this.additionalDifficulty;
    }

    public float getClampedAdditionalDifficulty() {
        return this.additionalDifficulty < 2.0f ? 0.0f : (this.additionalDifficulty > 4.0f ? 1.0f : (this.additionalDifficulty - 2.0f) / 2.0f);
    }

    private float calculateAdditionalDifficulty(EnumDifficulty difficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor) {
        if (difficulty == EnumDifficulty.PEACEFUL) {
            return 0.0f;
        }
        boolean flag = difficulty == EnumDifficulty.HARD;
        float f = 0.75f;
        float f1 = MathHelper.clamp_float(((float)worldTime + -72000.0f) / 1440000.0f, 0.0f, 1.0f) * 0.25f;
        f += f1;
        float f2 = 0.0f;
        f2 += MathHelper.clamp_float((float)chunkInhabitedTime / 3600000.0f, 0.0f, 1.0f) * (flag ? 1.0f : 0.75f);
        f2 += MathHelper.clamp_float(moonPhaseFactor * 0.25f, 0.0f, f1);
        if (difficulty == EnumDifficulty.EASY) {
            f2 *= 0.5f;
        }
        return (float)difficulty.getDifficultyId() * (f += f2);
    }
}

