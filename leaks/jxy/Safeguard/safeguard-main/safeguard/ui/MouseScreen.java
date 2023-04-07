package intentions.ui;

import org.lwjgl.input.Keyboard;

import intentions.modules.render.ClickGui;
import net.minecraft.client.gui.GuiScreen;

public class MouseScreen extends GuiScreen {

	public MouseScreen() {}
	
	private static int h, w;
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		
		if(Keyboard.KEY_RSHIFT == keyCode) {
			ClickGui.enable = false;
			mc.displayGuiScreen(null);
		}
		
	}
	
	@Override
	public void initGui() {
		w = width;
		h = height;
	}

	public static int getHeight() {
		return h;
	}
	public static int getWidth() {
		return w;
	}
	
}
