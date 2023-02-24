package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.Arrays;
import java.util.List;

@ModuleData(
        name = "Anti Spammer",
        category = ModuleCategory.VISUALS,
        description = "Hides known spammer messages"
)
public class AntiSpam extends Module {

    private List<String> spammers = Arrays.asList("FDPClient", "moonclient.xyz", "Go And play with tired-client.de");

    @Subscribe
    public void onPacket(PacketEvent event) {
        //make sure it's a chat packet :Troll:
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat chatPacket = event.getPacket();
            //get the formatted text
            String formattedText = chatPacket.getChatComponent().getFormattedText();

            //fdp spammer
            if (formattedText.contains("Buy") && formattedText.contains("stop") && formattedText.contains("using")) {
                event.setCancelled(true);
                return;
            }

            //go thru all the blocked strings
            for (String text : spammers) {
                //if it contains a blocked string, hide it
                if (formattedText.toLowerCase().contains(text.toLowerCase())) {
                    System.out.println("[Anti Spammer] Hid the message '" + formattedText + "'.");
                    event.setCancelled(true);
                }
            }
        }
    }
}
