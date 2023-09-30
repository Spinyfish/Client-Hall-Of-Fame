package haptic.util.base;

import haptic.util.movement.MovementUtils;
import haptic.util.network.PacketUtils;
import haptic.util.player.PlayerUtils;
import haptic.util.world.WorldUtils;
import net.minecraft.client.Minecraft;

public interface ModuleBaseUtil {
	
	static Minecraft mc = Minecraft.getMinecraft();
	static final MovementUtils move = new MovementUtils();
	static final PlayerUtils player = new PlayerUtils();
	static final WorldUtils world = new WorldUtils();
	static final PacketUtils packet = new PacketUtils();
	
}