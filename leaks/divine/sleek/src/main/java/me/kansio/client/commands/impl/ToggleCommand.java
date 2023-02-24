package me.kansio.client.commands.impl;

import me.kansio.client.Client;
import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.modules.ModuleManager;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.chat.ChatUtil;
@CommandData(
        name = "toggle",
        description = "Binds a module",
        aliases = {"t"}
)
public class ToggleCommand extends Command {

    @Override
    public void run(String[] args) {
        if (args.length != 1) {
            ChatUtil.log("Specify the module you'd like to toggle please.");
            return;
        }

        String moduleName = args[0];
        ModuleManager moduleManager = Client.getInstance().getModuleManager();

        Module mod = moduleManager.getModuleByNameIgnoreSpace(moduleName);

        if (mod == null) {
            ChatUtil.log("That module doesn't exist.");
            return;
        }

        ChatUtil.log("You've toggled " + moduleName);
        mod.toggle();
    }
}
