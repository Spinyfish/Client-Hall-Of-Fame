package me.eldodebug.soar.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.utils.interfaces.ICullable;
import net.minecraft.entity.Entity;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MixinEntity implements ICullable{

	private boolean culled;
	
    @Shadow 
    protected abstract HoverEvent getHoverEvent();
    
    @Shadow 
    public boolean onGround;

    private long cachedDisplayName;
    private IChatComponent cachedDisplayNameIChat;
    
    @Inject(method = "spawnRunningParticles", at = @At("HEAD"), cancellable = true)
    private void spawnRunningParticles(CallbackInfo ci) {
        if (!this.onGround) {
        	ci.cancel();
        }
    }
    
    @Inject(method = "getDisplayName", at = @At("RETURN"))
    protected void getDisplayName1(CallbackInfoReturnable<IChatComponent> cir) {
    	cachedDisplayNameIChat = cir.getReturnValue();
        cachedDisplayName = System.currentTimeMillis();
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    protected void getDisplayName2(CallbackInfoReturnable<IChatComponent> cir) {
        if (System.currentTimeMillis() - cachedDisplayName < 50L) {
            cir.setReturnValue(cachedDisplayNameIChat);
        }
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getHoverEvent()Lnet/minecraft/event/HoverEvent;"))
    private HoverEvent getDisplayName3(Entity instance) {
        return null;
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ChatStyle;setChatHoverEvent(Lnet/minecraft/event/HoverEvent;)Lnet/minecraft/util/ChatStyle;"))
    private ChatStyle getDisplayName4(ChatStyle instance, HoverEvent event) {
        return null;
    }
    
    @Redirect(method = "getBrightnessForRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockLoaded(Lnet/minecraft/util/BlockPos;)Z"))
    public boolean getBrightnessForRender(World world, BlockPos pos) {
        return true;
    }
    
    public boolean isCulled() {
    	return this.culled;
    }
    
    public void setCulled(boolean culled) {
    	this.culled = culled;
    }
}
