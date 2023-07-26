package com.craftworks.pearclient.mixins.injections;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tweaker implements ITweaker {

    private List<String> launchArgs = new ArrayList<>();

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.launchArgs.addAll(args);

        final String VERSION = "--version";
        final String ASSET_DIR = "--assetDir";
        final String GAME_DIR = "--gameDir";

        if (!args.contains(VERSION) && profile != null) {
            launchArgs.add(VERSION);
            launchArgs.add(profile);
        }
        if (!args.contains(ASSET_DIR) && profile != null) {
            launchArgs.add(ASSET_DIR);
            launchArgs.add(profile);
        }
        if (!args.contains(GAME_DIR) && profile != null) {
            launchArgs.add(GAME_DIR);
            launchArgs.add(profile);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinBootstrap.init();

        MixinEnvironment environment = MixinEnvironment.getDefaultEnvironment();

        environment.addConfiguration("mixins.pearclient.json");

        if (environment.getObfuscationContext() == null) {
            environment.setObfuscationContext("notch");
        }

        environment.setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String getLaunchTarget() {
        return MixinBootstrap.getPlatform().getLaunchTarget();
    }

    @Override
    public String[] getLaunchArguments() {
        return launchArgs.toArray(new String[0]);
    }
}
