package me.eldodebug.soar.mixin.mixins.texture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventSwitchTexture;
import net.minecraft.client.renderer.texture.TextureMap;

@Mixin(TextureMap.class)
public class MixinTextureMap {

	@Inject(method = "loadTextureAtlas", at = @At("RETURN"))
	public void loadTextureAtlas(CallbackInfo ci) {
        if(Soar.instance.eventManager != null) {
            EventSwitchTexture event = new EventSwitchTexture();
            event.call();
        }
	}
}
