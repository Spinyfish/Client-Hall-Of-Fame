package me.eldodebug.soar.mixin.mixins.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager {

    @Shadow 
    public abstract boolean isSoundPlaying(ISound sound);

    @Shadow 
    @Final 
    private Map<String, ISound> playingSounds;

    private final List<String> pausedSounds = new ArrayList<>();
    
    @Redirect(
        method = "pauseAllSounds",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;pause(Ljava/lang/String;)V", remap = false)
    )
    private void patcher$onlyPauseSoundIfNecessary(@Coerce SoundSystem soundSystem, String sound) {
        if (isSoundPlaying(playingSounds.get(sound))) {
            soundSystem.pause(sound);
            pausedSounds.add(sound);
        }
    }

    @Redirect(
        method = "resumeAllSounds",
        at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", remap = false)
    )
    private Iterator<String> patcher$iterateOverPausedSounds(Set<String> keySet) {
        return pausedSounds.iterator();
    }

    @Inject(method = "resumeAllSounds", at = @At("TAIL"))
    private void patcher$clearPausedSounds(CallbackInfo ci) {
    	pausedSounds.clear();
    }
    
    @Redirect( method = "playSound", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to play unknown soundEvent: {}", ordinal = 0)), at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Lorg/apache/logging/log4j/Marker;Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0, remap = false))
    private void silenceWarning(Logger instance, Marker marker, String s, Object[] objects) {}
}
