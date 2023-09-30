package de.verschwiegener.atero.util.splashscreen;

import java.awt.Color;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.splashscreen.splashscreen.RenderCommand;
import de.verschwiegener.atero.util.splashscreen.splashscreen.RenderType;
import de.verschwiegener.atero.util.splashscreen.splashscreen.SplashScreenRenderUtil;
import net.minecraft.client.Minecraft;

public class LineRenderer {
	
	float startX, startY, endX, endY, x, y;
	double xoffset, yoffset;
	RenderCommand rc;
	TimeUtils timer = new TimeUtils();
	
	private void setValues(float startx, float starty, float endX, float endY, float steps) {
		this.startX = startx;
		this.startY = starty;
		this.x = startx;
		this.y = starty;
		this.endX = endX;
		this.endY = endY;
		xoffset = (endX - startX) / steps;
		yoffset = (endY - startY) / steps;
	}
	public void setValues(RenderCommand rc) {
		this.rc = rc;
		setValues(rc.getX1(), rc.getY1(), rc.getX2(), rc.getY2(), rc.getSteps());
	}
	
	public void drawLineAnimated(Color color, float thicknes) {

		while(!(normalize(x - endX) < 1) || !(normalize(y - endY) < 1)) {
			if(timer.hasReached(20)) {
				timer.reset();
				GL11.glPushMatrix();
				
				GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
				GL11.glLineWidth(2F);
				GL11.glScaled(0.5, 0.5, 0.5);

				GL11.glEnable(GL11.GL_LINE_SMOOTH );
				GL11.glEnable(GL11.GL_POLYGON_SMOOTH );
				GL11.glHint(GL11.GL_LINE_SMOOTH_HINT,GL11.GL_NICEST );
				GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT,GL11.GL_NICEST );
				GL11.glBegin(GL11.GL_LINE_STRIP );
				
				//GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(startX, startY, 0);
				GL11.glVertex3f(x, y, 0);
				GL11.glEnd();

				GL11.glPopMatrix();

				x += xoffset;
				y += yoffset;
			
				//Minecraft.getMinecraft().updateDisplay();
				Display.update();
			}
		}
		
		drawLine(color, thicknes);
		
	}
	
	public void drawLine(Color color, float thicknes) {
		
		GL11.glPushMatrix();
		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
		
		GL11.glLineWidth(2F);
		
		GL11.glScaled(0.5, 0.5, 0.5);
		
		GL11.glEnable(GL11.GL_LINE_SMOOTH );
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH );
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT,GL11.GL_NICEST );
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT,GL11.GL_NICEST );
		GL11.glBegin(GL11.GL_LINE_STRIP );
		
		
		GL11.glVertex2f(startX, startY);
		GL11.glVertex2f(endX, endY);
		
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
	}

	
	public double normalize(double in) {
		if(in < 0) {
			return in /-1;
		}
		return in;
	}
	
}
