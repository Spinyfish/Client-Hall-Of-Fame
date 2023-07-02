package me.eldodebug.soar.mixin.mixins.render;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventCameraRotation;
import me.eldodebug.soar.management.events.impl.EventPlayerHeadRotation;
import me.eldodebug.soar.management.events.impl.EventRender3D;
import me.eldodebug.soar.management.events.impl.EventZoomFov;
import me.eldodebug.soar.management.mods.impl.MinimalBobbingMod;
import me.eldodebug.soar.management.mods.impl.MotionBlurMod;
import me.eldodebug.soar.management.mods.impl.MinimalDamageShakeMod;
import me.eldodebug.soar.management.mods.impl.OldAnimationsMod;
import me.eldodebug.soar.utils.interfaces.IMixinEntityLivingBase;
import me.eldodebug.soar.utils.interfaces.IMixinEntityRenderer;
import me.eldodebug.soar.utils.shader.MotionBlurUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IMixinEntityRenderer {

	@Shadow
	private long renderEndNanoTime;
	
    @Shadow
    private Entity pointedEntity;

    @Shadow
    private Minecraft mc;
    
    @Shadow
    private ShaderGroup theShaderGroup;
    
    @Shadow
    private boolean useShader;
    
    @Shadow
    public abstract void setupViewBobbing(float partialTicks);
    
    @Shadow
    public abstract void hurtCameraEffect(float partialTicks);
    
	private float eyeHeightSubtractor;
	private long lastEyeHeightUpdate;
	
	private float rotationYaw;
	private float prevRotationYaw;
	private float rotationPitch;
	private float prevRotationPitch;
	
	@Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader" + "/Framebuffer;bindFramebuffer(Z)V", shift = At.Shift.BEFORE))
    public void updateCameraAndRender(float partialTicks, long nanoTime, CallbackInfo ci) {
		
        List<ShaderGroup> shaders = new ArrayList<ShaderGroup>();

        if (this.theShaderGroup != null && this.useShader) {
            shaders.add(this.theShaderGroup);
        }

        ShaderGroup motionBlur = MotionBlurUtils.instance.getShader();

        if(Soar.instance.modManager.getModByClass(MotionBlurMod.class).isToggled()) {
            if (motionBlur != null){
                shaders.add(motionBlur);
            }

            for (ShaderGroup shader : shaders){
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                shader.loadShaderGroup(partialTicks);
                GlStateManager.popMatrix();
            }
        }
    }
    
	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setAngles(FF)V"))
	public void updateCameraAndRender(EntityPlayerSP entityPlayerSP, float yaw, float pitch) {
		EventPlayerHeadRotation event = new EventPlayerHeadRotation(yaw, pitch);
		event.call();
		yaw = event.getYaw();
		pitch = event.getPitch();

		if(!event.isCancelled()) {
			entityPlayerSP.setAngles(yaw, pitch);
		}
	}
	
	@Inject(method = "orientCamera", at = @At("HEAD"))
	public void orientCamera(float partialTicks, CallbackInfo ci) {
		
		rotationYaw = mc.getRenderViewEntity().rotationYaw;
		prevRotationYaw = mc.getRenderViewEntity().prevRotationYaw;
		rotationPitch = mc.getRenderViewEntity().rotationPitch;
		prevRotationPitch = mc.getRenderViewEntity().prevRotationPitch;
		float roll = 0;

		EventCameraRotation event = new EventCameraRotation(rotationYaw, rotationPitch, roll);
		event.call();
		
		rotationYaw = event.getYaw();
		rotationPitch = event.getPitch();
		roll = event.getRoll();

		prevRotationYaw = event.getYaw();
		prevRotationPitch = event.getPitch();
		GlStateManager.rotate(event.getRoll(), 0, 0, 1);
	}
	
	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
	public float getRotationYaw(Entity entity) {
		return rotationYaw;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F"))
	public float getPrevRotationYaw(Entity entity) {
		return prevRotationYaw;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F"))
	public float getRotationPitch(Entity entity) {
		return rotationPitch;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F"))
	public float getPrevRotationPitch(Entity entity) {
		return prevRotationPitch;
	}
	
    @Inject(method = "updateShaderGroupSize", at = @At("RETURN"))
    public void updateShaderGroupSize(int width, int height, CallbackInfo ci) {
    	
		if(mc.theWorld == null) {
			return;
		}
		
        if (OpenGlHelper.shadersSupported) {
         	ShaderGroup motionBlur = MotionBlurUtils.instance.getShader();

        	if (motionBlur != null){
        	    motionBlur.createBindFramebuffers(width, height);
        	}
        }
    }
    
    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;setupViewBobbing(F)V"))
    public void setupCameraTransform(EntityRenderer entityRenderer, float f) {
    	if(!Soar.instance.modManager.getModByClass(MinimalBobbingMod.class).isToggled()) {
    		this.setupViewBobbing(f);
    	}
    }
    
	@Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/EntityRenderer;hurtCameraEffect(F)V"))
	public void cancelWorldRotation(EntityRenderer entityRenderer, float f) {
    	if(!Soar.instance.modManager.getModByClass(MinimalDamageShakeMod.class).isToggled()) {
    		this.hurtCameraEffect(f);
    	}
	}
	
    @Inject(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z", shift = At.Shift.BEFORE))
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
    	EventRender3D event = new EventRender3D(partialTicks);
    	event.call();
    }

	@Inject(method = "renderHand", at = @At(value = "HEAD"))
	public void renderHand(float partialTicks, int xOffset, CallbackInfo callback) {
		if(mc.thePlayer != null && Soar.instance.modManager.getModByClass(OldAnimationsMod.class).isToggled() && 
				Soar.instance.settingsManager.getSettingByClass(OldAnimationsMod.class, "Block Hit").getValBoolean()
				&& mc.objectMouseOver != null
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
				&& mc.thePlayer != null
				&& mc.gameSettings.keyBindAttack.isKeyDown() && mc.gameSettings.keyBindUseItem.isKeyDown()
				&& mc.thePlayer.getItemInUseCount() > 0 && (!mc.thePlayer.isSwingInProgress
				|| mc.thePlayer.swingProgressInt >= ((IMixinEntityLivingBase) mc.thePlayer).accessArmSwingAnimationEnd()
				/ 2 || mc.thePlayer.swingProgressInt < 0)) {
			mc.thePlayer.swingProgressInt = -1;
			mc.thePlayer.isSwingInProgress = true;
		}
	}

	@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
	public float orientCamera(Entity entity) {
		if(Soar.instance.modManager.getModByClass(OldAnimationsMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(OldAnimationsMod.class, "Sneak").getValBoolean() && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			float height = player.getEyeHeight();
			if(player.isSneaking()) {
				height += 0.08F;
			}
			float actualEyeHeightSubtractor = player.isSneaking() ? 0.08F : 0;
			long sinceLastUpdate = System.currentTimeMillis() - lastEyeHeightUpdate;
			lastEyeHeightUpdate = System.currentTimeMillis();
			if(actualEyeHeightSubtractor > eyeHeightSubtractor) {
				eyeHeightSubtractor += sinceLastUpdate / 500f;
				if(actualEyeHeightSubtractor < eyeHeightSubtractor) {
					eyeHeightSubtractor = actualEyeHeightSubtractor;
				}
			}
			else if(actualEyeHeightSubtractor < eyeHeightSubtractor) {
				eyeHeightSubtractor -= sinceLastUpdate / 500f;
				if(actualEyeHeightSubtractor > eyeHeightSubtractor) {
					eyeHeightSubtractor = actualEyeHeightSubtractor;
				}
			}
			return height - eyeHeightSubtractor;
		}
		return entity.getEyeHeight();
	}
	
	@Inject(method = "getFOVModifier", at = @At("RETURN"), cancellable = true)
	public void getFOVModifier(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir) {
		EventZoomFov event = new EventZoomFov(cir.getReturnValue());
		event.call();
		
		cir.setReturnValue(event.getFov());
	}
	
    @Inject(method = "renderWorldPass", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0))
    private void renderWorldPassPre(CallbackInfo ci) {
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-0.325F, -0.325F);
    }

    @Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
    private void cancelStreamIndicator(CallbackInfo ci) {
        ci.cancel();
    }
    
    @Inject(method = "renderWorldPass", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0, shift = At.Shift.AFTER))
    private void renderWorldPassPost(CallbackInfo ci) {
        GlStateManager.disablePolygonOffset();
    }
    
    @Override
    public long getRendeerEndNanoTime() {
    	return renderEndNanoTime;
    }
}
