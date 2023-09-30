package markgg.ui.notifs;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public class Notification {
	private NotificationType type;
	private String title;
	private String messsage;
	private long start;

	private long fadedIn;
	private long fadeOut;
	private long end;

	public Notification(NotificationType type, String messsage, int length) {
		this.type = type;
		this.messsage = messsage;

		fadedIn = 200 * length;
		fadeOut = fadedIn + 500 * length;
		end = fadeOut + fadedIn;
	}

	public void show() {
		start = System.currentTimeMillis();
	}

	public Minecraft mc = Minecraft.getMinecraft();

	public boolean isShown() {
		return getTime() <= end;
	}

	private long getTime() {
		return System.currentTimeMillis() - start;
	}

	public void render() {
		double offset = 0;
		int width = 120;
		int height = 30;
		long time = getTime();

		if (time < fadedIn) {
			offset = Math.tanh(time / (double) (fadedIn) * 3.0) * width;
		} else if (time > fadeOut) {
			offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 3.0) * width);
		} else {
			offset = width;
		}

		Color color = new Color(0, 0, 0, 220);
		Color color1;

		if (type == NotificationType.ENABLE)
			color1 = new Color(80, 200, 120);
		else if (type == NotificationType.DISABLE)
			color1 = new Color(255, 191, 0);
		else {
			color1 = new Color(238, 75, 43);
		}

		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		drawRect(sr.getScaledWidth() - offset - 10, sr.getScaledHeight() - height, sr.getScaledWidth(), sr.getScaledHeight() - 15, -1879048192);
		drawRect(sr.getScaledWidth() - offset - 10, sr.getScaledHeight() - height, sr.getScaledWidth() - offset - 9, sr.getScaledHeight() - 15, color1.getRGB());

		fontRenderer.drawString(messsage, (int) (sr.getScaledWidth() - offset - 2 / fontRenderer.getStringWidth(messsage)) - 5, sr.getScaledHeight() - 26, -1);
	}

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.startDrawingQuads();
		worldrenderer.addVertex(left, bottom, 0.0D);
		worldrenderer.addVertex(right, bottom, 0.0D);
		worldrenderer.addVertex(right, top, 0.0D);
		worldrenderer.addVertex(left, top, 0.0D);
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRect(int mode, double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.startDrawingQuads();
		worldrenderer.addVertex(left, bottom, 0.0D);
		worldrenderer.addVertex(right, bottom, 0.0D);
		worldrenderer.addVertex(right, top, 0.0D);
		worldrenderer.addVertex(left, top, 0.0D);
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
}
