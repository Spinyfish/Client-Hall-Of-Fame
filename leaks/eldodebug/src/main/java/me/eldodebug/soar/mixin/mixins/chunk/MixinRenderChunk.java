package me.eldodebug.soar.mixin.mixins.chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.management.events.impl.EventRenderChunkPosition;
import me.eldodebug.soar.mixin.SoarTweaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {

	@Inject(method = "setPosition", at = @At("RETURN"))
	public void setPosition(BlockPos pos, CallbackInfo ci) {
		EventRenderChunkPosition event = new EventRenderChunkPosition((RenderChunk) (Object) this, pos);
		event.call();
	}
	
	@Redirect(method = "deleteGlResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/VertexBuffer;deleteGlBuffers()V"))
	public void cancelDelete(VertexBuffer instance) {
		if(Util.getOSType() == EnumOS.LINUX && Minecraft.getMinecraft().gameSettings.useVbo && SoarTweaker.hasOptifine) {
			return;
		}

		instance.deleteGlBuffers();
	}
}
