package net.optifine;

import net.minecraft.network.PacketThreadUtil;
import net.minecraft.src.Config;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class CustomLoadingScreens {
    private static CustomLoadingScreen[] screens = null;
    private static int screensMinDimensionId = 0;

    public static CustomLoadingScreen getCustomLoadingScreen() {
        if (screens == null) {
            return null;
        } else {
            final int i = PacketThreadUtil.lastDimensionId;
            final int j = i - screensMinDimensionId;
            CustomLoadingScreen customloadingscreen = null;

            if (j >= 0 && j < screens.length) {
                customloadingscreen = screens[j];
            }

            return customloadingscreen;
        }
    }

    public static void update() {
        screens = null;
        screensMinDimensionId = 0;
        final Pair<CustomLoadingScreen[], Integer> pair = parseScreens();
        screens = pair.getLeft();
        screensMinDimensionId = pair.getRight().intValue();
    }

    private static Pair<CustomLoadingScreen[], Integer> parseScreens() {
        final String s = "optifine/gui/loading/background";
        final String s1 = ".png";
        final String[] astring = ResUtils.collectFiles(s, s1);
        final Map<Integer, String> map = new HashMap();

        for (int i = 0; i < astring.length; ++i) {
            final String s2 = astring[i];
            final String s3 = StrUtils.removePrefixSuffix(s2, s, s1);
            final int j = Config.parseInt(s3, Integer.MIN_VALUE);

            if (j == Integer.MIN_VALUE) {
                warn("Invalid dimension ID: " + s3 + ", path: " + s2);
            } else {
                map.put(Integer.valueOf(j), s2);
            }
        }

        final Set<Integer> set = map.keySet();
        final Integer[] ainteger = set.toArray(new Integer[set.size()]);
        Arrays.sort(ainteger);

        if (ainteger.length <= 0) {
            return new ImmutablePair(null, Integer.valueOf(0));
        } else {
            final String s5 = "optifine/gui/loading/loading.properties";
            final Properties properties = ResUtils.readProperties(s5, "CustomLoadingScreens");
            final int k = ainteger[0].intValue();
            final int l = ainteger[ainteger.length - 1].intValue();
            final int i1 = l - k + 1;
            final CustomLoadingScreen[] acustomloadingscreen = new CustomLoadingScreen[i1];

            for (int j1 = 0; j1 < ainteger.length; ++j1) {
                final Integer integer = ainteger[j1];
                final String s4 = map.get(integer);
                acustomloadingscreen[integer.intValue() - k] = CustomLoadingScreen.parseScreen(s4, integer.intValue(), properties);
            }

            return new ImmutablePair(acustomloadingscreen, Integer.valueOf(k));
        }
    }

    public static void warn(final String str) {
        Config.warn("CustomLoadingScreen: " + str);
    }

    public static void dbg(final String str) {
        Config.dbg("CustomLoadingScreen: " + str);
    }
}
