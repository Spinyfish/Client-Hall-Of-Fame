package haptic.util.render;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetBoolean;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class DrawUtil extends GuiScreen {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static DrawUtil instance = new DrawUtil();
	
	private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
	
	public static void drawRoundedRect(double x, double y, double x1, double y1, double radius, int color) {
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		x *= 2.0D;
		y *= 2.0D;
		x1 *= 2.0D;
    	y1 *= 2.0D;
    	GL11.glEnable(3042);
    	GL11.glDisable(3553);
    	setColor(color);
    	GL11.glEnable(2848);
    	GL11.glBegin(9);
    	int i;
    	for (i = 0; i <= 90; i += 3)
    		GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
    	for (i = 90; i <= 180; i += 3)
    		GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
    	for (i = 0; i <= 90; i += 3)
    		GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius); 
    	for (i = 90; i <= 180; i += 3)
    		GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius); 
    	GL11.glEnd();
    	GL11.glEnable(3553);
    	GL11.glDisable(3042);
    	GL11.glDisable(2848);
    	GL11.glDisable(3042);
    	GL11.glEnable(3553);
    	GL11.glScaled(2.0D, 2.0D, 2.0D);
    	GL11.glPopAttrib();
    	GlStateManager.enableTexture2D();
    	GlStateManager.disableBlend();
	}
  
	public static void drawBorderedRoundedRect(float x, float y, float x1, float y1, float borderSize, int borderC, int insideC) {
		drawRoundedRect(x, y, x1, y1, borderSize, borderC);
		drawRoundedRect((x + 0.5F), (y + 0.5F), (x1 - 0.5F), (y1 - 0.5F), borderSize, insideC);
	}
  
	public static void drawBorderedRoundedRect(float x, float y, float x1, float y1, float radius, float borderSize, int borderC, int insideC) {
		drawRoundedRect(x, y, x1, y1, radius, borderC);
		drawRoundedRect((x + borderSize), (y + borderSize), (x1 - borderSize), (y1 - borderSize), radius, insideC);
	}
  
	public static void setColor(int color) {
		float a = (color >> 24 & 0xFF) / 255.0F;
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		GL11.glColor4f(r, g, b, a);
	}
	
	public static void renderNameTag(final String string, final double x, final double y, final double z) {
		final RenderManager renderManager = mc.getRenderManager();
		
		glPushMatrix();
		glTranslated(x - renderManager.renderPosX, y - renderManager.renderPosY, z - renderManager.renderPosZ);
		glNormal3f(0F, 1F, 0F);
      	glRotatef(-mc.getRenderManager().playerViewY, 0F, 1F, 0F);
      	glRotatef(mc.getRenderManager().playerViewX, 1F, 0F, 0F);
      	glScalef(-0.05F, -0.05F, 0.05F);
      	setGlCap(GL_LIGHTING, false);
      	setGlCap(GL_DEPTH_TEST, false);
      	setGlCap(GL_BLEND, true);
      	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
      	
      	final int width = mc.fontRendererObj.getStringWidth(string) / 2;
      	
      	Gui.drawRect(-width - 1, -1, width + 1, mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
      	mc.fontRendererObj.drawStringWithShadow(string, -width, 1.5F, Color.WHITE.getRGB());
      	
      	resetCaps();
      	glColor4f(1F, 1F, 1F, 1F);
      	glPopMatrix();
	}
	
	public static void setGlCap(final int cap, final boolean state) {
        glCapMap.put(cap, glGetBoolean(cap));
        setGlState(cap, state);
    }
	
	public static void resetCaps() {
        glCapMap.forEach(DrawUtil::setGlState);
        glCapMap.clear();
    }
	
	public static void setGlState(final int cap, final boolean state) {
        if (state)
            glEnable(cap);
        else
            glDisable(cap);
    }

	public static void drawHollowRect(float x, float y, float w, float h, int color) {
		Gui.drawHorizontalLine(x, w, y, color);
		Gui.drawHorizontalLine(x,  w, h, color);

		Gui.drawVerticalLine(x, h, y, color);
		Gui.drawVerticalLine(w, h, y, color);

	}
	
	public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }
}
