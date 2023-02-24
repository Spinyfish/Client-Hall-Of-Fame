package me.kansio.client.commands.impl;

import me.kansio.client.Client;
import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.chat.ChatUtil;


@CommandData(
        name = "panic",
        description = "Disables all modules"
)
public class PanicCommand extends Command {

    @Override
    public void run(String[] args) {
        for (Module mod : Client.getInstance().getModuleManager().getModules()) {
            if (mod.isToggled()) mod.toggle();
        }

        ChatUtil.log("Disabled all modules.");
    }
}
