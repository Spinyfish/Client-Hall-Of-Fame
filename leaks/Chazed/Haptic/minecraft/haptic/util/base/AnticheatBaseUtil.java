package haptic.util.base;

import haptic.util.anticheat.AnticheatMoveUtil;
import net.minecraft.client.Minecraft;

public interface AnticheatBaseUtil {
	
	static Minecraft mc = Minecraft.getMinecraft();
	static AnticheatMoveUtil move = new AnticheatMoveUtil();
	
}