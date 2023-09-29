package net.optifine;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

import java.util.Properties;

public class CustomLoadingScreen {
    private final ResourceLocation locationTexture;
    private int scaleMode = 0;
    private int scale = 2;
    private final boolean center;
    private static final int SCALE_DEFAULT = 2;
    private static final int SCALE_MODE_FIXED = 0;
    private static final int SCALE_MODE_FULL = 1;
    private static final int SCALE_MODE_STRETCH = 2;

    public CustomLoadingScreen(final ResourceLocation locationTexture, final int scaleMode, final int scale, final boolean center) {
        this.locationTexture = locationTexture;
        this.scaleMode = scaleMode;
        this.scale = scale;
        this.center = center;
    }

    public static CustomLoadingScreen parseScreen(final String path, final int dimId, final Properties props) {
        final ResourceLocation resourcelocation = new ResourceLocation(path);
        final int i = parseScaleMode(getProperty("scaleMode", dimId, props));
        final int j = i == 0 ? 2 : 1;
        final int k = parseScale(getProperty("scale", dimId, props), j);
        final boolean flag = Config.parseBoolean(getProperty("center", dimId, props), false);
        final CustomLoadingScreen customloadingscreen = new CustomLoadingScreen(resourcelocation, i, k, flag);
        return customloadingscreen;
    }

    private static String getProperty(final String key, final int dim, final Properties props) {
        if (props == null) {
            return null;
        } else {
            String s = props.getProperty("dim" + dim + "." + key);

            if (s != null) {
                return s;
            } else {
                s = props.getProperty(key);
                return s;
            }
        }
    }

    private static int parseScaleMode(String str) {
        if (str == null) {
            return 0;
        } else {
            str = str.toLowerCase().trim();

            if (str.equals("fixed")) {
                return 0;
            } else if (str.equals("full")) {
                return 1;
            } else if (str.equals("stretch")) {
                return 2;
            } else {
                CustomLoadingScreens.warn("Invalid scale mode: " + str);
                return 0;
            }
        }
    }

    private static int parseScale(String str, final int def) {
        if (str == null) {
            return def;
        } else {
            str = str.trim();
            final int i = Config.parseInt(str, -1);

            if (i < 1) {
                CustomLoadingScreens.warn("Invalid scale: " + str);
                return def;
            } else {
                return i;
            }
        }
    }

    public void drawBackground(final int width, final int height) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        Config.getTextureManager().bindTexture(this.locationTexture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        double d0 = 16 * this.scale;
        double d1 = (double) width / d0;
        double d2 = (double) height / d0;
        double d3 = 0.0D;
        double d4 = 0.0D;

        if (this.center) {
            d3 = (d0 - (double) width) / (d0 * 2.0D);
            d4 = (d0 - (double) height) / (d0 * 2.0D);
        }

        switch (this.scaleMode) {
            case 1:
                d0 = Math.max(width, height);
                d1 = (double) (this.scale * width) / d0;
                d2 = (double) (this.scale * height) / d0;

                if (this.center) {
                    d3 = (double) this.scale * (d0 - (double) width) / (d0 * 2.0D);
                    d4 = (double) this.scale * (d0 - (double) height) / (d0 * 2.0D);
                }

                break;

            case 2:
                d1 = this.scale;
                d2 = this.scale;
                d3 = 0.0D;
                d4 = 0.0D;
        }

        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(0.0D, height, 0.0D).tex(d3, d4 + d2).func_181669_b(255, 255, 255, 255).endVertex();
        worldrenderer.pos(width, height, 0.0D).tex(d3 + d1, d4 + d2).func_181669_b(255, 255, 255, 255).endVertex();
        worldrenderer.pos(width, 0.0D, 0.0D).tex(d3 + d1, d4).func_181669_b(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(d3, d4).func_181669_b(255, 255, 255, 255).endVertex();
        tessellator.draw();
    }
}
