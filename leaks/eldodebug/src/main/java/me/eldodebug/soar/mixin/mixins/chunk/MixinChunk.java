package me.eldodebug.soar.mixin.mixins.chunk;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.hooks.ChunkHook;
import me.eldodebug.soar.utils.ClientUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(Chunk.class)
public class MixinChunk {
	
	@Shadow
	@Final
    private ClassInheritanceMultiMap<Entity>[] entityLists;
	
	@Shadow
	@Final
    private World worldObj;
    
    @ModifyArg(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V", ordinal = 0), index = 1 )
    private int subtractOneFromY(int y) {
        return y - 1;
    }

    @Inject(method = {"getLightFor", "getLightSubtracted"}, at = @At("HEAD"), cancellable = true)
    private void patchFullbright(CallbackInfoReturnable<Integer> cir) {
        if (ClientUtils.isFullbright()) {
            cir.setReturnValue(15);
        }
    }
    
    @Overwrite
    public IBlockState getBlockState(BlockPos pos) {
        return ChunkHook.getBlockState((Chunk) (Object) this, pos);
    }
    
    @Inject(method = "onChunkUnload", at = @At("HEAD"))
    public void onChunkUnload(CallbackInfo ci) {
		 final List<EntityPlayer> players = new ArrayList<>();
		 
		 for (final ClassInheritanceMultiMap<Entity> classinheritancemultimap : entityLists) { 
			 for (final EntityPlayer player : classinheritancemultimap.getByClass(EntityPlayer.class)) {
				 players.add(player);
			 }
		 }
		 
		 for (final EntityPlayer player : players) {
			 worldObj.updateEntityWithOptionalForce(player, false);
		 }
    }
}
