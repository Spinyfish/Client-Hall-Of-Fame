/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.command;

import java.io.File;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.config.ConfigSystem;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class ConfigCommand
extends Command {
    private int configLoadRetries = 0;

    public ConfigCommand() {
        super("Config");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            PlayerUtil.sendClientMessage("Usage: .config <action> <config name>");
            return;
        }
        String action = args[0];
        String name = args[1];
        switch (action) {
            case "save": {
                Wrapper.getMonsoon().getConfigSystem().save(name);
                PlayerUtil.sendClientMessage("Saved config " + name + ".");
                break;
            }
            case "load": {
                if (Wrapper.getMonsoon().getConfigSystem().configExists(name)) {
                    try {
                        if (this.configLoadRetries >= 1) {
                            Wrapper.getMonsoon().getConfigSystem().load(name, true);
                        } else {
                            Wrapper.getMonsoon().getConfigSystem().loadNoCatch(name, false);
                        }
                    }
                    catch (ConfigSystem.ConfigForOldVersionException e) {
                        PlayerUtil.sendClientMessage(e.getMessage());
                        PlayerUtil.sendClientMessage("Support will not be provided by Monsoon for using this config.");
                        PlayerUtil.sendClientMessage("Run the command again to confirm you want to load this config.");
                        break;
                    }
                    PlayerUtil.sendClientMessage("Loaded config " + name + ".");
                    break;
                }
                PlayerUtil.sendClientMessage("A config with the name " + name + " does not exist!");
                break;
            }
            case "delete": {
                if (Wrapper.getMonsoon().getConfigSystem().configExists(name)) {
                    boolean deleted = new File("monsoon" + File.separator + "configs" + File.separator + name + ".json").delete();
                    if (deleted) {
                        PlayerUtil.sendClientMessage("Deleted config " + name + ".");
                        break;
                    }
                    PlayerUtil.sendClientMessage("Failed to delete config " + name + "!");
                    break;
                }
                PlayerUtil.sendClientMessage("A config with the name " + name + " does not exist!");
                break;
            }
            default: {
                PlayerUtil.sendClientMessage("Unknown action: " + action);
                PlayerUtil.sendClientMessage("Valid actions: 'save', 'load', 'delete'");
                PlayerUtil.sendClientMessage("Syntax: .config <action> <configname>");
            }
        }
    }
}

