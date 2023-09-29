package de.fanta.command.impl;

import de.fanta.Client;
import de.fanta.command.Command;
import de.fanta.design.AltsSaver;
import de.fanta.utils.FileUtil;

public class Toggle extends Command {
    public Toggle() {
        super("toggle");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            messageWithPrefix(" Toggle <Module> ");
            return;
        }
        String module = args[0];
        try {
            Client.INSTANCE.moduleManager.getModule(module).setState();
            messageWithPrefix(" " + Client.INSTANCE.moduleManager.getModule(module).name + " §was " + (Client.INSTANCE.moduleManager.getModule(module).getState() ? "§aenabled" : "§cdisabled"));
            if (Client.INSTANCE.moduleManager.getModule(module).getState()) {
                Client.INSTANCE.moduleManager.getModule(module).onEnable();
                FileUtil.saveModules();      
                FileUtil.saveKeys();
               // AltsSaver.saveAltsToFile();
               
               
                
         
               
            } else {
                Client.INSTANCE.moduleManager.getModule(module).onDisable();
                FileUtil.saveModules();
                FileUtil.saveValues("values", false);
                FileUtil.saveKeys();
              //  AltsSaver.saveAltsToFile();
               
       
               
            }
        } catch (Exception e) {
            messageWithPrefix("Module not found");
        }

    }
}
