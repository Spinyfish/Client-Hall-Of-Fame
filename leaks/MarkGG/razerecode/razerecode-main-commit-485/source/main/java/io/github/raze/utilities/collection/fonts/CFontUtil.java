/*
 * Copyright (c) 2023. Syncinus / Liticane
 * You cannot redistribute these files
 * without my written permission.
 */

package io.github.raze.utilities.collection.fonts;

import io.github.raze.utilities.system.fonts.CFontRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public enum CFontUtil {

    Jello_Light_12 ("Jello Light 12", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 12)),
    Jello_Light_16 ("Jello Light 16", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 16)),
    Jello_Light_20 ("Jello Light 20", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 20)),
    Jello_Light_24 ("Jello Light 24", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 24)),
    Jello_Light_28 ("Jello Light 28", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 28)),
    Jello_Light_32 ("Jello Light 32", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 32)),
    Jello_Light_36 ("Jello Light 36", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 36)),
    Jello_Light_40 ("Jello Light 40", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Light.ttf"), 40)),

    Jello_Regular_12 ("Jello Regular 12", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 12)),
    Jello_Regular_16 ("Jello Regular 16", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 16)),
    Jello_Regular_20 ("Jello Regular 20", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 20)),
    Jello_Regular_24 ("Jello Regular 24", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 24)),
    Jello_Regular_28 ("Jello Regular 28", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 28)),
    Jello_Regular_32 ("Jello Regular 32", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 32)),
    Jello_Regular_36 ("Jello Regular 26", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 36)),
    Jello_Regular_40 ("Jello Regular 40", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Regular.ttf"), 40)),

    Jello_Medium_12 ("Jello Medium 12", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 12)),
    Jello_Medium_16 ("Jello Medium 16", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 16)),
    Jello_Medium_20 ("Jello Medium 20", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 20)),
    Jello_Medium_24 ("Jello Medium 24", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 24)),
    Jello_Medium_28 ("Jello Medium 28", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 28)),
    Jello_Medium_32 ("Jello Medium 32", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 32)),
    Jello_Medium_36 ("Jello Medium 36", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 36)),
    Jello_Medium_40 ("Jello Medium 40", new CFontRenderer(new ResourceLocation("/raze/fonts/Jello-Medium.ttf"), 40));

    public final String name;
    public final CFontRenderer renderer;

    CFontUtil(String name, CFontRenderer renderer) {
        this.name = name;
        this.renderer = renderer;
    }

    public String getName() {
        return name;
    }

    public CFontRenderer getRenderer() {
        return renderer;
    }

    public static CFontUtil getFontByName(String name) {
        return Arrays.stream(values()).filter(font -> font.getName().equalsIgnoreCase(name)).findFirst().get();
    }
}