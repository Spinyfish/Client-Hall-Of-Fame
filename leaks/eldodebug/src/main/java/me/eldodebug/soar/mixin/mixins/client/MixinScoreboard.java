package me.eldodebug.soar.mixin.mixins.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

@Mixin(Scoreboard.class)
public abstract class MixinScoreboard {
	
    @Shadow 
    public abstract ScorePlayerTeam getTeam(String p_96508_1_);

    @Inject(method = "removeTeam", at = @At("HEAD"), cancellable = true)
    private void removeTeam1(ScorePlayerTeam team, CallbackInfo ci) {
        if (team == null) {
        	ci.cancel();
        }
    }

    @Redirect(method = "removeTeam", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0, remap = false))
    private <K, V> V removeTeam2(Map<K, V> instance, K o) {
        if (o != null) {
        	return instance.remove(o);
        }
        return null;
    }

    @Inject(method = "removeObjective", at = @At("HEAD"), cancellable = true)
    private void removeObjective(ScoreObjective objective, CallbackInfo ci) {
        if (objective == null) {
        	ci.cancel();
        }
    }

    @Redirect(method = "removeObjective", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0, remap = false))
    private <K, V> V removeObjective1(Map<K, V> instance, K o) {
        if (o != null) {
        	return instance.remove(o);
        }
        return null;
    }

    @Inject(method = "createTeam", at = @At(value = "CONSTANT", args = "stringValue=A team with the name '"), cancellable = true)
    private void createTeam(String name, CallbackInfoReturnable<ScorePlayerTeam> cir) {
        cir.setReturnValue(this.getTeam(name));
    }

    @Inject(method = "removePlayerFromTeam", at = @At(value = "CONSTANT", args = "stringValue=Player is either on another team or not on any team. Cannot remove from team '"), cancellable = true)
    private void removePlayerFromTeam(CallbackInfo ci) {
        ci.cancel();
    }
}
