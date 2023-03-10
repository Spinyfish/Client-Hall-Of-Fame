package store.femboy.spring.impl.command.impl;

import org.lwjgl.input.Keyboard;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.command.Command;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.util.AddChatMessage;

public class Bind extends Command {

    public Bind() {
        super("Bind", "Binds a module", "bind <name> <key>", "b");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if(args.length <= 1) {
            AddChatMessage.print("That's not how you bind kek");
        }


        if (args.length == 2) {
            String moduleName = args[0];
            String keyName = args[1];

            for (Module module : Spring.INSTANCE.getManager().modules) {
                if (module.name.equalsIgnoreCase(moduleName)) {
                    module.setKey(Keyboard.getKeyIndex(keyName.toUpperCase()));
                    AddChatMessage.print(String.format("%s is now binded to %s", module.name, Keyboard.getKeyName(module.getKey())));
                    break;
                }
            }
        }
    }
}
