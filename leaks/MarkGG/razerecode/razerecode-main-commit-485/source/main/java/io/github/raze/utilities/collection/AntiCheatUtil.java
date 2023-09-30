package io.github.raze.utilities.collection;

import io.github.raze.utilities.system.BaseUtility;
import net.minecraft.entity.Entity;

public class AntiCheatUtil implements BaseUtility {

	public static boolean validMotionY(Entity entity, float minMotion, float maxMotion) {
		return entity.motionY > minMotion && entity.motionY < maxMotion;
	}

}
