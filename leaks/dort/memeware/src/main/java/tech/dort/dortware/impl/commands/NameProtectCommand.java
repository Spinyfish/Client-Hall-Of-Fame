package tech.dort.dortware.impl.commands;

import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.modules.misc.NameProtect;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class NameProtectCommand extends Command {

    public NameProtectCommand() {
        super(new CommandData("nameprotect"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            if ("nameprotect".equalsIgnoreCase(command)) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    builder.append(args[i]).append(i == args.length - 1 ? "" : " ");
                }
                String name = builder.toString();

                if (name.equals(" ")) {
                    ChatUtil.displayChatMessage("Name cannot be \" \".");
                    return;
                }

                NameProtect.protectedName = name;
                ChatUtil.displayChatMessage("Changed your Name Protect name to " + name + ".");
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\nnameprotect [name]");
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("An unknown error occurred.");
        }
    }
}
