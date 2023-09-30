package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface ICommandSender
{
    String getName();

    IChatComponent getDisplayName();

    void addChatMessage(IChatComponent component);

    boolean canCommandSenderUseCommand(int permLevel, String commandName);

    BlockPosition getPosition();

    Vec3 getPositionVector();

    World getEntityWorld();

    Entity getCommandSenderEntity();

    boolean sendCommandFeedback();

    void setCommandStat(CommandResultStats.Type type, int amount);
}
