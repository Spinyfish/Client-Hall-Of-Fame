package today.getbypass;

import org.lwjgl.opengl.Display;

import today.getbypass.module.ModuleManager;

public class GetBypass {
	
	public static GetBypass instance = new GetBypass();
	
	public static String name = "GetBypass", version = "b1.0";
	
	public static ModuleManager moduleManager;
	
	public static void startClient() {
		moduleManager = new ModuleManager();
		
		
		
		
		
		Display.setTitle(name + " " + version);
	}

}
