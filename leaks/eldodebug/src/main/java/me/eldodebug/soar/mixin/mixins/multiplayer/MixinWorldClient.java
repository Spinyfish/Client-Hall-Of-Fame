package me.eldodebug.soar.mixin.mixins.multiplayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventPlaySound;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

@Mixin(WorldClient.class)
public class MixinWorldClient {
	
	@Shadow 
	@Final
	private Minecraft mc;
	
    @ModifyConstant(method = "doVoidFogParticles", constant = @Constant(intValue = 1000))
    private int doVoidFogParticles(int original) {
        return (Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Low Animation Tick").getValBoolean()) ? 100 : original;
    }
    
	@Inject(method = "playSound", at = @At(value = "HEAD"), cancellable = true)
	public void handlePlaySound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay, CallbackInfo ci) {
		EventPlaySound event = new EventPlaySound(soundName, volume, pitch, volume, pitch);
		event.call();
		
		if(event.getPitch() != event.getOriginalPitch() || event.getVolume() != event.getOriginalVolume()) {
			ci.cancel();
			volume = event.getVolume();
			pitch = event.getPitch();
			double distanceSq = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
			
			PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, (float) x, (float) y, (float) z);

			if(distanceDelay && distanceSq > 100.0D) {
				mc.getSoundHandler().playDelayedSound(positionedsoundrecord, (int) (Math.sqrt(distanceSq) / 40.0D * 20.0D));
			} else {
				mc.getSoundHandler().playSound(positionedsoundrecord);
			}
		}
	}
}
