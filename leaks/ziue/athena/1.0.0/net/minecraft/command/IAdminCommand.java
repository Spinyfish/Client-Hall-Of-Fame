package net.minecraft.command;

public interface IAdminCommand
{
    void notifyOperators(final ICommandSender p0, final ICommand p1, final int p2, final String p3, final Object... p4);
}
