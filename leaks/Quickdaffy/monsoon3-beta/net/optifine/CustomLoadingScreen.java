/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.Properties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomLoadingScreens;

public class CustomLoadingScreen {
    private ResourceLocation locationTexture;
    private int scaleMode = 0;
    private int scale = 2;
    private boolean center;
    private static final int SCALE_DEFAULT = 2;
    private static final int SCALE_MODE_FIXED = 0;
    private static final int SCALE_MODE_FULL = 1;
    private static final int SCALE_MODE_STRETCH = 2;

    public CustomLoadingScreen(ResourceLocation locationTexture, int scaleMode, int scale, boolean center) {
        this.locationTexture = locationTexture;
        this.scaleMode = scaleMode;
        this.scale = scale;
        this.center = center;
    }

    public static CustomLoadingScreen parseScreen(String path, int dimId, Properties props) {
        ResourceLocation resourcelocation = new ResourceLocation(path);
        int i = CustomLoadingScreen.parseScaleMode(CustomLoadingScreen.getProperty("scaleMode", dimId, props));
        int j = i == 0 ? 2 : 1;
        int k = CustomLoadingScreen.parseScale(CustomLoadingScreen.getProperty("scale", dimId, props), j);
        boolean flag = Config.parseBoolean(CustomLoadingScreen.getProperty("center", dimId, props), false);
        CustomLoadingScreen customloadingscreen = new CustomLoadingScreen(resourcelocation, i, k, flag);
        return customloadingscreen;
    }

    private static String getProperty(String key, int dim, Properties props) {
        if (props == null) {
            return null;
        }
        String s = props.getProperty("dim" + dim + "." + key);
        if (s != null) {
            return s;
        }
        s = props.getProperty(key);
        return s;
    }

    private static int parseScaleMode(String str) {
        if (str == null) {
            return 0;
        }
        if ((str = str.toLowerCase().trim()).equals("fixed")) {
            return 0;
        }
        if (str.equals("full")) {
            return 1;
        }
        if (str.equals("stretch")) {
            return 2;
        }
        CustomLoadingScreens.warn("Invalid scale mode: " + str);
        return 0;
    }

    private static int parseScale(String str, int def) {
        if (str == null) {
            return def;
        }
        int i = Config.parseInt(str = str.trim(), -1);
        if (i < 1) {
            CustomLoadingScreens.warn("Invalid scale: " + str);
            return def;
        }
        return i;
    }

    public void drawBackground(int width, int height) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        Config.getTextureManager().bindTexture(this.locationTexture);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        double d0 = 16 * this.scale;
        double d1 = (double)width / d0;
        double d2 = (double)height / d0;
        double d3 = 0.0;
        double d4 = 0.0;
        if (this.center) {
            d3 = (d0 - (double)width) / (d0 * 2.0);
            d4 = (d0 - (double)height) / (d0 * 2.0);
        }
        switch (this.scaleMode) {
            case 1: {
                d0 = Math.max(width, height);
                d1 = (double)(this.scale * width) / d0;
                d2 = (double)(this.scale * height) / d0;
                if (!this.center) break;
                d3 = (double)this.scale * (d0 - (double)width) / (d0 * 2.0);
                d4 = (double)this.scale * (d0 - (double)height) / (d0 * 2.0);
                break;
            }
            case 2: {
                d1 = this.scale;
                d2 = this.scale;
                d3 = 0.0;
                d4 = 0.0;
            }
        }
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(0.0, height, 0.0).func_181673_a(d3, d4 + d2).color(255, 255, 255, 255).end();
        worldrenderer.pos(width, height, 0.0).func_181673_a(d3 + d1, d4 + d2).color(255, 255, 255, 255).end();
        worldrenderer.pos(width, 0.0, 0.0).func_181673_a(d3 + d1, d4).color(255, 255, 255, 255).end();
        worldrenderer.pos(0.0, 0.0, 0.0).func_181673_a(d3, d4).color(255, 255, 255, 255).end();
        tessellator.draw();
    }
}

