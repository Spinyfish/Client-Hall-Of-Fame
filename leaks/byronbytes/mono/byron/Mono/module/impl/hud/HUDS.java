package byron.Mono.module.impl.hud;

import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.HUD;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

@ModuleInterface(name = "HUD", description = "screen.", category = Category.HUD)
public class HUDS extends Module {
	
	 
	  @Override
	  public void setup()
	  {
			super.setup();
			ArrayList<String> options = new ArrayList<>();
			
			rSetting(new Setting("HUDMode", this, "MonoV4", options));
			options.add("MonoV4");
			options.add("Mono");
	        options.add("Byronix");
	        options.add("Wish");
	  }
	  public static boolean isHud = false;

	@Override
	public void onEnable() {
		isHud = true;
	}

	@Override
	public void onDisable() {
		isHud = false;
	}

	@Subscribe
	    public void onUpdate(EventUpdate e)
	    {
	        switch(getSetting("HUDMode").getValString())
	        {
	            case "Mono":
	            	byron.Mono.interfaces.HUD.isMono = true;
	            	byron.Mono.interfaces.HUD.isByronix = false;
	            	byron.Mono.interfaces.HUD.isWish = false;
					HUD.isMonov4 = false;
	                break;
	            case "Byronix":
					HUD.isMonov4 = false;
	            	byron.Mono.interfaces.HUD.isMono = false;
	            	byron.Mono.interfaces.HUD.isByronix = true;
	            	byron.Mono.interfaces.HUD.isWish = false;
	                break;
				case "MonoV4":
					HUD.isMonov4 = true;
					byron.Mono.interfaces.HUD.isMono = false;
					byron.Mono.interfaces.HUD.isByronix = true;
					byron.Mono.interfaces.HUD.isWish = false;
					break;
	            case "Wish":
					HUD.isMonov4 = false;
	             	byron.Mono.interfaces.HUD.isMono = false;
	            	byron.Mono.interfaces.HUD.isByronix = false;
	            	byron.Mono.interfaces.HUD.isWish = true;
	            	

	        }
	    }

}
