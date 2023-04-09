package tech.dort.dortware.impl.commands;

import org.lwjgl.input.Keyboard;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class BindCommand extends Command {

    public BindCommand() {
        super(new CommandData("bind", "bind del"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            if (command.equalsIgnoreCase("bind")) {
                if (args[1].equalsIgnoreCase("del")) {
                    Module module = Client.INSTANCE.getModuleManager().getByNameIgnoreSpaceCaseInsensitive(args[2]);
                    ChatUtil.displayChatMessage("Deleted the bind.");
                    module.setKeyBind(0);
                } else {
                    Module module = Client.INSTANCE.getModuleManager().getByNameIgnoreSpaceCaseInsensitive(args[1]);
                    if (module != null) {
                        int key = Keyboard.getKeyIndex(args[2].toUpperCase());
                        if (key != -1) {
                            ChatUtil.displayChatMessage("The new bind is " + Keyboard.getKeyName(key) + ".");
                            module.setKeyBind(key);
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\nbind [module] [key]");
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\nbind del [module]");
        } catch (Exception exception) {
            exception.printStackTrace();
//            ChatUtil.displayChatMessage("Invalid arguments.");
        }
    }
}
