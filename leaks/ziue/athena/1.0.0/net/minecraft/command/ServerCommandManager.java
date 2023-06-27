package net.minecraft.command;

import net.minecraft.server.*;
import net.minecraft.network.rcon.*;
import net.minecraft.command.server.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.entity.player.*;

public class ServerCommandManager extends CommandHandler implements IAdminCommand
{
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
        }
        else {
            this.registerCommand(new CommandPublishLocalServer());
        }
        CommandBase.setAdminCommander(this);
    }
    
    @Override
    public void notifyOperators(final ICommandSender sender, final ICommand command, final int flags, final String msgFormat, final Object... msgParams) {
        boolean flag = true;
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        if (!sender.sendCommandFeedback()) {
            flag = false;
        }
        final IChatComponent ichatcomponent = new ChatComponentTranslation("chat.type.admin", new Object[] { sender.getName(), new ChatComponentTranslation(msgFormat, msgParams) });
        ichatcomponent.getChatStyle().setColor(EnumChatFormatting.GRAY);
        ichatcomponent.getChatStyle().setItalic(true);
        if (flag) {
            for (final EntityPlayer entityplayer : minecraftserver.getConfigurationManager().func_181057_v()) {
                if (entityplayer != sender && minecraftserver.getConfigurationManager().canSendCommands(entityplayer.getGameProfile()) && command.canCommandSenderUseCommand(sender)) {
                    final boolean flag2 = sender instanceof MinecraftServer && MinecraftServer.getServer().func_183002_r();
                    final boolean flag3 = sender instanceof RConConsoleSource && MinecraftServer.getServer().func_181034_q();
                    if (!flag2 && !flag3 && (sender instanceof RConConsoleSource || sender instanceof MinecraftServer)) {
                        continue;
                    }
                    entityplayer.addChatMessage(ichatcomponent);
                }
            }
        }
        if (sender != minecraftserver && minecraftserver.worldServers[0].getGameRules().getBoolean("logAdminCommands")) {
            minecraftserver.addChatMessage(ichatcomponent);
        }
        boolean flag4 = minecraftserver.worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
        if (sender instanceof CommandBlockLogic) {
            flag4 = ((CommandBlockLogic)sender).shouldTrackOutput();
        }
        if (((flags & 0x1) != 0x1 && flag4) || sender instanceof MinecraftServer) {
            sender.addChatMessage(new ChatComponentTranslation(msgFormat, msgParams));
        }
    }
}
