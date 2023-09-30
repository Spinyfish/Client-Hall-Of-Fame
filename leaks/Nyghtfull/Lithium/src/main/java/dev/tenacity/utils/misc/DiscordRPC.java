package dev.tenacity.utils.misc;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import dev.tenacity.Tenacity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DiscordRPC {

    public boolean running, canLoad;
    private Activity activity;
    private Core core;

    public DiscordRPC() {
        try {
            File discordLibrary = downloadNativeLibrary();
            if (discordLibrary == null) {
                System.err.println("Failed to download Discord SDK.");
                System.exit(-1);
            }
            // Initialize the Core
            Core.init(discordLibrary);
            canLoad = true;
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File downloadNativeLibrary() throws IOException {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        String name = "discord_game_sdk";
        String suffix;

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        if (osName.contains("windows")) {
            suffix = ".dll";
        } else if (osName.contains("linux")) {
            suffix = ".so";
        } else if (osName.contains("mac os")) {
            suffix = ".dylib";
        } else {
            throw new RuntimeException("cannot determine OS type: " + osName);
        }

		/*
		Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
		At this point we need the "x86_64" version, as this one is used in the ZIP.
		 */
        if (arch.equals("amd64"))
            arch = "x86_64";

        // Path of Discord's library inside the ZIP
        String zipPath = "lib/" + arch + "/" + name + suffix;

        // Open the URL as a ZipInputStream
        URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");

        HttpsURLConnection connection = (HttpsURLConnection) downloadUrl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");

        ZipInputStream zin = new ZipInputStream(connection.getInputStream());

        // Search for the right file inside the ZIP
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals(zipPath)) {
                // Create a new temporary directory
                // We need to do this, because we may not change the filename on Windows
                File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + name + System.nanoTime());
                if (!tempDir.mkdir())
                    throw new IOException("Cannot create temporary directory");
                tempDir.deleteOnExit();

                // Create a temporary file inside our directory (with a "normal" name)
                File temp = new File(tempDir, name + suffix);
                temp.deleteOnExit();

                // Copy the file in the ZIP to our temporary file
                Files.copy(zin, temp.toPath());

                // We are done, so close the input stream
                zin.close();

                // Return our temporary file
                return temp;
            }
            // next entry
            zin.closeEntry();
        }
        zin.close();
        // We couldn't find the library inside the ZIP
        return null;
    }

    public void start() {
        if (!canLoad || running) return;
        running = true;

        // Set parameters for the Core
        try {
            CreateParams params = new CreateParams();
            params.setClientID(979902812840419328L);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

            // Create the Core
            core = new Core(params);

            activity = new Activity();

            // General information
            activity.setDetails("Release: " + Tenacity.RELEASE.getName());

            // Setting a start time causes an "elapsed" field to appear
            activity.timestamps().setStart(Instant.now());

            // Large image
            activity.assets().setLargeImage("mc");
            activity.assets().setLargeText("Tenacity " + Tenacity.VERSION + " @ intent.store");

            // Finally, update the current activity to our activity
            core.activityManager().updateActivity(activity);

            // Run callbacks forever
            new Thread("Discord RPC") {
                @SuppressWarnings("BusyWait")
                @Override
                public void run() {
                    while (running) {
                        try {
                            ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
                            if (serverData != null) {
                                activity.setState("Playing on " + serverData.serverIP);
                            } else if (Minecraft.getMinecraft().isSingleplayer()) {
                                activity.setState("In singleplayer");
                            } else {
                                activity.setState("Currently idle");
                            }
                            core.activityManager().updateActivity(activity);
                            core.runCallbacks();
                            sleep(20);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            running = false;
            canLoad = false;
        }
    }

}
