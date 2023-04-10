package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.time.TimeHelper;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;

@HackInfo(name = "Plugin", category = Category.MISC)
public class Plugin extends Hack {
    private final TimeHelper timer = new TimeHelper();

    @Override
    public void onEnable() {
        mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/"));
        Crispy.addChatMessage("Finding plugins");
        timer.reset();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        timer.reset();
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {

            try {
                EventPacket ep = (EventPacket) e;

                if(ep.getPacket() instanceof S3APacketTabComplete) {
                    S3APacketTabComplete packet = (S3APacketTabComplete) ep.getPacket();
                    String[] commands = packet.func_149630_c();
                    String message = "";
                    int size = 0;
                    for(String command : commands) {
                        String pluginName = command.split(":")[0].substring(1);
                        if(!message.contains(pluginName) && command.contains(":") && !pluginName.equalsIgnoreCase("minecraft") && !pluginName.equalsIgnoreCase("bukkit")) {
                            size++;
                            if(message.isEmpty()) {
                                message += pluginName;
                            } else {
                                message += "\2478, \247a" + pluginName;
                            }
                        }
                    }
                    if (!message.isEmpty()) {
                        toggle();
                        Crispy.addChatMessage("\2477Plugins (\247f" + size + "\2477): \247a " + message + "\2477.");
                    }


                }
                if (timer.hasReached(3000)) {
                    toggle();
                    NotificationPublisher.queue("Plugin", "Took too long canceling", NotificationType.ERROR);
                }
            } catch (Exception andioop) {
                andioop.printStackTrace();
            }
        }
    }
}
