package net.minecraft.client.resources;

import java.util.*;
import net.minecraft.util.*;
import java.io.*;
import optifine.*;
import net.minecraft.client.resources.data.*;
import java.awt.image.*;
import net.minecraft.client.renderer.texture.*;
import com.google.common.collect.*;

public class DefaultResourcePack implements IResourcePack
{
    public static final Set defaultResourceDomains;
    private final Map mapAssets;
    private static final String __OBFID = "CL_00001073";
    
    public DefaultResourcePack(final Map mapAssetsIn) {
        this.mapAssets = mapAssetsIn;
    }
    
    @Override
    public InputStream getInputStream(final ResourceLocation location) throws IOException {
        final InputStream inputstream = this.getResourceStream(location);
        if (inputstream != null) {
            return inputstream;
        }
        final InputStream inputstream2 = this.getInputStreamAssets(location);
        if (inputstream2 != null) {
            return inputstream2;
        }
        throw new FileNotFoundException(location.getResourcePath());
    }
    
    public InputStream getInputStreamAssets(final ResourceLocation location) throws IOException, FileNotFoundException {
        final File file1 = this.mapAssets.get(location.toString());
        return (file1 != null && file1.isFile()) ? new FileInputStream(file1) : null;
    }
    
    private InputStream getResourceStream(final ResourceLocation location) {
        final String s = "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
        final InputStream inputstream = ReflectorForge.getOptiFineResourceStream(s);
        return (inputstream != null) ? inputstream : DefaultResourcePack.class.getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
    }
    
    @Override
    public boolean resourceExists(final ResourceLocation location) {
        return this.getResourceStream(location) != null || this.mapAssets.containsKey(location.toString());
    }
    
    @Override
    public Set getResourceDomains() {
        return DefaultResourcePack.defaultResourceDomains;
    }
    
    @Override
    public IMetadataSection getPackMetadata(final IMetadataSerializer p_135058_1_, final String p_135058_2_) throws IOException {
        try {
            final FileInputStream fileinputstream = new FileInputStream(this.mapAssets.get("pack.mcmeta"));
            return AbstractResourcePack.readMetadata(p_135058_1_, fileinputstream, p_135058_2_);
        }
        catch (RuntimeException var4) {
            return null;
        }
        catch (FileNotFoundException var5) {
            return null;
        }
    }
    
    @Override
    public BufferedImage getPackImage() throws IOException {
        return TextureUtil.readBufferedImage(DefaultResourcePack.class.getResourceAsStream("/" + new ResourceLocation("pack.png").getResourcePath()));
    }
    
    @Override
    public String getPackName() {
        return "Default";
    }
    
    static {
        defaultResourceDomains = (Set)ImmutableSet.of((Object)"minecraft", (Object)"realms");
    }
}
