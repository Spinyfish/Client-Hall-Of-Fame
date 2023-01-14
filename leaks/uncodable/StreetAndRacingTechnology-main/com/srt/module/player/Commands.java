package com.srt.module.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.srt.SRT;
import com.srt.events.Event;
import com.srt.events.listeners.EventChat;
import com.srt.module.ModuleBase;
import com.srt.settings.Setting;
import com.srt.settings.settings.BooleanSetting;
import com.srt.settings.settings.ModeSetting;
import com.srt.settings.settings.NumberSetting;

import net.minecraft.util.ChatComponentText;

public class Commands extends ModuleBase {

    public Commands() {
        super("Commands", Keyboard.KEY_NONE, Category.PLAYER);
		setDisplayName("Client Commands");
        this.setToggled(true);
    }

    public void onEvent(Event e) {
        if(e instanceof EventChat) {
            String message = ((EventChat) e).getMessage();
            String prefix = ".";

            if(message.startsWith(prefix)) {
                try {
                    String[] paramArray = message.split(" ");

                    if (paramArray[0].contains("bind")) {
                        SRT.moduleManager.getModuleByName(paramArray[1]).setKey(Keyboard.getKeyIndex(paramArray[2].toUpperCase(Locale.ROOT)));
                    }
                } catch(Exception exception) {
                    mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.DARK_PURPLE + "[SRT] " + ChatFormatting.YELLOW + "Syntax error."));
                }
                		try {
			                if (message.contains("config")) {
								String[] commandmsg = message.split(" ");
								if(commandmsg.length >= 1) {
									if(commandmsg[1].equalsIgnoreCase("save")) {
										File folder = new File("SRT");
										if(!folder.exists()) {
											folder.mkdir();
										}
										File config = new File("SRT/" + commandmsg[2] + ".txt");
										config.delete();
										System.out.println(config.getAbsolutePath());
										try {
											if (config.createNewFile()) {
												FileWriter configWrite = new FileWriter(config);
												for(ModuleBase m : SRT.moduleManager.getModules()) {
													String wawa = m.getName() + ";" + m.getKey() + ";" + m.isToggled();
													int waaa = 0;
													for(Setting setting : m.getSettings()) {
														if(setting instanceof ModeSetting || setting instanceof NumberSetting || setting instanceof BooleanSetting) {
															if(!(setting instanceof ModeSetting)) {
																wawa += ";" + waaa + ":" + setting.getValue();
															}else {
																ModeSetting modesetting = (ModeSetting)setting;
																wawa += ";" + waaa + ":" + modesetting.getCurrentValue();
															}
															waaa++;
														}
													}
													wawa += "\n";
													configWrite.write(wawa);
												}
												configWrite.close();
												mc.thePlayer.addChatMessage(new ChatComponentText("Config created."));
											} else {
												mc.thePlayer.addChatMessage(new ChatComponentText("Config already exists."));
											}
										} catch (IOException e1) {
											System.out.println("black");
										}
									} else if(commandmsg[1].equalsIgnoreCase("load")) {
										File config = new File("SRT/"+commandmsg[2] + ".txt");
										if (config.exists()) {
											FileReader fr = null;
											try {
												fr = new FileReader(config);
											} catch (FileNotFoundException e2) {
												System.out.println("black");
											}
											BufferedReader br = new BufferedReader(fr);
											StringBuffer sb = new StringBuffer();
											String line;
											try {
												while((line=br.readLine()) != null)
												{
													//System.out.println(line);
													String[] splited = line.split(";");
													ModuleBase module = SRT.moduleManager.getModuleByName(splited[0]);
													module.setKey(Integer.valueOf(splited[1]));
													//System.out.println("set key " + Integer.valueOf(splited[1]) + " for " + module.getName());
													module.setToggled(Boolean.valueOf(splited[2]));
													String[] settings = splited;
													settings = ArrayUtils.remove(settings, 0);
													settings = ArrayUtils.remove(settings, 1);
													if(settings.length > 2){
														settings = ArrayUtils.remove(settings, 2);
													}else {
														continue;
													}
													int black = 0;
													for(String a : settings) {
														if(a.split(":") != null && a.split(":").length > 1) {
															Setting setting = module.getSettings().get(Integer.valueOf(a.split(":")[0]));
															if(setting instanceof ModeSetting) {
																ModeSetting zaza = (ModeSetting)setting;
																zaza.setCurrentValue(a.split(":")[1]);
															}
															if(setting instanceof NumberSetting) {
																NumberSetting zaza = (NumberSetting)setting;
																zaza.setValue(Double.valueOf(a.split(":")[1]));
															}
															if(setting instanceof BooleanSetting) {
																BooleanSetting zaza = (BooleanSetting)setting;
																zaza.setValue(Boolean.valueOf(a.split(":")[1]));
															}
														}
														black++;
													}
												}
											} catch (NumberFormatException | IOException e1) {
												System.out.println("black");
											}
											try {
												fr.close();
											} catch (IOException e1) {
												System.out.println("black");
											}
											mc.thePlayer.addChatMessage(new ChatComponentText("Config loaded."));
										} else {
											mc.thePlayer.addChatMessage(new ChatComponentText("Config doesn't exist."));
										}
									} else if(commandmsg[1].equalsIgnoreCase("list")) {
										File folder = new File("SRT");
										if(folder.exists() && folder.isDirectory()) {
											mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.UNDERLINE + "Configs:"));
											for (final File fileEntry : folder.listFiles()) {
												mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.DARK_PURPLE + FilenameUtils.removeExtension(fileEntry.getName())));
											}
										}
									}
								} else {
									mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.DARK_PURPLE + "config list/save/load <configname>"));
								}
			                }
		                }catch(Exception e1) {
							
						}

                e.setCancelled(true);
            }
        }
    }
}