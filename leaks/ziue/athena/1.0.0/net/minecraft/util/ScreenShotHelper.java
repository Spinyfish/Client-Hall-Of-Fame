package net.minecraft.util;

import java.nio.*;
import java.io.*;
import net.minecraft.client.shader.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import javax.imageio.*;
import java.awt.image.*;
import net.minecraft.event.*;
import java.util.*;
import org.apache.logging.log4j.*;
import java.text.*;

public class ScreenShotHelper
{
    private static final Logger logger;
    private static final DateFormat dateFormat;
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;
    
    public static IChatComponent saveScreenshot(final File gameDirectory, final int width, final int height, final Framebuffer buffer) {
        return saveScreenshot(gameDirectory, null, width, height, buffer);
    }
    
    public static IChatComponent saveScreenshot(final File gameDirectory, final String screenshotName, int width, int height, final Framebuffer buffer) {
        try {
            final File file1 = new File(gameDirectory, "screenshots");
            file1.mkdir();
            if (OpenGlHelper.isFramebufferEnabled()) {
                width = buffer.framebufferTextureWidth;
                height = buffer.framebufferTextureHeight;
            }
            final int i = width * height;
            if (ScreenShotHelper.pixelBuffer == null || ScreenShotHelper.pixelBuffer.capacity() < i) {
                ScreenShotHelper.pixelBuffer = BufferUtils.createIntBuffer(i);
                ScreenShotHelper.pixelValues = new int[i];
            }
            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            ScreenShotHelper.pixelBuffer.clear();
            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(buffer.framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, ScreenShotHelper.pixelBuffer);
            }
            else {
                GL11.glReadPixels(0, 0, width, height, 32993, 33639, ScreenShotHelper.pixelBuffer);
            }
            ScreenShotHelper.pixelBuffer.get(ScreenShotHelper.pixelValues);
            TextureUtil.processPixelValues(ScreenShotHelper.pixelValues, width, height);
            BufferedImage bufferedimage = null;
            if (OpenGlHelper.isFramebufferEnabled()) {
                bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
                int k;
                for (int j = k = buffer.framebufferTextureHeight - buffer.framebufferHeight; k < buffer.framebufferTextureHeight; ++k) {
                    for (int l = 0; l < buffer.framebufferWidth; ++l) {
                        bufferedimage.setRGB(l, k - j, ScreenShotHelper.pixelValues[k * buffer.framebufferTextureWidth + l]);
                    }
                }
            }
            else {
                bufferedimage = new BufferedImage(width, height, 1);
                bufferedimage.setRGB(0, 0, width, height, ScreenShotHelper.pixelValues, 0, width);
            }
            File file2;
            if (screenshotName == null) {
                file2 = getTimestampedPNGFileForDirectory(file1);
            }
            else {
                file2 = new File(file1, screenshotName);
            }
            ImageIO.write(bufferedimage, "png", file2);
            final IChatComponent ichatcomponent = new ChatComponentText(file2.getName());
            ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
            ichatcomponent.getChatStyle().setUnderlined(true);
            return new ChatComponentTranslation("screenshot.success", new Object[] { ichatcomponent });
        }
        catch (Exception exception) {
            ScreenShotHelper.logger.warn("Couldn't save screenshot", (Throwable)exception);
            return new ChatComponentTranslation("screenshot.failure", new Object[] { exception.getMessage() });
        }
    }
    
    private static File getTimestampedPNGFileForDirectory(final File gameDirectory) {
        final String s = ScreenShotHelper.dateFormat.format(new Date()).toString();
        int i = 1;
        File file1;
        while (true) {
            file1 = new File(gameDirectory, s + ((i == 1) ? "" : ("_" + i)) + ".png");
            if (!file1.exists()) {
                break;
            }
            ++i;
        }
        return file1;
    }
    
    static {
        logger = LogManager.getLogger();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    }
}
