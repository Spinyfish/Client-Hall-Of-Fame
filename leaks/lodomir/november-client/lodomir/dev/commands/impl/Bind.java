/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package lodomir.dev.commands.impl;

import lodomir.dev.November;
import lodomir.dev.commands.Command;
import lodomir.dev.modules.Module;
import org.lwjgl.input.Keyboard;

public class Bind
extends Command {
    public Bind() {
        super("Bind", "It Binds a modules by typing module name and hotkey.", "bind <name> <key> | clear", "b");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 2) {
            String moduleName = args[0];
            String keyName = args[1];
            boolean foundModule = false;
            for (Module module : November.INSTANCE.moduleManager.modules) {
                if (!module.name.equalsIgnoreCase(moduleName)) continue;
                module.setKey(Keyboard.getKeyIndex((String)keyName.toUpperCase()));
                November.Log(String.format("Bound %s to %s", module.name, Keyboard.getKeyName((int)module.getKey())));
                foundModule = true;
                break;
            }
            if (!foundModule) {
                November.Log("Module does not exist.");
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                for (Module module : November.INSTANCE.moduleManager.modules) {
                    module.setKey(0);
                }
            }
            November.Log("Cleared all of the binds.");
        }
    }
}

