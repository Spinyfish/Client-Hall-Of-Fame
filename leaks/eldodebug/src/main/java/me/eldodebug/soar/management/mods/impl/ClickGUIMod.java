package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventKey;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;

public class ClickGUIMod extends Mod{

	public ClickGUIMod() {
		super("ClickGUI", "Show client settings", ModCategory.OTHER);
	}

	@Override
	public void setup() {
		this.setHide(true);
		this.setToggled(true);
	}
	
	@EventTarget
	public void onKey(EventKey event) {
		if(event.getKey() == Soar.instance.keyBindManager.CLICKGUI.getKeyCode()) {
	    	mc.displayGuiScreen(Soar.instance.guiManager.getClickGUI());
		}
	}
	
    @Override
    public void onDisable() {
        super.onEnable();
        this.setToggled(true);
    }
}
