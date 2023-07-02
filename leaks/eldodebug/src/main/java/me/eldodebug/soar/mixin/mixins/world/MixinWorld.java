package me.eldodebug.soar.mixin.mixins.world;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.eldodebug.soar.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

@Mixin(World.class)
public class MixinWorld {
		
    @Shadow 
    @Final 
    public boolean isRemote;
    
    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void checkLightFor(CallbackInfoReturnable<Boolean> cir) {
        if (this.canFullbright()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"getLightFromNeighborsFor", "getLightFromNeighbors", "getRawLight", "getLight(Lnet/minecraft/util/BlockPos;)I", "getLight(Lnet/minecraft/util/BlockPos;Z)I" }, at = @At("HEAD"), cancellable = true)
    private void getLightFromNeighborsFor(CallbackInfoReturnable<Integer> cir) {
        if (this.canFullbright()) {
            cir.setReturnValue(15);
        }
    }

    @Unique
    private boolean canFullbright() {
        return Minecraft.getMinecraft().isCallingFromMinecraftThread() && ClientUtils.isFullbright();
    }
    
    @ModifyVariable(method = "updateEntityWithOptionalForce", at = @At("STORE"), ordinal = 1)
    private boolean updateEntityWithOptionalForce(boolean isForced) {
        return isForced && !this.isRemote;
    }
    
    @Inject(method = "getCollidingBoundingBoxes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb, CallbackInfoReturnable<List<AxisAlignedBB>> cir, List<AxisAlignedBB> list) {
        if (entityIn instanceof EntityTNTPrimed || entityIn instanceof EntityFallingBlock || entityIn instanceof EntityItem || entityIn instanceof EntityFX) {
            cir.setReturnValue(list);
        }
    }
    
    @Redirect(method = "getHorizon", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getHorizon()D", remap = false))
    private double getHorizon(WorldProvider worldProvider) {
        return 0.0D;
    }
}
