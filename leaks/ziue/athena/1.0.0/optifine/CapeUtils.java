package optifine;

import net.minecraft.client.entity.*;
import org.apache.commons.io.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import java.io.*;
import net.minecraft.client.renderer.texture.*;
import java.awt.image.*;
import java.awt.*;

public class CapeUtils
{
    public static void downloadCape(final AbstractClientPlayer p_downloadCape_0_) {
        final String s = p_downloadCape_0_.getNameClear();
        if (s != null && !s.isEmpty()) {
            final String s2 = "http://s.optifine.net/capes/" + s + ".png";
            final String s3 = FilenameUtils.getBaseName(s2);
            final ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s3);
            final TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            final ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);
            if (itextureobject != null && itextureobject instanceof ThreadDownloadImageData) {
                final ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData)itextureobject;
                if (threaddownloadimagedata.imageFound != null) {
                    if (threaddownloadimagedata.imageFound) {
                        p_downloadCape_0_.setLocationOfCape(resourcelocation);
                    }
                    return;
                }
            }
            final IImageBuffer iimagebuffer = new IImageBuffer() {
                ImageBufferDownload ibd = new ImageBufferDownload();
                
                @Override
                public BufferedImage parseUserSkin(final BufferedImage image) {
                    return CapeUtils.parseCape(image);
                }
                
                @Override
                public void skinAvailable() {
                    p_downloadCape_0_.setLocationOfCape(resourcelocation);
                }
            };
            final ThreadDownloadImageData threaddownloadimagedata2 = new ThreadDownloadImageData(null, s2, null, iimagebuffer);
            threaddownloadimagedata2.pipeline = true;
            texturemanager.loadTexture(resourcelocation, threaddownloadimagedata2);
        }
    }
    
    public static BufferedImage parseCape(final BufferedImage p_parseCape_0_) {
        int i = 64;
        int j = 32;
        for (int k = p_parseCape_0_.getWidth(), l = p_parseCape_0_.getHeight(); i < k || j < l; i *= 2, j *= 2) {}
        final BufferedImage bufferedimage = new BufferedImage(i, j, 2);
        final Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(p_parseCape_0_, 0, 0, null);
        graphics.dispose();
        return bufferedimage;
    }
}
