/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.November;
import lodomir.dev.commands.Command;
import lodomir.dev.modules.Module;

public class Panic
extends Command {
    public Panic() {
        super("Panic", "Disables all modules", "panic", "panic");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 1) {
            return;
        }
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            m.setEnabled(false);
        }
    }
}

