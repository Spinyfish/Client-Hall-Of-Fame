package igbt.astolfy.module.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;
import igbt.astolfy.utils.ColorUtils;
import igbt.astolfy.utils.PotionUtils;
import igbt.astolfy.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import static igbt.astolfy.utils.ColorUtils.astolfo;
import static igbt.astolfy.utils.ColorUtils.fade;

public class Hud extends ModuleBase {

	public static ModeSetting notificationType;
	public static ModeSetting backgroundMode;
	public static ModeSetting colorMode;
	public static ModeSetting hudMode;
	
	public Hud() {
		super("Hud", 0, Category.VISUALS);
		hudMode = new ModeSetting("Mode", "Astolfy", "Astolfo");
		notificationType = new ModeSetting("Notify Type", "Right","Top","Left");
		colorMode = new ModeSetting("Color", "Astolfy", "Wave");
		backgroundMode = new ModeSetting("ArrayList", "Wave","Solid");
		addSettings(hudMode, colorMode, backgroundMode, notificationType);
		setToggled(true);
	}

	public void onEnable() { }
	
	public static class sortDefaultFont implements Comparator<ModuleBase> {
		@Override
		public int compare(ModuleBase arg0, ModuleBase arg1) {
				if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg0.getName() + (arg0.getSuffix().length() > 1 ? (" [" + arg0.getSuffix()) : "]")) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg1.getName() + (arg1.getSuffix().length() > 1 ? (" [" + arg1.getSuffix()) : "]"))) {
					return -1;
				} else if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg0.getName() + (arg0.getSuffix().length() > 1 ? (" [" + arg0.getSuffix()) : "]")) < Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg1.getName() + (arg1.getSuffix().length() > 1 ? (" [" + arg1.getSuffix()) : "]")))
					return 1;
			return 0;
		}
	}
	
	public static class sortCustomFont implements Comparator<ModuleBase> {
		@Override
		public int compare(ModuleBase arg0, ModuleBase arg1) {
				if (mc.customFont.getStringWidth(arg0.getName() + (arg0.getSuffix().length() > 1 ? (" " + arg0.getSuffix()) + "" : "")) > mc.customFont.getStringWidth(arg1.getName() + (arg1.getSuffix().length() > 1 ? (" " + arg1.getSuffix()) + "": ""))) {
					return -1;
				} else if (mc.customFont.getStringWidth(arg0.getName() + (arg0.getSuffix().length() > 1 ? (" " + arg0.getSuffix()) + "" : "")) < mc.customFont.getStringWidth(arg1.getName() + (arg1.getSuffix().length() > 1 ? (" " + arg1.getSuffix()) + "" : "")))
					return 1;
			return 0;
		}
	}
	
	public static class sortPotionNames implements Comparator<PotionEffect> {

		@Override
		public int compare(PotionEffect o1, PotionEffect o2) {
			Potion potion1 = Potion.potionTypes[o1.getPotionID()];
            String ampliferString1 = PotionUtils.getAmplifierString(o1.getAmplifier());
			String PotionString1 =  I18n.format(potion1.getName(), new Object[0]) + " " + (ampliferString1.length() > 0 ? ampliferString1 + " " : "") + "(" + "\247f" +  Potion.getDurationString(o1) + " ";
				
			Potion potion2 = Potion.potionTypes[o2.getPotionID()];
            String ampliferString2 = PotionUtils.getAmplifierString(o2.getAmplifier());
			String PotionString2 =  I18n.format(potion2.getName(), new Object[0]) + " " + (ampliferString2.length() > 0 ? ampliferString2 + " " : "") + "(" + "\247f" +  Potion.getDurationString(o2) + " ";
				
			
			if (mc.customFont.getStringWidth(PotionString1) < mc.customFont.getStringWidth(PotionString2))
				return -1;
			else if (mc.customFont.getStringWidth(PotionString2) < mc.customFont.getStringWidth(PotionString1))
				return 1;
			return 0;
		}

	}
	
	public static int getColor(int offset) {
		switch(colorMode.getCurrentValue()) {

			case "Astolfy":
				return astolfo(offset * 2);
			case "Wave":
				return fade(new Color(255, 89, 252),offset, 10).getRGB();

		}
		return 0;
	}

	
	public void onEvent(Event event) {
		if(event instanceof EventRender2D) {
			EventRender2D e = (EventRender2D)event;
			CopyOnWriteArrayList<ModuleBase> modules = Astolfy.i.moduleManager.getModules();
			Collections.sort(modules, new sortCustomFont());
			int count = 0;
			int totalCount = 0;
			mc.customFont.drawStringWithShadow(Astolfy.i.clientName.substring(0,1) + "\247f" + Astolfy.i.clientName.substring(1,Astolfy.i.clientName.length()), 3, 3, getColor(3));
			//mc.fontRendererObj.drawStringWithShadow("Sex",5,5,-1);
			double padding = 7;
			for(ModuleBase mod : modules) {
				if(mod.isToggled()) {
					String text = mod.getName() + (mod.getSuffix().length() > 0 ?" \2477" + mod.getSuffix() + "": "");
					if(count == 0) {

						if(hudMode.getCurrentValue().equalsIgnoreCase("Astolfy"))
							Gui.drawRect(e.getWidth() - mc.customFont.getStringWidth(text) - padding - 1, 3, e.getWidth() - 2, 2, getColor(3));
					}
					double x = e.getWidth() - mc.customFont.getStringWidth(text) - padding - 1;
					double y = (count * (mc.customFont.getHeight() + 3)) + 3;
					double endX = e.getWidth() - 3;
					double endY = (count * (mc.customFont.getHeight() + 3)) + mc.customFont.getHeight() + 6;

					if(hudMode.getCurrentValue().equalsIgnoreCase("Astolfo")){
						x += 2;
						endX += 2;
						y = (count * (mc.customFont.getHeight() + 3));
						endY = (count * (mc.customFont.getHeight() + 3))+mc.customFont.getHeight() + 3;
					}//else

					if(backgroundMode.getCurrentValue().equalsIgnoreCase("Solid"))
						Gui.drawRect(x, y, endX, endY, 0x80000000);
					else {
						Color startColor = new Color(getColor((int) y));
						Color endColor = new Color(getColor((int) endY));
						Gui.drawGradientRect(x, y, endX, endY, ColorUtils.setColorOpacity(startColor, 50).getRGB(), ColorUtils.setColorOpacity(endColor, 50).getRGB());
						Gui.drawRect(x, y, endX, endY, 0x80000000);
					}
					Gui.drawGradientRect(endX,y,endX + 1,y + mc.customFont.getHeight() + 2 + 1,getColor((int)y),getColor((int)endY));
					
					
					mc.customFont.drawDropString(text, endX - mc.customFont.getStringWidth(text) - (padding / 2 - 1), (y + 5 - (mc.customFont.getHeight() / 2)), getColor((int)y));
					count++;
				}
				totalCount++;
			}
			double numBPS = Math.floor((Math.hypot(Math.abs(mc.thePlayer.motionX) * 20 * mc.timer.timerSpeed, Math.abs(mc.thePlayer.motionZ) * 20 * mc.timer.timerSpeed)) * 100) / 100;
			mc.customFont.drawString("FPS: \247f" + mc.getDebugFPS() + "", 3, e.getHeight() - 3 - mc.customFont.getHeight(), getColor((int)e.getHeight() - 3 - mc.customFont.getHeight()));
			mc.customFont.drawString("BPS: \247f" + numBPS + "", 3, e.getHeight() - 4 - (mc.customFont.getHeight() * 2), getColor((int)e.getHeight() - 4 - (mc.customFont.getHeight() * 2)));
			count = 0;
			Collection<PotionEffect> potionList = mc.thePlayer.getActivePotionEffects();
			ArrayList<PotionEffect> potions = potionList.stream().collect(Collectors.toCollection(ArrayList::new));
			Collections.sort(potions, new sortPotionNames());
			Collections.reverse(potions);
			for(PotionEffect p : potions) {
				count++;
                Potion potion = Potion.potionTypes[p.getPotionID()];
                String ampliferString = PotionUtils.getAmplifierString(p.getAmplifier());
				String PotionString =  I18n.format(potion.getName(), new Object[0]) + " " + (ampliferString.length() > 0 ? ampliferString + " " : "") + "(" + "\247f" +  Potion.getDurationString(p) + " ";
				mc.customFont.drawString(PotionString, e.getWidth() - 3 - mc.customFont.getStringWidth(PotionString), e.getHeight() - 3 - ((mc.customFont.getHeight() + 1) * count), getColor((int) (e.getHeight() - 3 - ((mc.customFont.getHeight() + 1) * count))));
				mc.customFont.drawString(")", e.getWidth() - 3 - mc.customFont.getStringWidth(" "), e.getHeight() - 3 - ((mc.customFont.getHeight() + 1) * count), getColor((int) (e.getHeight() - 3 - ((mc.customFont.getHeight() + 1) * count))));
			}
			//Gui.drawRect(0, 0, 10, 10, -1);
		}
	}
}
