package me.eldodebug.soar.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.hooks.RenderTNTPrimedHook;
import me.eldodebug.soar.management.mods.impl.TNTTimerMod;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.entity.item.EntityTNTPrimed;

@Mixin(RenderTNTPrimed.class)
public class MixinRenderTNTPrimed {

	@Inject(method = "doRender", at = @At("HEAD"))
	public void doRender(EntityTNTPrimed entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
		if(Soar.instance.modManager.getModByClass(TNTTimerMod.class).isToggled()) {
			RenderTNTPrimedHook.doRender((RenderTNTPrimed) (Object) this, entity, x, y, z, partialTicks);
		}
	}
}
