package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomGuis;
import net.optifine.EmissiveTextures;
import net.optifine.RandomEntities;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TextureManager implements ITickable, IResourceManagerReloadListener {
    private static final Logger logger = LogManager.getLogger();
    private final Map<ResourceLocation, ITextureObject> mapTextureObjects = Maps.newHashMap();
    private final List<ITickable> listTickables = Lists.newArrayList();
    private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
    private final IResourceManager theResourceManager;

    public TextureManager(final IResourceManager resourceManager) {
        this.theResourceManager = resourceManager;
    }

    public void bindTexture(ResourceLocation resource) {
        if (Config.isRandomEntities()) {
            resource = RandomEntities.getTextureLocation(resource);
        }

        if (Config.isCustomGuis()) {
            resource = CustomGuis.getTextureLocation(resource);
        }

        ITextureObject itextureobject = this.mapTextureObjects.get(resource);

        if (EmissiveTextures.isActive()) {
            itextureobject = EmissiveTextures.getEmissiveTexture(itextureobject, this.mapTextureObjects);
        }

        if (itextureobject == null) {
            itextureobject = new SimpleTexture(resource);
            this.loadTexture(resource, itextureobject);
        }

        if (Config.isShaders()) {
            ShadersTex.bindTexture(itextureobject);
        } else {
            TextureUtil.bindTexture(itextureobject.getGlTextureId());
        }
    }

    public boolean loadTickableTexture(final ResourceLocation textureLocation, final ITickableTextureObject textureObj) {
        if (this.loadTexture(textureLocation, textureObj)) {
            this.listTickables.add(textureObj);
            return true;
        } else {
            return false;
        }
    }

    public boolean loadTexture(final ResourceLocation textureLocation, ITextureObject textureObj) {
        boolean flag = true;

        try {
            textureObj.loadTexture(this.theResourceManager);
        } catch (final IOException ioexception) {
            logger.warn("Failed to load texture: " + textureLocation, ioexception);
            textureObj = TextureUtil.missingTexture;
            this.mapTextureObjects.put(textureLocation, textureObj);
            flag = false;
        } catch (final Throwable throwable) {
            final ITextureObject textureObjf = textureObj;
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addCrashSection("Resource location", textureLocation);
            crashreportcategory.addCrashSectionCallable("Texture object class", () -> textureObjf.getClass().getName());
            throw new ReportedException(crashreport);
        }

        this.mapTextureObjects.put(textureLocation, textureObj);
        return flag;
    }

    public ITextureObject getTexture(final ResourceLocation textureLocation) {
        return this.mapTextureObjects.get(textureLocation);
    }

    public ResourceLocation getDynamicTextureLocation(final String name, DynamicTexture texture) {
        if (name.equals("logo")) {
            texture = Config.getMojangLogoTexture(texture);
        }

        Integer integer = this.mapTextureCounters.get(name);

        if (integer == null) {
            integer = Integer.valueOf(1);
        } else {
            integer = Integer.valueOf(integer.intValue() + 1);
        }

        this.mapTextureCounters.put(name, integer);
        final ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
        this.loadTexture(resourcelocation, texture);
        return resourcelocation;
    }

    public void tick() {
        for (final ITickable itickable : this.listTickables) {
            itickable.tick();
        }
    }

    public void deleteTexture(final ResourceLocation textureLocation) {
        final ITextureObject itextureobject = this.getTexture(textureLocation);

        if (itextureobject != null) {
            this.mapTextureObjects.remove(textureLocation);
            TextureUtil.deleteTexture(itextureobject.getGlTextureId());
        }
    }

    public void onResourceManagerReload(final IResourceManager resourceManager) {
        Config.dbg("*** Reloading textures ***");
        Config.log("Resource packs: " + Config.getResourcePackNames());
        final Iterator iterator = this.mapTextureObjects.keySet().iterator();

        while (iterator.hasNext()) {
            final ResourceLocation resourcelocation = (ResourceLocation) iterator.next();
            final String s = resourcelocation.getResourcePath();

            if (s.startsWith("mcpatcher/") || s.startsWith("optifine/") || EmissiveTextures.isEmissive(resourcelocation)) {
                final ITextureObject itextureobject = this.mapTextureObjects.get(resourcelocation);

                if (itextureobject instanceof AbstractTexture) {
                    final AbstractTexture abstracttexture = (AbstractTexture) itextureobject;
                    abstracttexture.deleteGlTexture();
                }

                iterator.remove();
            }
        }

        EmissiveTextures.update();

        for (final Object e : new HashSet(this.mapTextureObjects.entrySet())) {
            final Entry<ResourceLocation, ITextureObject> entry = (Entry<ResourceLocation, ITextureObject>) e;
            this.loadTexture(entry.getKey(), entry.getValue());
        }
    }

    public void reloadBannerTextures() {
        for (final Object e : new HashSet(this.mapTextureObjects.entrySet())) {
            final Entry<ResourceLocation, ITextureObject> entry = (Entry<ResourceLocation, ITextureObject>) e;
            final ResourceLocation resourcelocation = entry.getKey();
            final ITextureObject itextureobject = entry.getValue();

            if (itextureobject instanceof LayeredColorMaskTexture) {
                this.loadTexture(resourcelocation, itextureobject);
            }
        }
    }
}
