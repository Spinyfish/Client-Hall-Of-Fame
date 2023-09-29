package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.MathUtils;
import net.optifine.util.PropertiesOrdered;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class CustomPanorama {
    private static CustomPanoramaProperties customPanoramaProperties = null;
    private static final Random random = new Random();

    public static CustomPanoramaProperties getCustomPanoramaProperties() {
        return customPanoramaProperties;
    }

    public static void update() {
        customPanoramaProperties = null;
        final String[] astring = getPanoramaFolders();

        if (astring.length > 1) {
            final Properties[] aproperties = getPanoramaProperties(astring);
            final int[] aint = getWeights(aproperties);
            final int i = getRandomIndex(aint);
            final String s = astring[i];
            Properties properties = aproperties[i];

            if (properties == null) {
                properties = aproperties[0];
            }

            if (properties == null) {
                properties = new PropertiesOrdered();
            }

            final CustomPanoramaProperties custompanoramaproperties = new CustomPanoramaProperties(s, properties);
            customPanoramaProperties = custompanoramaproperties;
        }
    }

    private static String[] getPanoramaFolders() {
        final List<String> list = new ArrayList();
        list.add("textures/gui/title/background");

        for (int i = 0; i < 100; ++i) {
            final String s = "optifine/gui/background" + i;
            final String s1 = s + "/panorama_0.png";
            final ResourceLocation resourcelocation = new ResourceLocation(s1);

            if (Config.hasResource(resourcelocation)) {
                list.add(s);
            }
        }

        final String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    private static Properties[] getPanoramaProperties(final String[] folders) {
        final Properties[] aproperties = new Properties[folders.length];

        for (int i = 0; i < folders.length; ++i) {
            String s = folders[i];

            if (i == 0) {
                s = "optifine/gui";
            } else {
                Config.dbg("CustomPanorama: " + s);
            }

            final ResourceLocation resourcelocation = new ResourceLocation(s + "/background.properties");

            try {
                final InputStream inputstream = Config.getResourceStream(resourcelocation);

                if (inputstream != null) {
                    final Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    Config.dbg("CustomPanorama: " + resourcelocation.getResourcePath());
                    aproperties[i] = properties;
                    inputstream.close();
                }
            } catch (final IOException var7) {
            }
        }

        return aproperties;
    }

    private static int[] getWeights(final Properties[] propertiess) {
        final int[] aint = new int[propertiess.length];

        for (int i = 0; i < aint.length; ++i) {
            Properties properties = propertiess[i];

            if (properties == null) {
                properties = propertiess[0];
            }

            if (properties == null) {
                aint[i] = 1;
            } else {
                final String s = properties.getProperty("weight", null);
                aint[i] = Config.parseInt(s, 1);
            }
        }

        return aint;
    }

    private static int getRandomIndex(final int[] weights) {
        final int i = MathUtils.getSum(weights);
        final int j = random.nextInt(i);
        int k = 0;

        for (int l = 0; l < weights.length; ++l) {
            k += weights[l];

            if (k > j) {
                return l;
            }
        }

        return weights.length - 1;
    }
}
