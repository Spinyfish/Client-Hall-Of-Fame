package me.eldodebug.soar.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.HypixelMod;
import me.eldodebug.soar.utils.server.HypixelUtils;
import me.eldodebug.soar.utils.server.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumChatFormatting;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends Render<AbstractClientPlayer>{

    protected MixinRenderPlayer(RenderManager renderManager) {
		super(renderManager);
	}

	@Redirect(method = "renderRightArm", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;isSneak:Z", ordinal = 0))
    private void resetArmState(ModelPlayer modelPlayer, boolean value) {
        modelPlayer.isRiding = modelPlayer.isSneak = false;
    }
	
	@Inject(method = "renderOffsetLivingLabel", at = @At("RETURN"))
	public void renderLevel(AbstractClientPlayer entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_, CallbackInfo callback) {
		
		boolean toggle = Soar.instance.modManager.getModByClass(HypixelMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(HypixelMod.class, "Level Head").getValBoolean();
		
		if(toggle && ServerUtils.isHypixel()) {
			String levelhead = HypixelUtils.getHypixelLevel(entityIn == Minecraft.getMinecraft().thePlayer, entityIn.getDisplayName().getFormattedText(), entityIn.getUniqueID());

			if(levelhead != null) {
				renderLivingLabel(entityIn, EnumChatFormatting.AQUA + "Level: " + EnumChatFormatting.YELLOW + levelhead, x, y + ((double) ((float) getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p_177069_9_)), z, 64);
			}
		}
	}
}
