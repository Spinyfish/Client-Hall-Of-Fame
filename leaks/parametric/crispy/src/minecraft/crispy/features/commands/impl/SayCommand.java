package crispy.features.commands.impl;

import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

@CommandInfo(name = "Say", description = "Bypasses the command filter", syntax = ".say [message]", alias = "say")
public class SayCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if (args.length > 0) {
            String message = args[0];
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendNoEvent(new C01PacketChatMessage(message));
        } else {
            message(getSyntax(), true);
        }
    }
}
