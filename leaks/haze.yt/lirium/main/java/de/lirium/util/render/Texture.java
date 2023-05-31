package de.lirium.util.render;

import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

	private final BufferedImage bufferedImage;
	private final int width, height, id;
	private final boolean smooth;
	private boolean initialized;
	private int delay;

	public Texture(final BufferedImage bufferedImage, final boolean smooth) {
		this.bufferedImage = bufferedImage;
		this.width = bufferedImage.getWidth();
		this.height = bufferedImage.getHeight();
		this.id = glGenTextures();
		this.smooth = smooth;
	}

	public Texture(final BufferedImage bufferedImage) {
		this(bufferedImage, false);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
		if (!initialized) {
			try {
				this.initTexture();
			} catch (Exception ignored) {
			}
		}
	}

	private void initTexture() {
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, smooth ? GL_LINEAR_MIPMAP_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, smooth ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
		bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(bufferedImage.getWidth() * bufferedImage.getHeight() * 4);
		for (int y1 = 0; y1 < bufferedImage.getHeight(); y1++) {
			for (int x1 = 0; x1 < bufferedImage.getWidth(); x1++) {
				int pixel = pixels[y1 * bufferedImage.getWidth() + x1];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D);
		initialized = true;
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getId() {
		return id;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
}