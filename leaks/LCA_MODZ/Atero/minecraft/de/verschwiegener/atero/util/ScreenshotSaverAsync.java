package de.verschwiegener.atero.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.util.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ScreenshotSaverAsync implements Runnable{
    
    private int width;
    
    private int height;
    
    private String captureTime;
    
    private int[] pixels;
    
    private Framebuffer frameBuffer;
    
    public static void saveScrenShotAsync(int width, int height, int[] pixels, Framebuffer buffer) {
	ScreenshotSaverAsync saver = new ScreenshotSaverAsync();
	saver.width = width;
	saver.height = height;
	saver.captureTime = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());
	saver.pixels = pixels;
	saver.frameBuffer = buffer;
	(new Thread(saver)).start();
    }

    @Override
    public void run() {
	BufferedImage image = null;
	if (OpenGlHelper.isFramebufferEnabled()) {
	    image = new BufferedImage(this.frameBuffer.framebufferWidth, this.frameBuffer.framebufferHeight, 1);
	    int diff = this.frameBuffer.framebufferTextureHeight - this.frameBuffer.framebufferHeight;
	    for (int i = diff; i < this.frameBuffer.framebufferTextureHeight; i++) {
		for (int j = 0; j < this.frameBuffer.framebufferWidth; j++) {
		    int pixel = this.pixels[i * this.frameBuffer.framebufferTextureWidth + j];
		    image.setRGB(j, i - diff, pixel);
		}
	    }
	} else {
	    image = new BufferedImage(this.width, this.height, 1);
	    image.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
	}
	File ssDir = new File("screenshots");
	File ssFile = new File("screenshots", this.captureTime + ".png");
	int iterator = 0;
	while (ssFile.exists()) {
	    iterator++;
	    ssFile = new File("screenshots", this.captureTime + "_" + iterator + ".png");
	}
	
	try {
	    ssDir.mkdirs();
	    ImageIO.write(image, "png", ssFile);
	    IChatComponent ichatcomponent = new ChatComponentText(ssFile.getName());
            ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, ssFile.getAbsolutePath()));
            ichatcomponent.getChatStyle().setUnderlined(Boolean.valueOf(true));
            
            ChatUtil.addIChatComponent(new ChatComponentTranslation("screenshot.success", new Object[] {ichatcomponent}));
	} catch (IOException e) {
	    e.printStackTrace();
	    ChatUtil.addIChatComponent(new ChatComponentTranslation("screenshot.failure", new Object[] {e.getMessage()}));
	}
    }
    
    private static IntBuffer pixelBuffer;
    
    private static int[] pixelValues;
    
    public static void takeScreenshot() {
      Minecraft mc = Minecraft.getMinecraft();
      Framebuffer frameBuffer = mc.getFramebuffer();
      int screenshotWidth = mc.displayWidth;
      int screenshotHeight = mc.displayHeight;
      if (OpenGlHelper.isFramebufferEnabled()) {
        screenshotWidth = frameBuffer.framebufferTextureWidth;
        screenshotHeight = frameBuffer.framebufferTextureHeight;
      } 
      int targetCapacity = screenshotWidth * screenshotHeight;
      if (pixelBuffer == null || pixelBuffer.capacity() < targetCapacity) {
        pixelBuffer = BufferUtils.createIntBuffer(targetCapacity);
        pixelValues = new int[targetCapacity];
      } 
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);
      pixelBuffer.clear();
      if (OpenGlHelper.isFramebufferEnabled()) {
        GlStateManager.bindTexture(frameBuffer.framebufferTexture);
        GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
      } else {
        GL11.glReadPixels(0, 0, screenshotWidth, screenshotHeight, 32993, 33639, pixelBuffer);
      } 
      pixelBuffer.get(pixelValues);
      TextureUtil.processPixelValues(pixelValues, screenshotWidth, screenshotHeight);
      int[] pixelCopy = new int[pixelValues.length];
      System.arraycopy(pixelValues, 0, pixelCopy, 0, pixelValues.length);
      saveScrenShotAsync(screenshotWidth, screenshotHeight, pixelCopy, frameBuffer);
    }

}
