package net.minecraft.command;

import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;

public interface ICommandSender
{
    String getName();
    
    IChatComponent getDisplayName();
    
    void addChatMessage(final IChatComponent p0);
    
    boolean canCommandSenderUseCommand(final int p0, final String p1);
    
    BlockPos getPosition();
    
    Vec3 getPositionVector();
    
    World getEntityWorld();
    
    Entity getCommandSenderEntity();
    
    boolean sendCommandFeedback();
    
    void setCommandStat(final CommandResultStats.Type p0, final int p1);
}
