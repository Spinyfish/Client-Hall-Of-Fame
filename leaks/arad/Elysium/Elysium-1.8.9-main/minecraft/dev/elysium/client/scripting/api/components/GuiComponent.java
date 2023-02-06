package dev.elysium.client.scripting.api.components;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaValue;

import dev.elysium.client.scripting.Script;
import net.minecraft.client.Minecraft;

public class GuiComponent extends APIComponent{

	
	public LuaValue close = null;
	
	
	
	public GuiComponent(Script script) {
		super(script);
	}
	public boolean pauseGame = false;
	
	public void Open() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiScreenWrapper(this));
	}
	
	public void SetPauseGame(boolean value) {
		this.pauseGame = value;
	}
	public void Connect(String s, LuaValue f) {
		if(!f.isfunction())
			return;
		if(s.equalsIgnoreCase("close"))
			close = f;
		
	}
}
