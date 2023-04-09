package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.enums.PacketDirection;

public class NameProtect extends Module {

    public static String protectedName = "User"; // TODO save nameprotect name like module settings

    public NameProtect(ModuleData moduleData) {
        super(moduleData);
    }

    public static boolean isEnabled() {
        return Client.INSTANCE.getModuleManager().get(NameProtect.class).isToggled();
    }

    @Subscribe
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketDirection() == PacketDirection.INBOUND) {
            if (packetEvent.getPacket() instanceof S02PacketChat) {
                S02PacketChat chat = packetEvent.getPacket();
                IChatComponent chatComponent = chat.getChatComponent();
                if (chatComponent instanceof ChatComponentText && chat.isChat()) {
                    System.out.println(chatComponent.getFormattedText());
                    String text = chatComponent.getFormattedText().replace(mc.thePlayer.getName(), protectedName);
                    packetEvent.setPacket(new S02PacketChat(new ChatComponentText(text)));
                }
            }
        }
    }
}
