package me.wavelength.baseclient.overlay;

import java.awt.Color;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.gui.clickgui.ClickGui;
import me.wavelength.baseclient.gui.clickgui.GuiBind;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Time;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class HotbarOverlay extends EventListener {

	public HotbarOverlay() {
		BaseClient.instance.getEventManager().registerListener(this);
	}

	/** The rest of the code is in GuiIngame#renderTooltip() */
	@Override
	public void onRender2D(Render2DEvent event) {
		if (BaseClient.instance.isDefaultHotbar())
			return;

		//RenderUtils.drawModalRectFromRight(5, 0, 5, 21, Color.WHITE.getRGB());

		RenderUtils.drawStringFromBottomRight("[BPS] " + String.format("%.2f", Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * 20*mc.timer.timerSpeed), 9, 10, Color.WHITE.getRGB());

		GuiScreen currentScreen = mc.currentScreen;

		if (currentScreen != null && !(currentScreen instanceof ClickGui) && !(currentScreen instanceof GuiBind))
			return;

		renderText(event, "&f[&fFPS&f]&f " + Minecraft.getDebugFPS(), 2);
	}

	private void renderText(Render2DEvent event, String text, int x) {
		String fpsText = text;
		RenderUtils.drawStringFromBottomLeft(fpsText, x, 10, -1);
	}

}