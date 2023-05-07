package client.util.file;

import client.Client;
import net.minecraft.client.Minecraft;

import java.io.File;

public final class FileManager {
    public static final File DIRECTORY = new File(Minecraft.getMinecraft().mcDataDir, Client.NAME);

    public void init() {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
        }
    }
}
