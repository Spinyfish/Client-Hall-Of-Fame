/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture
extends AbstractTexture {
    private static final Logger logger = LogManager.getLogger();
    public final List<String> layeredTextureNames;

    public LayeredTexture(String ... textureNames) {
        this.layeredTextureNames = Lists.newArrayList((Object[])textureNames);
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        BufferedImage bufferedimage = null;
        try {
            for (String s : this.layeredTextureNames) {
                if (s == null) continue;
                InputStream inputstream = resourceManager.getResource(new ResourceLocation(s)).getInputStream();
                BufferedImage bufferedimage1 = TextureUtil.readBufferedImage(inputstream);
                if (bufferedimage == null) {
                    bufferedimage = new BufferedImage(bufferedimage1.getWidth(), bufferedimage1.getHeight(), 2);
                }
                bufferedimage.getGraphics().drawImage(bufferedimage1, 0, 0, null);
            }
        }
        catch (IOException ioexception) {
            logger.error("Couldn't load layered image", (Throwable)ioexception);
            return;
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedimage);
    }
}

