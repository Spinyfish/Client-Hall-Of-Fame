/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.ITweaker
 *  net.minecraft.launchwrapper.Launch
 *  net.minecraft.launchwrapper.LaunchClassLoader
 */
package wtf.monsoon.misc.asm;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.main.Main;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import wtf.monsoon.misc.asm.transformer.MonsoonClassTransformer;

public class Tweaker
implements ITweaker {
    private final ArrayList<String> arguments = new ArrayList();
    private Set<String> exceptions;

    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
        this.arguments.addAll(args);
        if (gameDir != null) {
            this.arguments.add("--gameDir");
            this.arguments.add(gameDir.getAbsolutePath());
        }
        if (assetsDir != null) {
            this.arguments.add("--assetsDir");
            this.arguments.add(assetsDir.getAbsolutePath());
        }
        if (version != null) {
            this.arguments.add("--version");
            this.arguments.add(version);
        }
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer(MonsoonClassTransformer.class.getName());
        this.unlockLwjgl();
    }

    private void unlockLwjgl() {
        try {
            Field transformerExceptions = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            transformerExceptions.setAccessible(true);
            Object o = transformerExceptions.get(Launch.classLoader);
            ((Set)o).remove("org.lwjgl.");
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String getLaunchTarget() {
        return Main.class.getName();
    }

    public String[] getLaunchArguments() {
        return this.arguments.toArray(new String[0]);
    }
}

