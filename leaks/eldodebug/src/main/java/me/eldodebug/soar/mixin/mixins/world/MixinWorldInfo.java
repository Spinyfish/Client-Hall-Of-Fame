package me.eldodebug.soar.mixin.mixins.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.TimeChangerMod;
import net.minecraft.world.storage.WorldInfo;

@Mixin(WorldInfo.class)
public class MixinWorldInfo {

	@Shadow
	private long worldTime;
	
	@Overwrite
	public void setWorldTime(long time) {
		boolean toggle = Soar.instance.modManager.getModByClass(TimeChangerMod.class).isToggled();
		long customTime = (long) Soar.instance.settingsManager.getSettingByClass(TimeChangerMod.class, "Time").getValDouble();
		this.worldTime = toggle ? customTime : time;
	}
}
