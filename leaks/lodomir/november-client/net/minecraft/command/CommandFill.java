/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandFill
extends CommandBase {
    @Override
    public String getCommandName() {
        return "fill";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.fill.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        int j;
        block24: {
            block19: {
                if (args.length < 7) {
                    throw new WrongUsageException("commands.fill.usage", new Object[0]);
                }
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
                BlockPos blockpos = CommandFill.parseBlockPos(sender, args, 0, false);
                BlockPos blockpos1 = CommandFill.parseBlockPos(sender, args, 3, false);
                Block block = CommandBase.getBlockByText(sender, args[6]);
                int i = 0;
                if (args.length >= 8) {
                    i = CommandFill.parseInt(args[7], 0, 15);
                }
                BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos.getY(), blockpos1.getY()), Math.min(blockpos.getZ(), blockpos1.getZ()));
                BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos.getY(), blockpos1.getY()), Math.max(blockpos.getZ(), blockpos1.getZ()));
                j = (blockpos3.getX() - blockpos2.getX() + 1) * (blockpos3.getY() - blockpos2.getY() + 1) * (blockpos3.getZ() - blockpos2.getZ() + 1);
                if (j > 32768) {
                    throw new CommandException("commands.fill.tooManyBlocks", j, 32768);
                }
                if (blockpos2.getY() < 0 || blockpos3.getY() >= 256) break block19;
                World world = sender.getEntityWorld();
                for (int k = blockpos2.getZ(); k < blockpos3.getZ() + 16; k += 16) {
                    for (int l = blockpos2.getX(); l < blockpos3.getX() + 16; l += 16) {
                        if (world.isBlockLoaded(new BlockPos(l, blockpos3.getY() - blockpos2.getY(), k))) continue;
                        throw new CommandException("commands.fill.outOfWorld", new Object[0]);
                    }
                }
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;
                if (args.length >= 10 && block.hasTileEntity()) {
                    String s = CommandFill.getChatComponentFromNthArg(sender, args, 9).getUnformattedText();
                    try {
                        nbttagcompound = JsonToNBT.getTagFromJson(s);
                        flag = true;
                    }
                    catch (NBTException nbtexception) {
                        throw new CommandException("commands.fill.tagError", nbtexception.getMessage());
                    }
                }
                ArrayList list = Lists.newArrayList();
                j = 0;
                for (int i1 = blockpos2.getZ(); i1 <= blockpos3.getZ(); ++i1) {
                    for (int j1 = blockpos2.getY(); j1 <= blockpos3.getY(); ++j1) {
                        for (int k1 = blockpos2.getX(); k1 <= blockpos3.getX(); ++k1) {
                            TileEntity tileentity;
                            IBlockState iblockstate1;
                            TileEntity tileentity1;
                            BlockPos blockpos4;
                            block20: {
                                block21: {
                                    block23: {
                                        block22: {
                                            blockpos4 = new BlockPos(k1, j1, i1);
                                            if (args.length < 9) break block20;
                                            if (args[8].equals("outline") || args[8].equals("hollow")) break block21;
                                            if (!args[8].equals("destroy")) break block22;
                                            world.destroyBlock(blockpos4, true);
                                            break block20;
                                        }
                                        if (!args[8].equals("keep")) break block23;
                                        if (!world.isAirBlock(blockpos4)) {
                                            continue;
                                        }
                                        break block20;
                                    }
                                    if (!args[8].equals("replace") || block.hasTileEntity()) break block20;
                                    if (args.length > 9) {
                                        Block block1 = CommandBase.getBlockByText(sender, args[9]);
                                        if (world.getBlockState(blockpos4).getBlock() != block1) continue;
                                    }
                                    if (args.length <= 10) break block20;
                                    int l1 = CommandBase.parseInt(args[10]);
                                    IBlockState iblockstate = world.getBlockState(blockpos4);
                                    if (iblockstate.getBlock().getMetaFromState(iblockstate) != l1) {
                                        continue;
                                    }
                                    break block20;
                                }
                                if (k1 != blockpos2.getX() && k1 != blockpos3.getX() && j1 != blockpos2.getY() && j1 != blockpos3.getY() && i1 != blockpos2.getZ() && i1 != blockpos3.getZ()) {
                                    if (!args[8].equals("hollow")) continue;
                                    world.setBlockState(blockpos4, Blocks.air.getDefaultState(), 2);
                                    list.add(blockpos4);
                                    continue;
                                }
                            }
                            if ((tileentity1 = world.getTileEntity(blockpos4)) != null) {
                                if (tileentity1 instanceof IInventory) {
                                    ((IInventory)((Object)tileentity1)).clear();
                                }
                                world.setBlockState(blockpos4, Blocks.barrier.getDefaultState(), block == Blocks.barrier ? 2 : 4);
                            }
                            if (!world.setBlockState(blockpos4, iblockstate1 = block.getStateFromMeta(i), 2)) continue;
                            list.add(blockpos4);
                            ++j;
                            if (!flag || (tileentity = world.getTileEntity(blockpos4)) == null) continue;
                            nbttagcompound.setInteger("x", blockpos4.getX());
                            nbttagcompound.setInteger("y", blockpos4.getY());
                            nbttagcompound.setInteger("z", blockpos4.getZ());
                            tileentity.readFromNBT(nbttagcompound);
                        }
                    }
                }
                for (BlockPos blockpos5 : list) {
                    Block block2 = world.getBlockState(blockpos5).getBlock();
                    world.notifyNeighborsRespectDebug(blockpos5, block2);
                }
                if (j <= 0) {
                    throw new CommandException("commands.fill.failed", new Object[0]);
                }
                break block24;
            }
            throw new CommandException("commands.fill.outOfWorld", new Object[0]);
        }
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, j);
        CommandFill.notifyOperators(sender, (ICommand)this, "commands.fill.success", j);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length > 0 && args.length <= 3 ? CommandFill.func_175771_a(args, 0, pos) : (args.length > 3 && args.length <= 6 ? CommandFill.func_175771_a(args, 3, pos) : (args.length == 7 ? CommandFill.getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : (args.length == 9 ? CommandFill.getListOfStringsMatchingLastWord(args, "replace", "destroy", "keep", "hollow", "outline") : (args.length == 10 && "replace".equals(args[8]) ? CommandFill.getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null))));
    }
}

