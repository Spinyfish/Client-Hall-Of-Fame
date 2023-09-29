package haptic.ui.menu;

import java.awt.Color;

import haptic.Haptic;
import haptic.altlogin.GuiAltLogin;
import haptic.util.misc.TimerUtil;
import haptic.util.render.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class HapticMainMenu extends GuiScreen {
	
	private TimerUtil timer = new TimerUtil(); // that was for the animation
	private double posYName, posYMenu; // that was also for the animation
	
	public HapticMainMenu() {
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		mc.displayGuiScreen(new GuiMainMenu()); // yes
	}
	
}
