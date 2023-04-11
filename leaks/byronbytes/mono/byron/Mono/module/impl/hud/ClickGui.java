package byron.Mono.module.impl.hud;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import org.lwjgl.input.Keyboard;

import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;

@ModuleInterface(name = "ClickGUI", description = "Main place for modules.", category = Category.HUD)
public class ClickGui extends Module {
	
    public ClickGui()
    {
        super(Keyboard.KEY_RSHIFT);
		super.setup();
		rSetting(new Setting("Blur", this, false));
		rSetting(new Setting("HideOnTopMessage", this, true));
      
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Mono.INSTANCE.getClickGui());
       }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.displayGuiScreen(Mono.INSTANCE.getClickGui());
    }


}
