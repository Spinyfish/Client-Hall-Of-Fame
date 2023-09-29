package de.verschwiegener.atero.util.splashscreen;

import java.awt.Color;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.render.RenderUtil;
import de.verschwiegener.atero.util.splashscreen.splashscreen.RenderCommand;

public class CurveRenderer {
	
	float cx,cy, diameter, heightoffset, widthoffset;
	Color color;
	
	public void setValues(RenderCommand ec) {
		System.out.println("Command: " + ec);
		cx = ec.getX1();
		cy = ec.getY1();
		this.heightoffset = ec.getHeightoffset();
		this.widthoffset = ec.getWidthoffset();
		diameter = ec.getDiameter();
		color = Color.WHITE;
	}
	
	public void draw() {
		drawlefthalf(cx, cy, Color.white);
		Display.update();
	}

	public void drawFinish() {
		double i = Math.PI;
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		GL11.glPointSize(2F);
		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
				color.getAlpha() / 255.0F);

		GL11.glBegin(GL11.GL_POINTS);
		while (i >= 0) {
			i -= 0.02;
			GL11.glVertex2d(cx + (Math.sin(i) * diameter), cy + (Math.cos(i) * diameter));
		}
		Display.update();
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void drawlefthalf(float x, float y, Color color) {
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		GL11.glPointSize(2F);
		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);

		GL11.glBegin(GL11.GL_POINTS);
		
		double i = Math.PI;
		while(i >= 0) {
				i-= 0.02;
				GL11.glVertex2d(x + (Math.sin(i) * diameter), y + (Math.cos(i) * diameter));
				Display.update();
		}
		/*while(i <= Math.PI) {
			i+= 0.01;
			GL11.glVertex2d(x + (Math.sin(i) * diameter), y + (Math.cos(i) * diameter));
		}*/
		Display.update();
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void drawCustom() {
		drawCustomCircle(cx, cy, widthoffset, heightoffset, color);
	}
	
	private void drawCustomCircle(float x, float y, float width, float height, Color color) {
		final double twicePi = Math.PI * 2;
		final int triageAmount = (int) Math.max(4.0D, (Math.max(width, height) * twicePi) / 4.0D);
		
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		GL11.glLineWidth(2F);
		
		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
				color.getAlpha() / 255.0F);
		GL11.glBegin(GL11.GL_POINTS);
		
		double i = 0;
		
		while(i < triageAmount) {
			i += 0.04;
			GL11.glVertex2d(x + (width * Math.cos((i * twicePi) / triageAmount)), y + (height * Math.sin((i * twicePi) / triageAmount)));
			Display.update();
		}
		
		/*for (int i = 0; i <= triageAmount; ++i) {
			System.out.println("Triage: " + triageAmount + " I: " + i);
			GL11.glVertex2d(x + (width * Math.cos((i * twicePi) / triageAmount)), y + (height * Math.sin((i * twicePi) / triageAmount)));
			Display.update();
		}*/
		Display.update();
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	

}
