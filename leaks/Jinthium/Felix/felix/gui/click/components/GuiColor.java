package felix.gui.click.components;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JColorChooser;

import org.lwjgl.input.Mouse;

import felix.Client;
import felix.gui.click.Panel;
import felix.gui.click.util.ClickUtil;
import felix.util.other.Logger;
import felix.util.visual.Colors;
import felix.util.visual.RenderUtil;
import felix.value.impl.ColorValue;

public class GuiColor implements GuiComponent {
	
	private ColorValue setting;
	
	private int posX, posY;

	private ColorIndicator ci = new ColorIndicator(posX, posY);
	
	public GuiColor(ColorValue setting) {
		this.setting = setting;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		
	}

	@Override
	public void keyTyped(int keyCode, char typedChar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 101;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 93;
	}

	@Override
	public boolean allowScroll() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		renderGUI(posX, posY);
		this.posY = posY;
		this.posX = posX;
		boolean isOver = ClickUtil.isHovered(posX + 4, posY + 14, 92, 76, mouseX, mouseY);
		RenderUtil.drawBorderedRect(ci.getX(), ci.getY(), 5, 5, 0.8, Panel.black100, Color.TRANSLUCENT);
		if (isOver && Mouse.isButtonDown(0)) {
			ci.setX(mouseX - 2);
			ci.setY(mouseY - 3);
		}
		if (Mouse.isButtonDown(0) && isOver) {
			final Point p = MouseInfo.getPointerInfo().getLocation();
			Robot robot = null;
			try {
				robot = new Robot();
			} 
			catch (AWTException e) {
				e.printStackTrace();
			}
			Color c = robot.getPixelColor(p.x, p.y);
			setting.setValue(c.getRGB());
		}
	}
	
	public void renderGUI(int posX, int posY) {
		int width = 16;
		
		Panel.fR.drawStringWithShadow(setting.getLabel(), posX + 2, posY + 1, Panel.fontColor);
		
		RenderUtil.drawHorizontalGradient(posX + 2 + width + width + width + width + width, posY + 11, width, 80, Color.magenta.getRGB(), Color.red.getRGB());
		RenderUtil.drawHorizontalGradient(posX + 2 + width + width + width + width, posY + 11, width, 80, Color.blue.getRGB(), Color.magenta.getRGB());
		RenderUtil.drawHorizontalGradient(posX + 2 + width + width + width, posY + 11, width, 80, Color.cyan.getRGB(), Color.blue.getRGB());
		RenderUtil.drawHorizontalGradient(posX + 2 + width + width, posY + 11, width, 80, Color.green.getRGB(), Color.cyan.getRGB());
		RenderUtil.drawHorizontalGradient(posX + 2 + width, posY + 11, width, 80, Color.yellow.getRGB(), Color.green.getRGB());
		RenderUtil.drawHorizontalGradient(posX + 2, posY + 11, width, 80, Color.red.getRGB(), Color.yellow.getRGB());
		
		RenderUtil.drawVerticalGradient(posX + 2, posY + 3, 97, 87, new Color(255, 255, 255, 255).getRGB(), Color.TRANSLUCENT);
		
		RenderUtil.drawBorderedRect(posX + 2, posY + 11, 97, 80, 1, Panel.black195, Color.TRANSLUCENT);
	}
	
	public void setSetting(ColorValue setting) {
		this.setting = setting;
	}
	
	public ColorValue getSetting() {
		return setting;
	}
}
