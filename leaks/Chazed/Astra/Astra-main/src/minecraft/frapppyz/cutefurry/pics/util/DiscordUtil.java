package frapppyz.cutefurry.pics.util;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class DiscordUtil {

    public static void startup() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "!");
        }).build();
        DiscordRPC.discordInitialize("1099662023593312256", handlers, true);
        createNewPresence();
    }

    public static void createNewPresence() {
        DiscordRichPresence rich;
        if(Minecraft.getMinecraft().getCurrentServerData() != null && Minecraft.getMinecraft().thePlayer.getName() != null){
            rich = new DiscordRichPresence.Builder("UID: " + HWIDUtil.UID).setDetails("Cheating on " + Minecraft.getMinecraft().getCurrentServerData().serverIP + ".").build();

        }else{
            rich = new DiscordRichPresence.Builder("UID: " + HWIDUtil.UID).setDetails("In the menus.").build();
        }
        rich.largeImageKey = "astra";
        DiscordRPC.discordUpdatePresence(rich);
    }

}
