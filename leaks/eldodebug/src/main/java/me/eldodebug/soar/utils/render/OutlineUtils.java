package me.eldodebug.soar.utils.render;

import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.utils.color.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;

public class OutlineUtils {

    public static void drawOutline(float x, float y, float wdith, float height, float width, float radius, int color) {
    	
    	GlStateManager.enableBlend();
    	ColorUtils.setColor(-1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11. GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        wdith *= 2.0D;
        height *= 2.0D;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        ColorUtils.setColor(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        
        int i;
        for (i = 0; i <= 90; i += 3) {
        	GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
        }
        for (i = 90; i <= 180; i += 3) {
        	GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, (y + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
        }
        for (i = 0; i <= 90; i += 3) {
        	GL11.glVertex2d((x + wdith) - radius + Math.sin(i * Math.PI / 180.0D) * radius, (y + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius); 
        }
        for (i = 90; i <= 180; i += 3) {
        	GL11.glVertex2d((x + wdith) - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius); 
        }
        GL11.glEnd();
        GL11.glLineWidth(1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
        

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_FLAT);
        ColorUtils.setColor(-1);
    }
    
    public static void drawGradientOutline(float x, float y, float wdith, float height, float width, float radius, int color, int color2, int color3, int color4) {
    	
    	GlStateManager.enableBlend();
    	ColorUtils.setColor(-1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11. GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        wdith *= 2.0D;
        height *= 2.0D;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        ColorUtils.setColor(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        
        int i;
        for (i = 0; i <= 90; i += 3) {
        	GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
        }
        ColorUtils.setColor(color2);
        for (i = 90; i <= 180; i += 3) {
        	GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, (y + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
        }
        ColorUtils.setColor(color3);
        for (i = 0; i <= 90; i += 3) {
        	GL11.glVertex2d((x + wdith) - radius + Math.sin(i * Math.PI / 180.0D) * radius, (y + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius); 
        }
        ColorUtils.setColor(color4);
        for (i = 90; i <= 180; i += 3) {
        	GL11.glVertex2d((x + wdith) - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius); 
        }
        GL11.glEnd();
        GL11.glLineWidth(1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
        

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_FLAT);
        ColorUtils.setColor(-1);
    }
}
