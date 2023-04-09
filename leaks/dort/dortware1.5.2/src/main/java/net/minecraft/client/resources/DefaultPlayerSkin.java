package net.minecraft.client.resources;

import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class DefaultPlayerSkin {
    private static final ResourceLocation steve = new ResourceLocation("textures/entity/steve.png");
    private static final ResourceLocation alex = new ResourceLocation("textures/entity/alex.png");

    public static ResourceLocation getDefaultSkinLegacy() {
        return steve;
    }

    public static ResourceLocation getDefaultSkin(UUID uuid) {
        return isSlimSkin(uuid) ? alex : steve;
    }

    public static String getSkinType(UUID uuid) {
        return isSlimSkin(uuid) ? "slim" : "default";
    }

    private static boolean isSlimSkin(UUID uuid) {
        return (uuid.hashCode() & 1) == 1;
    }
}
