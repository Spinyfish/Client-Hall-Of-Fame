package com.srt;

import org.lwjgl.opengl.Display;

import com.srt.module.ModuleManager;
import com.thunderware.utils.api.DiscordAPI.RPC;

public class SRT {

	public RPC richPresence;
	
	public static SRT instance = new SRT();
	
	public String buildVersion = "1.1.0";
	public static ModuleManager moduleManager = new ModuleManager();
	
	public void setupClient() {
		richPresence = new RPC();
		Display.setTitle("SRT | " + buildVersion);
		richPresence.start();
		moduleManager.setupModules();
	}
	
	public void onClientClose() {
		richPresence.shutdown();
	}
}
