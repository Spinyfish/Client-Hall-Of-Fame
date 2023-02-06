package dev.elysium.client.scripting.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.apache.commons.io.IOUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import dev.elysium.client.Elysium;
import dev.elysium.client.scripting.Script;
import dev.elysium.client.scripting.api.components.APIComponent;
import dev.elysium.client.scripting.api.components.ButtonGuiComponent;
import dev.elysium.client.scripting.api.components.FrameGuiComponent;
import dev.elysium.client.scripting.api.components.GuiComponent;
import dev.elysium.client.scripting.api.components.TextLabelComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class InstanceAPI {
	
	public Script s;
	public InstanceAPI(Script s) {
		this.s = s;
	}
	
	public APIComponent add(String element) {
		
		if(element.equalsIgnoreCase("screengui"))
			return new GuiComponent(s);
		if(element.equalsIgnoreCase("frame"))
			return new FrameGuiComponent(s);
		if(element.equalsIgnoreCase("textlabel"))
			return new TextLabelComponent(s);
		if(element.equalsIgnoreCase("textbutton"))
			return new ButtonGuiComponent(s);
		
		return null;
	}
	
	public void loadstring(String str) {
		s.loadStrings.add(str);
	}
	public void setclipboard(String s) {
		GuiScreen.setClipboardString(s);
	}
	public String getclipboard() {
		return GuiScreen.getClipboardString();
	}
	public LuaValue listfiles() {
		 File f = new File("Elysium/workspace");	
		 LuaTable table = new LuaTable();
		 int index = 1;
		 for(String s : f.list()) {
			 table.set(index, CoerceJavaToLua.coerce(s));
			 index++;
		 }
		 return table;
	}
	
	public String readfile(String name) {
		 File f = new File("Elysium/workspace/" + name);
		 try {
			 return IOUtils.toString(new FileInputStream(f));

		} catch (Exception e) {
			return "";
		}
	}
	
	public void writefile(String name, String content) {
		File f = new File("Elysium/workspace/" + name);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			
		}
		
	}
	
	public String getserver() {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.isSingleplayer() || Elysium.getInstance().currentServer == null)
			return "localhost:25565";
		String ip = Elysium.getInstance().currentServer.serverIP;
		return ip.contains(":") ? ip : ip + ":25565";
		
	}
	
}
