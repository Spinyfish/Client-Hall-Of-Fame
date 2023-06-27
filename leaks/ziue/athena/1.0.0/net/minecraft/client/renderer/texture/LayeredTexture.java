package net.minecraft.client.renderer.texture;

import com.google.common.collect.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import java.awt.image.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

public class LayeredTexture extends AbstractTexture
{
    private static final Logger logger;
    public final List<String> layeredTextureNames;
    
    public LayeredTexture(final String... textureNames) {
        this.layeredTextureNames = (List<String>)Lists.newArrayList((Object[])textureNames);
    }
    
    @Override
    public void loadTexture(final IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        BufferedImage bufferedimage = null;
        try {
            for (final String s : this.layeredTextureNames) {
                if (s != null) {
                    final InputStream inputstream = resourceManager.getResource(new ResourceLocation(s)).getInputStream();
                    final BufferedImage bufferedimage2 = TextureUtil.readBufferedImage(inputstream);
                    if (bufferedimage == null) {
                        bufferedimage = new BufferedImage(bufferedimage2.getWidth(), bufferedimage2.getHeight(), 2);
                    }
                    bufferedimage.getGraphics().drawImage(bufferedimage2, 0, 0, null);
                }
            }
        }
        catch (IOException ioexception) {
            LayeredTexture.logger.error("Couldn't load layered image", (Throwable)ioexception);
            return;
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedimage);
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
