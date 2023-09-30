package net.minecraft.command;

import com.google.gson.JsonParseException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CommandTitle extends CommandBase {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "title";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.title.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.title.usage");
        } else {
            if (args.length < 3) {
                if ("title".equals(args[1]) || "subtitle".equals(args[1])) {
                    throw new WrongUsageException("commands.title.usage.title");
                }

                if ("times".equals(args[1])) {
                    throw new WrongUsageException("commands.title.usage.times");
                }
            }

            final EntityPlayerMP entityplayermp = getPlayer(sender, args[0]);
            final S45PacketTitle.Type s45packettitle$type = S45PacketTitle.Type.byName(args[1]);

            if (s45packettitle$type != S45PacketTitle.Type.CLEAR && s45packettitle$type != S45PacketTitle.Type.RESET) {
                if (s45packettitle$type == S45PacketTitle.Type.TIMES) {
                    if (args.length != 5) {
                        throw new WrongUsageException("commands.title.usage");
                    } else {
                        final int i = parseInt(args[2]);
                        final int j = parseInt(args[3]);
                        final int k = parseInt(args[4]);
                        final S45PacketTitle s45packettitle2 = new S45PacketTitle(i, j, k);
                        entityplayermp.playerNetServerHandler.sendPacket(s45packettitle2);
                        notifyOperators(sender, this, "commands.title.success");
                    }
                } else if (args.length < 3) {
                    throw new WrongUsageException("commands.title.usage");
                } else {
                    final String s = buildString(args, 2);
                    final IChatComponent ichatcomponent;

                    try {
                        ichatcomponent = IChatComponent.Serializer.jsonToComponent(s);
                    } catch (final JsonParseException jsonparseexception) {
                        final Throwable throwable = ExceptionUtils.getRootCause(jsonparseexception);
                        throw new SyntaxErrorException("commands.tellraw.jsonException", throwable == null ? "" : throwable.getMessage());
                    }

                    final S45PacketTitle s45packettitle1 = new S45PacketTitle(s45packettitle$type, ChatComponentProcessor.processComponent(sender, ichatcomponent, entityplayermp));
                    entityplayermp.playerNetServerHandler.sendPacket(s45packettitle1);
                    notifyOperators(sender, this, "commands.title.success");
                }
            } else if (args.length != 2) {
                throw new WrongUsageException("commands.title.usage");
            } else {
                final S45PacketTitle s45packettitle = new S45PacketTitle(s45packettitle$type, null);
                entityplayermp.playerNetServerHandler.sendPacket(s45packettitle);
                notifyOperators(sender, this, "commands.title.success");
            }
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, S45PacketTitle.Type.getNames()) : null);
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
