package client.command.impl;

import client.Client;
import client.command.Command;
import client.module.Module;
import client.util.chat.ChatUtil;

public final class Toggle extends Command {
    public Toggle() {
        super("Toggles the specified module", "toggle", "t");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 2) {
            final Module module = Client.INSTANCE.getModuleManager().get(args[1]);

            if (module == null) {
                ChatUtil.display("Invalid module");
                return;
            }

            module.toggle();
            ChatUtil.display("Toggled %s", (module.getModuleInfo().name() + " " + (module.isEnabled() ? "on" : "off")));
        } else {
            error();
        }
    }
}
