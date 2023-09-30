package me.jinthium.straight.impl.utils.entity.impl;

import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.commands.FriendCommand;
import me.jinthium.straight.impl.utils.entity.ICheck;
import net.minecraft.entity.Entity;

public class FriendCheck implements ICheck {
    @Override
    public boolean validate(Entity entity) {
        return !FriendCommand.isFriend(entity.getName());
    }
}
