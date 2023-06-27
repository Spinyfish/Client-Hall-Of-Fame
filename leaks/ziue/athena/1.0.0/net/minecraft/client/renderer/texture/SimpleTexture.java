package net.minecraft.client.renderer.texture;

import net.minecraft.util.*;
import net.minecraft.client.resources.data.*;
import optifine.*;
import shadersmod.client.*;
import net.minecraft.client.resources.*;
import java.awt.image.*;
import java.io.*;
import org.apache.logging.log4j.*;

public class SimpleTexture extends AbstractTexture
{
    private static final Logger logger;
    protected final ResourceLocation textureLocation;
    private static final String __OBFID = "CL_00001052";
    
    public SimpleTexture(final ResourceLocation textureResourceLocation) {
        this.textureLocation = textureResourceLocation;
    }
    
    @Override
    public void loadTexture(final IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        InputStream inputstream = null;
        try {
            final IResource iresource = resourceManager.getResource(this.textureLocation);
            inputstream = iresource.getInputStream();
            final BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            boolean flag = false;
            boolean flag2 = false;
            if (iresource.hasMetadata()) {
                try {
                    final TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");
                    if (texturemetadatasection != null) {
                        flag = texturemetadatasection.getTextureBlur();
                        flag2 = texturemetadatasection.getTextureClamp();
                    }
                }
                catch (RuntimeException runtimeexception) {
                    SimpleTexture.logger.warn("Failed reading metadata of: " + this.textureLocation, (Throwable)runtimeexception);
                }
            }
            if (Config.isShaders()) {
                ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedimage, flag, flag2, resourceManager, this.textureLocation, this.getMultiTexID());
            }
            else {
                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag2);
            }
        }
        finally {
            if (inputstream != null) {
                inputstream.close();
            }
        }
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
