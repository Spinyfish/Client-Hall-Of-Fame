package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScaledResolution
{
	public static double scaledWidthD;
    public static double scaledHeightD;
    public static int scaledWidth;
    public static int scaledHeight;
    public static int scaleFactor;

    public ScaledResolution(Minecraft p_i46445_1_)
    {
        scaledWidth = p_i46445_1_.displayWidth;
        scaledHeight = p_i46445_1_.displayHeight;
        scaleFactor = 1;
        boolean flag = p_i46445_1_.isUnicode();
        int i = p_i46445_1_.gameSettings.guiScale;

        if (i == 0)
        {
            i = 1000;
        }

        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240)
        {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
        {
            --this.scaleFactor;
        }

        scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    public static int getScaledWidth()
    {
        return scaledWidth;
    }

    public static int getScaledHeight()
    {
        return scaledHeight;
    }

    public static double getScaledWidth_double()
    {
        return scaledWidthD;
    }

    public static double getScaledHeight_double()
    {
        return scaledHeightD;
    }

    public static int getScaleFactor()
    {
        return scaleFactor;
    }
}