package me.eldodebug.soar.mixin.mixins.resources;

import java.io.File;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.ResourcePackRepository;

@Mixin(ResourcePackRepository.class)
public class MixinResourcePackRepository {
	
    @Shadow 
    @Final
    private File dirServerResourcepacks;

    @Inject(method = "deleteOldServerResourcesPacks", at = @At("HEAD"))
    private void createDirectory(CallbackInfo ci) {
        if (!this.dirServerResourcepacks.exists()) {
            this.dirServerResourcepacks.mkdirs();
        }
    }
}
