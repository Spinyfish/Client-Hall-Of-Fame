package me.r.touchgrass.ui.clickgui;

import java.io.IOException;
import java.util.ArrayList;

import me.r.touchgrass.file.files.ClickGuiConfig;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.ui.clickgui.component.Component;
import me.r.touchgrass.ui.clickgui.component.Frame;
import me.r.touchgrass.utils.BlurUtil;
import me.r.touchgrass.utils.ParticleGenerator;
import me.r.touchgrass.utils.ReflectionUtil;
import me.r.touchgrass.touchgrass;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

public class ClickGui extends GuiScreen {

	public static ArrayList<Frame> frames;
	public static final int color = 0x99cfdcff;
	ParticleGenerator particleGenerator = new ParticleGenerator(100, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);


	public ClickGui() {
		frames = new ArrayList<>();
		int frameX = 5;
		for(Category category : Category.values()) {
			Frame frame = new Frame(category);
			frame.setX(frameX);
			frames.add(frame);
			frameX += frame.getWidth() + 1;
		}
	}

	@Override
	public void initGui() {

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		boolean particles = touchgrass.getClient().settingsManager.getSettingByName("Particles").isEnabled();
		boolean blur = touchgrass.getClient().settingsManager.getSettingByName("Blur").isEnabled();
		drawRect(0, 0, this.width, this.height, 0x66101010);
		if (blur) {
			BlurUtil.blurAll(0.1f);
		}
		for (Frame frame : frames) {
			frame.renderFrame(this.fontRendererObj);
			frame.updatePosition(mouseX, mouseY);
			for (Component comp : frame.getComponents()) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
		if(particles) {
			particleGenerator.drawParticles(0, 0, false);
		}
	}

	@Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
		for(Frame frame : frames) {
			if(frame.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
				frame.setDrag(true);
				frame.dragX = mouseX - frame.getX();
				frame.dragY = mouseY - frame.getY();
			}
			if(frame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
				frame.setOpen(!frame.isOpen());
			}
			if(frame.isOpen()) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.mouseClicked(mouseX, mouseY, mouseButton);
					}
				}
			}
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		for(Frame frame : frames) {
			if(frame.isOpen() && keyCode != 1) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.keyTyped(typedChar, keyCode);
					}
				}
			}
		}
		if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
	}

	@Override
	public void onGuiClosed() {
		if(this.mc.entityRenderer.getShaderGroup() != null) {
			this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
			try {
				ReflectionUtil.theShaderGroup.set(Minecraft.getMinecraft().entityRenderer, null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(!touchgrass.getClient().panic) {
			ClickGuiConfig clickGuiConfig = new ClickGuiConfig();
			clickGuiConfig.saveConfig();
		}

		super.onGuiClosed();
	}

	
	@Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
		for(Frame frame : frames) {
			frame.setDrag(false);
		}
		for(Frame frame : frames) {
			if(frame.isOpen()) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.mouseReleased(mouseX, mouseY, state);
					}
				}
			}
		}
	}

	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
