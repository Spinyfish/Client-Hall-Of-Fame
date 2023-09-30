package felix.module.impl.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.VanillaFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.render.EventRender2D;
import felix.module.Module;
import felix.module.impl.combat.AntiBot;
import felix.module.impl.combat.KillAura;
import felix.util.font.FontRenderer;
import felix.util.other.MathUtils;
import felix.util.other.PlayerUtil;
import felix.util.other.TimeHelper;
import felix.util.visual.RenderUtil;
import felix.value.impl.BooleanValue;

public final class TargetHUD extends Module {

	private final TimeHelper animationStopwatch = new TimeHelper();
	private double healthBarWidth;

	private final BooleanValue showPlayers = new BooleanValue("Show Players", true);
	private final BooleanValue showMonsters = new BooleanValue("Show Monsters", true);
	private final BooleanValue showInvisibles = new BooleanValue("Show Invisibles", true);
	private final BooleanValue showAnimals = new BooleanValue("Show Animals", true);
	private final BooleanValue showPassives = new BooleanValue("Show Passives", true);

	private double x, y;

	public TargetHUD() {
		super("TargetHUD", 0, ModuleCategory.VISUALS);
		setHidden(true);
		addValues(showPlayers, showMonsters, showAnimals, showInvisibles, showPassives);
	}

	@Handler
    public void onRender2D(final EventRender2D event) {
        final VanillaFontRenderer fr = mc.fontRendererObj;
        final KillAura killAura = (KillAura) Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        boolean guichat = mc.currentScreen instanceof GuiChat;
        EntityLivingBase entityPlayer = guichat ? mc.thePlayer : killAura.target;
        if (mc.thePlayer != null && killAura.target != null || guichat) {
        	if (isValidEntity(entityPlayer) || entityPlayer == mc.thePlayer) {
	            GlStateManager.pushMatrix();
	            GlStateManager.translate(x, y, 0);
	            final ScaledResolution scaledResolution2;
	            final ScaledResolution scaledResolution = scaledResolution2 = new ScaledResolution(mc);
	            int var141 = scaledResolution.getScaledWidth();
	            int var151 = scaledResolution.getScaledHeight();
	            int mouseX = Mouse.getX() * var141 / mc.displayWidth;
	            int mouseY = var151 - Mouse.getY() * var151 / mc.displayHeight - 1;
	            
	            GlStateManager.enableBlend();
	            
	            final float n = 2;
	            scaledResolution2.scaledWidth *= (int)(1.0f / n);
	            final ScaledResolution scaledResolution3 = scaledResolution;
	            scaledResolution3.scaledHeight *= (int)(1.0f / n);
	            final int n2 = scaledResolution.getScaledHeight() / 2 + 200;
	            final int n3 = scaledResolution.getScaledWidth() / 2 + 300;
	            RenderUtil.drawRect(n3 + 1f, n2 + 1, 140.0, 37.6, new Color(25, 25, 25, 210).getRGB());
	            if (Mouse.isButtonDown(0) && guichat) {
	            	setX(mouseX - 300);
	            	setY(mouseY - 200);
	            }
	            String string = String.format("%.1f", entityPlayer.getHealth() / 2.0f);
	            
	            GlStateManager.pushMatrix();
	            GlStateManager.scale(n, n, n);
	            mc.fontRendererObj.drawStringWithShadow(string.replace(".0", ""), (float)(n3 - 121), (float)(n2 - 93), RenderUtil.drawHealth(entityPlayer));
	            mc.fontRendererObj.drawStringWithShadow("\u2764", n3 - 130, n2 - 94, RenderUtil.drawHealth(entityPlayer));
	            GlStateManager.popMatrix();
	            final double n4 = 137.0f / entityPlayer.getMaxHealth() * (double)Math.min(entityPlayer.getHealth(), entityPlayer.getMaxHealth());
	            if (animationStopwatch.isDelayComplete(15)) {
	                healthBarWidth = RenderUtil.animate(n4, healthBarWidth, 0.05);
	                animationStopwatch.reset();
	            }
	            RenderUtil.drawRect((float)n3 + 2f, (float)n2 + (float)34, 138, (float)3.5, (RenderUtil.darker(new Color(RenderUtil.drawHealth(entityPlayer)), 0.35f).getRGB()));
	            float less = entityPlayer.getHealth() == 0 || entityPlayer.getHealth() == entityPlayer.getMaxHealth() ? 0 : 4f;
	            RenderUtil.drawRect((float)n3 + 2f, (float)n2 + (float)34, (float)healthBarWidth + (float)0.9, (float)3.5, (RenderUtil.drawHealth(entityPlayer.getHealth(), entityPlayer.getMaxHealth()).getRGB()));
	            RenderUtil.drawRect((float)n3 + 2f, (float)n2 + (float)34, n4 + (float)0.9, (float)3.5, (RenderUtil.drawHealth(entityPlayer)));
	            
	            final String name = entityPlayer instanceof EntityPlayer ? ((EntityPlayer) entityPlayer).getGameProfile().getName() : killAura.target.getDisplayName().getFormattedText();
	            GlStateManager.enableBlend();
	
	            mc.fontRendererObj.drawStringWithShadow(name, (float)(n3 + 35), (float)(n2 + 3), -855638017);
	            if (entityPlayer instanceof EntityPlayer) {
	            	mc.getTextureManager().bindTexture(((AbstractClientPlayer) entityPlayer).getLocationSkin());
		            GlStateManager.enableBlend();
		            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
		            Gui.drawScaledCustomSizeModalRect(n3 + 2, n2 + 2, 8.0f, 8.0f, 8, 8, 31, 31, 64.0f, 64.0f);
	            }
	            GlStateManager.disableBlend();
	            GlStateManager.popMatrix();
        	}
        	if (!isValidEntity(entityPlayer)) {
        		entityPlayer = null;
        	}
        }
    }

	public void onEnable() {
		super.onEnable();
	}

	public void onDisable() {
		super.onDisable();
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	private boolean isValidEntity(final EntityLivingBase target) {
		if (target instanceof EntityMob && !showMonsters.isEnabled()) {
			return false;
		}
		if (target.isInvisible() && target instanceof EntityPlayer && !showInvisibles.isEnabled()) {
			return false;
		}
		if (target instanceof EntityPlayer && !showPlayers.isEnabled()) {
			return false;
		}
		if (target instanceof EntityGolem || target instanceof EntityVillager && !showPassives.isEnabled()) {
			return false;
		}
		if (target instanceof EntityAnimal && !showAnimals.isEnabled()) {
			return false;
		}
		return true;
	}
}
