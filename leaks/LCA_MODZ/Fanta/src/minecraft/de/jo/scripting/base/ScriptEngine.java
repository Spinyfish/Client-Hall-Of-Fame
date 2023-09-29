package de.jo.scripting.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.fanta.Client;
import net.minecraft.client.Minecraft;

public class ScriptEngine {
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	/*Directory where all scripts will be saved*/
	public static final File DIR = new File(Minecraft.getMinecraft().mcDataDir, Client.INSTANCE.name+"/scripts");
	/*Loaded Scripts*/
	private List<Script> scripts;
	
	private ScriptExecutor executor;
	
	public ScriptEngine() {
		if(!DIR.exists() || !DIR.isDirectory()) DIR.mkdir();
		if(scripts == null) scripts = new ArrayList<Script>();
		if(executor == null) executor = new ScriptExecutor();
		if(!(DIR.listFiles().length == 0)) {
			for(File file : DIR.listFiles()) {
				if(file.getName().endsWith(Script.FILE_TYPE)) {
					Script script = new Script(file);
					scripts.add(script);
				}
			}
		}
		for(Script script : scripts) {
			getExecutor().execute(script);
		}
	}
	
	public ScriptExecutor getExecutor() {
		return executor;
	}
	
	public List<Script> getScripts() {
		return scripts;
	}
}
