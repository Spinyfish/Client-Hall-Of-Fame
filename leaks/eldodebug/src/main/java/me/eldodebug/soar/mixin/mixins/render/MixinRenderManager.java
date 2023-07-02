package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.management.events.impl.EventRenderHitbox;
import me.eldodebug.soar.utils.interfaces.ICullable;
import me.eldodebug.soar.utils.interfaces.IMixinRender;
import me.eldodebug.soar.utils.interfaces.IMixinRenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements IMixinRenderManager{

	@Shadow
	public TextureManager renderEngine;
	
	@Shadow
    private double renderPosX;
	
	@Shadow
    private double renderPosY;
	
	@Shadow
    private double renderPosZ;
	
	@Shadow
	public abstract <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn);
	
	@SuppressWarnings("unchecked")
	@Inject(method = "doRenderEntity", at = @At("HEAD"), cancellable = true)
	public void cullEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean hideDebugBox, CallbackInfoReturnable<Boolean> cir) {
		
		if(((ICullable) entity).isCulled()) {
			((IMixinRender<Entity>) getEntityRenderObject(entity)).doRenderName(entity, x, y, z);
			cir.setReturnValue(renderEngine == null);
		}
	}
	
	@Inject(method = "renderDebugBoundingBox", at = @At("HEAD"), cancellable = true)
	public void onRenderHitbox(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
		EventRenderHitbox event = new EventRenderHitbox(entityIn, x, y, z, entityYaw, partialTicks);
		event.call();
		
		if(event.isCancelled()) {
			ci.cancel();
		}
	}
	
	@Override
	public double getRenderPosX() {
		return this.renderPosX;
	}

	@Override
	public double getRenderPosY() {
		return this.renderPosY;
	}

	@Override
	public double getRenderPosZ() {
		return this.renderPosZ;
	}

}
