/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.November;
import lodomir.dev.commands.Command;
import lodomir.dev.modules.Module;

public class Toggle
extends Command {
    public Toggle() {
        super("Toggle", "It toggles a modules by typing module name.", "toggle <name>", "t");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length > 0) {
            String moduleName = args[0];
            boolean foundModule = false;
            for (Module module : November.INSTANCE.moduleManager.modules) {
                if (!module.name.equalsIgnoreCase(moduleName)) continue;
                module.toggle();
                November.Log((module.isEnabled() ? "Enabled" : "Disabled") + " " + moduleName);
                foundModule = true;
                break;
            }
            if (!foundModule) {
                November.Log("Module does not exist.");
            }
        }
    }
}

