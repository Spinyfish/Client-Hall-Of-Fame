/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.main;

import dev.codeman.eventbus.EventBus;
import dev.myth.api.logger.Logger;
import dev.myth.api.manager.ManagerManager;
import dev.myth.api.utils.SpotifyAPI;
import dev.myth.api.utils.StringUtil;
import dev.myth.api.utils.process.ProcessUtil;
import dev.myth.api.utils.rotation.GCDHandler;
import dev.myth.managers.FeatureManager;
import dev.myth.api.utils.render.shader.ShaderLoader;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.util.Session;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

import java.io.*;
import java.net.UnknownHostException;

public enum ClientMain {
    INSTANCE("Myth", "X", new String[] { "$kush", "CodeMan", "Auxy" }, BuildType.DEVELOPMENT, "220822" );

    /** Client Values */
    @Getter private final String name, version;
    @Getter private final String[] authors;
    @Getter private final BuildType buildtype;
    @Getter private final String buildDate;

    ClientMain(final String name, final String version, final String[] authors, final BuildType buildType, final String buildDate) {
        this.name = name;
        this.version = version;
        this.authors = authors;
        this.buildtype = buildType;
        this.buildDate = buildDate;
    }

    /** Rofl kartoffel */
    public ManagerManager manager;
    public EventBus eventBus;
    public SpotifyAPI spotifyAPI;

    /** Login */
    @Getter @Setter private String username = "MythClarinet1337";
    @Getter @Setter private String tempKey = "AdolfLP";
    @Getter @Setter private int uid = Integer.MAX_VALUE;
    @Getter @Setter private boolean loggedIn = false;

    /** Other */
    public long startTime;

    /** Method called when the Client is launching */
    public void init() {
        try {
            ViaMCP.getInstance().start();
            ViaMCP.getInstance().initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Minecraft.getMinecraft().session = new Session("OlafScholzLP", "", "", "legacy");
        Minecraft.getMinecraft().gameSettings.fancyGraphics = false;
        Minecraft.getMinecraft().gameSettings.ofFastRender = false;

        Logger.doLog("Starting " + this.name + " " + this.version);
        this.startTime = System.currentTimeMillis();
        this.eventBus = new EventBus();
        this.manager = new ManagerManager();
        this.eventBus.subscribe(this.manager.getManager(FeatureManager.class));

//        this.eventBus.subscribe(new GCDHandler());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            /** LOL KARTOFFEL */
            final String tempDir = System.getProperty("java.io.tmpdir");
            final File baseFolder = new File(tempDir, "4yrfpuer.qyg");

            try {
                FileUtils.forceDelete(baseFolder);
            } catch (Exception ignored) { /** Falls das passiert dann ist uhm ja schlecht gelaufen */ }

            this.manager.shutdown();

            Logger.doLog("Shutdown " + this.name + " " + this.version + " after " + StringUtil.formatTime(System.currentTimeMillis() - this.startTime));
        }));

        new Thread(() -> {
            OldServerPinger serverPinger = new OldServerPinger();
            serverPinger.keepPing = true;
            while (true) {
                try {
                    Thread.sleep(3000);
                    Minecraft mc = Minecraft.getMinecraft();
                    if(!mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.thePlayer != null) {
                        serverPinger.ping(mc.getCurrentServerData());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "Pinger").start();

        new Thread(() -> {
            /** Check if processhacker is running */
            while (true) {
                try {
                    Thread.sleep(150L);

                    if (ProcessUtil.isProcessRunning("ProcessHacker.exe") ||
                            ProcessUtil.isProcessRunning("ProcessHacker64.exe") ||
                            ProcessUtil.isProcessRunning("HTTPDebuggerUI.exe") ||
                            ProcessUtil.isProcessRunning("HTTPDebuggerUI64.exe") ||
                            ProcessUtil.isProcessRunning("HTTPDebugger.exe") ||
                            ProcessUtil.isProcessRunning("HTTPDebugger64.exe") ||
                            ProcessUtil.isProcessRunning("Wireshark.exe"))
                        System.exit(420);

                } catch (Exception ignored) {}
            }
        }, "Rofl").start();

        Logger.doLog("Finished in " + (System.currentTimeMillis() - this.startTime) + "ms");
    }

    public enum BuildType {
        RELEASE("Release"),
        DEVELOPMENT("Development");

        @Getter private final String name;

        BuildType(final String name) {
            this.name = name;
        }
    }
}
