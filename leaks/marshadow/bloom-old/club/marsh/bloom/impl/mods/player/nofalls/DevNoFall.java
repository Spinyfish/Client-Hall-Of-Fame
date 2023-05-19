package club.marsh.bloom.impl.mods.player.nofalls;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import net.minecraft.util.BlockPos;
import club.marsh.bloom.impl.events.UpdateEvent;

public class DevNoFall extends Mode {
	
	public DevNoFall(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer).add(0,-(1 + Math.abs(mc.thePlayer.motionY)),0)) && mc.thePlayer.fallDistance > 3) {
			mc.thePlayer.fallDistance = 0;
			mc.thePlayer.setPosition(e.x,e.y-(1 + Math.abs(mc.thePlayer.motionY))-1,e.z);
			e.y -= -(1 + Math.abs(mc.thePlayer.motionY));
			e.y -= 1;
			e.ground = true;
		}
	}
	
	
}
