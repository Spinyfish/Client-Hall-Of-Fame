/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.CommandResultStats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface ICommandSender {
    public String getName();

    public IChatComponent getDisplayName();

    public void addChatMessage(IChatComponent var1);

    public boolean canCommandSenderUseCommand(int var1, String var2);

    public BlockPos getPosition();

    public Vec3 getPositionVector();

    public World getEntityWorld();

    public Entity getCommandSenderEntity();

    public boolean sendCommandFeedback();

    public void setCommandStat(CommandResultStats.Type var1, int var2);
}

