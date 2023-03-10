/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer.texture;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture
extends AbstractTexture {
    private static final Logger LOG = LogManager.getLogger();
    private final ResourceLocation textureLocation;
    private final List<String> field_174949_h;
    private final List<EnumDyeColor> field_174950_i;

    public LayeredColorMaskTexture(ResourceLocation textureLocationIn, List<String> p_i46101_2_, List<EnumDyeColor> p_i46101_3_) {
        this.textureLocation = textureLocationIn;
        this.field_174949_h = p_i46101_2_;
        this.field_174950_i = p_i46101_3_;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        BufferedImage bufferedimage;
        this.deleteGlTexture();
        try {
            BufferedImage bufferedimage1 = TextureUtil.readBufferedImage(resourceManager.getResource(this.textureLocation).getInputStream());
            int i = bufferedimage1.getType();
            if (i == 0) {
                i = 6;
            }
            bufferedimage = new BufferedImage(bufferedimage1.getWidth(), bufferedimage1.getHeight(), i);
            Graphics graphics = bufferedimage.getGraphics();
            graphics.drawImage(bufferedimage1, 0, 0, null);
            for (int j = 0; j < 17 && j < this.field_174949_h.size() && j < this.field_174950_i.size(); ++j) {
                InputStream inputstream;
                BufferedImage bufferedimage2;
                String s = this.field_174949_h.get(j);
                MapColor mapcolor = this.field_174950_i.get(j).getMapColor();
                if (s == null || (bufferedimage2 = TextureUtil.readBufferedImage(inputstream = resourceManager.getResource(new ResourceLocation(s)).getInputStream())).getWidth() != bufferedimage.getWidth() || bufferedimage2.getHeight() != bufferedimage.getHeight() || bufferedimage2.getType() != 6) continue;
                for (int k = 0; k < bufferedimage2.getHeight(); ++k) {
                    for (int l = 0; l < bufferedimage2.getWidth(); ++l) {
                        int i1 = bufferedimage2.getRGB(l, k);
                        if ((i1 & 0xFF000000) == 0) continue;
                        int j1 = (i1 & 0xFF0000) << 8 & 0xFF000000;
                        int k1 = bufferedimage1.getRGB(l, k);
                        int l1 = MathHelper.func_180188_d(k1, mapcolor.colorValue) & 0xFFFFFF;
                        bufferedimage2.setRGB(l, k, j1 | l1);
                    }
                }
                bufferedimage.getGraphics().drawImage(bufferedimage2, 0, 0, null);
            }
        }
        catch (IOException ioexception) {
            LOG.error("Couldn't load layered image", (Throwable)ioexception);
            return;
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedimage);
    }
}

