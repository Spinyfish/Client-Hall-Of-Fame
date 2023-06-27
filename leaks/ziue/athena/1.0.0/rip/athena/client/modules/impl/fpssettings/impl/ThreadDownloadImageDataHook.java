package rip.athena.client.modules.impl.fpssettings.impl;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;
import javax.imageio.*;
import java.io.*;

public class ThreadDownloadImageDataHook extends SimpleTexture
{
    public ThreadDownloadImageDataHook(final ResourceLocation textureResourceLocation) {
        super(textureResourceLocation);
    }
    
    public static void getImprovedCacheLoading(final ThreadDownloadImageData data) {
        new Thread(() -> {
            if (data.imageThread == null) {
                if (data.cacheFile != null && data.cacheFile.isFile()) {
                    ThreadDownloadImageData.logger.debug("Loading http texture from local cache ({})", new Object[] { data.cacheFile });
                    try {
                        data.bufferedImage = ImageIO.read(data.cacheFile);
                        if (data.imageBuffer != null) {
                            data.setBufferedImage(data.imageBuffer.parseUserSkin(data.bufferedImage));
                        }
                    }
                    catch (IOException ioexception) {
                        ThreadDownloadImageData.logger.error("Couldn't load skin " + data.cacheFile, (Throwable)ioexception);
                        data.loadTextureFromServer();
                    }
                }
                else {
                    data.loadTextureFromServer();
                }
            }
        }).start();
    }
}
