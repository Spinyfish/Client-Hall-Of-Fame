package de.fanta.command.impl;

import org.lwjgl.input.Keyboard;

import de.fanta.Client;
import de.fanta.command.Command;
import de.fanta.module.Module;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.FileUtil;

public class Bind extends Command {

    public Bind() {
        super("bind");
    }

    @Override
    public void execute(String[] args) {

        try {
            Module mod = Client.INSTANCE.moduleManager.getModule(args[0]);
            if (args.length != 2) {
            
                messageWithPrefix("§7Bind §8<§bModule§8> §8<§bButton§8>§f");
              
                return;
            }

            String key = args[1];

            if (mod != null) {
            	
                mod.setKeyBind(Keyboard.getKeyIndex(args[1].toUpperCase()));
              
                messageWithPrefix(" §3The Module §5§l" + mod.name + "§r§3 was set on §9§l" + key.toUpperCase() + "§r.");
                FileUtil.saveKeys();
             
            }else {
            	
                messageWithPrefix(" §cThe Module §5§l" + args[0] + "§r§c was not found :( .");
               
            }
        }catch (ArrayIndexOutOfBoundsException e){
        	
            messageWithPrefix(" §7Bind §8<§bModule§8> §8<§bButton§8>§f");
            e.printStackTrace();
        
        }
    }

    }

