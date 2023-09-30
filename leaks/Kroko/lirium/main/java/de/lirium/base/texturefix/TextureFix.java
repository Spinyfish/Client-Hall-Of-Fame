package de.lirium.base.texturefix;

import god.buddy.aot.BCompiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Map;

public class TextureFix {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    public static LinkedList<UnloadEntry> toUnload = new LinkedList<>();

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void runFix() {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> {
            TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
            if (textureMap == null)
                return;
            long l = 0;
            byte b = 0;
            for (TextureAtlasSprite textureAtlasSprite : getData(textureMap).values()) {
                if(!textureAtlasSprite.hasAnimationMetadata()) {
                    b++;
                    l += (textureAtlasSprite.getIconWidth() * textureAtlasSprite.getIconHeight() * 4L);
                    textureAtlasSprite.setFramesTextureData(new FixList(textureAtlasSprite));
                }
            }
            l *= (1 + Minecraft.getMinecraft().gameSettings.mipmapLevels);
            System.out.println("Fixed Textures: " + b + " Saved: " + DECIMAL_FORMAT.format(toMB(l)) + "MB (" + l + " bytes)");
        });
    }

    public static void markForUnload(TextureAtlasSprite atlasSprite) {
        toUnload.add(new UnloadEntry(atlasSprite));
    }

    public static void onClientTick() {
        if (toUnload.size() > 0 && toUnload.getFirst().unload())
            toUnload.removeFirst();
    }

    private long toMB(long l) {
        return l / 1024 / 1024;
    }

    Map<String, TextureAtlasSprite> getData(TextureMap textureMap) {
        try {
            Field[] fields = textureMap.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == Map.class) {
                    field.setAccessible(true);
                    return (Map<String, TextureAtlasSprite>) field.get(textureMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reloadTextureData(TextureAtlasSprite sprite) {
        reloadTextureData(sprite, Minecraft.getMinecraft().getResourceManager(), Minecraft.getMinecraft().getTextureMapBlocks());
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static void reloadTextureData(TextureAtlasSprite sprite, IResourceManager resourceManager, TextureMap textureMap) {
        final ResourceLocation resourceLocation = getResourceLocation(sprite, textureMap);
        if(sprite.hasCustomLoader(resourceManager, resourceLocation)) {
            sprite.load(resourceManager, resourceLocation);
        } else {
            try {
                final IResource resource = resourceManager.getResource(resourceLocation);
                final PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(resource);
                final BufferedImage[] bufferedImages = new BufferedImage[1 + Minecraft.getMinecraft().gameSettings.mipmapLevels];
                bufferedImages[0] = TextureUtil.readBufferedImage(resource.getInputStream());
                sprite.loadSprite(pngsizeinfo, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static ResourceLocation getResourceLocation(TextureAtlasSprite sprite, TextureMap textureMap) {
        final ResourceLocation resourceLocation = new ResourceLocation(sprite.getIconName());
        return new ResourceLocation(resourceLocation.getResourceDomain(), String.format("%s%s%s", "textures", resourceLocation.getResourcePath(), ".png"));
    }

    public static class UnloadEntry {
        int count = 2;
        final TextureAtlasSprite sprite;

        public UnloadEntry(TextureAtlasSprite sprite) {
            this.sprite = sprite;
        }

        public boolean unload() {
            this.count--;
            if (this.count <= 0) {
                this.sprite.clearFramesTextureData();
                return true;
            }
            return false;
        }
    }
}
