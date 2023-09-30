package de.fanta.command.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fanta.Client;
import de.fanta.command.Command;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.FileUtil;
import net.minecraft.client.Minecraft;

public class setSkin extends Command {
	public setSkin() {
		super("setSkin");
	}

	@Override
	public void execute(String[] args) {
	
		this.setSkinToPlayer(args[0]);
	//	this.getSkinURL("bb80a4c1-04a4-4fc4-b080-0b1c897a0830");
		
	}	
	public static void setSkinToPlayer(String setSkin) {
		try {
			try {
				URL url = new URL("https://api.minecraftservices.com/minecraft/profile/skins");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setRequestProperty("Authorization", "Bearer " + Minecraft.getMinecraft().getSession().getToken());
				conn.setRequestProperty("Content-Type", "application/json");
				String data = "{\n    \"variant\": \"classic\",\n    \"url\": \"" + setSkin + "\"\n}";
				byte[] out = data.getBytes(StandardCharsets.UTF_8);
				OutputStream stream = conn.getOutputStream();
				stream.write(out);
				ChatUtil.sendChatMessageWithPrefix(
						"Response: " + conn.getResponseCode() + " " + conn.getResponseMessage());
				if (conn.getResponseMessage().equals("OK")) {
					ChatUtil.sendChatMessageWithPrefix("§aSkin was set!");
				} else {
					ChatUtil.sendChatMessageWithPrefix("§cSkin couldn't be set!");
				}
				conn.disconnect();
			} catch (IOException ex) {
				System.err.println("Username couldn't be set!");
				ex.printStackTrace();
			}
			ChatUtil.sendChatMessage("set Skin to : " + setSkin);

		} catch (Exception e) {
		}
	}
	
	
	public static void getSkinURL(String uid) {
		URL url;
		try {
			url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setDoOutput(true);
	        StringBuilder stringBuilder = new StringBuilder();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String text;
	        while((text = reader.readLine()) != null) {
	            stringBuilder.append(text);
	        }
	        System.out.println(stringBuilder.toString().substring(0, stringBuilder.toString().length()));
	        JsonObject jsonObject = new JsonParser().parse(stringBuilder.toString().substring(1, stringBuilder.toString().length())).getAsJsonObject();
	        JsonObject object2 = jsonObject.get("properties").getAsJsonObject();
	        String value = object2.get("value").getAsString();
	        System.out.println(value + " yikes");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
