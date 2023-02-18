/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 */
package vip.astroline.client.storage.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public enum GLUtils {
    INSTANCE;

    public Minecraft mc = Minecraft.getMinecraft();

    public void rescale(double factor) {
        this.rescale((double)this.mc.displayWidth / factor, (double)this.mc.displayHeight / factor);
    }

    public void rescaleMC() {
        ScaledResolution resolution = new ScaledResolution(this.mc);
        this.rescale(this.mc.displayWidth / resolution.getScaleFactor(), this.mc.displayHeight / resolution.getScaleFactor());
    }

    public void rescale(double width, double height) {
        GlStateManager.clear((int)256);
        GlStateManager.matrixMode((int)5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho((double)0.0, (double)width, (double)height, (double)0.0, (double)1000.0, (double)3000.0);
        GlStateManager.matrixMode((int)5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate((float)0.0f, (float)0.0f, (float)-2000.0f);
    }
}
