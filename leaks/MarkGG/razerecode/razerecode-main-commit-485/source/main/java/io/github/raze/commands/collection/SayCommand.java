package io.github.raze.commands.collection;

import io.github.raze.commands.system.BaseCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.Arrays;

public class SayCommand extends BaseCommand {

    public SayCommand() {
        super("Say", "Say stuff with the command prefix", "say <Text>", "s");
    }

    public String onCommand(String[] arguments, String command) {
        if (arguments.length == 1) {
            return "Usage: " + getSyntax();
        }

        String chatMessage = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        (Minecraft.getMinecraft()).thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(chatMessage));
        return "";
    }

}
