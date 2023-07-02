package me.eldodebug.soar.mixin.mixins.chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;

@Mixin(ChunkRenderDispatcher.class)
public class MixinChunkRenderDispatcher {

    @Inject(method = "getNextChunkUpdate", at = @At("HEAD"))
    public void limitChunkUpdates(final CallbackInfoReturnable<ChunkCompileTaskGenerator> cir) throws InterruptedException {
    	
    	if(Soar.instance.modManager != null && Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Chunk Delay").getValBoolean()) {
    		
            int mode = Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Delay").getValInt();
            
            Thread.sleep((mode == 1) ? 15L : ((mode == 2) ? 50L : ((mode == 3) ? 110L : ((mode == 4) ? 150L : ((mode == 5) ? 200L : -1L)))));
    	}
    }
}
