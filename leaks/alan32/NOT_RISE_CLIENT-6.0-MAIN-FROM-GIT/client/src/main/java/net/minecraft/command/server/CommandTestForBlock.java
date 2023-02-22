package net.minecraft.command.server;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.*;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CommandTestForBlock extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "testforblock";
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
        return "commands.testforblock.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 4) {
            throw new WrongUsageException("commands.testforblock.usage");
        } else {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            final BlockPos blockpos = parseBlockPos(sender, args, 0, false);
            final Block block = Block.getBlockFromName(args[3]);

            if (block == null) {
                throw new NumberInvalidException("commands.setblock.notFound", args[3]);
            } else {
                int i = -1;

                if (args.length >= 5) {
                    i = parseInt(args[4], -1, 15);
                }

                final World world = sender.getEntityWorld();

                if (!world.isBlockLoaded(blockpos)) {
                    throw new CommandException("commands.testforblock.outOfWorld");
                } else {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    boolean flag = false;

                    if (args.length >= 6 && block.hasTileEntity()) {
                        final String s = getChatComponentFromNthArg(sender, args, 5).getUnformattedText();

                        try {
                            nbttagcompound = JsonToNBT.getTagFromJson(s);
                            flag = true;
                        } catch (final NBTException nbtexception) {
                            throw new CommandException("commands.setblock.tagError", nbtexception.getMessage());
                        }
                    }

                    final IBlockState iblockstate = world.getBlockState(blockpos);
                    final Block block1 = iblockstate.getBlock();

                    if (block1 != block) {
                        throw new CommandException("commands.testforblock.failed.tile", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()), block1.getLocalizedName(), block.getLocalizedName());
                    } else {
                        if (i > -1) {
                            final int j = iblockstate.getBlock().getMetaFromState(iblockstate);

                            if (j != i) {
                                throw new CommandException("commands.testforblock.failed.data", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()), Integer.valueOf(j), Integer.valueOf(i));
                            }
                        }

                        if (flag) {
                            final TileEntity tileentity = world.getTileEntity(blockpos);

                            if (tileentity == null) {
                                throw new CommandException("commands.testforblock.failed.tileEntity", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()));
                            }

                            final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                            tileentity.writeToNBT(nbttagcompound1);

                            if (!NBTUtil.func_181123_a(nbttagcompound, nbttagcompound1, true)) {
                                throw new CommandException("commands.testforblock.failed.nbt", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()));
                            }
                        }

                        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                        notifyOperators(sender, this, "commands.testforblock.success", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()));
                    }
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : (args.length == 4 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null);
    }
}
