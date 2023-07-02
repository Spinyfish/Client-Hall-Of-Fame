package me.eldodebug.soar.mixin.mixins.render;

import java.awt.Color;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.BlockOverlayMod;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.interfaces.IMixinRenderGlobal;
import me.eldodebug.soar.utils.interfaces.IMixinVisGraph;
import me.eldodebug.soar.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal implements IMixinRenderGlobal {
	
    @Shadow
    private WorldClient theWorld;
    
    private SimpleAnimation animationX = new SimpleAnimation(0.0F);
    private SimpleAnimation animationY = new SimpleAnimation(0.0F);
    private SimpleAnimation animationZ = new SimpleAnimation(0.0F);
    
    @Inject(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", shift = At.Shift.AFTER))
    private void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci) {
        if (Soar.instance.modManager.getModByClass(BlockOverlayMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(BlockOverlayMod.class, "Outline").getValBoolean()) {
            ColorUtils.setColor(ColorUtils.getClientColor(1).getRGB());
        }
    }
    
    @ModifyArg(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glLineWidth(F)V", remap = false))
    private float getLineWidth(float lineWidth) {
        return (Soar.instance.modManager.getModByClass(BlockOverlayMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(BlockOverlayMod.class, "Outline").getValBoolean()) ? Soar.instance.settingsManager.getSettingByClass(BlockOverlayMod.class, "Width").getValInt() : lineWidth;
    }
    
    @Inject(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;drawSelectionBoundingBox(Lnet/minecraft/util/AxisAlignedBB;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void drawSelectionBoxFill(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci, float f, BlockPos blockPos, Block block) {
        if (Soar.instance.modManager.getModByClass(BlockOverlayMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(BlockOverlayMod.class, "Fill").getValBoolean()) {
            ColorUtils.setColor(new Color(ColorUtils.getClientColor(0).getRed(), ColorUtils.getClientColor(0).getGreen(), ColorUtils.getClientColor(0).getBlue(), (int) (Soar.instance.settingsManager.getSettingByClass(BlockOverlayMod.class, "Opacity").getValFloat() * 255)).getRGB());
            
            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;

            animationX.setAnimation(blockPos.getX(), 16);
            animationY.setAnimation(blockPos.getY(), 16);
            animationZ.setAnimation(blockPos.getZ(), 16);
            
            RenderUtils.drawFilledWithGL(block.getSelectedBoundingBox(this.theWorld, blockPos).expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026).offset(-x, -y, -z));
        }
    }
    
    @Inject(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;depthMask(Z)V", ordinal = 0, shift = At.Shift.AFTER))
    private void disableDepth(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci) {
        if (Soar.instance.modManager.getModByClass(BlockOverlayMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(BlockOverlayMod.class, "Depth").getValBoolean()) {
            GlStateManager.disableDepth();
        }
    }

    @Inject(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;depthMask(Z)V", ordinal = 1))
    private void enableDepth(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci) {
        if (Soar.instance.modManager.getModByClass(BlockOverlayMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(BlockOverlayMod.class, "Depth").getValBoolean()) {
            GlStateManager.enableDepth();
        }
    }
    
    @Inject(method = "getVisibleFacings", at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/chunk/VisGraph;<init>()V", shift = At.Shift.AFTER, remap = false), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setLimitScan(CallbackInfoReturnable<Set<EnumFacing>> cir, VisGraph visgraph) {
        ((IMixinVisGraph) visgraph).setLimitScan(true);
    }

	@Override
	public WorldClient getWorldClient() {
		return theWorld;
	}
}
