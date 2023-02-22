package net.optifine.shaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.*;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class SimpleShaderTexture extends AbstractTexture {
    private final String texturePath;
    private static final IMetadataSerializer METADATA_SERIALIZER = makeMetadataSerializer();

    public SimpleShaderTexture(final String texturePath) {
        this.texturePath = texturePath;
    }

    public void loadTexture(final IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        final InputStream inputstream = Shaders.getShaderPackResourceStream(this.texturePath);

        if (inputstream == null) {
            throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
        } else {
            try {
                final BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
                final TextureMetadataSection texturemetadatasection = this.loadTextureMetadataSection();
                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, texturemetadatasection.getTextureBlur(), texturemetadatasection.getTextureClamp());
            } finally {
                IOUtils.closeQuietly(inputstream);
            }
        }
    }

    private TextureMetadataSection loadTextureMetadataSection() {
        final String s = this.texturePath + ".mcmeta";
        final String s1 = "texture";
        final InputStream inputstream = Shaders.getShaderPackResourceStream(s);

        if (inputstream != null) {
            final IMetadataSerializer imetadataserializer = METADATA_SERIALIZER;
            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
            TextureMetadataSection texturemetadatasection1;

            try {
                final JsonObject jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
                final TextureMetadataSection texturemetadatasection = imetadataserializer.parseMetadataSection(s1, jsonobject);

                if (texturemetadatasection == null) {
                    return new TextureMetadataSection(false, false, new ArrayList());
                }

                texturemetadatasection1 = texturemetadatasection;
            } catch (final RuntimeException runtimeexception) {
                SMCLog.warning("Error reading metadata: " + s);
                SMCLog.warning("" + runtimeexception.getClass().getName() + ": " + runtimeexception.getMessage());
                return new TextureMetadataSection(false, false, new ArrayList());
            } finally {
                IOUtils.closeQuietly(bufferedreader);
                IOUtils.closeQuietly(inputstream);
            }

            return texturemetadatasection1;
        } else {
            return new TextureMetadataSection(false, false, new ArrayList());
        }
    }

    private static IMetadataSerializer makeMetadataSerializer() {
        final IMetadataSerializer imetadataserializer = new IMetadataSerializer();
        imetadataserializer.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        imetadataserializer.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        imetadataserializer.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        imetadataserializer.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        imetadataserializer.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
        return imetadataserializer;
    }
}
