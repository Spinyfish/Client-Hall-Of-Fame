/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.November;
import lodomir.dev.commands.Command;
import lodomir.dev.modules.Module;

public class Visible
extends Command {
    public Visible() {
        super("Visible", "Sets module visibility.", "visible <module>", "v");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 2) {
            String moduleName = args[1];
            Module module = November.INSTANCE.getModuleManager().getModule(moduleName);
            if (module.isVisible()) {
                module.setVisible(!module.isVisible());
                November.Log(moduleName + " is now " + (module.isVisible() ? "visible" : "hidden"));
            }
        }
    }
}

