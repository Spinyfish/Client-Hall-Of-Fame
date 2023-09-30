package net.optifine;

import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;

import java.util.BitSet;

public class SmartAnimations {
    private static boolean active;
    private static final BitSet spritesRendered = new BitSet();
    private static final BitSet texturesRendered = new BitSet();

    public static boolean isActive() {
        return active && !Shaders.isShadowPass;
    }

    public static void update() {
        active = Config.getGameSettings().ofSmartAnimations;
    }

    public static void spriteRendered(final int animationIndex) {
        spritesRendered.set(animationIndex);
    }

    public static void spritesRendered(final BitSet animationIndexes) {
        if (animationIndexes != null) {
            spritesRendered.or(animationIndexes);
        }
    }

    public static boolean isSpriteRendered(final int animationIndex) {
        return spritesRendered.get(animationIndex);
    }

    public static void resetSpritesRendered() {
        spritesRendered.clear();
    }

    public static void textureRendered(final int textureId) {
        if (textureId >= 0) {
            texturesRendered.set(textureId);
        }
    }

    public static boolean isTextureRendered(final int texId) {
        return texId >= 0 && texturesRendered.get(texId);
    }

    public static void resetTexturesRendered() {
        texturesRendered.clear();
    }
}
