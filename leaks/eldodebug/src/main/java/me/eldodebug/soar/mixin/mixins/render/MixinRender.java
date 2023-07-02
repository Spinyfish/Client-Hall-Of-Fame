package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import me.eldodebug.soar.utils.interfaces.IMixinRender;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

@Mixin(Render.class)
public abstract class MixinRender <T extends Entity> implements IMixinRender<T>{

	@Override
	@Invoker("renderName")
	public abstract void doRenderName(T entity, double x, double y, double z);
}
