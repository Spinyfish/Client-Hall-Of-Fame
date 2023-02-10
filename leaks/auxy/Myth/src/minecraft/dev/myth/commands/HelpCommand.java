package dev.myth.commands;

import com.sun.security.ntlm.Client;
import dev.myth.api.command.Command;
import dev.myth.api.logger.Logger;
import dev.myth.main.ClientMain;
import dev.myth.managers.CommandManager;
import net.minecraft.util.EnumChatFormatting;

@Command.Info(name = "help")
public class HelpCommand extends Command {

    @Override
    public void run(String[] args) {
        for (Command command : ClientMain.INSTANCE.manager.getManager(CommandManager.class).getCommands().values()) {
            Logger.doLog(EnumChatFormatting.WHITE + command.getName());
        }
    }
}
