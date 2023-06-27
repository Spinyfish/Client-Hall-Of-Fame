package net.minecraft.client.resources;

import net.minecraft.util.*;
import net.minecraft.client.resources.data.*;
import com.google.common.base.*;
import java.io.*;
import org.apache.commons.io.*;
import com.google.gson.*;
import java.awt.image.*;
import net.minecraft.client.renderer.texture.*;
import org.apache.logging.log4j.*;

public abstract class AbstractResourcePack implements IResourcePack
{
    private static final Logger resourceLog;
    public final File resourcePackFile;
    private static final String __OBFID = "CL_00001072";
    
    public AbstractResourcePack(final File resourcePackFileIn) {
        this.resourcePackFile = resourcePackFileIn;
    }
    
    private static String locationToName(final ResourceLocation location) {
        return String.format("%s/%s/%s", "assets", location.getResourceDomain(), location.getResourcePath());
    }
    
    protected static String getRelativeName(final File p_110595_0_, final File p_110595_1_) {
        return p_110595_0_.toURI().relativize(p_110595_1_.toURI()).getPath();
    }
    
    @Override
    public InputStream getInputStream(final ResourceLocation location) throws IOException {
        return this.getInputStreamByName(locationToName(location));
    }
    
    @Override
    public boolean resourceExists(final ResourceLocation location) {
        return this.hasResourceName(locationToName(location));
    }
    
    protected abstract InputStream getInputStreamByName(final String p0) throws IOException;
    
    protected abstract boolean hasResourceName(final String p0);
    
    protected void logNameNotLowercase(final String p_110594_1_) {
        AbstractResourcePack.resourceLog.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", new Object[] { p_110594_1_, this.resourcePackFile });
    }
    
    @Override
    public IMetadataSection getPackMetadata(final IMetadataSerializer p_135058_1_, final String p_135058_2_) throws IOException {
        return readMetadata(p_135058_1_, this.getInputStreamByName("pack.mcmeta"), p_135058_2_);
    }
    
    static IMetadataSection readMetadata(final IMetadataSerializer p_110596_0_, final InputStream p_110596_1_, final String p_110596_2_) {
        JsonObject jsonobject = null;
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(p_110596_1_, Charsets.UTF_8));
            jsonobject = new JsonParser().parse((Reader)bufferedreader).getAsJsonObject();
        }
        catch (RuntimeException runtimeexception) {
            throw new JsonParseException((Throwable)runtimeexception);
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedreader);
        }
        return p_110596_0_.parseMetadataSection(p_110596_2_, jsonobject);
    }
    
    @Override
    public BufferedImage getPackImage() throws IOException {
        return TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
    }
    
    @Override
    public String getPackName() {
        return this.resourcePackFile.getName();
    }
    
    static {
        resourceLog = LogManager.getLogger();
    }
}
