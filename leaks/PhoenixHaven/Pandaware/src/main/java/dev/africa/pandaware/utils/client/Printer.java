package dev.africa.pandaware.utils.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ChatComponentText;
import org.tinylog.Logger;

@UtilityClass
public class Printer implements MinecraftInstance {
    public void chat(Object text) {
        chatComponent(new ChatComponentText(String.format("§c§l%s §7» §f%s",
                Client.getInstance().getManifest().getClientName(), text)));
    }

    public void chatSilent(Object text) {
        chatComponent(new ChatComponentText(String.valueOf(text)));
    }

    public void chatComponent(ChatComponentText text) {
        if (mc.thePlayer != null) {
            mc.ingameGUI.getChatGUI().printChatMessage(text);
        }
    }

    public void consoleError(Object text) {
        Logger.error(String.format("[%s] %s", Client.getInstance().getManifest().getClientName(), text));
    }

    public void consoleInfo(Object text) {
        Logger.info(String.format("[%s] %s", Client.getInstance().getManifest().getClientName(), text));
    }
}
