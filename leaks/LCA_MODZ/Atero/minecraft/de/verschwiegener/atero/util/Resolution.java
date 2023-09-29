package de.verschwiegener.atero.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

/**
 * Copyright 2020, Koks Team
 * Please don't use the code
 */

public class Resolution {

    public static Resolution INSTANCE;

    public int width, height, scale;

    public Resolution(Minecraft mc) {
        width = mc.displayWidth;
        height = mc.displayHeight;
        scale = 1;
        boolean flag = mc.isUnicode();
        int i = mc.gameSettings.guiScale;

        if (i == 0)
        {
            i = 1000;
        }

        while (this.scale < i && this.width / (this.scale + 1) >= 320 && this.height / (this.scale + 1) >= 240)
        {
            ++this.scale;
        }

        if (flag && this.scale % 2 != 0 && this.scale != 1)
        {
            --scale;
        }

        this.width = this.width / this.scale;
        this.height = this.width / this.scale;
    }

    public void update(Minecraft mc) {
        width = mc.displayWidth;
        height = mc.displayHeight;
        scale = 1;
        boolean flag = mc.isUnicode();
        int i = mc.gameSettings.guiScale;

        if (i == 0)
            i = 1000;

        while (scale < i && width / (scale + 1) >= 320 && height / (scale + 1) >= 240) {
            ++scale;
        }

        if (flag && scale % 2 != 0 && scale != 1) {
            --scale;
        }

        double scaledWidthD = (double) width / (double) scale;
        double scaledHeightD = (double) height / (double) scale;
        width = MathHelper.ceiling_double_int(scaledWidthD);
        height = MathHelper.ceiling_double_int(scaledHeightD);
    }

    public static Resolution getResolution() {
        if(INSTANCE == null) {
            INSTANCE = new Resolution(Minecraft.getMinecraft());
        }
        INSTANCE.update(Minecraft.getMinecraft());
        return INSTANCE;
    }
}
