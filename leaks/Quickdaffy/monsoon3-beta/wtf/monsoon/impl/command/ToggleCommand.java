/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class ToggleCommand
extends Command {
    public ToggleCommand() {
        super("Toggle");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            PlayerUtil.sendClientMessage("Usage: .toggle <module>");
            return;
        }
        String moduleName = args[0];
        if (Wrapper.getMonsoon().getModuleManager().getModuleByName(moduleName) != null) {
            Module module = Wrapper.getMonsoon().getModuleManager().getModuleByName(moduleName);
            module.toggle();
        }
    }
}

