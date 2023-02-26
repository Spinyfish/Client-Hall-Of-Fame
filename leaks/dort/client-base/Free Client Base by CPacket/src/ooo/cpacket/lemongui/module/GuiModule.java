package ooo.cpacket.lemongui.module;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import ooo.cpacket.lemongui.clickgui.ClickGui;
import ooo.cpacket.lemongui.settings.Setting;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.module.Module;

/**
 *  Made by HeroCode & xTrM_
 *  it's free to use
 *  but you have to credit us
 *
 *  @author HeroCode
 */
public class GuiModule extends Module {

	public ClickGui clickgui;
	
    public GuiModule()
    {
        super("ClickGUI", Keyboard.KEY_RSHIFT, Category.HIDDEN);
    }
    
    @Override
    public void onEnable()
    {
    	if(this.clickgui == null)
    		this.clickgui = new ClickGui();
    	
    	mc.displayGuiScreen(this.clickgui);
    	toggle();
    }

	@Override
	public void onDisable() {
		
	}
}
