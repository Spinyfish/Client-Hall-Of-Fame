package felix.module.impl.player;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.module.Module;
import felix.util.other.PlayerUtil;
import net.minecraft.network.play.server.S02PacketChat;

public final class KillSay extends Module {
	
	private int messageIndex;
	private final String[] MESSAGES = new String[]{"Don't mind me, %s I'm just testing the anticheat!", "Your family tree must be a cactus %s because everyone on it is a prick.", "Back to the lobby you go! %s", "I'm not hacking, %s just wasn't clicking fast enough", "Have fun in spectator %s", "Some babies were dropped on their heads, but %s was clearly thrown at a wall", "I would slap %s but that would be considered animal abuse", "Arguing with %s is like talking to a wall, although a wall could come up with better responses", "Shitted on %s harder than archybot", "%s is so fat they cause the tide to come in", "Your mom your dad, the one's you never had! %s", "How did %s even press the start game button", "Keep barking %s someday you will say something intelligent"};
	
	public KillSay() {
		super("KillSay", 0, ModuleCategory.PLAYER);
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
			String text = packet.getChatComponent().getUnformattedText();
			if (PlayerUtil.isOnServer("mineplex")) {
				text = text.substring(text.indexOf(" "));
			}
			if (text.contains("by " + mc.thePlayer.getGameProfile().getName())) {				
				if (messageIndex >= MESSAGES.length) {
					messageIndex = 0;
				}
				
				mc.thePlayer.sendChatMessage(String.format(MESSAGES[messageIndex], text.split(" ")[0]));
				++messageIndex;
			}
		}
	}
}

