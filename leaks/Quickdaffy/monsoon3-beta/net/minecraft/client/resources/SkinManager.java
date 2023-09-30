/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.InsecureTextureException
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 */
package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCacheLoader;

    public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        this.skinCacheDir = skinCacheDirectory;
        this.sessionService = sessionService;
        this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(GameProfile p_load_1_) throws Exception {
                return Minecraft.getMinecraft().getSessionService().getTextures(p_load_1_, false);
            }
        });
    }

    public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type p_152792_2_) {
        return this.loadSkin(profileTexture, p_152792_2_, null);
    }

    public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type p_152789_2_, final SkinAvailableCallback skinAvailableCallback) {
        final ResourceLocation resourcelocation = new ResourceLocation("skins/" + profileTexture.getHash());
        ITextureObject itextureobject = this.textureManager.getTexture(resourcelocation);
        if (itextureobject != null) {
            if (skinAvailableCallback != null) {
                skinAvailableCallback.skinAvailable(p_152789_2_, resourcelocation, profileTexture);
            }
        } else {
            File file1 = new File(this.skinCacheDir, profileTexture.getHash().length() > 2 ? profileTexture.getHash().substring(0, 2) : "xx");
            File file2 = new File(file1, profileTexture.getHash());
            final ImageBufferDownload iimagebuffer = p_152789_2_ == MinecraftProfileTexture.Type.SKIN ? new ImageBufferDownload() : null;
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer(){

                @Override
                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (iimagebuffer != null) {
                        image = iimagebuffer.parseUserSkin(image);
                    }
                    return image;
                }

                @Override
                public void skinAvailable() {
                    if (iimagebuffer != null) {
                        iimagebuffer.skinAvailable();
                    }
                    if (skinAvailableCallback != null) {
                        skinAvailableCallback.skinAvailable(p_152789_2_, resourcelocation, profileTexture);
                    }
                }
            });
            this.textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
        }
        return resourcelocation;
    }

    public void loadProfileTextures(final GameProfile profile, final SkinAvailableCallback skinAvailableCallback, final boolean requireSecure) {
        THREAD_POOL.submit(new Runnable(){

            @Override
            public void run() {
                final HashMap map = Maps.newHashMap();
                try {
                    map.putAll(SkinManager.this.sessionService.getTextures(profile, requireSecure));
                }
                catch (InsecureTextureException insecureTextureException) {
                    // empty catch block
                }
                if (map.isEmpty() && profile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
                    profile.getProperties().clear();
                    profile.getProperties().putAll((Multimap)Minecraft.getMinecraft().func_181037_M());
                    map.putAll(SkinManager.this.sessionService.getTextures(profile, false));
                }
                Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                    @Override
                    public void run() {
                        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                            SkinManager.this.loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, skinAvailableCallback);
                        }
                        if (map.containsKey(MinecraftProfileTexture.Type.CAPE)) {
                            SkinManager.this.loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, skinAvailableCallback);
                        }
                    }
                });
            }
        });
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
        return (Map)this.skinCacheLoader.getUnchecked((Object)profile);
    }

    public static interface SkinAvailableCallback {
        public void skinAvailable(MinecraftProfileTexture.Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
    }
}

