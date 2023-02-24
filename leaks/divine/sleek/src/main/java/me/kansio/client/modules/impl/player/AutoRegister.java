package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.network.PacketUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleData(
        name = "Auto Register",
        description = "Automatically registers you",
        category = ModuleCategory.PLAYER
)
public class AutoRegister extends Module {

    @Subscribe
    public void onChat(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();

            if (packet.getChatComponent().getUnformattedText().contains("/register <password> <repeat password>")) {
                PacketUtil.sendPacketNoEvent(new C01PacketChatMessage("/register SleekCheat SleekCheat"));
                ChatUtil.log("Automatically registered with password 'SleekCheat'");
            }
        }
    }

}
