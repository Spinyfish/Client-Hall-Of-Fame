package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@HackInfo(name = "PirateSpeak", category = Category.MISC)
public class PirateSpeak extends Hack {

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            if (packet instanceof C01PacketChatMessage) {
                C01PacketChatMessage packetChatMessage = (C01PacketChatMessage) packet;
                e.setCancelled(true);
                new Thread(() -> {
                    String translate = translate(packetChatMessage.getMessage());
                    mc.thePlayer.sendQueue.addToSendNoEvent(new C01PacketChatMessage(translate));
                }).start();
            }
        }
    }

    private String translate(String message) {
        String lol = "";
        try {
            URL url = new URL("https://pirate.monkeyness.com/api/translate?english=" + message.replaceAll(" ", "%20"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            lol = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lol;
    }
}
