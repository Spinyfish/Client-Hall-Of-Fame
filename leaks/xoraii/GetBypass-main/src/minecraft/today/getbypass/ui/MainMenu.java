package today.getbypass.ui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import today.getbypass.GetBypass;

public class MainMenu extends GuiScreen{
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(new ResourceLocation("GetBypass/main_menu.png"));
		this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
		
		this.drawGradientRect(0, height - 120, width, height, 0x00000000, 0xff000000);
		
		Gui.drawRect(525, 170, 755, 525, new Color(0, 0, 0, 170).getRGB());
		GlStateManager.pushMatrix();
		GlStateManager.translate(width/2f, height/2f, 0);
		GlStateManager.scale(3, 3, 1);
		GlStateManager.translate(-(width/2), -(height/2), 0);
		this.drawCenteredString(mc.fontRendererObj, GetBypass.instance.name, 641.5f, 300, -1);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(width/2f, height/2f, 0);
		GlStateManager.translate(-(width/2), -(height/2), 0);
		this.drawCenteredString(mc.fontRendererObj, "Made with ❤ by Xoraii, Liticane, G8LOL", 640, 510, -1);
		GlStateManager.popMatrix();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
		this.buttonList.add(new GuiButton(1, 540, height / 2 - 40, "Singleplayer"));
		this.buttonList.add(new GuiButton(2, 540, height / 2 - 15, "Multiplayer"));
		this.buttonList.add(new GuiButton(3, 540, height / 2 + 10, "Settings"));
		this.buttonList.add(new GuiButton(4, 540, height / 2 + 35, "Quit"));
		super.initGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 1) {
			mc.displayGuiScreen(new GuiSelectWorld(this));
		}
		if(button.id == 2) {
			mc.displayGuiScreen(new GuiMultiplayer(this));
		}
		if(button.id == 3) {
			mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
		}
		if(button.id == 4) {
			mc.shutdown();
		}
		super.actionPerformed(button);
	}

}
