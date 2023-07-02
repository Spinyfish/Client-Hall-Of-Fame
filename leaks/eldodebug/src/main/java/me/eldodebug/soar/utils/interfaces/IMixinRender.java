package me.eldodebug.soar.utils.interfaces;

import net.minecraft.entity.Entity;

public interface IMixinRender <T extends Entity> {
	void doRenderName(T entity, double x, double y, double z);
}
