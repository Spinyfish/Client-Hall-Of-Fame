package juul.util.objects;

import juul.inject.wrappers.MinecraftWrapper;
import juul.util.misc.MathUtils;

public class ScaledRes {
    private final double scaledWidthD;
    private final double scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public ScaledRes(MinecraftWrapper mc) {
        this.scaledWidth = mc.displayWidth();
        this.scaledHeight = mc.displayHeight();
        this.scaleFactor = 1;
        boolean var4 = mc.isUnicode();
        int var5 = mc.getGameSettings().guiScale();

        if (var5 == 0) {
            var5 = 1000;
        }

        while (this.scaleFactor < var5 && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }

        if (var4 && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        this.scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathUtils.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathUtils.ceiling_double_int(this.scaledHeightD);
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public double getScaledWidth_double() {
        return this.scaledWidthD;
    }

    public double getScaledHeight_double() {
        return this.scaledHeightD;
    }

    public int getScaleFactor() {
        return this.scaleFactor;
    }
}
