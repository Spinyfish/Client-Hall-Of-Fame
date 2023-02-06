package dev.elysium.client.scripting;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.scripting.api.InstanceAPI;
import dev.elysium.client.scripting.api.MinecraftAPI;
import dev.elysium.client.scripting.api.ScriptAPI;
import net.minecraft.client.Minecraft;
public class Script extends Mod{
	Globals globals = JsePlatform.standardGlobals();
	
	public List<String> loadStrings = new ArrayList<String>();
	
	public Script(String name, String description, Category cat) {
		super(name, description, cat);
	}
	public static Script newScript(String content) {
		System.out.println("NEW LUA SCRIPT LOADING!!!!");
		Script s = new Script("", "", Category.COMBAT);
		s.globals.set("instance", CoerceJavaToLua.coerce(new InstanceAPI(s)));
		s.globals.set("script", CoerceJavaToLua.coerce(new ScriptAPI(s)));
		s.globals.set("game", CoerceJavaToLua.coerce(new MinecraftAPI(Minecraft.getMinecraft())));
		s.globals.load("function loadstring(str)\ninstance:loadstring(str)\nend").call();
		s.globals.load("function getidentity()\nreturn 69\nend").call();
		s.globals.load("function setclipboard(str)\ninstance:setclipboard(str)\nend").call();
		s.globals.load("function getclipboard()\nreturn instance:getclipboard()\nend").call();
		s.globals.load("function listfiles()\nreturn instance:listfiles()\nend").call();
		s.globals.load("function readfile(str)\nreturn instance:readfile(str)\nend").call();
		s.globals.load("function writefile(name, content)\ninstance:writefile(name, content)\nend").call();
		
		LuaValue chunk = s.globals.load(content);
		LuaValue ret = chunk.call();
		if(s.globals.get("name") != LuaNil.NIL && s.globals.get("category") != LuaNil.NIL) {
			s.name = s.globals.get("name").toString();
			s.description = s.globals.get("description").toString();
			s.category = Category.valueOf(s.globals.get("category").toString().toUpperCase());
			Elysium.getInstance().getModManager().getMods().add(s);
		}
		
		for(String str : s.loadStrings) {
			ScriptManager.scripts.add(Script.newScript(str));
		}
		return s;
		 
		 
	}
	
	public void onEnable() {
		super.onEnable();
		try {
			if(globals.get("onEnable").isfunction()) {
				globals.get("onEnable").call();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void onDisable() {
		super.onDisable();
		try {
			if(globals.get("onDisable").isfunction()) {
				globals.get("onDisable").call();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@EventTarget
	public void onEventMotion(EventMotion e) {
		if(globals.get("onMotion").isfunction()) {
			globals.get("onMotion").call(CoerceJavaToLua.coerce(e));
		}
	}
	
	
}