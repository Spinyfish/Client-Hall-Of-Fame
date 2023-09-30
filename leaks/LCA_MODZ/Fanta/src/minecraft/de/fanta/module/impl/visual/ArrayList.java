package de.fanta.module.impl.visual;

import java.awt.Color;
import java.math.BigInteger;
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
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ArrayList extends Module {
	public static ArrayList INSTANCE;
	public static double Blur;
	public static double Alpha;
	public static double RainbowOffset;


	//private static double Alpha = 0;
	
	
	public ArrayList() {
		super("Arraylist", Keyboard.KEY_NONE, Type.Visual, Color.blue);
		this.settings.add(new Setting("Rainbow", new CheckBox(true)));
		this.settings.add(new Setting("Backround", new CheckBox(true)));
		this.settings.add(new Setting("ModuleColor", new CheckBox(false)));
		this.settings.add(new Setting("SideWardsRect", new CheckBox(true)));
		this.settings.add(new Setting("Suffix", new CheckBox(true)));
		this.settings.add(new Setting("Alpha", new Slider(0, 255, 0.1, 200)));
		this.settings.add(new Setting("RainbowOffset", new Slider(1, 20, 0.1, 18.1)));
		// this.settings.add(new Setting("Fade", new Slider(0, 255, 0.1, 200)));

		this.settings.add(new Setting("Fonts", new DropdownBox("Roboto-Thin",
				new String[] { "Winter Insight", "Arial", "Roboto-Thin", "MC", "Ambien" })));
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
				if (!Client.INSTANCE.moduleManager.getModule("Themes").isState()) {
					if (((CheckBox) this.getSetting("Suffix").getSetting()).state) {
						int count = 0;
						int rainbow = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						float offset2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator
								.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer3
										.getStringWidth(value.getMode().isEmpty() ? value.name
												: String.format("%s %s%s", value.name, EnumChatFormatting.WHITE,
														value.getMode() + " "))));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;

							float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer3
									.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 3;
							float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer3
									.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 5;

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												m.getColor().getRGB());

									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.rainbow((int) offset2 * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.getColor2());
									}
								}
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(), offset2 + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer3
											.drawString(
													m.getMode().isEmpty() ? m.name
															: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
																	m.getMode()),
													wSet, offset2 - 1, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer3.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset2 - 1, this.rainbow((int) offset2 * (int) RainbowOffset));
								} else {
									Client.INSTANCE.unicodeBasicFontRenderer3.drawString(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()),
											wSet, offset2 - 1, this.getColor2());
								}

								offset2 += 10;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(m.name) + 3)
									.setSpeed(200).setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer3.FONT_HEIGHT).setSpeed(200)
									.setReversed(!m.isState()).update();

							float wSet = sr.getScaledWidth() - m.anim.getValue();

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								rainbow++;
								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					} else {
						int count = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(
								value -> -Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(value.name)));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;
							float offset = count2 * (Client.INSTANCE.arial.FONT_HEIGHT);
							float wSet = sr.getScaledWidth()
									- Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(m.name) - 3;
							float wSet2 = sr.getScaledWidth()
									- Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(m.name) - 5;
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												m.getColor().getRGB());

									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												this.rainbow((int) offset * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11, this.getColor2());
									}
								}
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset, sr.getScaledWidth(), offset + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								count2++;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer3.getStringWidth(m.name) + 3)
									.setSpeed(200).setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.arial.FONT_HEIGHT)
									.setSpeed(200).setReversed(!m.isState()).update();
							float wSet = sr.getScaledWidth() - m.anim.getValue();
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer3
											.drawString(
													m.getMode().isEmpty() ? m.name
															: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
																	m.getMode()),
													wSet, offset - 1, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer3.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset - 1, this.rainbow((int) offset * (int) RainbowOffset));
								} else {
									Client.INSTANCE.unicodeBasicFontRenderer3.drawString(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()),
											wSet, offset - 1, this.getColor2());
								}

								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					}

				}
			} else if (input.equalsIgnoreCase("MC")) {
				if (!Client.INSTANCE.moduleManager.getModule("Themes").isState()) {
					if (((CheckBox) this.getSetting("Suffix").getSetting()).state) {
						int count = 0;
						int rainbow = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						float offset2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(value -> -sr.getScaledWidth()
								- mc.fontRendererObj.getStringWidth(value.getMode().isEmpty() ? value.name
										: String.format("%s %s%s", value.name, EnumChatFormatting.WHITE,
												value.getMode() + " "))));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;

							float wSet = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getMode().isEmpty()
									? m.name
									: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + ""));
							float wSet2 = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getMode().isEmpty()
									? m.name
									: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode() + "")) - 2;

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												m.getColor().getRGB());

									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.rainbow((int) offset2 * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.getColor2());
									}
								}
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(), offset2 + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									mc.fontRendererObj
											.drawString(
													m.getMode().isEmpty() ? m.name
															: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
																	m.getMode()),
													wSet, offset2 + 2, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									mc.fontRendererObj.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset2 + 2, this.rainbow((int) offset2 * (int) RainbowOffset));
								} else {
									mc.fontRendererObj.drawString(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()),
											wSet, offset2 + 2, this.getColor2());
								}

								offset2 += 10;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(mc.fontRendererObj.getStringWidth(m.name) + 3).setSpeed(200)
									.setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(mc.fontRendererObj.getFontHeight())
									.setSpeed(200).setReversed(!m.isState()).update();

							float wSet = sr.getScaledWidth() - m.anim.getValue();

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								rainbow++;
								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					} else {
						int count = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules
								.sort(Comparator.comparingInt(value -> -mc.fontRendererObj.getStringWidth(value.name)));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;
							float offset = count2 * (mc.fontRendererObj.getFontHeight());
							float wSet = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name);
							float wSet2 = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.name) - 5;
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {

								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset + 1, sr.getScaledWidth(), offset + 11,
												m.getColor().getRGB());
									}
								} else

								if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												this.rainbow((int) offset * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset + 1, sr.getScaledWidth(), offset + 11,
												this.getColor2());
									}
								}
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset + 1, sr.getScaledWidth(), offset + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								count2++;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(mc.fontRendererObj.getStringWidth(m.name) + 3).setSpeed(200)
									.setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(mc.fontRendererObj.getFontHeight())
									.setSpeed(200).setReversed(!m.isState()).update();
							float wSet = sr.getScaledWidth() - m.anim.getValue();
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									mc.fontRendererObj
											.drawString(
													m.getMode().isEmpty() ? m.name
															: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
																	m.getMode()),
													wSet, offset + 2, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
									mc.fontRendererObj.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset + 2, this.rainbow((int) offset * (int) RainbowOffset));
								} else {
									mc.fontRendererObj.drawStringWithShadow(m.name, wSet + 1, (int) (offset) + 2,
											this.getColor2());
								}

								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					}

				}
			} else if (input.equalsIgnoreCase("Roboto-Thin")) {
				if (!Client.INSTANCE.moduleManager.getModule("Themes").isState()) {
					if (((CheckBox) this.getSetting("Suffix").getSetting()).state) {
						int count = 0;
						int rainbow = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						float offset2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator
								.comparingInt(value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer4
										.getStringWidth(value.getMode().isEmpty() ? value.name
												: String.format("%s %s%s", value.name, EnumChatFormatting.WHITE,
														value.getMode() + " "))));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;

							float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer4
									.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 3;
							float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer4
									.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 5;

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												m.getColor().getRGB());
									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.rainbow((int) offset2 * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.getColor2());
									}
								}
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(), offset2 + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer4
											.drawString(
													m.getMode().isEmpty() ? m.name
															: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
																	m.getMode()),
													wSet, offset2 - 1, m.getColor().getRGB());
								} else

								if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer4.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset2 - 1, this.rainbow((int) offset2 * (int) RainbowOffset));
								} else {
									Client.INSTANCE.unicodeBasicFontRenderer4.drawString(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()),
											wSet, offset2 - 1, this.getColor2());
								}

								offset2 += 10;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) + 3)
									.setSpeed(200).setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer4.FONT_HEIGHT).setSpeed(200)
									.setReversed(!m.isState()).update();

							float wSet = sr.getScaledWidth() - m.anim.getValue();

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								rainbow++;
								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					} else {
						int count = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(
								value -> -Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(value.name)));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;
							float offset = count2 * (Client.INSTANCE.unicodeBasicFontRenderer4.FONT_HEIGHT);
							float wSet = sr.getScaledWidth()
									- Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) - 3;
							float wSet2 = sr.getScaledWidth()
									- Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) - 5;
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												m.getColor().getRGB());
									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;

									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												this.rainbow((int) offset * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11, this.getColor2());
									}
								}
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset, sr.getScaledWidth(), offset + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								count2++;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer4.getStringWidth(m.name) + 3)
									.setSpeed(200).setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer4.FONT_HEIGHT).setSpeed(200)
									.setReversed(!m.isState()).update();
							float wSet = sr.getScaledWidth() - m.anim.getValue();
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer4.drawStringWithShadow(m.name, wSet,
											(int) (offset) - 1, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
									Client.INSTANCE.unicodeBasicFontRenderer4.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset - 1, this.rainbow((int) offset * (int) RainbowOffset));
								} else {
									Client.INSTANCE.unicodeBasicFontRenderer4.drawStringWithShadow(m.name, wSet,
											(int) (offset) - 1, this.getColor2());
								}

								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					}

				}

			} else if (input.equalsIgnoreCase("Ambien")) {
				if (!Client.INSTANCE.moduleManager.getModule("Themes").isState()) {
					if (((CheckBox) this.getSetting("Suffix").getSetting()).state) {
						int count = 0;
						int rainbow = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						float offset2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(value -> -sr.getScaledWidth()
								- Client.INSTANCE.ViolenceTabGUi.getStringWidth(value.getMode().isEmpty() ? value.name
										: String.format("%s %s%s", value.name, EnumChatFormatting.WHITE,
												value.getMode() + " "))));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;

							float wSet = sr.getScaledWidth()
									- Client.INSTANCE.ViolenceTabGUi.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 3;
							float wSet2 = sr.getScaledWidth()
									- Client.INSTANCE.ViolenceTabGUi.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 5;

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												m.getColor().getRGB());
									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.rainbow((int) offset2 * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.getColor2());
									}
								}
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(), offset2 + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.ViolenceTabGUi
											.drawString(
													m.getMode().isEmpty() ? m.name
															: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
																	m.getMode()),
													wSet, offset2 - 1, m.getColor().getRGB());
								} else

								if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									Client.INSTANCE.ViolenceTabGUi.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset2 - 1, this.rainbow((int) offset2 * (int) RainbowOffset));
								} else {
									Client.INSTANCE.ViolenceTabGUi.drawString(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()),
											wSet, offset2 - 1, this.getColor2());
								}

								offset2 += 10;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.ViolenceTabGUi.getStringWidth(m.name) + 3).setSpeed(200)
									.setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.ViolenceTabGUi.FONT_HEIGHT)
									.setSpeed(200).setReversed(!m.isState()).update();

							float wSet = sr.getScaledWidth() - m.anim.getValue();

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								rainbow++;
								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					} else {
						int count = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator
								.comparingInt(value -> -Client.INSTANCE.ViolenceTabGUi.getStringWidth(value.name)));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;
							float offset = count2 * (Client.INSTANCE.ViolenceTabGUi.FONT_HEIGHT);
							float wSet = sr.getScaledWidth() - Client.INSTANCE.ViolenceTabGUi.getStringWidth(m.name)
									- 3;
							float wSet2 = sr.getScaledWidth() - Client.INSTANCE.ViolenceTabGUi.getStringWidth(m.name)
									- 5;
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												m.getColor().getRGB());
									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;

									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												this.rainbow((int) offset * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11, this.getColor2());
									}
								}
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset, sr.getScaledWidth(), offset + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								count2++;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.ViolenceTabGUi.getStringWidth(m.name) + 3).setSpeed(200)
									.setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0).setMax(Client.INSTANCE.ViolenceTabGUi.FONT_HEIGHT)
									.setSpeed(200).setReversed(!m.isState()).update();
							float wSet = sr.getScaledWidth() - m.anim.getValue();
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.ViolenceTabGUi.drawStringWithShadow(m.name, wSet,
											(int) (offset) - 1, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
									Client.INSTANCE.ViolenceTabGUi.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset - 1, this.rainbow((int) offset * (int) RainbowOffset));
								} else {
									Client.INSTANCE.ViolenceTabGUi.drawStringWithShadow(m.name, wSet,
											(int) (offset) - 1, this.getColor2());
								}

								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					}

				}

			} else if (input.equalsIgnoreCase("Arial")) {
				if (!Client.INSTANCE.moduleManager.getModule("Themes").isState()) {
					if (((CheckBox) this.getSetting("Suffix").getSetting()).state) {
						int count = 0;
						int rainbow = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						float offset2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules
								.sort(Comparator
										.comparingInt(
												value -> -sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer
														.getStringWidth(value.getMode().isEmpty() ? value.name
																: String.format("%s %s%s", value.name,
																		EnumChatFormatting.WHITE,
																		value.getMode() + " "))));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;

							float wSet = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer
									.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 3;
							float wSet2 = sr.getScaledWidth() - Client.INSTANCE.unicodeBasicFontRenderer
									.getStringWidth(m.getMode().isEmpty() ? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
													m.getMode() + " "))
									- 5;

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												m.getColor().getRGB());
									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.rainbow((int) offset2 * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset2, sr.getScaledWidth(), offset2 + 11,
												this.getColor2());
									}
								}
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset2, sr.getScaledWidth(), offset2 + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer
											.drawStringWithShadow(
													m.getMode().isEmpty() ? m.name
															: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
																	m.getMode()),
													wSet, offset2 - 1, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset2 - 1, this.rainbow((int) offset2 * (int) RainbowOffset));
								} else {
									Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.getMode().isEmpty()
											? m.name
											: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY, m.getMode()),
											wSet, offset2 - 1, this.getColor2());
								}

								offset2 += 10;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3)
									.setSpeed(200).setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer.FONT_HEIGHT).setSpeed(200)
									.setReversed(!m.isState()).update();

							float wSet = sr.getScaledWidth() - m.anim.getValue();

							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								rainbow++;
								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					} else {
						int count = 0;
						int index = 0;
						int count2 = 0;
						int index2 = 0;
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						Client.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(
								value -> -Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.name)));
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState())
								continue;
							float offset = count2 * (Client.INSTANCE.unicodeBasicFontRenderer.FONT_HEIGHT);
							float wSet = sr.getScaledWidth()
									- Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) - 3;
							float wSet2 = sr.getScaledWidth()
									- Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) - 5;
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												m.getColor().getRGB());
									}
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11,
												this.rainbow((int) offset * (int) RainbowOffset));
									}
								} else {
									if (((CheckBox) this.getSetting("SideWardsRect").getSetting()).state
											&& ((CheckBox) this.getSetting("Backround").getSetting()).state) {
										Gui.drawRect(wSet2, offset, sr.getScaledWidth(), offset + 11, this.getColor2());
									}
								}
								Alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
								if (((CheckBox) this.getSetting("Backround").getSetting()).state) {
									Gui.drawRect(wSet2 + 1, offset, sr.getScaledWidth(), offset + 10,
											new Color(20, 20, 20, (int) Alpha).getRGB());
								}

								count2++;
								index2++;
							}
						}
						float offset = 0;
						for (final Module m : Client.INSTANCE.moduleManager.modules) {
							if (!m.isState() && m.anim.getValue() == 0)
								continue;
							// WIDTH
							m.anim.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(m.name) + 3)
									.setSpeed(200).setReversed(!m.isState()).update();
							// HIGH
							m.anim2.setEase(Easing.LINEAR).setMin(0)
									.setMax(Client.INSTANCE.unicodeBasicFontRenderer.FONT_HEIGHT).setSpeed(200)
									.setReversed(!m.isState()).update();
							float wSet = sr.getScaledWidth() - m.anim.getValue();
							if (!mc.gameSettings.showDebugInfo && (m.name != "HUD") && m.name != "Animations"
									&& m.name != "") {
								if (((CheckBox) this.getSetting("ModuleColor").getSetting()).state) {
									Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet,
											(int) (offset) - 1, m.getColor().getRGB());
								} else if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
									RainbowOffset = ((Slider) this.getSetting("RainbowOffset").getSetting()).curValue;
									Client.INSTANCE.unicodeBasicFontRenderer.drawString(
											m.getMode().isEmpty() ? m.name
													: String.format("%s %s%s", m.name, EnumChatFormatting.GRAY,
															m.getMode()),
											wSet, offset - 1, this.rainbow((int) offset * (int) RainbowOffset));
								} else {
									Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(m.name, wSet,
											(int) (offset) - 1, this.getColor2());
								}

								count++;
								index++;
								offset += m.anim2.getValue();
							}
							GL11.glDisable(GL11.GL_BLEND);
						}
					}

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
		GL11.glScissor((int) ((int) (x * (float) factor)),
				(int) ((int) (((float) scale.getScaledHeight() - y2) * (float) factor)),
				(int) ((int) ((x2 - x) * (float) factor)), (int) ((int) ((y2 - y) * (float) factor)));
	}

	public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}

	public static Color SkyRainbow(int counter, float bright, float st) {
		double v1 = Math.ceil(System.currentTimeMillis() + counter * 109L) / 5;
		return Color.getHSBColor(
				(double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st,
				bright);
	}
}
