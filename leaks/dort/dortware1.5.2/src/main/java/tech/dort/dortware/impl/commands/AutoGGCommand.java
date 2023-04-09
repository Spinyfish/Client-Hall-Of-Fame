package tech.dort.dortware.impl.commands;

import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.modules.misc.AutoHypixel;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class AutoGGCommand extends Command {

    public AutoGGCommand() {
        super(new CommandData("autogg"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            if (command.equalsIgnoreCase("autogg")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    builder.append(args[i]).append(i == args.length - 1 ? "" : " ");
                }
                String msg = builder.toString();

                if (msg.equals(" ")) {
                    ChatUtil.displayChatMessage("Message cannot be \" \".");
                    return;
                }

                AutoHypixel.ggMsg = msg;
                ChatUtil.displayChatMessage("Changed your Auto GG message to " + msg + ".");
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\nautogg [message]");
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("An unknown error occurred.");
        }
    }
}
