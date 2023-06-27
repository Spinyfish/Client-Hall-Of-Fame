package rip.athena.client.utils.discord;

import de.jcm.discordgamesdk.activity.*;
import java.util.*;
import java.net.*;
import javax.net.ssl.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.util.zip.*;
import java.io.*;
import de.jcm.discordgamesdk.*;
import rip.athena.client.*;
import java.time.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;

public class DiscordRPC
{
    private boolean running;
    private boolean canLoad;
    private Activity activity;
    private Core core;
    
    public DiscordRPC() {
        try {
            final File discordLibrary = downloadNativeLibrary();
            if (discordLibrary == null) {
                System.err.println("Failed to download Discord SDK.");
                System.exit(-1);
            }
            Core.init(discordLibrary);
            this.canLoad = true;
            this.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static File downloadNativeLibrary() throws IOException {
        final String name = "discord_game_sdk";
        final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String suffix;
        if (osName.contains("windows")) {
            suffix = ".dll";
        }
        else if (osName.contains("linux")) {
            suffix = ".so";
        }
        else {
            if (!osName.contains("mac os")) {
                throw new RuntimeException("Cannot determine OS type: " + osName);
            }
            suffix = ".dylib";
        }
        if (arch.equals("amd64")) {
            arch = "x86_64";
        }
        final String zipPath = "lib/" + arch + "/" + name + suffix;
        final URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");
        final HttpsURLConnection connection = (HttpsURLConnection)downloadUrl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
        try (final ZipInputStream zin = new ZipInputStream(connection.getInputStream())) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equals(zipPath)) {
                    final File tempDir = Files.createTempDirectory("java-" + name + System.nanoTime(), (FileAttribute<?>[])new FileAttribute[0]).toFile();
                    tempDir.deleteOnExit();
                    final File temp = new File(tempDir, name + suffix);
                    temp.deleteOnExit();
                    Files.copy(zin, temp.toPath(), new CopyOption[0]);
                    zin.closeEntry();
                    return temp;
                }
                zin.closeEntry();
            }
        }
        return null;
    }
    
    public void start() {
        if (!this.canLoad || this.running) {
            return;
        }
        this.running = true;
        try {
            final CreateParams params = new CreateParams();
            params.setClientID(620970277249089536L);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
            this.core = new Core(params);
            (this.activity = new Activity()).setDetails("Release: " + Athena.INSTANCE.getClientName());
            this.activity.timestamps().setStart(Instant.now());
            this.activity.assets().setLargeImage("athena-logo");
            this.activity.assets().setLargeText("Athena " + Athena.INSTANCE.getClientVersion() + " @ athena.rip");
            this.core.activityManager().updateActivity(this.activity);
            ServerData serverData;
            new Thread(() -> {
                while (this.running) {
                    try {
                        serverData = Minecraft.getMinecraft().getCurrentServerData();
                        if (serverData != null) {
                            this.activity.setState("Playing on " + serverData.serverIP);
                        }
                        else if (Minecraft.getMinecraft().isSingleplayer()) {
                            this.activity.setState("In Singleplayer");
                        }
                        else {
                            this.activity.setState("Currently Idle");
                        }
                        this.core.activityManager().updateActivity(this.activity);
                        this.core.runCallbacks();
                        Thread.sleep(20L);
                        continue;
                    }
                    catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                        continue;
                    }
                    break;
                }
            }, "Discord RPC").start();
        }
        catch (Exception e3) {
            e3.printStackTrace();
            this.running = false;
            this.canLoad = false;
        }
    }
}
