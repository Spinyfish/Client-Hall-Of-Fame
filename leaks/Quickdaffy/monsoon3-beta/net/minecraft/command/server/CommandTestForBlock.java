/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandTestForBlock
extends CommandBase {
    @Override
    public String getCommandName() {
        return "testforblock";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.testforblock.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        int j;
        IBlockState iblockstate;
        Block block1;
        World world;
        if (args.length < 4) {
            throw new WrongUsageException("commands.testforblock.usage", new Object[0]);
        }
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
        BlockPos blockpos = CommandTestForBlock.parseBlockPos(sender, args, 0, false);
        Block block = Block.getBlockFromName(args[3]);
        if (block == null) {
            throw new NumberInvalidException("commands.setblock.notFound", args[3]);
        }
        int i = -1;
        if (args.length >= 5) {
            i = CommandTestForBlock.parseInt(args[4], -1, 15);
        }
        if (!(world = sender.getEntityWorld()).isBlockLoaded(blockpos)) {
            throw new CommandException("commands.testforblock.outOfWorld", new Object[0]);
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        boolean flag = false;
        if (args.length >= 6 && block.hasTileEntity()) {
            String s = CommandTestForBlock.getChatComponentFromNthArg(sender, args, 5).getUnformattedText();
            try {
                nbttagcompound = JsonToNBT.getTagFromJson(s);
                flag = true;
            }
            catch (NBTException nbtexception) {
                throw new CommandException("commands.setblock.tagError", nbtexception.getMessage());
            }
        }
        if ((block1 = (iblockstate = world.getBlockState(blockpos)).getBlock()) != block) {
            throw new CommandException("commands.testforblock.failed.tile", blockpos.getX(), blockpos.getY(), blockpos.getZ(), block1.getLocalizedName(), block.getLocalizedName());
        }
        if (i > -1 && (j = iblockstate.getBlock().getMetaFromState(iblockstate)) != i) {
            throw new CommandException("commands.testforblock.failed.data", blockpos.getX(), blockpos.getY(), blockpos.getZ(), j, i);
        }
        if (flag) {
            TileEntity tileentity = world.getTileEntity(blockpos);
            if (tileentity == null) {
                throw new CommandException("commands.testforblock.failed.tileEntity", blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            tileentity.writeToNBT(nbttagcompound1);
            if (!NBTUtil.func_181123_a(nbttagcompound, nbttagcompound1, true)) {
                throw new CommandException("commands.testforblock.failed.nbt", blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
        }
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
        CommandTestForBlock.notifyOperators(sender, (ICommand)this, "commands.testforblock.success", blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length > 0 && args.length <= 3 ? CommandTestForBlock.func_175771_a(args, 0, pos) : (args.length == 4 ? CommandTestForBlock.getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null);
    }
}

