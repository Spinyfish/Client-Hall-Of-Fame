package net.minecraft.client.resources;

import java.io.*;
import com.mojang.authlib.*;
import java.util.*;
import com.google.common.cache.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import java.awt.image.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import com.mojang.authlib.minecraft.*;
import com.google.common.collect.*;
import java.util.concurrent.*;

public class SkinManager
{
    private static final ExecutorService THREAD_POOL;
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCacheLoader;
    
    public SkinManager(final TextureManager textureManagerInstance, final File skinCacheDirectory, final MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        this.skinCacheDir = skinCacheDirectory;
        this.sessionService = sessionService;
        this.skinCacheLoader = (LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>)CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>() {
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(final GameProfile p_load_1_) throws Exception {
                return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)Minecraft.getMinecraft().getSessionService().getTextures(p_load_1_, false);
            }
        });
    }
    
    public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type p_152792_2_) {
        return this.loadSkin(profileTexture, p_152792_2_, null);
    }
    
    public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type p_152789_2_, final SkinAvailableCallback skinAvailableCallback) {
        final ResourceLocation resourcelocation = new ResourceLocation("skins/" + profileTexture.getHash());
        final ITextureObject itextureobject = this.textureManager.getTexture(resourcelocation);
        if (itextureobject != null) {
            if (skinAvailableCallback != null) {
                skinAvailableCallback.skinAvailable(p_152789_2_, resourcelocation, profileTexture);
            }
        }
        else {
            final File file1 = new File(this.skinCacheDir, (profileTexture.getHash().length() > 2) ? profileTexture.getHash().substring(0, 2) : "xx");
            final File file2 = new File(file1, profileTexture.getHash());
            final IImageBuffer iimagebuffer = (p_152789_2_ == MinecraftProfileTexture.Type.SKIN) ? new ImageBufferDownload() : null;
            final ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
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
        SkinManager.THREAD_POOL.submit(new Runnable() {
            @Override
            public void run() {
                final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)Maps.newHashMap();
                try {
                    map.putAll(SkinManager.this.sessionService.getTextures(profile, requireSecure));
                }
                catch (InsecureTextureException ex) {}
                if (map.isEmpty() && profile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
                    profile.getProperties().clear();
                    profile.getProperties().putAll((Multimap)Minecraft.getMinecraft().func_181037_M());
                    map.putAll(SkinManager.this.sessionService.getTextures(profile, false));
                }
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                            SkinManager.this.loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, skinAvailableCallback);
                        }
                        if (map.containsKey(MinecraftProfileTexture.Type.CAPE)) {
                            SkinManager.this.loadSkin(map.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, skinAvailableCallback);
                        }
                    }
                });
            }
        });
    }
    
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCache(final GameProfile profile) {
        return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)this.skinCacheLoader.getUnchecked((Object)profile);
    }
    
    static {
        THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    }
    
    public interface SkinAvailableCallback
    {
        void skinAvailable(final MinecraftProfileTexture.Type p0, final ResourceLocation p1, final MinecraftProfileTexture p2);
    }
}
