package de.fanta.command.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.fanta.Client;
import de.fanta.command.Command;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

public class Config extends Command {
	private File dir = new File(
			Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/" + "configs" + "/");

	public Config() {
		super("config");
	}

	@Override
	public void execute(String[] args) {
		if (args.length >= 1) {
			switch (args[0]) {
			case "load":
				try {
				if (new File(dir, args[1] + ".txt").exists()) {
					FileUtil.loadValues(args[1], true, false);
					// FileUtil.loadModules();
					messageWithPrefix("Config loaded");
				} else {
					messageWithPrefix("Config does not exist!");
				}
				}catch (Exception e) {
					
				}
				break;
			case "onlineload":
				FileUtil.loadValues(args[1], true, true);
				break;
			case "onlinelist":
				Thread thread = new Thread(() -> {
					ChatUtil.sendChatMessageWithPrefix("Loading...");
					ArrayList<String> configs = new ArrayList<>();
					try {
						URLConnection urlConnection = new URL(
								"https://raw.githubusercontent.com/LCAMODZ/Fanta-configs/main/configs.json")
										.openConnection();
						urlConnection.setConnectTimeout(10000);
						urlConnection.connect();
						try (BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(urlConnection.getInputStream()))) {
							String text;
							while ((text = bufferedReader.readLine()) != null) {
								if (text.contains("404: Not Found")) {
									ChatUtil.sendChatMessageWithPrefix(
											"An error occurred while loading the configs from Github.");
									return;
								}
								configs.add(text);
							}
						}
					} catch (IOException e) {
						ChatUtil.sendChatMessageWithPrefix("An error occurred while loading the configs from Github.");
						e.printStackTrace();
					}
					for (String config : configs) {
						ChatUtil.sendChatMessageWithPrefix(config);
					}
				});
				thread.start();
				break;
			case "save":
				FileUtil.saveValues(args[1], true);
				messageWithPrefix("Config saved");
				// FileUtil.saveModules();
				break;
			case "list":
				try {
					File[] files = dir.listFiles();
					String list = "";
					for (int i = 0; i < files.length; i++) {
						list += ", " + files[i].getName().replace(".txt", "");
					}
					messageWithPrefix("§7Configs: §f" + list.substring(2));
					break;
				} catch (Exception e) {
				}
			}
		} else {
			messageWithPrefix("§7config §8<§bload/save/list/onlineload/onlinelist§8> §8<§bname§8>§f");
		}

	}
}