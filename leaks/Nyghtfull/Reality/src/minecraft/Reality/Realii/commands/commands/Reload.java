package Reality.Realii.commands.commands;

import Reality.Realii.Client;
import Reality.Realii.commands.Command;
import Reality.Realii.event.EventBus;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.utils.cheats.player.Helper;

public class Reload extends Command {
    public Reload() {
        super("reload", new String[]{"reloadmods"}, "", "reload all modules(config may lose or crash)");
    }

    @Override
    public String execute(String[] var1) {
    	// unregister all the mods
    	for (Module mod : ModuleManager.enabledModules) {
			EventBus.getInstance().unregister(mod);
		}
    	// clear the modules lists
        ModuleManager.modules.clear();
        ModuleManager.enabledModules.clear();
        // unregister ModuleManager::onKey....
        EventBus.getInstance().unregister(Client.instance.getModuleManager());
        
        // reload modules
        Client.instance.getModuleManager().init();
        Helper.sendMessage("Reloaded");
        return null;
    }
}
