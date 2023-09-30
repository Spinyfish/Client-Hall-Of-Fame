package intentions.ui;

import java.util.Comparator;

import intentions.modules.Module;
import intentions.modules.render.TabGUI;
import intentions.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import intentions.Client;
import intentions.events.listeners.EventRenderGUI;

public class HUD {
	
	public Minecraft mc = Minecraft.getMinecraft();

	public void draw() {
		
		if(!TabGUI.openTabGUI) return;
		
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayWidth);
		FontRenderer fr = mc.fontRendererObj;
		
		Client.modules.sort(Comparator.comparingInt(m -> 
			fr.getStringWidth(((Module)m).name))
			.reversed()
		 );
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(4, 4, 0);
		GlStateManager.scale(1.25f, 1.25f, 1);
		GlStateManager.translate(-4, -4, 0);
		fr.drawStringWithShadow(Client.fullName, 4, 4, TabGUI.color.getMode().equalsIgnoreCase("Normal") ? 0x00ff1100 : ColorUtil.getRainbowColor());
		GlStateManager.popMatrix();
		
		int count = 0;
	
		for(Module m : Client.modules) {
			if(!m.toggled || m.name.equals("TabGUI") || m.name.equals("ClickGui"))
				continue;
			
			double offset = count*(fr.FONT_HEIGHT + 6);

			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name) - 10, offset, sr.getScaledWidth() - fr.getStringWidth(m.name) - 8, 6 + fr.FONT_HEIGHT + offset, 0x90ff1100);
			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name) - 8, offset, sr.getScaledWidth(), 6 + fr.FONT_HEIGHT + offset, 0x90000000);
			fr.drawStringWithShadow(m.name, sr.getScaledWidth() - fr.getStringWidth(m.name) - 4, 4 + offset, -1);
			
			count++;
		}
		
		Client.onEvent(new EventRenderGUI());
		
	}
	
}
