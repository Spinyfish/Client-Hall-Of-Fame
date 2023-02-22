package net.minecraft.command.server;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.List;

public class CommandMessage extends CommandBase {
    public List<String> getCommandAliases() {
        return Arrays.asList("w", "msg");
    }

    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "tell";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 0;
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.message.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.message.usage");
        } else {
            final EntityPlayer entityplayer = getPlayer(sender, args[0]);

            if (entityplayer == sender) {
                throw new PlayerNotFoundException("commands.message.sameTarget");
            } else {
                final IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof EntityPlayer));
                final ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.message.display.incoming", sender.getDisplayName(), ichatcomponent.createCopy());
                final ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.message.display.outgoing", entityplayer.getDisplayName(), ichatcomponent.createCopy());
                chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
                chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
                entityplayer.addChatMessage(chatcomponenttranslation);
                sender.addChatMessage(chatcomponenttranslation1);
            }
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     *
     * @param args  The arguments that were given
     * @param index The argument index that we are checking
     */
    public boolean isUsernameIndex(final String[] args, final int index) {
        return index == 0;
    }
}
