package tech.dort.dortware.impl.commands;

import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.modules.misc.Spammer;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class SpamCommand extends Command {

    public SpamCommand() {
        super(new CommandData("spam", "spammer"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            if ("spam".equalsIgnoreCase(command)) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    builder.append(args[i]).append(i == args.length - 1 ? "" : " ");
                }
                String spam = builder.toString();

                if (spam.equals(" ")) {
                    ChatUtil.displayChatMessage("Spam cannot be \" \".");
                    return;
                }

                Spammer.spamString = spam;
                ChatUtil.displayChatMessage("Changed your Spam message to " + spam + ".");
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\nspam [message]");
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("An unknown error occurred.");
        }
    }
}
