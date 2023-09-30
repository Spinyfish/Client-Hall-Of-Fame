package wtf.automn.module.impl.visual;


import net.minecraft.client.settings.KeyBinding;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.module.settings.SettingBoolean;

@ModuleInfo(name = "customchat", displayName = "CustomChat", category = Category.VISUAL)

public class ModuleCustomChat extends Module {
    public final SettingBoolean shadow = new SettingBoolean("shadow", true, "Shadow", this, "Shadow");
    public final SettingBoolean glow = new SettingBoolean("glow", false, "Glow", this, "Glow");
    public final SettingBoolean rounded = new SettingBoolean("rounded", true, "Rounded", this, "Rounded");
    @Override
    protected void onDisable() {

    }

    @Override
    protected void onEnable() {
    }
    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {

    }
}
