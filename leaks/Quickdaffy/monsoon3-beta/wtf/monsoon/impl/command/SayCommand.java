/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.command;

import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class SayCommand
extends Command {
    public SayCommand() {
        super("Say");
    }

    @Override
    public void execute(String[] args) {
        StringBuilder concatenated = new StringBuilder();
        for (String arg : args) {
            concatenated.append(arg).append(" ");
        }
        if (concatenated.charAt(concatenated.length() - 1) == ' ') {
            concatenated.deleteCharAt(concatenated.length() - 1);
        }
        PlayerUtil.sendClientMessage("said '" + concatenated + "'");
        Wrapper.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(concatenated.toString()));
    }
}

