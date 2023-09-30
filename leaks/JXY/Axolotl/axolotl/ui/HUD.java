package axolotl.ui;

import java.util.ArrayList;
import java.util.Comparator;

import axolotl.Axolotl;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.Setting;
import axolotl.cheats.settings.SpecialSettings;
import axolotl.ui.ClickGUI.dropDown.ClickGui;
import font.CFontRenderer;
import axolotl.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class HUD {
	
	public Minecraft mc = Minecraft.getMinecraft();
	public Module hud;
	public int goneThrough = 0;
	public TabGUI tabGUI = new TabGUI();
	public Module tabGUIModule;
	public String var1 = "sen";
	
	public void drawToScreen() {

		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayWidth);
		CFontRenderer fr = mc.customFont;
		if(tabGUIModule.toggled)
			tabGUI.draw(fr, sr);
		
		Axolotl.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(m -> 
		
				fr.getStringWidth(((Module)m).name))
				.reversed()
		);
		if(!Axolotl.INSTANCE.clientOn)return;
		if(hud == null) hud = Axolotl.INSTANCE.moduleManager.getModule("HUD");
		if(hud == null)return;
		if(!hud.toggled) {
			return;
		}

		ArrayList<Module> modules = new ArrayList<>(Axolotl.INSTANCE.moduleManager.modules);
		
		modules.sort(Comparator.comparingInt(m -> 
		
			fr.getStringWidth(((Module)m).name + getMainModeString((Module)m)))

			.reversed()
			
		);

		fr.drawString(Axolotl.INSTANCE.full_name.substring(0,1), 1, 1, ColorUtil.getDynamicColor(0));

		fr.drawString(Axolotl.INSTANCE.full_name.substring(1), 1 + fr.getStringWidth(Axolotl.INSTANCE.full_name.substring(0,1)), 1, 0xFFFFFF);
		
		int count = 0;

		axolotl.cheats.modules.impl.render.HUD hudModule = ((axolotl.cheats.modules.impl.render.HUD)hud);

		for(Module m : modules) {
			if(!m.toggled)
				continue;
			double offset = (count*(fr.getHeight() + 2) + count*2);
			int col = ColorUtil.getDynamicColor(count);
			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(getMainModeString(m)) - fr.getStringWidth(m.name) - (mainModeIsSpecial(m) ? 0 : 4), offset, sr.getScaledWidth(), offset + 2 + fr.getHeight() + 2, 0x60000000);
			fr.drawString(m.name, sr.getScaledWidth() - fr.getStringWidth(m.name + getMainModeString(m)) - (mainModeIsSpecial(m) ? -2 : 2), (int) (2 + offset), col);
			fr.drawString(getMainModeString(m), sr.getScaledWidth() - fr.getStringWidth(getMainModeString(m)) - 1, (int) (2 + offset), 0xFFFFFF);
			count++;
		}
		if(hudModule.BPS.isEnabled()) {
			double numBPS = Math.floor((Math.hypot(Math.abs(mc.thePlayer.motionX) * 20 * mc.timer.timerSpeed, Math.abs(mc.thePlayer.motionZ) * 20 * mc.timer.timerSpeed)) * 100) / 100;
			String q = "" + (numBPS != 0 ? numBPS : "0.00");
			String bps = q + (q.length() <= 3 ? "0" : "");
			fr.drawString("BPS: " + bps, 1, sr.getScaledHeight() - 6, -1);
		}
		
	}
	
	public String getMainModeString(Module m) {
		Setting s = m.getSpecialSetting();
		if(s != null) {
			return " " + s.getSpecificValue();
		}
		return "";
	}

	private boolean mainModeIsSpecial(Module m) {
		Setting s = m.getSpecialSetting();
		return s instanceof SpecialSettings;
	}

	public HUD() {
		tabGUIModule = Axolotl.INSTANCE.moduleManager.getModule("TabGUI");
	}
	
}
