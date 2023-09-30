package haptic.module.impl.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventRender;
import haptic.font.FontUtil;
import haptic.font.MinecraftFontRenderer;
import haptic.module.Category;
import haptic.module.Module;
import haptic.setting.impl.ModeSetting;
import haptic.util.misc.MathUtils;
import haptic.util.render.ColorUtil;
import haptic.util.render.DrawUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class HUD extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Haptic", "Haptic", "Astolfo");
	
	public HUD() {
		super("HUD", Category.RENDER);
		this.addSettings(mode);
		this.setEnabledSilently(true);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventRender) {
			switch(mode.getMode()) {
			case "Haptic":
				renderHaptic();
				break;
			case "Astolfo":
				renderAstolfo();
				break;
			}
		}
	}
	
	private void renderHaptic() {
		final MinecraftFontRenderer fr = FontUtil.product_sans;
		final ScaledResolution sr = new ScaledResolution(mc);
		
		fr.drawStringWithShadow(Haptic.getFullFormattedName(), 4, 4, Haptic.getClientColor());
		
		float bpt = (float) (MathUtils.square(mc.thePlayer.posX - mc.thePlayer.lastTickPosX) + MathUtils.square(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ));
		float bps = (float) MathUtils.round((Math.sqrt(bpt) * 20) * mc.timer.timerSpeed, 0.01);
		
		String bpsrender = "BPS : " + bps;

		fr.drawStringWithShadow(bpsrender, 3, sr.getScaledHeight() - 12, -1);
		
		ArrayList<Module> moduleList = getEnabledModules();
		
		try {
			moduleList.sort((m1, m2) -> (int) (Math.round((fr.getStringWidth(m1.getDisplayName()) * 2) - Math.round(fr.getStringWidth(m2.getDisplayName()) * 2))));
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		Collections.reverse(moduleList);
		
		int offsetY = 3;
		
		for(Module m : moduleList) {
			String displayName = m.getDisplayName();
			double nameLength = fr.getStringWidth(displayName);
			
			fr.drawStringWithShadow(displayName, sr.getScaledWidth() - nameLength - 5, offsetY + 1.5F, new Color(247, 76, 7).getRGB());
			offsetY += 11;
		}
	}
	
	private void renderAstolfo() {
		final MinecraftFontRenderer fr = FontUtil.product_sans;
		final ScaledResolution sr = new ScaledResolution(mc);
		
		fr.drawStringWithShadow(Haptic.getFullFormattedName(), 4, 4, ColorUtil.astolfoColors(2, 40));
		
		float bpt = (float) (MathUtils.square(mc.thePlayer.posX - mc.thePlayer.lastTickPosX) + MathUtils.square(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ));
		float bps = (float) MathUtils.round((Math.sqrt(bpt) * 20) * mc.timer.timerSpeed, 0.01);
		
		String bpsrender = "BPS : " + bps;

		fr.drawStringWithShadow(bpsrender, 3, sr.getScaledHeight() - 12, -1);
		
		ArrayList<Module> moduleList = getEnabledModules();
		
		try {
			moduleList.sort((m1, m2) -> (int) (Math.round((fr.getStringWidth(m1.getDisplayName()) * 2) - Math.round(fr.getStringWidth(m2.getDisplayName()) * 2))));
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		Collections.reverse(moduleList);
		
		int offsetY = 6;
		
		for(Module m : moduleList) {
			String displayName = m.getDisplayName();
			double nameLength = fr.getStringWidth(displayName);
			
			for(int i = 0; i < 11; i++) {
				Gui.drawRect(sr.getScaledWidth() - 5, offsetY + i, sr.getScaledWidth() - 3, offsetY + i + 1, ColorUtil.astolfoColors(2, (int) ((offsetY + i - 1) * 1.3)));
			}
			Gui.drawRect(sr.getScaledWidth() - nameLength - 11, offsetY, sr.getScaledWidth() - 5, offsetY + 11, 0x80000000);
			fr.drawStringWithShadow(displayName, sr.getScaledWidth() - nameLength - 8, offsetY + 1.5F, ColorUtil.astolfoColors(2, (int) (offsetY * 1.3)));
			offsetY += 11;
		}
	}
	
	private ArrayList<Module> getEnabledModules() {
		ArrayList<Module> enabledModules = new ArrayList<>();
		for(Module m : Haptic.getModuleManager().getModules()) {
			if(m.isEnabled()) {
				enabledModules.add(m);
			}
		}
		return enabledModules;
	}
	
}