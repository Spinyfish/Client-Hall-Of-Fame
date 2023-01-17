package com.alan.clients.command.impl;

import com.alan.clients.command.Command;
import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.util.chat.ChatUtil;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Toggle extends Command {

    public Toggle() {
        super("command.toggle.description", "toggle", "t");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 2) {
            final Module module = Client.INSTANCE.getModuleManager().get(args[1]);

            if (module == null) {
                ChatUtil.display("command.bind.invalidmodule");
                return;
            }

            module.toggle();
            ChatUtil.display("command.toggle.toggled", (module.getModuleInfo().name() + " " + (module.isEnabled() ? "on" : "off")));
        } else {
            error();
        }
    }
}