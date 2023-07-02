package me.eldodebug.soar.mixin.mixins.render;

import java.nio.IntBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.utils.interfaces.IMixinWorldRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements IMixinWorldRenderer{

    @Shadow 
    private IntBuffer rawIntBuffer;
    
    @Shadow 
    private VertexFormat vertexFormat;
    
    @Shadow
    private boolean isDrawing;
    
    @Inject(method = "finishDrawing", at = @At(value = "INVOKE", target = "Ljava/nio/ByteBuffer;limit(I)Ljava/nio/Buffer;", remap = false))
    private void finishDrawing(CallbackInfo ci) {
        this.rawIntBuffer.position(0);
    }

    @Inject(method = "endVertex", at = @At("HEAD"))
    private void endVertex(CallbackInfo ci) {
        this.rawIntBuffer.position(this.rawIntBuffer.position() + this.vertexFormat.getIntegerSize());
    }

	@Override
	public boolean isDrawing() {
		return isDrawing;
	}
}
