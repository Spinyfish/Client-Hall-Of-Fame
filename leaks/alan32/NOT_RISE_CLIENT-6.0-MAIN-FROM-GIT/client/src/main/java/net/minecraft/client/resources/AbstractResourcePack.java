package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.*;

public abstract class AbstractResourcePack implements IResourcePack {
    private static final Logger resourceLog = LogManager.getLogger();
    public final File resourcePackFile;

    public AbstractResourcePack(final File resourcePackFileIn) {
        this.resourcePackFile = resourcePackFileIn;
    }

    private static String locationToName(final ResourceLocation location) {
        return String.format("%s/%s/%s", "assets", location.getResourceDomain(), location.getResourcePath());
    }

    protected static String getRelativeName(final File p_110595_0_, final File p_110595_1_) {
        return p_110595_0_.toURI().relativize(p_110595_1_.toURI()).getPath();
    }

    public InputStream getInputStream(final ResourceLocation location) throws IOException {
        return this.getInputStreamByName(locationToName(location));
    }

    public boolean resourceExists(final ResourceLocation location) {
        return this.hasResourceName(locationToName(location));
    }

    protected abstract InputStream getInputStreamByName(String name) throws IOException;

    protected abstract boolean hasResourceName(String name);

    protected void logNameNotLowercase(final String p_110594_1_) {
        resourceLog.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", new Object[]{p_110594_1_, this.resourcePackFile});
    }

    public <T extends IMetadataSection> T getPackMetadata(final IMetadataSerializer p_135058_1_, final String p_135058_2_) throws IOException {
        return readMetadata(p_135058_1_, this.getInputStreamByName("pack.mcmeta"), p_135058_2_);
    }

    static <T extends IMetadataSection> T readMetadata(final IMetadataSerializer p_110596_0_, final InputStream p_110596_1_, final String p_110596_2_) {
        JsonObject jsonobject = null;
        BufferedReader bufferedreader = null;

        try {
            bufferedreader = new BufferedReader(new InputStreamReader(p_110596_1_, Charsets.UTF_8));
            jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
        } catch (final RuntimeException runtimeexception) {
            throw new JsonParseException(runtimeexception);
        } finally {
            IOUtils.closeQuietly(bufferedreader);
        }

        return p_110596_0_.parseMetadataSection(p_110596_2_, jsonobject);
    }

    public BufferedImage getPackImage() throws IOException {
        return TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
    }

    public String getPackName() {
        return this.resourcePackFile.getName();
    }
}
