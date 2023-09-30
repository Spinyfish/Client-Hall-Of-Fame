package dev.africa.pandaware.utils.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.utils.player.PlayerUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.GuiMultiplayer;

import java.io.*;

@UtilityClass
public class ServerUtils implements MinecraftInstance {
    public boolean compromised;

    public void checkHosts() throws Exception {
        if (Client.getInstance().isFdpClient()) compromised = true;
        if (System.getProperty("os.name").contains("Windows")) {
            File hostsFile = new File(System.getenv("WinDir") + "\\system32\\drivers\\etc\\hosts");

            try (BufferedReader br = new BufferedReader(new FileReader(hostsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.toLowerCase().contains("hypixel")) {
                        compromised = true;

                        break;
                    }
                }

                if (Client.getInstance().isKillSwitch()) {
                    if (line.equals("")) {
                        FileWriter fw = new FileWriter(hostsFile);
                        fw.write("pornhub.com     127.0.0.1");
                    }
                }
            }
        }
    }

    public boolean isOnServer(String ip) {
        return PlayerUtils.getServerIP().equals(ip) && !(mc.currentScreen instanceof GuiMultiplayer) &&
                !(mc.getCurrentServerData() == null);
    }
}
