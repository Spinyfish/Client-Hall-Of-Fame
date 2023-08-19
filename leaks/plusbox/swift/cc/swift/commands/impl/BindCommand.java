package cc.swift.commands.impl;

import cc.swift.Swift;
import cc.swift.commands.Command;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import org.lwjgl.input.Keyboard;

public final class BindCommand extends Command {
    public BindCommand() {
        super("Bind", "Binds modules", new String[]{"b"});
    }

    @Override
    public void onCommand(String[] args) {
        switch (args.length) {
            default:
                ChatUtil.printChatMessage(".bind <module>");
                ChatUtil.printChatMessage(".bind <module> <key>");
                break;
            case 1: {
                if (args[0].equals("list")) {
                    ChatUtil.printChatMessage("Binds:");
                    for (Module module : Swift.INSTANCE.getModuleManager().getModules().values()) {
                        if (module.getKey() != 0) {
                            ChatUtil.printChatMessage(module.getName() + ": " + Keyboard.getKeyName(module.getKey()));
                        }
                    }
                } else {
                    Module mod = Swift.INSTANCE.getModuleManager().getModule(args[0]);
                    ChatUtil.printChatMessage(mod.getName() + " is bound to " + Keyboard.getKeyName(mod.getKey()));
                }
                break;
            }
            case 2: {
                Module mod = Swift.INSTANCE.getModuleManager().getModule(args[0]);
                if (mod == null) {
                    ChatUtil.printChatMessage("Module not found");
                    return;
                }
                int keyBind = Keyboard.getKeyIndex(args[1].toUpperCase());
                mod.setKey(keyBind);
                ChatUtil.printChatMessage(mod.getName() + " is bound to " + Keyboard.getKeyName(mod.getKey()));
                break;
            }
        }

    }
}
