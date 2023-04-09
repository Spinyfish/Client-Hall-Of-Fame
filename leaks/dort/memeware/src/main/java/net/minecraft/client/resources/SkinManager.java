package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SkinManager {
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache skinCacheLoader;
    // private static final String __OBFID = "CL_00001830";

    public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        this.skinCacheDir = skinCacheDirectory;
        this.sessionService = sessionService;
        this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader() {
            // private static final String __OBFID = "CL_00001829";
            public Map func_152786_a(GameProfile gameProfile) {
                return Minecraft.getMinecraft().getSessionService().getTextures(gameProfile, false);
            }

            public Object load(Object gameProfile) {
                return this.func_152786_a((GameProfile) gameProfile);
            }
        });
    }

    /**
     * Used in the Skull renderer to fetch a skin. May download the skin if it's not in the cache
     */
    public ResourceLocation loadSkin(MinecraftProfileTexture minecraftProfileTexture, Type textureType) {
        return this.loadSkin(minecraftProfileTexture, textureType, null);
    }

    /**
     * May download the skin if its not in the cache, can be passed a SkinManager#SkinAvailableCallback for handling
     */
    public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final Type textureType, final SkinManager.SkinAvailableCallback callback) {
        final ResourceLocation location = new ResourceLocation("skins/" + profileTexture.getHash());
        ITextureObject texture = this.textureManager.getTexture(location);

        if (texture != null) {
            if (callback != null) {
                callback.skinCallBack(textureType, location, profileTexture);
            }
        } else {
            File skinSubDir = new File(this.skinCacheDir, profileTexture.getHash().substring(0, 2));
            File skinCacheFile = new File(skinSubDir, profileTexture.getHash());
            final ImageBufferDownload imageBufferDownload = textureType == Type.SKIN ? new ImageBufferDownload() : null;
            ThreadDownloadImageData skinDownloadThread = new ThreadDownloadImageData(skinCacheFile, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
                // private static final String __OBFID = "CL_00001828";
                public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
                    if (imageBufferDownload != null) {
                        bufferedImage = imageBufferDownload.parseUserSkin(bufferedImage);
                    }

                    return bufferedImage;
                }

                public void func_152634_a() {
                    if (imageBufferDownload != null) {
                        imageBufferDownload.func_152634_a();
                    }

                    if (callback != null) {
                        callback.skinCallBack(textureType, location, profileTexture);
                    }
                }
            });
            this.textureManager.loadTexture(location, skinDownloadThread);
        }

        return location;
    }

    public void loadProfileTextures(final GameProfile gameProfile, final SkinManager.SkinAvailableCallback callback, final boolean requireSecure) {
        THREAD_POOL.submit(new Runnable() {
            // private static final String __OBFID = "CL_00001827";
            public void run() {
                final HashMap var1 = Maps.newHashMap();

                try {
                    var1.putAll(SkinManager.this.sessionService.getTextures(gameProfile, requireSecure));
                } catch (InsecureTextureException var3) {
                    ;
                }

                if (var1.isEmpty() && gameProfile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
                    var1.putAll(SkinManager.this.sessionService.getTextures(SkinManager.this.sessionService.fillProfileProperties(gameProfile, false), false));
                }

                Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                    // private static final String __OBFID = "CL_00001826";
                    public void run() {
                        if (var1.containsKey(Type.SKIN)) {
                            SkinManager.this.loadSkin((MinecraftProfileTexture) var1.get(Type.SKIN), Type.SKIN, callback);
                        }

                        if (var1.containsKey(Type.CAPE)) {
                            SkinManager.this.loadSkin((MinecraftProfileTexture) var1.get(Type.CAPE), Type.CAPE, callback);
                        }
                    }
                });
            }
        });
    }

    public Map loadSkinFromCache(GameProfile gameProfile) {
        return (Map) this.skinCacheLoader.getUnchecked(gameProfile);
    }

    public interface SkinAvailableCallback {
        void skinCallBack(Type var1, ResourceLocation resourceLocation, MinecraftProfileTexture profileTexture);
    }
}
