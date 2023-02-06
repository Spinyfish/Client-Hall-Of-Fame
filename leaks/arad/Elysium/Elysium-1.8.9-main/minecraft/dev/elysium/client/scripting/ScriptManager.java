package dev.elysium.client.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;

public class ScriptManager {
	
	public static List<Script> scripts = new ArrayList<Script>();
	
	public static void loadScripts() {
		if(!(new File("Elysium/scripts").exists()))
			new File("Elysium/scripts").mkdir();
		if(!(new File("Elysium/autoexec").exists()))
			new File("Elysium/autoexec").mkdir();
		if(!(new File("Elysium/workspace").exists()))
			new File("Elysium/workspace").mkdir();
		 File f = new File("Elysium/autoexec");	
		 for (String scriptFiles : f.list()) {
			 try {
				 scripts.add(Script.newScript(IOUtils.toString(new FileInputStream("Elysium/autoexec/" + scriptFiles), "UTF-8")));
				 
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		 }
	}
	
	public static void unloadScripts() {
		Elysium.getInstance().getModManager().getMods().forEach(m -> {
			if(scripts.contains(m) && m.toggled)
				m.toggle();
		});
		Elysium.getInstance().getModManager().getMods().removeIf(m -> scripts.contains(m));
		
	}
	
}
