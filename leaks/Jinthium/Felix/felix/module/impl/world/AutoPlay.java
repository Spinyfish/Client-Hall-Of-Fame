package felix.module.impl.world;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.gui.notification.Notifications;
import felix.module.Module;
import felix.util.other.PlayerUtil;
import felix.util.other.TimeHelper;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoPlay extends Module {
	private TimeHelper timer;
	
	private EnumValue<Mode> mode = new EnumValue("AutoPlay Mode", Mode.Solo);
	
	private NumberValue<Integer> delay = new NumberValue<>("Join Delay", 2, 1, 5);
	
    public AutoPlay() {
        super("AutoPlay", 0, ModuleCategory.PLAYER);
        addValues(mode, delay);
        timer = new TimeHelper();
    }
    
    @Override
    public void onEnable() {
    	super.onEnable();
    }
    
    @Override
    public void onDisable() {
    	super.onDisable();
    	timer.reset();
    }

    @Handler
    public void onReceivePacket(final EventPacketReceive eventPacketReceive) {
        if (eventPacketReceive.getPacket() instanceof S02PacketChat) {
            final S02PacketChat s02PacketChat = (S02PacketChat) eventPacketReceive.getPacket();

            if (!s02PacketChat.getChatComponent().getUnformattedText().isEmpty()) {
                String message = s02PacketChat.getChatComponent().getUnformattedText();
                final int delay = this.delay.getValue()*1000;
                if (message.contains("You won! Want to play again?") || message.contains("You died! Want to play again?") && PlayerUtil.isOnServer("hypixel")) {
                	Thread thread = new Thread(){
                		public void run(){
                        	Notifications.getManager().post("AutoPlay", "Sending you to a game in " + delay / 1000);
                        	try { 
                        		Thread.sleep(delay); 
                        	} 
                        	catch (Exception e) {
                        		e.printStackTrace();
                        	};
                        	switch (mode.getValue()) {
	                        	case Solo: {
	                        		mc.getNetHandler().addToSendQueueNoEvent(new C01PacketChatMessage("/play solo_insane"));
	                        		break;
	                        	}
	                        	case Teams: {
	                        		mc.getNetHandler().addToSendQueueNoEvent(new C01PacketChatMessage("/play teams_insane"));
	                        		break;
	                        	}
                        	}
                		}
                	};
                	thread.start();                 
                }
            }
        }
    }
    
    private enum Mode {
    	Solo, Teams;
    }
}
