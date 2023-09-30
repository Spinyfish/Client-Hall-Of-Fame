/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package wtf.monsoon.impl.command;

import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class BindCommand
extends Command {
    public BindCommand() {
        super("Bind");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            PlayerUtil.sendClientMessage("Usage: .bind <module> <key> | .bind del <module>");
            return;
        }
        String operation = args[0];
        Module module = null;
        switch (operation) {
            case "del": {
                module = Wrapper.getMonsoon().getModuleManager().getModuleByName(args[1]);
                if (module != null) {
                    module.getKey().setValue(new Bind(0, Bind.Device.KEYBOARD));
                    PlayerUtil.sendClientMessage("Removed the bind for module " + module.getName() + ".");
                    Wrapper.getNotifManager().notify(NotificationType.INFO, "Bind", "Removed the bind for module " + module.getName() + ".");
                    break;
                }
                PlayerUtil.sendClientMessage("No module named " + args[1] + "!");
                Wrapper.getNotifManager().notify(NotificationType.ERROR, "Error", "No module named " + args[1] + "!");
                break;
            }
            case "help": {
                PlayerUtil.sendClientMessage("Usage: .bind <module> <key> | .bind del <module>");
                break;
            }
            default: {
                module = Wrapper.getMonsoon().getModuleManager().getModuleByName(args[0]);
                String keyName = args[1];
                int key = Keyboard.getKeyIndex((String)keyName.toUpperCase());
                if (module != null) {
                    module.getKey().setValue(new Bind(key, Bind.Device.KEYBOARD));
                    PlayerUtil.sendClientMessage("Bound " + module.getName() + " to key " + keyName + ".");
                    Wrapper.getNotifManager().notify(NotificationType.INFO, "Bind", "Bound " + module.getName() + " to key " + keyName + ".");
                    break;
                }
                PlayerUtil.sendClientMessage("No module named " + args[1] + "!");
                Wrapper.getNotifManager().notify(NotificationType.ERROR, "Error", "No module named " + args[1] + "!");
            }
        }
    }
}

