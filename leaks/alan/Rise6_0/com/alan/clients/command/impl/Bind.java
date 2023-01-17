package com.alan.clients.command.impl;

import com.alan.clients.command.Command;
import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.util.chat.ChatUtil;
import org.lwjgl.input.Keyboard;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Bind extends Command {

    public Bind() {
        super("command.bind.description", "bind", "keybind");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 3) {
            final Module module = Client.INSTANCE.getModuleManager().get(args[1]);

            if (module == null) {
                ChatUtil.display("command.bind.invalidmodule");
                return;
            }

            final String inputCharacter = args[2].toUpperCase();
            final int keyCode = Keyboard.getKeyIndex(inputCharacter);

            module.setKeyCode(keyCode);
            ChatUtil.display("Bound " + module.getDisplayName() + " to " + Keyboard.getKeyName(keyCode) + ".");
        } else {
            error();
        }
    }
}