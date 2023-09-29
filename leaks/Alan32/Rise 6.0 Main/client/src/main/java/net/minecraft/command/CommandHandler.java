package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CommandHandler implements ICommandManager {
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, ICommand> commandMap = Maps.newHashMap();
    private final Set<ICommand> commandSet = Sets.newHashSet();

    public int executeCommand(final ICommandSender sender, String rawCommand) {
        rawCommand = rawCommand.trim();

        if (rawCommand.startsWith("/")) {
            rawCommand = rawCommand.substring(1);
        }

        String[] astring = rawCommand.split(" ");
        final String s = astring[0];
        astring = dropFirstString(astring);
        final ICommand icommand = this.commandMap.get(s);
        final int i = this.getUsernameIndex(icommand, astring);
        int j = 0;

        if (icommand == null) {
            final ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.notFound");
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
        } else if (icommand.canCommandSenderUseCommand(sender)) {
            if (i > -1) {
                final List<Entity> list = PlayerSelector.matchEntities(sender, astring[i], Entity.class);
                final String s1 = astring[i];
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());

                for (final Entity entity : list) {
                    astring[i] = entity.getUniqueID().toString();

                    if (this.tryExecute(sender, astring, icommand, rawCommand)) {
                        ++j;
                    }
                }

                astring[i] = s1;
            } else {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);

                if (this.tryExecute(sender, astring, icommand, rawCommand)) {
                    ++j;
                }
            }
        } else {
            final ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.generic.permission");
            chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation1);
        }

        sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, j);
        return j;
    }

    protected boolean tryExecute(final ICommandSender sender, final String[] args, final ICommand command, final String input) {
        try {
            command.processCommand(sender, args);
            return true;
        } catch (final WrongUsageException wrongusageexception) {
            final ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.usage", new ChatComponentTranslation(wrongusageexception.getMessage(), wrongusageexception.getErrorObjects()));
            chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation2);
        } catch (final CommandException commandexception) {
            final ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception.getMessage(), commandexception.getErrorObjects());
            chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation1);
        } catch (final Throwable var9) {
            final ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception");
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
            logger.warn("Couldn't process command: '" + input + "'");
        }

        return false;
    }

    /**
     * adds the command and any aliases it has to the internal map of available commands
     */
    public ICommand registerCommand(final ICommand command) {
        this.commandMap.put(command.getCommandName(), command);
        this.commandSet.add(command);

        for (final String s : command.getCommandAliases()) {
            final ICommand icommand = this.commandMap.get(s);

            if (icommand == null || !icommand.getCommandName().equals(s)) {
                this.commandMap.put(s, command);
            }
        }

        return command;
    }

    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] dropFirstString(final String[] input) {
        final String[] astring = new String[input.length - 1];
        System.arraycopy(input, 1, astring, 0, input.length - 1);
        return astring;
    }

    public List<String> getTabCompletionOptions(final ICommandSender sender, final String input, final BlockPos pos) {
        final String[] astring = input.split(" ", -1);
        final String s = astring[0];

        if (astring.length == 1) {
            final List<String> list = Lists.newArrayList();

            for (final Entry<String, ICommand> entry : this.commandMap.entrySet()) {
                if (CommandBase.doesStringStartWith(s, entry.getKey()) && entry.getValue().canCommandSenderUseCommand(sender)) {
                    list.add(entry.getKey());
                }
            }

            return list;
        } else {
            if (astring.length > 1) {
                final ICommand icommand = this.commandMap.get(s);

                if (icommand != null && icommand.canCommandSenderUseCommand(sender)) {
                    return icommand.addTabCompletionOptions(sender, dropFirstString(astring), pos);
                }
            }

            return null;
        }
    }

    public List<ICommand> getPossibleCommands(final ICommandSender sender) {
        final List<ICommand> list = Lists.newArrayList();

        for (final ICommand icommand : this.commandSet) {
            if (icommand.canCommandSenderUseCommand(sender)) {
                list.add(icommand);
            }
        }

        return list;
    }

    public Map<String, ICommand> getCommands() {
        return this.commandMap;
    }

    /**
     * Return a command's first parameter index containing a valid username.
     */
    private int getUsernameIndex(final ICommand command, final String[] args) {
        if (command == null) {
            return -1;
        } else {
            for (int i = 0; i < args.length; ++i) {
                if (command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i])) {
                    return i;
                }
            }

            return -1;
        }
    }
}
