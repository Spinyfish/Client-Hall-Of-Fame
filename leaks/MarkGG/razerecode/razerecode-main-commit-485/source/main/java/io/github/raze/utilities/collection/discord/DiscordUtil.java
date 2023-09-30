package io.github.raze.utilities.collection.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DiscordUtil {

	public static File downloadDiscordLibrary() throws IOException {
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

		if (arch.equals("amd64"))
			arch = "x86_64";

		String zipPath = "lib/" + arch + "/" + name + suffix;

		URL download = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");
		HttpURLConnection connection = (HttpURLConnection) download.openConnection();
		connection.setRequestProperty("User-Agent", "discord-game-sdk4j (https://github.com/JnCrMx/discord-game-sdk4j)");
		ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());

		ZipEntry entry;

		while ((entry = zipInputStream.getNextEntry()) != null) {

			if (entry.getName().equals(zipPath)) {
				File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + name + System.nanoTime());

				if (!tempDir.mkdir()) {
					throw new IOException("Cannot create temporary directory");
				}

				tempDir.deleteOnExit();

				File temp = new File(tempDir, name + suffix);
				temp.deleteOnExit();

				Files.copy(zipInputStream, temp.toPath());

				zipInputStream.close();

				return temp;
			}

			zipInputStream.closeEntry();
		}

		zipInputStream.close();

		return null;
	}

	public static void bootstrap() {

		new Thread("Discord RPC Thread") {

			@Override
			public void run() {

				File discordLibrary;

				try {
					discordLibrary = downloadDiscordLibrary();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				if (discordLibrary == null) {
					System.out.println("Failed to Download Discord Game SDK.");
					return;
				}

				Core.init(discordLibrary);

				try (CreateParams params = new CreateParams()) {
					params.setClientID(1101539083009917028L);
					params.setFlags(CreateParams.getDefaultFlags());

					try (Core core = new Core(params)) {
						try (Activity activity = new Activity()) {
							activity.setState("Minecraft 1.8.9");

							activity.timestamps().setStart(Instant.now());

							activity.assets().setLargeImage("logo");

							core.activityManager().updateActivity(activity);
						}

						while (true) {
							core.runCallbacks();

							try {
								Thread.sleep(16);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}.start();
	}

}