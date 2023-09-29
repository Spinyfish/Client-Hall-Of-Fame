package de.fanta.clickgui.intellij.openables;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Mouse;

import de.fanta.clickgui.intellij.ClickGuiMainPane;
import de.fanta.module.Module;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ClickGuiOpenableModule extends ClickGuiOpenable{

	private Module module;
	private ClickGuiMainPane pane;
	
	public ClickGuiOpenableModule(float x, float y, float baseHeight, float width, Module module, ClickGuiMainPane pane) {
		super(x, y, baseHeight, width, null, Type.CLASS, module.name.replace(" ", "")+".java");
		this.module = module;
		this.pane = pane;
	}
	
	@Override
	public void draw(float mouseX, float mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		boolean hovered = false;
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + baseHeight)
			hovered = true;
		if (hovered && System.currentTimeMillis() - lastMS >= 100) {
			switch (type) {
			case FOLDER:
				break;
			case CLASS:
				try {
					if (Mouse.isButtonDown(0)) {
						this.module.toggle();
					}
					if (Mouse.isButtonDown(1))
						pane.openedModule = this.module;
						pane.init = true;
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
			break;
		case CLASS:
			GlStateManager.pushMatrix();
			GlStateManager.color(1f, 1f, 1f);
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/class.png"));
			drawModalRectWithCustomSizedTexture(x + 3, y + 2, 0, 0, 12, 12, 12, 12);
			GlStateManager.popMatrix();
			MENU_FONT.drawString(name, x + 16, y + 2,
					module.isState() ? Color.white.getRGB() : ClickGuiMainPane.MENU_FONT_COLOR);
			this.width = 16 + MENU_FONT.getStringWidth(name) + 2;
			break;
		default:
			break;
		}
	}

}
