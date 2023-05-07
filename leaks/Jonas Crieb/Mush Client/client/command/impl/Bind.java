package client.command.impl;

import client.Client;
import client.command.Command;
import client.module.Module;
import client.util.chat.ChatUtil;
import org.lwjgl.input.Keyboard;

public final class Bind extends Command {
    public Bind() {
        super("Binds a module to the given key", "bind", "keybind");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 3) {
            final Module module = Client.INSTANCE.getModuleManager().get(args[1]);

            if (module == null) {
                ChatUtil.display("Invalid module");
                return;
            }

            final String inputCharacter = args[2].toUpperCase();
            final int keyIndex = Keyboard.getKeyIndex(inputCharacter);

            module.setKeyIndex(keyIndex);
            ChatUtil.display("Bound " + module.getDisplayName() + " to " + Keyboard.getKeyName(keyIndex) + ".");
        } else {
            error();
        }
    }
}
