package me.jinthium.straight.impl.modules.misc;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.network.ServerChatEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.StringSetting;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.core.jmx.Server;

public class StreamerMode extends Module {

    private final BooleanSetting scrambleGameID = new BooleanSetting("Scramble GameID", true);
    private final ModeSetting nameMode = new ModeSetting("Name Mode", "Scramble", "Scramble", "Custom", "None");
    private final StringSetting name = new StringSetting("Name", "Straightware User");

    public StreamerMode(){
        super("Streamer Mode", Category.MISC);
        name.addParent(nameMode, r -> nameMode.is("Custom"));
        this.addSettings(nameMode, name, scrambleGameID);
    }

    @Callback
    final EventCallback<ServerChatEvent> serverChatEventEventCallback = event -> {
        if(!nameMode.is("None")){
            final IChatComponent chatComponent = event.getChatComponent();
            if (chatComponent instanceof ChatComponentText) {
                String clientUsername = String.format("%s(%s%s%s)%s", EnumChatFormatting.GRAY, EnumChatFormatting.DARK_PURPLE,
                        Client.INSTANCE.getUser().getUsername(), EnumChatFormatting.GRAY, EnumChatFormatting.RESET);
                final String newMessage = chatComponent.getFormattedText().replace(
                        mc.session.getUsername(), getReplacedName() + " " + clientUsername);

                final ChatComponentText newChatComponentText = new ChatComponentText(newMessage);
                newChatComponentText.setChatStyle(event.getChatComponent().getChatStyle());
                event.setChatComponent(newChatComponentText);
            }
        }
    };

    public String getReplacedName(){
        switch(nameMode.getMode()){
            case "Scramble" -> {
                return EnumChatFormatting.OBFUSCATED + mc.thePlayer.getGameProfile().getName() + EnumChatFormatting.RESET;
            }
            case "Custom" -> {
                return name.getString();
            }
        }
        return mc.thePlayer.getGameProfile().getName();
    }
}
