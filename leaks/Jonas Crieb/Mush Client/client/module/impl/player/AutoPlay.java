package client.module.impl.player;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.packet.PacketReceiveEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import net.minecraft.network.play.server.S02PacketChat;
@ModuleInfo(name = "AutoPlay", description = "", category = Category.OTHER)
public class AutoPlay extends Module {
    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (event.getPacket() instanceof S02PacketChat){
            final S02PacketChat packet = (S02PacketChat) event.getPacket();
            if (packet.getChatComponent().getUnformattedText().contains("Deseja jogar novamente? CLIQUE AQUI!")){
                mc.thePlayer.sendChatMessage("/play swsolo");
            }
            if (packet.getChatComponent().getUnformattedText().contains("CARNAVAL NO MUSH")){
                mc.thePlayer.sendChatMessage("/play swsolo");
            }
        }
    };
}