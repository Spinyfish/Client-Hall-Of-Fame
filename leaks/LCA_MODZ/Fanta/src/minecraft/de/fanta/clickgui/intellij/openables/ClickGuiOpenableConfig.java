package de.fanta.clickgui.intellij.openables;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.clickgui.intellij.ClickGuiMainPane;
import de.fanta.module.Module;
import de.fanta.utils.FileUtil;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ClickGuiOpenableConfig extends ClickGuiOpenable{

	private String config;
	private ClickGuiMainPane pane;
	
	private File dir = new File(
			Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/" + "configs" + "/");
	private boolean online;
	
	public ClickGuiOpenableConfig(float x, float y, float baseHeight, float width, String config, ClickGuiMainPane pane, boolean online) {
		super(x, y, baseHeight, width, null, Type.CLASS, config);
		this.config = config;
		this.pane = pane;
		this.online = online;
	}
	
	@Override
	public void draw(float mouseX, float mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		boolean hovered = false;
		String useString = !config.endsWith(".txt") ? config : config.substring(0, config.length()-4);
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + baseHeight)
			hovered = true;
		if (hovered && System.currentTimeMillis() - lastMS >= 100) {
			switch (type) {
			case FOLDER:
				break;
			case CLASS:
				try {
					if (Mouse.isButtonDown(0)) {
						if(!this.online) {
							if(Arrays.asList(dir.list()).contains(useString.endsWith(".txt") ? useString : useString+".txt")) {
								System.out.println("test");
								FileUtil.loadValues(useString, true, false);
								lastMS = System.currentTimeMillis();							
							}
						}else {
							try {
								FileUtil.loadValues(useString, true, true);
							} catch (Exception e) {
							}
						}
					}
					if (Mouse.isButtonDown(1)) {
						if(!this.online) {
							if(Arrays.asList(dir.list()).contains(useString.endsWith(".txt") ? useString : useString+".txt")) {
								FileUtil.loadValues(useString, true, false);
								lastMS = System.currentTimeMillis();							
							}
						}else {
							try {
								FileUtil.loadValues(useString, true, true);
							} catch (Exception e) {
							}
						}
					}
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
			if(!online) {
				MENU_FONT.drawString(name, x + 16, y + 2,
						Arrays.asList(dir.list()).contains(useString.endsWith(".txt") ? useString : useString+".txt") ? Color.white.getRGB() : Color.red.getRGB());
			}else {
				MENU_FONT.drawString(name, x + 16, y + 2,
						ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR);
			}
			this.width = 16 + MENU_FONT.getStringWidth(name) + 2;
			break;
		default:
			break;
		}
	}

}
