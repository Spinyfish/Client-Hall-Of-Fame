/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import java.util.ArrayList;
import java.util.List;
import lodomir.dev.November;
import lodomir.dev.commands.Command;

public class Friend
extends Command {
    private final List<String> friends = new ArrayList<String>();

    public Friend() {
        super("Friend", "Adds a player as a friend", "friend", "f");
    }

    @Override
    public void onCommand(String[] args, String command) {
        November.Log("Added \"" + args[0] + "\" as a friend.");
    }
}

