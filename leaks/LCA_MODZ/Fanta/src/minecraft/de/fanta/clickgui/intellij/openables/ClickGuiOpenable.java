package de.fanta.clickgui.intellij.openables;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Mouse;

import de.fanta.clickgui.intellij.ClickGuiMainPane;
import de.fanta.gui.font.ClientFont;
import de.fanta.gui.font.ClientFont.FontType;
import de.fanta.gui.font.GlyphPageFontRenderer;
import de.fanta.module.Module;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

public class ClickGuiOpenable extends Gui {

	protected float x, y, baseHeight, width, height;
	protected List<ClickGuiOpenable> children;
	protected boolean extended = false;
	protected Type type;
	protected String name;

	public ClickGuiOpenable(float x, float y, float baseHeight, float width, List<ClickGuiOpenable> children, Type type,
			String name) {
		this.x = x;
		this.y = y;
		this.baseHeight = baseHeight;
		this.width = width;
		this.height = this.baseHeight;
		this.children = children;
		this.type = type;
		this.name = name;
		this.lastMS = System.currentTimeMillis();
	}

	protected long lastMS = System.currentTimeMillis();
	protected Runnable runnable;
	public static final GlyphPageFontRenderer MENU_FONT = ClientFont.font(19, FontType.ARIAL, true);

	public void draw(float mouseX, float mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		boolean hovered = false;
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + baseHeight)
			hovered = true;
		if (hovered && System.currentTimeMillis() - lastMS >= 100) {
			switch (type) {
			case FOLDER:
				if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
					extended = !extended;
					lastMS = System.currentTimeMillis();
				break;
			case CLASS:
				try {
					if (Mouse.isButtonDown(0)) {}
					if (Mouse.isButtonDown(1))
						runnable.run();
						lastMS = System.currentTimeMillis();
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}
		}
		switch (type) {
		case FOLDER:
			int add = 0;
			for (ClickGuiOpenable child : this.children) {
				child.x = this.x + 10;
				child.y = this.y + baseHeight + (add);
				if (extended) {
					child.draw(mouseX, mouseY);
				}
				add += child.getHeight();
			}
			this.height = extended ? this.baseHeight + add : this.baseHeight;
			
			if (extended) {
				RenderUtil.draw2dLine(x + 3, y + 5, x + 7, y + 9, Color.white);
				RenderUtil.draw2dLine(x + 11, y + 5, x + 7, y + 9, Color.white);
			} else {
				RenderUtil.draw2dLine(x + 5, y + 3, x + 9, y + 7, Color.white);
				RenderUtil.draw2dLine(x + 5, y + 11, x + 9, y + 7, Color.white);
			}
			GlStateManager.color(1f, 1f, 1f);
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/folder.png"));
			drawModalRectWithCustomSizedTexture(x + 15, y + 2, 0, 0, 12, 12, 12, 12);
			MENU_FONT.drawString(name, x + 28, y + 2, Color.white.getRGB());
			this.width = 28 + MENU_FONT.getStringWidth(name) + 2;
			break;
		case CLASS:
			GlStateManager.pushMatrix();
			GlStateManager.color(1f, 1f, 1f);
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/class.png"));
			drawModalRectWithCustomSizedTexture(x + 3, y + 2, 0, 0, 12, 12, 12, 12);
			GlStateManager.popMatrix();
			MENU_FONT.drawString(name, x + 16, y + 2,
					Color.white.getRGB());
			this.width = 16 + MENU_FONT.getStringWidth(name) + 2;
			break;
		default:
			break;
		}
	}

	public void setRunnable(Runnable runner) {
		this.runnable = runner;
	}

	public enum Type {
		FOLDER, CLASS;
	}

	public float getBaseHeight() {
		return baseHeight;
	}

	public List<ClickGuiOpenable> getChildren() {
		return children;
	}

	public float getHeight() {
		return height;
	}

	public long getLastMS() {
		return lastMS;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public float getWidth() {
		return width;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

}
