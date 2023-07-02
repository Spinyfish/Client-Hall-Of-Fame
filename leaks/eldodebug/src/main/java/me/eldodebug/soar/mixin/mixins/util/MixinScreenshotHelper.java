package me.eldodebug.soar.mixin.mixins.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;

@Mixin(ScreenShotHelper.class)
public class MixinScreenshotHelper {

	@Shadow
	private static int[] pixelValues;
	
	@Shadow
	private static IntBuffer pixelBuffer;

	@Shadow
	@Final
	private static Logger logger;
	
	@Shadow
	private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
		throw new UnsupportedOperationException();
	}
	
	@Overwrite
	public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer) {
		try {
			File file1 = new File(gameDirectory, "screenshots");
			file1.mkdir();

			if (OpenGlHelper.isFramebufferEnabled()) {
				width = buffer.framebufferTextureWidth;
				height = buffer.framebufferTextureHeight;
			}

			int i = width * height;

			if (pixelBuffer == null || pixelBuffer.capacity() < i) {
				pixelBuffer = BufferUtils.createIntBuffer(i);
				pixelValues = new int[i];
			}

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();

			if (OpenGlHelper.isFramebufferEnabled()) {
				GlStateManager.bindTexture(buffer.framebufferTexture);
				GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
						(IntBuffer) pixelBuffer);
			} else {
				GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
						(IntBuffer) pixelBuffer);
			}

			pixelBuffer.get(pixelValues);
			TextureUtil.processPixelValues(pixelValues, width, height);
			BufferedImage bufferedimage = null;

			if (OpenGlHelper.isFramebufferEnabled()) {
				bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
				int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

				for (int k = j; k < buffer.framebufferTextureHeight; ++k) {
					for (int l = 0; l < buffer.framebufferWidth; ++l) {
						bufferedimage.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
					}
				}
			} else {
				bufferedimage = new BufferedImage(width, height, 1);
				bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
			}

			File file2;

			if (screenshotName == null) {
				file2 = getTimestampedPNGFileForDirectory(file1);
			} else {
				file2 = new File(file1, screenshotName);
			}
			final BufferedImage image = bufferedimage;
			new Thread(() -> {
				try {
					ImageIO.write(image, "png", (File) file2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
			IChatComponent ichatcomponent = new ChatComponentText(file2.getName());
			ichatcomponent.getChatStyle()
					.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
			ichatcomponent.getChatStyle().setUnderlined(Boolean.valueOf(true));
			return new ChatComponentTranslation("screenshot.success", new Object[] { ichatcomponent });
		} catch (Exception exception) {
			logger.warn((String) "Couldn\'t save screenshot", (Throwable) exception);
			return new ChatComponentTranslation("screenshot.failure", new Object[] { exception.getMessage() });
		}
	}
}
