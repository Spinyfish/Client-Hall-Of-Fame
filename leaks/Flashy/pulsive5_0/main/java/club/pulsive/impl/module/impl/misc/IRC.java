package club.pulsive.impl.module.impl.misc;

import club.pulsive.api.main.Pulsive;
import club.pulsive.client.intent.IRCConnection;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;

@ModuleInfo(name = "IRC", description = "IRC Chat", category = Category.CLIENT)
public class IRC extends Module {

    public Property<Boolean> shareServer = new Property<>("Share Server", true);
    public Property<Boolean> shareUsername = new Property<>("Share Username", true);

    public static IRC getInstance(){
        return Pulsive.INSTANCE.getModuleManager().getModule(IRC.class);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
