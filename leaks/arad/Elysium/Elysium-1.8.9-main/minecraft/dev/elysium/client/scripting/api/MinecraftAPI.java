package dev.elysium.client.scripting.api;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;

public class MinecraftAPI {

	
	private Minecraft real;
	
	public MinecraftAPI(Minecraft real) {
		this.real = real;
	}
	
	public PlayerAPI GetPlayer() {
		return new PlayerAPI(real.thePlayer);
	}
	
	public String HttpGet(String uri) {
		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			return IOUtils.toString(con.getInputStream());
		} catch (Exception e) {
			return "";
		}
		
		
	}
	
}
