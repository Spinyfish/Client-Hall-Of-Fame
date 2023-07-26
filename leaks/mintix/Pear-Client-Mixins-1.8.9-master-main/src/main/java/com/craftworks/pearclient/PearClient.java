package com.craftworks.pearclient;

import com.craftworks.pearclient.event.EventManager;
import com.craftworks.pearclient.util.font.FontUtil;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PearClient {

    public static PearClient instance = new PearClient();
    private Minecraft mc = Minecraft.getMinecraft();
    private static final Logger logger = LogManager.getLogger();

    public static EventManager eventManager;

    public static final String name = "Pear Client";
    public static final String prefix = "[PearLogger] ";
    public static final String version = "1.0.0";
    public static final String branch = "master";
    public static final String mcVersion = "1.8.9";
    public static final String title = name + " " + mcVersion + " (" + version + "/" + branch + ")";

    public void onStartup() {
        logger.info(prefix + "Starting " + name + " " + mcVersion);
        eventManager = new EventManager();
        eventManager.unregister(this);
        FontUtil.bootstrap();
    }

    public void onShutdown() {
        logger.info(prefix + "Closing " + name + " " + mcVersion);
        eventManager = new EventManager();
        eventManager.unregister(this);
    }
}
