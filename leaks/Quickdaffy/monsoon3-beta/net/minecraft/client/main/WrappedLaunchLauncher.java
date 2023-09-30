/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.Launch
 */
package net.minecraft.client.main;

import java.util.Arrays;
import net.minecraft.launchwrapper.Launch;
import wtf.monsoon.misc.asm.Tweaker;

public class WrappedLaunchLauncher {
    public static void main(String[] args) {
        String[] thing = new String[]{"--tweakClass", Tweaker.class.getName(), "launchedCorrectlyYouMonkey"};
        Launch.main((String[])WrappedLaunchLauncher.concat(args, thing));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}

