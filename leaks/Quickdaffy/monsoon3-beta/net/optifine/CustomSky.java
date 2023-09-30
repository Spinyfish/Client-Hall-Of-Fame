/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.CustomSkyLayer;
import net.optifine.render.Blender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;

public class CustomSky {
    private static CustomSkyLayer[][] worldSkyLayers = null;

    public static void reset() {
        worldSkyLayers = null;
    }

    public static void update() {
        CustomSky.reset();
        if (Config.isCustomSky()) {
            worldSkyLayers = CustomSky.readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies() {
        CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        String s = "mcpatcher/sky/world";
        int i = -1;
        for (int j = 0; j < acustomskylayer.length; ++j) {
            String s1 = s + j + "/sky";
            ArrayList<CustomSkyLayer> list = new ArrayList<CustomSkyLayer>();
            for (int k = 1; k < 1000; ++k) {
                String s2 = s1 + k + ".properties";
                try {
                    ResourceLocation resourcelocation = new ResourceLocation(s2);
                    InputStream inputstream = Config.getResourceStream(resourcelocation);
                    if (inputstream == null) break;
                    PropertiesOrdered properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    Config.dbg("CustomSky properties: " + s2);
                    String s3 = s1 + k + ".png";
                    CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);
                    if (!customskylayer.isValid(s2)) continue;
                    ResourceLocation resourcelocation1 = new ResourceLocation(customskylayer.source);
                    ITextureObject itextureobject = TextureUtils.getTexture(resourcelocation1);
                    if (itextureobject == null) {
                        Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        continue;
                    }
                    customskylayer.textureId = itextureobject.getGlTextureId();
                    list.add(customskylayer);
                    inputstream.close();
                    continue;
                }
                catch (FileNotFoundException var15) {
                    break;
                }
                catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
            }
            if (list.size() <= 0) continue;
            CustomSkyLayer[] acustomskylayer2 = list.toArray(new CustomSkyLayer[list.size()]);
            acustomskylayer[j] = acustomskylayer2;
            i = j;
        }
        if (i < 0) {
            return null;
        }
        int l = i + 1;
        CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[l][0];
        for (int i1 = 0; i1 < acustomskylayer1.length; ++i1) {
            acustomskylayer1[i1] = acustomskylayer[i1];
        }
        return acustomskylayer1;
    }

    public static void renderSky(World world, TextureManager re, float partialTicks) {
        CustomSkyLayer[] acustomskylayer;
        int i;
        if (worldSkyLayers != null && (i = world.provider.getDimensionId()) >= 0 && i < worldSkyLayers.length && (acustomskylayer = worldSkyLayers[i]) != null) {
            long j = world.getWorldTime();
            int k = (int)(j % 24000L);
            float f = world.getCelestialAngle(partialTicks);
            float f1 = world.getRainStrength(partialTicks);
            float f2 = world.getThunderStrength(partialTicks);
            if (f1 > 0.0f) {
                f2 /= f1;
            }
            for (int l = 0; l < acustomskylayer.length; ++l) {
                CustomSkyLayer customskylayer = acustomskylayer[l];
                if (!customskylayer.isActive(world, k)) continue;
                customskylayer.render(world, k, f, f1, f2);
            }
            float f3 = 1.0f - f1;
            Blender.clearBlend(f3);
        }
    }

    public static boolean hasSkyLayers(World world) {
        if (worldSkyLayers == null) {
            return false;
        }
        int i = world.provider.getDimensionId();
        if (i >= 0 && i < worldSkyLayers.length) {
            CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];
            return acustomskylayer == null ? false : acustomskylayer.length > 0;
        }
        return false;
    }
}

