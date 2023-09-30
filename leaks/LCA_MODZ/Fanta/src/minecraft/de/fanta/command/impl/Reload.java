package de.fanta.command.impl;

import com.mojang.realmsclient.dto.RealmsServer.McoServerComparator;

import de.fanta.Client;
import de.fanta.command.Command;
import de.fanta.command.CommandManager;
import de.fanta.module.ModuleManager;
import de.fanta.utils.FileUtil;
import net.minecraft.client.Minecraft;

public class Reload extends Command {
    public Reload() {
        super("reload");
    }
   
    @Override
    public void execute(String[] args) {
        if (args.length != 3) {  
            messageWithPrefix(" §7Modules reloaded §f");
            messageWithPrefix("§7Commands reloaded §f");
            FileUtil.save();      
            Client.INSTANCE.moduleManager.modules.clear();
            Client.INSTANCE.moduleManager = new ModuleManager();
            Client.INSTANCE.commandManager = new CommandManager();
            Client.INSTANCE.onStart2();
            FileUtil.loadModules();
            FileUtil.loadKeys();
    		//FileUtil.loadValues("values", false);
            return;
        }
    }
}