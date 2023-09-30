package net.optifine;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.render.Blender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CustomSky {
    private static CustomSkyLayer[][] worldSkyLayers = null;

    public static void reset() {
        worldSkyLayers = null;
    }

    public static void update() {
        reset();

        if (Config.isCustomSky()) {
            worldSkyLayers = readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies() {
        final CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        final String s = "mcpatcher/sky/world";
        int i = -1;

        for (int j = 0; j < acustomskylayer.length; ++j) {
            final String s1 = s + j + "/sky";
            final List list = new ArrayList();

            for (int k = 1; k < 1000; ++k) {
                final String s2 = s1 + k + ".properties";

                try {
                    final ResourceLocation resourcelocation = new ResourceLocation(s2);
                    final InputStream inputstream = Config.getResourceStream(resourcelocation);

                    if (inputstream == null) {
                        break;
                    }

                    final Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    Config.dbg("CustomSky properties: " + s2);
                    final String s3 = s1 + k + ".png";
                    final CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);

                    if (customskylayer.isValid(s2)) {
                        final ResourceLocation resourcelocation1 = new ResourceLocation(customskylayer.source);
                        final ITextureObject itextureobject = TextureUtils.getTexture(resourcelocation1);

                        if (itextureobject == null) {
                            Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        } else {
                            customskylayer.textureId = itextureobject.getGlTextureId();
                            list.add(customskylayer);
                            inputstream.close();
                        }
                    }
                } catch (final FileNotFoundException var15) {
                    break;
                } catch (final IOException ioexception) {
                    ioexception.printStackTrace();
                }
            }

            if (list.size() > 0) {
                final CustomSkyLayer[] acustomskylayer2 = (CustomSkyLayer[]) list.toArray(new CustomSkyLayer[list.size()]);
                acustomskylayer[j] = acustomskylayer2;
                i = j;
            }
        }

        if (i < 0) {
            return null;
        } else {
            final int l = i + 1;
            final CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[l][0];

            for (int i1 = 0; i1 < acustomskylayer1.length; ++i1) {
                acustomskylayer1[i1] = acustomskylayer[i1];
            }

            return acustomskylayer1;
        }
    }

    public static void renderSky(final World world, final TextureManager re, final float partialTicks) {
        if (worldSkyLayers != null) {
            final int i = world.provider.getDimensionId();

            if (i >= 0 && i < worldSkyLayers.length) {
                final CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];

                if (acustomskylayer != null) {
                    final long j = world.getWorldTime();
                    final int k = (int) (j % 24000L);
                    final float f = world.getCelestialAngle(partialTicks);
                    final float f1 = world.getRainStrength(partialTicks);
                    float f2 = world.getThunderStrength(partialTicks);

                    if (f1 > 0.0F) {
                        f2 /= f1;
                    }

                    for (int l = 0; l < acustomskylayer.length; ++l) {
                        final CustomSkyLayer customskylayer = acustomskylayer[l];

                        if (customskylayer.isActive(world, k)) {
                            customskylayer.render(world, k, f, f1, f2);
                        }
                    }

                    final float f3 = 1.0F - f1;
                    Blender.clearBlend(f3);
                }
            }
        }
    }

    public static boolean hasSkyLayers(final World world) {
        if (worldSkyLayers == null) {
            return false;
        } else {
            final int i = world.provider.getDimensionId();

            if (i >= 0 && i < worldSkyLayers.length) {
                final CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];
                return acustomskylayer != null && acustomskylayer.length > 0;
            } else {
                return false;
            }
        }
    }
}
