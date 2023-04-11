package byron.Mono.module.impl.visual;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;

@ModuleInterface(name = "Animations", description = "sword go brr", category = Category.Visual)
public class Animations extends Module{
	
	
	public void setup()
	{
		super.setup();
		ArrayList<String> options = new ArrayList<>();
        options.add("Exhibition");
        options.add("Swoosh");
        options.add("Swing");
        rSetting(new Setting("Sword Animation", this, "Swoosh", options));
		
	}
    @Override
    public void onEnable() {
        super.onEnable();
    }

    
    @Subscribe
    public void onUpdate(EventUpdate e)
    {
        switch(getSetting("Sword Animation").getValString())
        {
            case "Exhibition":
                exhi();
                break;
                
            case "Swoosh":
            	swoosh();
            	break;
            	
            case "Swing":
            	swing();
            	break;
        }
        
}
    
    
    
    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Boolean exhimode = false;
    public static Boolean swooshmode = true;
    public static Boolean swingmode = false;
    
    public void exhi()
    
    {   
     exhimode = true;
     swooshmode = false;
     swingmode = false;
    }
    
    public void swoosh()
    {
    	  exhimode = false;
    	  swooshmode = true;
    	  swingmode = false;
    }
    	
    public void swing()
    {
    	  exhimode = false;
    	  swooshmode = false;
    	  swingmode = true;
    }
    }

