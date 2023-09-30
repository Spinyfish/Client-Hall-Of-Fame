/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect
extends CommandBase {
    @Override
    public String getCommandName() {
        return "effect";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.effect.usage";
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        int i;
        if (args.length < 2) {
            throw new WrongUsageException("commands.effect.usage", new Object[0]);
        }
        EntityLivingBase entitylivingbase = CommandEffect.getEntity(sender, args[0], EntityLivingBase.class);
        if (args[1].equals("clear")) {
            if (entitylivingbase.getActivePotionEffects().isEmpty()) {
                throw new CommandException("commands.effect.failure.notActive.all", entitylivingbase.getCommandSenderName());
            }
            entitylivingbase.clearActivePotions();
            CommandEffect.notifyOperators(sender, (ICommand)this, "commands.effect.success.removed.all", entitylivingbase.getCommandSenderName());
            return;
        }
        try {
            i = CommandEffect.parseInt(args[1], 1);
        }
        catch (NumberInvalidException numberinvalidexception) {
            Potion potion = Potion.getPotionFromResourceLocation(args[1]);
            if (potion == null) {
                throw numberinvalidexception;
            }
            i = potion.id;
        }
        int j = 600;
        int l = 30;
        int k = 0;
        if (i < 0 || i >= Potion.potionTypes.length || Potion.potionTypes[i] == null) throw new NumberInvalidException("commands.effect.notFound", i);
        Potion potion1 = Potion.potionTypes[i];
        if (args.length >= 3) {
            l = CommandEffect.parseInt(args[2], 0, 1000000);
            j = potion1.isInstant() ? l : l * 20;
        } else if (potion1.isInstant()) {
            j = 1;
        }
        if (args.length >= 4) {
            k = CommandEffect.parseInt(args[3], 0, 255);
        }
        boolean flag = true;
        if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
            flag = false;
        }
        if (l > 0) {
            PotionEffect potioneffect = new PotionEffect(i, j, k, false, flag);
            entitylivingbase.addPotionEffect(potioneffect);
            CommandEffect.notifyOperators(sender, (ICommand)this, "commands.effect.success", new ChatComponentTranslation(potioneffect.getEffectName(), new Object[0]), i, k, entitylivingbase.getCommandSenderName(), l);
            return;
        } else {
            if (!entitylivingbase.isPotionActive(i)) throw new CommandException("commands.effect.failure.notActive", new ChatComponentTranslation(potion1.getName(), new Object[0]), entitylivingbase.getCommandSenderName());
            entitylivingbase.removePotionEffect(i);
            CommandEffect.notifyOperators(sender, (ICommand)this, "commands.effect.success.removed", new ChatComponentTranslation(potion1.getName(), new Object[0]), entitylivingbase.getCommandSenderName());
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandEffect.getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : (args.length == 2 ? CommandEffect.getListOfStringsMatchingLastWord(args, Potion.func_181168_c()) : (args.length == 5 ? CommandEffect.getListOfStringsMatchingLastWord(args, "true", "false") : null));
    }

    protected String[] getAllUsernames() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}

