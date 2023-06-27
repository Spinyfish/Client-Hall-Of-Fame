package net.minecraft.client.renderer;

import java.awt.image.*;

public interface IImageBuffer
{
    BufferedImage parseUserSkin(final BufferedImage p0);
    
    void skinAvailable();
}
