package de.fanta.clickgui.defaultgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.clickgui.defaultgui.components.ConfigBox;
import de.fanta.clickgui.defaultgui.components.FontBox;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.FileUtil;

import java.io.IOException;

public class ClickGui extends GuiScreen {
	public boolean activ;

	private final Minecraft mc = Minecraft.getMinecraft();
	private final Panel panels;
	private final ConfigBox configBox;

	de.fanta.utils.Translate translate;

	public ClickGui(boolean activ, float x, float y) {
		
		panels = new Panel(x, y);
		configBox = new ConfigBox(x, y + 200);
		this.activ = activ;
		
	}

	private void scissorBox(float x, float y, float width, float length) {
		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
		int scaleFactor = scale.getScaleFactor();
		GL11.glScissor((int) (x * scaleFactor), (int) ((scale.getScaledHeight() - length) * scaleFactor),
				(int) ((width - x) * scaleFactor), (int) ((length - y) * scaleFactor));
	}

	public void drawMenu(float mouseX, float mouseY) {
		if (!activ)
			return;

		ScaledResolution scaledRes = new ScaledResolution(mc);

		panels.drawPanel(mouseX, mouseY);
		configBox.drawConfigBox(mouseX, mouseY);
		this.translate = new de.fanta.utils.Translate(0.0f, 0.0f);
		if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
			if (mc.entityRenderer.theShaderGroup != null) {
				mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			}
			mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		try {
			panels.panelClicked(mouseX, mouseY, mouseButton);
			configBox.configBoxClicked(mouseX, mouseY, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (IOException ignored) {
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		panels.panelReleased(mouseX, mouseY, state);
		configBox.configBoxReleased(mouseX, mouseY, state);
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		panels.panelHandleInput();
		super.handleMouseInput();
	}

	@Override
	public void onGuiClosed() {
		if (mc.entityRenderer.theShaderGroup != null) {
			mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			mc.entityRenderer.theShaderGroup = null;
		}
		this.activ = false;

		super.onGuiClosed();
	}
}
