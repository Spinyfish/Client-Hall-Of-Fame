package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;
import juul.util.Utils;

public class PlayerControllerMPWrapper extends Wrapper {

	public PlayerControllerMPWrapper(Object instance) {
		super("net.minecraft.client.multiplayer.PlayerControllerMP", instance);
	}
	
	public void windowClick(int windowId, int slotId, int p_78753_3_, int p_78753_4_, Object playerIn) {
		EntityPlayerWrapper player = new EntityPlayerWrapper(playerIn);
		callMethodWithTypes("windowClick", new Object[] { windowId, slotId, p_78753_3_, p_78753_4_, playerIn }, "int", "int", "int", "int", player.getClassNameWithPath());
	}
	
}
