package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;

public class GameSettingsWrapper extends Wrapper {

	public KeyBindingWrapper keyBindAttack, keyBindSprint, keyBindSneak;
	
	public GameSettingsWrapper(Object instance) {
		super("net.minecraft.client.settings.GameSettings", instance);
		keyBindAttack = new KeyBindingWrapper(keyBindAttack());
		keyBindSprint = new KeyBindingWrapper(keyBindSprint());
		keyBindSneak = new KeyBindingWrapper(keyBindSneak());
	}
	
	public float mouseSensitivity() { return (float) getField("mouseSensitivity"); }

	public int guiScale(){ return (int) getField("guiScale"); }
	
	public void mouseSensitivity(float sensitivity) { setField("mouseSensitivity", sensitivity); }
	
	public Object keyBindAttack() { return getField("keyBindAttack"); }
	
	public Object keyBindSprint() { return getField("keyBindSprint"); }

	public Object keyBindSneak() { return getField("keyBindSneak"); }

	public float getGammaSetting() { return (float) getField("gammaSetting"); }

	public void setGammaSetting(float gamma) { setField("gammaSetting", gamma); }

	public boolean fastRender() { return (boolean) getField("ofFastRender"); }

}
