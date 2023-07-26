package com.craftworks.pearclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.craftworks.pearclient.discord.DiscordRP;
import com.craftworks.pearclient.event.EventManager;
import com.craftworks.pearclient.event.EventTarget;
import com.craftworks.pearclient.event.impl.EventClientTick;
import com.craftworks.pearclient.gui.hud.HudScreen;
import com.craftworks.pearclient.gui.panel.ClickGUI;
import com.craftworks.pearclient.gui.splash.SplashPR;
import com.craftworks.pearclient.hud.mods.ModManager;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;

public class PearClient {

    public static PearClient instance = new PearClient();
    private Minecraft mc = Minecraft.getMinecraft();
    private static final Logger logger = LogManager.getLogger();

    public static EventManager eventManager;
    
    public DiscordRP discordRP = new DiscordRP();;
    
    public ModManager modManager;

    public static final String name = "Pear Client";
    public static final String prefix = "[PearLogger] ";
    public static final String version = "1.0.0";
    public static final String branch = "master";
    public static final String mcVersion = "1.8.9";
    public static final String title = name + " " + mcVersion + " (" + version + "/" + branch + ")";

    public void onStartup() {
    	discordRP.onStartup();
    	SplashPR.setProgress(0, "Discord Rich Presence");
        logger.info(prefix + "Starting " + name + " " + mcVersion);
        eventManager = new EventManager();
        eventManager.register(this);
        modManager = new ModManager();
    }

    public void onShutdown() {
    	discordRP.onShutdown();
        logger.info(prefix + "Closing " + name + " " + mcVersion);
        eventManager = new EventManager();
        eventManager.unregister(this);
    }
    
    @EventTarget
	public void onTick(EventClientTick event) {
		if(mc.gameSettings.HUD_CONFIG.isPressed()) {
			mc.displayGuiScreen(new HudScreen());
		}
	}
}
