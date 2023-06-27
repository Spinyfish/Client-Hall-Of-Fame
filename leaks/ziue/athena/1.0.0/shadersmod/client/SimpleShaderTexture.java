package shadersmod.client;

import net.minecraft.client.resources.*;
import net.minecraft.client.renderer.texture.*;
import org.apache.commons.io.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import shadersmod.common.*;
import com.google.gson.*;
import net.minecraft.client.resources.data.*;

public class SimpleShaderTexture extends AbstractTexture
{
    private String texturePath;
    private static final IMetadataSerializer METADATA_SERIALIZER;
    
    public SimpleShaderTexture(final String texturePath) {
        this.texturePath = texturePath;
    }
    
    @Override
    public void loadTexture(final IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        final InputStream inputstream = Shaders.getShaderPackResourceStream(this.texturePath);
        if (inputstream == null) {
            throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
        }
        try {
            final BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            final TextureMetadataSection texturemetadatasection = this.loadTextureMetadataSection();
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, texturemetadatasection.getTextureBlur(), texturemetadatasection.getTextureClamp());
        }
        finally {
            IOUtils.closeQuietly(inputstream);
        }
    }
    
    private TextureMetadataSection loadTextureMetadataSection() {
        final String s = this.texturePath + ".mcmeta";
        final String s2 = "texture";
        final InputStream inputstream = Shaders.getShaderPackResourceStream(s);
        if (inputstream != null) {
            final IMetadataSerializer imetadataserializer = SimpleShaderTexture.METADATA_SERIALIZER;
            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
            TextureMetadataSection texturemetadatasection2;
            try {
                final JsonObject jsonobject = new JsonParser().parse((Reader)bufferedreader).getAsJsonObject();
                final TextureMetadataSection texturemetadatasection = imetadataserializer.parseMetadataSection(s2, jsonobject);
                if (texturemetadatasection == null) {
                    return new TextureMetadataSection(false, false, new ArrayList<Integer>());
                }
                texturemetadatasection2 = texturemetadatasection;
            }
            catch (RuntimeException runtimeexception) {
                SMCLog.warning("Error reading metadata: " + s);
                SMCLog.warning("" + runtimeexception.getClass().getName() + ": " + runtimeexception.getMessage());
                return new TextureMetadataSection(false, false, new ArrayList<Integer>());
            }
            finally {
                IOUtils.closeQuietly((Reader)bufferedreader);
                IOUtils.closeQuietly(inputstream);
            }
            return texturemetadatasection2;
        }
        return new TextureMetadataSection(false, false, new ArrayList<Integer>());
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
    
    static {
        METADATA_SERIALIZER = makeMetadataSerializer();
    }
}
