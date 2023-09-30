package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.client.Timer;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.EnumChatFormatting;

@ModuleInfo(name = "Plugins", description = "skidded from old stitch", category = Category.PLAYER)
public class PluginFinderModule extends Module {
    private final Timer timer = new Timer();

    public void onEnable() {
        if (mc.isSingleplayer()) {
            Client.getInstance().getNotificationManager().addNotification(Notification.Type.WARNING, "YOU'RE IN SINGLEPLAYER FUCKTARD", 5);
            this.toggle(false);
        }
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            this.timer.reset();
            try {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/"));
            }
            catch (Exception ignored) {
            }
        }
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = e -> {
        if (e.getPacket() instanceof S3APacketTabComplete) {
            S3APacketTabComplete s3APacketTabComplete = e.getPacket();
            String[] commands = s3APacketTabComplete.func_149630_c();
            String message = "";
            int size = 0;
            for (String command : commands) {
                String pluginName = command.split(":")[0].substring(1);
                if (message.contains(pluginName) || !command.contains(":") ||
                        pluginName.equalsIgnoreCase("minecraft") ||
                        pluginName.equalsIgnoreCase("bukkit"))
                    continue;
                ++size;
                message = message.isEmpty() ? message + pluginName : message + ", " + EnumChatFormatting.GREEN + pluginName;
            }
            if(message.isEmpty()) {
                Printer.chat("No plugins OR Server has AntiTabComplete Plugin Installed.");
            } else {
                Printer.chat(String.format("Plugins: (%s) ", new Object[]{String.valueOf(size)}) + EnumChatFormatting.GREEN + message);
            }
            this.toggle(false);
        }
    };

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (this.timer.hasReached(2500)) {
            Printer.chat("No plugins or anti tab complete installed");
            this.toggle(false);
        }
    };
}
