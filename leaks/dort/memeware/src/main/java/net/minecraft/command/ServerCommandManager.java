package net.minecraft.command;

import net.minecraft.command.common.CommandReplaceItem;
import net.minecraft.command.server.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class ServerCommandManager extends CommandHandler implements IAdminCommand {
    // private static final String __OBFID = "CL_00000922";

    public ServerCommandManager() {
        this.registerCommand(new CommandTime());
        this.registerCommand(new CommandGameMode());
        this.registerCommand(new CommandDifficulty());
        this.registerCommand(new CommandDefaultGameMode());
        this.registerCommand(new CommandKill());
        this.registerCommand(new CommandToggleDownfall());
        this.registerCommand(new CommandWeather());
        this.registerCommand(new CommandXP());
        this.registerCommand(new CommandTeleport());
        this.registerCommand(new CommandGive());
        this.registerCommand(new CommandReplaceItem());
        this.registerCommand(new CommandStats());
        this.registerCommand(new CommandEffect());
        this.registerCommand(new CommandEnchant());
        this.registerCommand(new CommandParticle());
        this.registerCommand(new CommandEmote());
        this.registerCommand(new CommandShowSeed());
        this.registerCommand(new CommandHelp());
        this.registerCommand(new CommandDebug());
        this.registerCommand(new CommandMessage());
        this.registerCommand(new CommandBroadcast());
        this.registerCommand(new CommandSetSpawnpoint());
        this.registerCommand(new CommandSetDefaultSpawnpoint());
        this.registerCommand(new CommandGameRule());
        this.registerCommand(new CommandClearInventory());
        this.registerCommand(new CommandTestFor());
        this.registerCommand(new CommandSpreadPlayers());
        this.registerCommand(new CommandPlaySound());
        this.registerCommand(new CommandScoreboard());
        this.registerCommand(new CommandExecuteAt());
        this.registerCommand(new CommandTrigger());
        this.registerCommand(new CommandAchievement());
        this.registerCommand(new CommandSummon());
        this.registerCommand(new CommandSetBlock());
        this.registerCommand(new CommandFill());
        this.registerCommand(new CommandClone());
        this.registerCommand(new CommandCompare());
        this.registerCommand(new CommandBlockData());
        this.registerCommand(new CommandTestForBlock());
        this.registerCommand(new CommandMessageRaw());
        this.registerCommand(new CommandWorldBorder());
        this.registerCommand(new CommandTitle());
        this.registerCommand(new CommandEntityData());

        if (MinecraftServer.getServer().isDedicatedServer()) {
            this.registerCommand(new CommandOp());
            this.registerCommand(new CommandDeOp());
            this.registerCommand(new CommandStop());
            this.registerCommand(new CommandSaveAll());
            this.registerCommand(new CommandSaveOff());
            this.registerCommand(new CommandSaveOn());
            this.registerCommand(new CommandBanIp());
            this.registerCommand(new CommandPardonIp());
            this.registerCommand(new CommandBanPlayer());
            this.registerCommand(new CommandListBans());
            this.registerCommand(new CommandPardonPlayer());
            this.registerCommand(new CommandServerKick());
            this.registerCommand(new CommandListPlayers());
            this.registerCommand(new CommandWhitelist());
            this.registerCommand(new CommandSetPlayerTimeout());
        } else {
            this.registerCommand(new CommandPublishLocalServer());
        }

        CommandBase.setAdminCommander(this);
    }

    public void notifyOperators(ICommandSender sender, ICommand command, int p_152372_3_, String msgFormat, Object... msgParams) {
        boolean var6 = true;
        MinecraftServer var7 = MinecraftServer.getServer();

        if (!sender.sendCommandFeedback()) {
            var6 = false;
        }

        ChatComponentTranslation var8 = new ChatComponentTranslation("chat.type.admin", sender.getName(), new ChatComponentTranslation(msgFormat, msgParams));
        var8.getChatStyle().setColor(EnumChatFormatting.GRAY);
        var8.getChatStyle().setItalic(true);

        if (var6) {
            for (Object o : var7.getConfigurationManager().playerEntityList) {
                EntityPlayer var10 = (EntityPlayer) o;

                if (var10 != sender && var7.getConfigurationManager().canSendCommands(var10.getGameProfile()) && command.canCommandSenderUseCommand(sender)) {
                    var10.addChatMessage(var8);
                }
            }
        }

        if (sender != var7 && var7.worldServers[0].getGameRules().getGameRuleBooleanValue("logAdminCommands")) {
            var7.addChatMessage(var8);
        }

        boolean var11 = var7.worldServers[0].getGameRules().getGameRuleBooleanValue("sendCommandFeedback");

        if (sender instanceof CommandBlockLogic) {
            var11 = ((CommandBlockLogic) sender).func_175571_m();
        }

        if ((p_152372_3_ & 1) != 1 && var11) {
            sender.addChatMessage(new ChatComponentTranslation(msgFormat, msgParams));
        }
    }
}
