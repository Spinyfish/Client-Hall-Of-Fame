package de.fanta.module.impl.visual;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.util.concurrent.AtomicDouble;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import fr.lavache.anime.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class Themes extends Module {
	public static Themes INSTANCE;
	public static double Blur;

	public Themes() {
		super("Themes", Keyboard.KEY_NONE, Type.Visual, Color.blue);
		this.settings.add(new Setting("VantaShadow", new CheckBox(false)));
		this.settings.add(new Setting("VantaMagenta", new CheckBox(false)));
		this.settings.add(new Setting("BlurStärke", new Slider(0, 1000, 0.1, 50)));
		this.settings.add(
				new Setting("ArrayColor", new DropdownBox("RED", new String[] { "RED", "BLUE", "GREEN", "YELLOW" ,"Novoline","Rainbow"})));
		this.settings.add(
				new Setting("Fonts", new DropdownBox("Arial-Shadow", new String[] { "Winter Insight", "Arial", "Arial-Shadow","Roboto-Thin", "MC", "MC-Shadow" ,"Arial-Rainbow" ,"Medusa", "Astolfo", "Rainbow", "Novoline", "RemixOld", "Clean", "Flux", "Custom" , "Sigma4", "Test", "Vanta", "Test2","Violence","Skid", "Hero", "Holo","Jello", "ZeroDay"})));
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
		INSTANCE = this;
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	int i = 0;
	Module mod;

	boolean max = false;
	private String mod1 = "";
	
	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick) {

		}

		if (e instanceof EventRender2D && e.isPre()) {
			ScaledResolution sr = new ScaledResolution(mc);
			String input = ((DropdownBox) this.getSetting("Fonts").getSetting()).curOption;
			if (input.equalsIgnoreCase("Winter Insight")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.arial.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							Client.INSTANCE.unicodeBasicFontRenderer3.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(255, 0, 0), new Color(64, 0, 0), index / 12.4)
											.getRGB());
							break;
						case "BLUE":
							Client.INSTANCE.unicodeBasicFontRenderer3.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 37, 255), new Color(0, 16, 110), index / 12.4)
											.getRGB());
							break;
						case "GREEN":
							Client.INSTANCE.unicodeBasicFontRenderer3.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 255, 4), new Color(0, 54, 0), index / 12.4)
											.getRGB());
							break;
						case "YELLOW":
							Client.INSTANCE.unicodeBasicFontRenderer3.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(230, 255, 0), new Color(98, 122, 1), index / 12.4)
											.getRGB());
							break;
						case "Novoline":
							Client.INSTANCE.unicodeBasicFontRenderer3.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(3,81,74,255), new Color(5,244,222,255), index / 4.4)
									.getRGB());
							
							break;
						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
				
				
				
				
				
			} else if (input.equalsIgnoreCase("Arial-Rainbow")) {
					int count = 0;
				    int rainbow = 0;
					int index = 0;
					int count2 = 0;
					int index2 = 0;
					float offset2 = 0;
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					Client.INSTANCE.moduleManager.modules.sort(Comparator
							.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
					for (final Module m : Client.INSTANCE.moduleManager.modules) {
						if (!m.isState())
							continue;
						
						float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
						float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

						if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
							Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
							
//							startScissor(wSet2, -1, sr.getScaledWidth(),
//									offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
						
//							final AtomicBoolean hasMode = new AtomicBoolean(false);
							
//						boolean hasMode = ((DropdownBox) m.settings).curOption
							
							Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
									offset2 + 11, this.rainbow((int) offset2 * 9));
							
							Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(),
									offset2 + 10, Color.black.getRGB());
							
//							System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
							Client.INSTANCE.unicodeBasicFontRenderer.drawString(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet, offset2 - 1,
									this.rainbow((int) offset2 * 9));
							
						
//							Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//									offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
							
//							stopScissor();
//							stopScissor();
							// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
							// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//			                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//			                                y,
//			                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//			                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

							offset2 += 10;
							index2++;

						}
					}
					float offset = 0;
					for (final Module m : Client.INSTANCE.moduleManager.modules) {
						if (!m.isState() && m.anim.getValue() == 0)
							continue;
//							if (!m.isState())
//								continue;

						// Animate anim = new Animate();

						// WIDTH
						m.anim.setEase(Easing.LINEAR).setMin(0)
								.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
								.setReversed(!m.isState()).update();

						// HIGH
						m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
								.setReversed(!m.isState()).update();

						float wSet = sr.getScaledWidth() - m.anim.getValue();

						if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						
//								Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//										this.rainbow(rainbow * 150));
							

//			                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//			                                y,
//			                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//			                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
							rainbow++;
							count++;
							index++;
							offset += m.anim2.getValue();
						}
						GL11.glDisable(GL11.GL_BLEND);

					}
					
			}else if(input.equalsIgnoreCase("Holo")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.Holo.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.Holo.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.Holo.getStringWidth(m.name) - 5;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.Holo.getStringWidth(m.name) -5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet, offset-5, sr.getScaledWidth() , offset +
								 Client.INSTANCE.Holo.FONT_HEIGHT + 1.5F, (float) 50);
						Gui.drawRect(wSet, offset-5, sr.getScaledWidth() , offset +
						 Client.INSTANCE.Holo.FONT_HEIGHT ,new Color(255,255,255, 27).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.Holo.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.Holo.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
					Gui.drawRect(sr.getScaledWidth()-1F, offset, sr.getScaledWidth(), offset+10,getGradientOffset(Color.cyan, Color.blue, index / 12.4)
							.getRGB());

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							Client.INSTANCE.Holo.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							break;
						case "BLUE":
							Client.INSTANCE.Holo.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							break;
						case "GREEN":
							Client.INSTANCE.Holo.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							break;
						case "YELLOW":
							Client.INSTANCE.Holo.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							
							break;
						case "Novoline":
							Client.INSTANCE.Holo.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							
							break;

						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			}else if(input.equalsIgnoreCase("Jello")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.Jello3.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.Jello3.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.Jello3.getStringWidth(m.name) - 5;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.Jello3.getStringWidth(m.name) -5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet, offset-5, sr.getScaledWidth() , offset +
								 Client.INSTANCE.Jello3.FONT_HEIGHT + 1.5F, (float) 50);
					//	Gui.drawRect(wSet, offset-5, sr.getScaledWidth() , offset +
					//	 Client.INSTANCE.Jello3.FONT_HEIGHT ,new Color(255,255,255, 27).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.Jello3.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.Jello3.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
					//Gui.drawRect(sr.getScaledWidth()-1F, offset, sr.getScaledWidth(), offset+10,getGradientOffset(Color.cyan, Color.blue, index / 12.4)
					//		.getRGB());

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							Client.INSTANCE.Jello3.drawString(m.name, wSet -1 , (int) (offset),
									-1);
							break;
						case "BLUE":
							Client.INSTANCE.Jello3.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							break;
						case "GREEN":
							Client.INSTANCE.Jello3.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							break;
						case "YELLOW":
							Client.INSTANCE.Jello3.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							
							break;
						case "Novoline":
							Client.INSTANCE.Jello3.drawString(m.name, wSet -1 , (int) (offset) - 1,
									-1);
							
							break;

						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
					
			} else if (input.equalsIgnoreCase("Sigma4")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.Sigma.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.Sigma.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.Sigma.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
								offset2 + 11, this.rainbowSigma((int) offset2 * 15));
						
						Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(),
								offset2 + 10, Color.black.getRGB());
						
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
						Client.INSTANCE.Sigma.drawString(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet, offset2 - 1,
								this.rainbowSigma((int) offset2 * 15));
						
					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.Sigma.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.Sigma.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
				
					
			} else if (input.equalsIgnoreCase("RemixOld")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer6.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer6.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer6.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),offset2 + 11, getGradientOffset(new Color(227,145,16,255), new Color(95,61,8,255), index / 4.4)	.getRGB());
						
						Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(30,30,30,255).getRGB());
						Gui.drawRect(sr.getScaledWidth() +0.5F, offset2, sr.getScaledWidth()-0.5F, offset2+10,   getGradientOffset(new Color(227,145,16,255), new Color(95,61,8,255), index / 4.4)
								.getRGB());

//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
						Client.INSTANCE.unicodeBasicFontRenderer6.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet -0.5F, offset2 -1,
								getGradientOffset(new Color(227,145,16,255), new Color(95,61,8,255), index / 3.4)
								.getRGB());
						
					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;
						index++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer6.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
				
				
				
				
					
			} else if (input.equalsIgnoreCase("Astolfo")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
						
						Gui.drawRect(wSet2 , offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(0,0,0,120).getRGB());
						
						
						
						
						
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet, offset2 - 1,
								this.SkyRainbow(index, 1, 0.6F).getRGB());
						
						index++;
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Custom")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -mc.fontRendererObj.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (mc.fontRendererObj.getFontHeight());
					float wSet = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						//Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
					
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(mc.fontRendererObj.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(mc.fontRendererObj.getFontHeight()).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
						Gui.drawRect(wSet - 1, offset, sr.getScaledWidth(),
								offset + 10, Color.black.getRGB());
							mc.fontRendererObj.drawStringWithShadow(m.name, wSet, (int) (offset),
									getColor2());
							
						
//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Medusa")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.name + " " + value.getMode())));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
//						Blur = ((Slider) this.getSetting("BlurStÃ¤rke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
						
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());
						
						Gui.drawRect(wSet2 - 4, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(40,40,40).getRGB());
						
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
						Client.INSTANCE.unicodeBasicFontRenderer.drawString(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 5, offset2 - 1,
								new Color(100,254,155).getRGB());
						Gui.drawRect(sr.getScaledWidth()-4, offset2, sr.getScaledWidth()-2, offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? new Color(100,254,155).getRGB() : Color.gray.getRGB());
					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Rainbow")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
					
						Gui.drawRect(wSet2 - 4, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(0,0,0,180).getRGB());
					
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
					
						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 5, offset2 - 1,
								 -1);
						
						Gui.drawRect(sr.getScaledWidth()-2F, offset2, sr.getScaledWidth(), offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? this.rainbow((int) offset2 * 9) : this.rainbow((int) offset2 * 9));

					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
				
					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Flux")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer7.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer7.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer7.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
					
						Gui.drawRect(wSet2 - 4, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(0,0,0,180).getRGB());
					
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
					
						Client.INSTANCE.unicodeBasicFontRenderer7.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 4, offset2 - 1,
								this.rainbow((int) offset2 * 9));
						
						Gui.drawRect(sr.getScaledWidth()-2F, offset2, sr.getScaledWidth(), offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? this.rainbow((int) offset2 * 9) : this.rainbow((int) offset2 * 9));

					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer7.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
				
					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Violence")) {
				AtomicInteger count = new AtomicInteger(0);
			    AtomicInteger rainbow = new AtomicInteger(0);
			    AtomicInteger index = new AtomicInteger(0);
			    AtomicInteger count2 = new AtomicInteger(0);
			    AtomicInteger index2 = new AtomicInteger(0);
			    AtomicDouble offset2 = new AtomicDouble(0);
			
			    GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.stream().sorted(Comparator.comparingInt(value -> -mc.fontRendererObj.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.GRAY, value.getMode() + " ")))).filter(Module::isState).forEach(m -> {
							float wSet = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode())) - 3;
							float wSet2 = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode())) - 5;

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
								Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
								
//								startScissor(wSet2, -1, sr.getScaledWidth(),
//										offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
							
//								final AtomicBoolean hasMode = new AtomicBoolean(false);
								
//							boolean hasMode = ((DropdownBox) m.settings).curOption
								
//								Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//										offset2 + 11, this.rainbow((int) offset2 * 9));
								Gui.drawRect(wSet2 +3, offset2.floatValue() +2.6F, sr.getScaledWidth(),
										offset2.floatValue() + 12F, new Color(0,0,0,100).getRGB());
							
//								System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
								mc.fontRendererObj.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet+ 2, (offset2.floatValue()) +2,
										m.oneTimeColor);
								
							//	Gui.drawRect(sr.getScaledWidth()-2F, offset2, sr.getScaledWidth(), offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? this.rainbow((int) offset2 * 9) : this.rainbow((int) offset2 * 9));

							
//								Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//										offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
								
//								stopScissor();
//								stopScissor();
								// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
								// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//				                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//				                                y,
//				                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//				                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

								offset2.getAndAdd((double)9.5F);
								index2.getAndAdd(1);

							}
				
						float offset = 0;
						for (final Module mod : Client.INSTANCE.moduleManager.modules) {
							if (!mod.isState() && mod.anim.getValue() == 0)
								continue;
//								if (!m.isState())
//									continue;

							// Animate anim = new Animate();

							// WIDTH
//							mod.anim.setEase(Easing.LINEAR).setMin(0)
//									.setMax(Client.INSTANCE.unicodeBasicFontRenderer7.getStringWidth(mod.name) + 3).setSpeed(200)
//									.setReversed(!mod.isState()).update();

							// HIGH
//							mod.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
//									.setReversed(!mod.isState()).update();

							//float wSet1 = sr.getScaledWidth() - mod.anim.getValue();
						
							if (!mc.gameSettings.showDebugInfo && (mod.name != "HUD") && mod.name != "Animations" && mod.name != "") {
							
//								Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//											this.rainbow(rainbow * 150));
								

//				                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//				                                y,
//				                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//				                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
								rainbow.addAndGet(1);
								count.getAndAdd(1);
								index.getAndAdd(1);
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
			
						});
			} else if (input.equalsIgnoreCase("Skid")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.Skid.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.Skid.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.WHITE, m.getMode() + " ")) - 4;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.Skid.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.WHITE, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
					
						Gui.drawRect(wSet2 +2, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(0,0,0,120).getRGB());
					
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
					
						Client.INSTANCE.Skid.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.WHITE, m.getMode()), wSet+0.5F, offset2 + 0.5F,
								m.oneTimeColor);
						
						Gui.drawRect(wSet2 + 1.5F, offset2, wSet2 + 0.5F, offset2 + 10, -1);
						
					

					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.Skid.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.Skid.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
				
					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("ZeroDay")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.verdana.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.verdana.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.WHITE, m.getMode() + " ")) - 2;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.verdana.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.WHITE, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
					
						Gui.drawRect(wSet2 +2, offset2 +1, sr.getScaledWidth(),
								offset2 + 11, new Color(0,0,0,110).getRGB());
					
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
					
						Client.INSTANCE.verdana.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet+0.5F, (offset2-1),
								this.rainbow((int) offset2 * 8));
						
						Gui.drawRect(wSet2 + 2.5F, offset2 + 1, wSet2 + 0.5F, offset2 + 11F, this.rainbow((int) offset2 * 8));
						
					

					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.verdana.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.verdana.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
				
					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Vanta")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
					
						Gui.drawRect(wSet2 - 4, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(30,30,30,120).getRGB());
					
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
						
						
						
						if (((CheckBox) this.getSetting("VantaShadow").getSetting()).state) {
							if (((CheckBox) this.getSetting("VantaMagenta").getSetting()).state) {
								Client.INSTANCE.unicodeBasicFontRenderer5.drawString(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 4, offset2 - 1,
										Color.magenta.getRGB());
							}else {
								Client.INSTANCE.unicodeBasicFontRenderer5.drawStringWithShadow2(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 4, offset2 - 1,
										-1);
							}
						
						}else {
							Client.INSTANCE.unicodeBasicFontRenderer5.drawString(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 4, offset2 - 1,
									-1);
						}
						
						if (((CheckBox) this.getSetting("VantaMagenta").getSetting()).state) {
						Gui.drawRect(sr.getScaledWidth()-2F, offset2, sr.getScaledWidth(), offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? Color.magenta.getRGB() :Color.magenta.getRGB());
						}else {
							Gui.drawRect(sr.getScaledWidth()-2F, offset2, sr.getScaledWidth(), offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? this.rainbowSigma((int) offset2 * 9) : this.rainbowSigma((int) offset2 * 9));
	
						}

					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.unicodeBasicFontRenderer5.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
				
					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Test")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.fluxTabGuiFont.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.fluxTabGuiFont.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.fluxTabGuiFont.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
						
						Gui.drawRect(wSet2 - 4, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(0,0,0,170).getRGB());
					
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
					
						Client.INSTANCE.fluxTabGuiFont.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 4, offset2 - 2.5F,
								this.rainbowSigma((int) offset2 * 9));
						//this.rainbowSigma((int) offset2 * 9)
					
						Gui.drawRect(sr.getScaledWidth()-2F, offset2, sr.getScaledWidth(), offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? this.rainbowSigma((int) offset2 * 9) : this.rainbowSigma((int) offset2 * 9));

					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;
						index++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer7.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
					
					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
					
			} else if (input.equalsIgnoreCase("Clean")) {
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
//						Gui.drawRect(wSet2, offset2, sr.getScaledWidth(),
//								offset2 + 11, this.rainbow((int) offset2 * 9));
					
						Gui.drawRect(wSet2 - 4, offset2, sr.getScaledWidth(),
								offset2 + 10, new Color(0,0,0,180).getRGB());
					
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
					
						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 5, offset2 - 1,
								 -1);
						
						Gui.drawRect(sr.getScaledWidth()-2F, offset2, sr.getScaledWidth(), offset2+10, -1);

					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();
				
					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Test2")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.unicodeBasicFontRenderer5.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
//						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
//						Client.blurHelper.blur2(wSet -7, offset + 4, sr.getScaledWidth() - 3,
//								offset + 13, (float) 10);
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.unicodeBasicFontRenderer5.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						
						Gui.drawRect(wSet -7, offset + 4, sr.getScaledWidth() - 3,
								offset + 13, new Color(0,0,0,130).getRGB());
							Client.INSTANCE.unicodeBasicFontRenderer5.drawString(m.name, wSet - 5, (int) (offset) +3,
									getGradientOffset(new Color(0, 255, 0), new Color(0, 64, 0), index / 5.4)
									.getRGB());
//									this.rainbow((int) offset * 20));
							

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
						index2++;
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Hero")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.unicodeBasicFontRenderer4.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						//Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
					
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.unicodeBasicFontRenderer4.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Gui.drawRect(wSet - 4, offset +2, sr.getScaledWidth() - 2,
								offset + 11, new Color(0,0,0,125).getRGB());
						Gui.drawRect(sr.getScaledWidth()-2F, offset + 2.5F, sr.getScaledWidth(), offset+11, m.getColor().getRGB());
						Client.INSTANCE.unicodeBasicFontRenderer4.drawString(m.name , wSet- 3, (int) (offset),
								 m.getColor().getRGB());
						Gui.drawRect(wSet - 4, offset +2, sr.getScaledWidth() - 2,
								offset + 11, new Color(0,0,0,110).getRGB());
						
							
						
//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Arial")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.arial.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							Client.INSTANCE.unicodeBasicFontRenderer.drawString(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(255, 0, 0), new Color(64, 0, 0), index / 12.4)
											.getRGB());
							break;
						case "BLUE":
							Client.INSTANCE.unicodeBasicFontRenderer.drawString(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 37, 255), new Color(0, 16, 110), index / 12.4)
											.getRGB());
							break;
						case "GREEN":
							Client.INSTANCE.unicodeBasicFontRenderer.drawString(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 255, 4), new Color(0, 54, 0), index / 12.4)
											.getRGB());
							break;
						case "YELLOW":
							Client.INSTANCE.unicodeBasicFontRenderer.drawString(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(230, 255, 0), new Color(98, 122, 1), index / 12.4)
											.getRGB());
							
							break;
						case "Novoline":
							Client.INSTANCE.unicodeBasicFontRenderer.drawString(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(3,81,74,255), new Color(5,244,222,255), index / 4.4)
									.getRGB());
							
							break;
						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
				
			} else if (input.equalsIgnoreCase("Novoline")) {
		
				int count = 0;
			    int rainbow = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				float offset2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(value.getMode().isEmpty() ? value.name : String.format("%s %s%s", value.name, EnumChatFormatting.WHITE, value.getMode() + " "))));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;
					
					float wSet = sr.getScaledWidth()   - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + " ")) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
//						Blur = ((Slider) this.getSetting("BlurStÃ¤rke").getSetting()).curValue;
						
//						startScissor(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F);
					
//						final AtomicBoolean hasMode = new AtomicBoolean(false);
						
//					boolean hasMode = ((DropdownBox) m.settings).curOption
						
						
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());
//						
//						Gui.drawRect(wSet2 - 4, offset2, sr.getScaledWidth(),
//								offset2 + 10, new Color(40,40,40).getRGB());
						
//						System.out.println(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()));
						Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.getMode().isEmpty() ? m.name : String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()), wSet - 5, offset2 +1,
								getGradientOffset(new Color(3,81,74,255), new Color(5,244,222,255), index / 4.4)
								.getRGB());
						//Gui.drawRect(sr.getScaledWidth()-4, offset2, sr.getScaledWidth()-2, offset2+10, (m.getMode().equalsIgnoreCase("") || m.getMode() == null || m.getMode().isEmpty()) ? new Color(100,254,155).getRGB() : Color.gray.getRGB());
					
//						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
//								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						
//						stopScissor();
//						stopScissor();
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						offset2 += 10;
						index2++;
						index++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
					
//							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset2) - 1,
//									this.rainbow(rainbow * 150));
						

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());
						rainbow++;
						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("MC")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -mc.fontRendererObj.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (mc.fontRendererObj.getFontHeight());
					float wSet = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
								offset + mc.fontRendererObj.getFontHeight(), (float) Blur);
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(mc.fontRendererObj.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(mc.fontRendererObj.getFontHeight()).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							mc.fontRendererObj.drawString(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(255, 0, 0), new Color(64, 0, 0), index / 12.4)
											.getRGB());
							break;
						case "BLUE":
							mc.fontRendererObj.drawString(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 37, 255), new Color(0, 16, 110), index / 12.4)
											.getRGB());
							break;
						case "GREEN":
							mc.fontRendererObj.drawString(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 255, 4), new Color(0, 54, 0), index / 12.4)
											.getRGB());
							break;
						case "YELLOW":
							mc.fontRendererObj.drawString(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(230, 255, 0), new Color(98, 122, 1), index / 12.4)
											.getRGB());
							break;
						case "Novoline":
							mc.fontRendererObj.drawString(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(3,81,74,255), new Color(5,244,222,255), index / 4.4)
									.getRGB());
							
							break;
						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("MC-Shadow")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -mc.fontRendererObj.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (mc.fontRendererObj.getFontHeight());
					float wSet = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
								offset + mc.fontRendererObj.getFontHeight() , (float) Blur);
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(mc.fontRendererObj.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(mc.fontRendererObj.getFontHeight()).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							mc.fontRendererObj.drawStringWithShadow(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(255, 0, 0), new Color(64, 0, 0), index / 12.4)
											.getRGB());
							break;
						case "BLUE":
							mc.fontRendererObj.drawStringWithShadow(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 37, 255), new Color(0, 16, 110), index / 12.4)
											.getRGB());
							break;
						case "GREEN":
							mc.fontRendererObj.drawStringWithShadow(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 255, 4), new Color(0, 54, 0), index / 12.4)
											.getRGB());
							break;
						case "YELLOW":
							mc.fontRendererObj.drawStringWithShadow(m.name, (int) wSet, (int) (offset) - 1,
									getGradientOffset(new Color(230, 255, 0), new Color(98, 122, 1), index / 12.4)
											.getRGB());
							break;
						case "Novoline":
							mc.fontRendererObj.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(3,81,74,255), new Color(5,244,222,255), index / 4.4)
									.getRGB());
							
							break;
						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Roboto-Thin")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.arial.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							Client.INSTANCE.unicodeBasicFontRenderer4.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(255, 0, 0), new Color(64, 0, 0), index / 12.4)
											.getRGB());
							break;
						case "BLUE":
							Client.INSTANCE.unicodeBasicFontRenderer4.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 37, 255), new Color(0, 16, 110), index / 12.4)
											.getRGB());
							break;
						case "GREEN":
							Client.INSTANCE.unicodeBasicFontRenderer4.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 255, 4), new Color(0, 54, 0), index / 12.4)
											.getRGB());
							break;
						case "YELLOW":
							Client.INSTANCE.unicodeBasicFontRenderer4.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(230, 255, 0), new Color(98, 122, 1), index / 12.4)
											.getRGB());
							break;
						case "Novoline":
							Client.INSTANCE.unicodeBasicFontRenderer4.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(3,81,74,255), new Color(5,244,222,255), index / 4.4)
									.getRGB());
							
							break;
						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
			} else if (input.equalsIgnoreCase("Arial-Shadow")) {
				int count = 0;
				int index = 0;
				int count2 = 0;
				int index2 = 0;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				Client.INSTANCE.moduleManager.modules.sort(Comparator
						.comparingInt(value -> -Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.name)));
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState())
						continue;

					float offset = count2 * (Client.INSTANCE.arial.FONT_HEIGHT);
					float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) - 3;
					float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) - 5;

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						Blur = ((Slider) this.getSetting("BlurStärke").getSetting()).curValue;
						Client.blurHelper.blur2(wSet2, -1, sr.getScaledWidth(),
								offset + Client.INSTANCE.arial.FONT_HEIGHT + 5.5F, (float) Blur);
						// Gui.drawRect(wSet, offset, sr.getScaledWidth() , offset +
						// Client.INSTANCE.arial.FONT_HEIGHT + 1.5F ,new Color(15,15,15, 255).getRGB());

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count2++;
						index2++;

					}
				}
				float offset = 0;
				for (final Module m : Client.INSTANCE.moduleManager.modules) {
					if (!m.isState() && m.anim.getValue() == 0)
						continue;
//						if (!m.isState())
//							continue;

					// Animate anim = new Animate();

					// WIDTH
					m.anim.setEase(Easing.LINEAR).setMin(0)
							.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3).setSpeed(200)
							.setReversed(!m.isState()).update();

					// HIGH
					m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT).setSpeed(200)
							.setReversed(!m.isState()).update();

					float wSet = sr.getScaledWidth() - m.anim.getValue();

					if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations" && m.name != "") {
						switch (((DropdownBox) this.getSetting("ArrayColor").getSetting()).curOption) {

						case "RED":
							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(255, 0, 0), new Color(64, 0, 0), index / 12.4)
											.getRGB());
							break;
						case "BLUE":
							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 37, 255), new Color(0, 16, 110), index / 12.4)
											.getRGB());
							break;
						case "GREEN":
							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(0, 255, 4), new Color(0, 54, 0), index / 12.4)
											.getRGB());
							break;
						case "YELLOW":
							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(230, 255, 0), new Color(98, 122, 1), index / 12.4)
											.getRGB());
							break;
						case "Novoline":
							Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet, (int) (offset) - 1,
									getGradientOffset(new Color(3,81,74,255), new Color(5,244,222,255), index / 4.4)
									.getRGB());
							
							break;
						}

//		                        Gui.drawRect(e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) - 2,
//		                                y,
//		                                e.sr.getScaledWidth() - m.getSlide() - getXOffset(m) + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getStringWidth(moduleName) + 4,
//		                                y + me.xbuttonn.Skidsense.INSTANCE.getClientFont().getFontHeight() + 1, new Color(20, 20, 20).getRGB());

						count++;
						index++;
						offset += m.anim2.getValue();
					}
					GL11.glDisable(GL11.GL_BLEND);

				}
				
			}
		}

	}

	public static Color getGradientOffset(Color color1, Color color2, double index) {
		double offs = (Math.abs(((System.currentTimeMillis()) / 13)) / 60D) + index;
		if (offs > 1) {
			double left = offs % 1;
			int off = (int) offs;
			offs = off % 2 == 0 ? left : 1 - left;
		}
		double inverse_percent = 1 - offs;
		int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offs);
		int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offs);
		int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offs);
		return new Color(redPart, greenPart, bluePart);
	}
	public static int rainbow(int delay) {
		double rainbowState = Math.ceil(((System.currentTimeMillis() + delay) / 7L));
		rainbowState %= 360.0D;
		return Color.getHSBColor((float) (rainbowState / 360.0D), 0.9F, 1F).getRGB();
	}
	public static int rainbowSigma(int delay) {
		double rainbowState = Math.ceil(((System.currentTimeMillis() + delay) / 9L));
		rainbowState %= 360.0D;
		return Color.getHSBColor((float) (rainbowState / 360.0D), 0.5F, 1F).getRGB();
	}
	   public static void startScissor(final float startX, final float startY, final float endX, final float endY) {
           final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
           final float width = endX - startX;
           final float height = endY - startY;
           assert Minecraft.getMinecraft().currentScreen != null;
           final float bottomY = Minecraft.getMinecraft().currentScreen.height - endY;
           final float factor = (float) scaledResolution.getScaleFactor();

           final float scissorX = (startX * factor);
           final float scissorY = (bottomY * factor);
           final float scissorWidth = (width * factor);
           final float scissorHeight = (height * factor);
           GL11.glScissor((int) scissorX, (int) scissorY, (int) scissorWidth, (int) scissorHeight);
           GL11.glEnable(GL11.GL_SCISSOR_TEST);
       }

       public static void stopScissor() {
           GL11.glDisable(GL11.GL_SCISSOR_TEST);
       }
       public static void prepareScissorBox(float x, float y, float x2, float y2) {
           ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
           int factor = scale.getScaleFactor();
           GL11.glScissor((int)((int)(x * (float)factor)), (int)((int)(((float)scale.getScaledHeight() - y2) * (float)factor)), (int)((int)((x2 - x) * (float)factor)), (int)((int)((y2 - y) * (float)factor)));
       }
       public int getColor2() {
   		try {
   			return ((ColorValue)getSetting("Color").getSetting()).color;
   		} catch (Exception e) {
   			return Color.white.getRGB();
   		}
   	}
       public static Color SkyRainbow(int counter, float bright, float st) {
           double v1 = Math.ceil(System.currentTimeMillis() + counter * 109L) / 5;
           return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright);
       }
       
      public final int getHeight() {
    	  int height = 0;
    	  
    	  for (Module module : Client.INSTANCE.moduleManager.modules)
    	  {
    		  if (!module.state)
    			  continue;
    		  height += module.anim2.getValue();
    	  }
    	  return height;
      }
       
}
