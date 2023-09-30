package felix.module.impl.player;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.module.Module;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoGG extends Module {
	
    private final String[] strings = new String[]{"1st Killer - ", "1st Place - ", "Winner: ", " - Damage Dealt - ", "1st - ", "Winning Team - ", "Winners: ", "Winner: ", "Winning Team: ", " win the game!", "Top Seeker: ", "1st Place: ", "Last team standing!", "Winner #1 (", "Top Survivors", "Winners - "};

    public AutoGG() {
        super("AutoGG", 0, ModuleCategory.WORLD);
        setHidden(true);
    }
    
    public void onEnable() {
    	super.onEnable();
    }
    
    public void onDisable() {
    	super.onDisable();
    }

    @Handler
    public void onReceivePacket(final EventPacketReceive event) {
        if (event.getPacket() instanceof S02PacketChat) {
            final S02PacketChat packet = (S02PacketChat) event.getPacket();
            for (String str : strings) {
                if (packet.getChatComponent().getUnformattedText().contains(str)) {
                    mc.thePlayer.sendChatMessage("/achat gg");
                }
            }
        }
    }
}