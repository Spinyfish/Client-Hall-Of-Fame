/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class FriendCommand
extends Command {
    public FriendCommand() {
        super("friend");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            PlayerUtil.sendClientMessage("Usage: .friend <action> <name>");
            return;
        }
        String action = args[0];
        String name = args[1];
        switch (action) {
            case "add": {
                Wrapper.getMonsoon().getFriendManager().addFriend(name);
                PlayerUtil.sendClientMessage("Added friend with name " + name + ".");
                break;
            }
            case "del": 
            case "remove": {
                if (Wrapper.getMonsoon().getFriendManager().removeFriend(name)) {
                    PlayerUtil.sendClientMessage("Added friend with name " + name + ".");
                    break;
                }
                PlayerUtil.sendClientMessage("Could not find friend with name " + name + ".");
            }
        }
    }
}

