package juul.module.render;

import juul.Juul;
import juul.module.Module;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;
//import lol.juul.api.Destroy;

public class SelfDestruct extends Module {

	public SelfDestruct() {
		super("Self Destruct", "Remove Juul from Minecraft", Keyboard.KEY_NONE, Category.RENDER);
	}

	public void onEnable() {
		if (mc.currentScreen() instanceof juul.ui.GUI)
			mc.displayGuiScreen(null);

		Juul.INSTANCE.closed = true;
		if (Juul.INSTANCE.getLookupTable().current != null)
			//Destroy.destroy();

			try {
				Field field = InetAddress.class.getDeclaredField("addressCache");
				field.setAccessible(true);
				Object obj = field.get(null);

				synchronized (obj) {
					Field cacheField = obj.getClass().getDeclaredField("cache");
					cacheField.setAccessible(true);
					Map cacheMap = (Map) cacheField.get(obj);
					cacheMap.clear();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		System.gc();
		System.runFinalization();
	}
	
}
