package felix.command.impl;

import felix.Client;
import felix.command.Command;
import felix.module.Module;
import felix.util.other.Logger;

public class Toggle extends Command {

    public Toggle() {
        super("toggle");
    }

    @Override
    public String usage() {
        return "toggle <module>";
    }

    @Override
    public void executeCommand(String[] commandArguments) {
        if(commandArguments.length != 1) {
            this.printUsage();
        }
        if(commandArguments.length == 1) {
          String moduleName = commandArguments[0];

              if (Client.INSTANCE.getModuleManager().getModule(moduleName) != null) {
                  Module module = Client.INSTANCE.getModuleManager().getModule(moduleName);
                  Logger.print("Toggled " + module.getName());
                  module.toggle();
              } 
              else {
            	  Logger.print("Could not find a module with that name!");
              }
          }
     }
}
