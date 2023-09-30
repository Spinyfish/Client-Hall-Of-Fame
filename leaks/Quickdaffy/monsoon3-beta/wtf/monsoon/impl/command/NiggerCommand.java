/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.command;

import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class NiggerCommand
extends Command {
    public NiggerCommand() {
        super("Nigger");
    }

    @Override
    public void execute(String[] args) {
        PlayerUtil.sendClientMessage("i hate black jews too -JustNathan");
    }
}

