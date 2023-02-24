package me.kansio.client.commands.impl;

import me.kansio.client.Client;
import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.chat.ChatUtil;
import org.lwjgl.input.Keyboard;
@CommandData(
        name = "binds",
        description = "Lists binds"
)
public class BindsCommand extends Command {

    @Override
    public void run(String[] args) {
        ChatUtil.log("The Current Binds Are:");
        for (Module module : Client.getInstance().getModuleManager().getModules()) {
            if (module.getKeyBind() != 0) {
                ChatUtil.log(module.getName() + " - " + Keyboard.getKeyName(module.getKeyBind()));
            }
        }
    }
}
