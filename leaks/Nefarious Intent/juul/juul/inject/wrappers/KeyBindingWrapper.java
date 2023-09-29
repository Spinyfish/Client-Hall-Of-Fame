package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;

public class KeyBindingWrapper extends Wrapper {
	
	public KeyBindingWrapper(Object instance) {
		super("net.minecraft.client.settings.KeyBinding", instance);
	}

	public boolean getIsKeyPressed() { return (boolean) getPrivateField("pressed"); }
	
	public void setPressed(boolean state) { setPrivateField("pressed", state); }

	public int getKeyCode() { return (int) getPrivateField("keyCode"); }
	
}
