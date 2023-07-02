package me.eldodebug.soar.mixin.mixins.vertex;

import java.nio.ByteBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.vertex.VertexBuffer;

@Mixin(VertexBuffer.class)
public class MixinVertexBuffer {
	
    @Shadow
    private int glBufferId;

    @Inject(method = "bufferData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/VertexBuffer;bindBuffer()V"), cancellable = true)
    private void ignoreRemovedBuffers(ByteBuffer byteBuffer, CallbackInfo ci) {
        if (this.glBufferId == -1) {
            ci.cancel();
        }
    }
}
