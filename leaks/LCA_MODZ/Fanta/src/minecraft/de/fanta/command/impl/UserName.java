package de.fanta.command.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import de.fanta.command.Command;
import de.fanta.utils.ChatUtil;
import net.minecraft.client.Minecraft;

public class UserName extends Command {
	public UserName() {
		super("setUserName");
	}

	@Override
	public void execute(String[] args) {
		try {
			this.setUsername(args[0]);
			ChatUtil.sendChatMessage(args[0]);

		} catch (Exception e) {

		}
	}

	/*
	 * SFA Required
	 */
	public static void setUsername(String string) {

		Thread t = new Thread(() -> {
			try {
				ChatUtil.sendChatMessageWithPrefix("§7Trying to set username...");
				URL url = new URL("https://api.minecraftservices.com/minecraft/profile/name/" + string);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("PUT");
				conn.setDoOutput(true);
				conn.setRequestProperty("Authorization", "Bearer " + Minecraft.getMinecraft().getSession().getToken());
				conn.setRequestProperty("Content-Type", "application/json");

				ChatUtil.sendChatMessageWithPrefix(
						"Response: " + conn.getResponseCode() + " " + conn.getResponseMessage());
				if (conn.getResponseMessage().equals("OK")) {
					ChatUtil.sendChatMessageWithPrefix("§aUsername was set!");
				} else {
					ChatUtil.sendChatMessageWithPrefix("§cUsername couldn't be set!");
				}

				conn.disconnect();
			} catch (IOException ex) {
				System.err.println("Username couldn't be set!");
				ex.printStackTrace();
			}

		});

		t.start();

	}
}
