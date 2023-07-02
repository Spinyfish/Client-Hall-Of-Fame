package me.eldodebug.soar.management.mods.impl;

import java.util.ArrayList;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventAttackEntity;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;

public class ParticlesMod extends Mod{

	public ParticlesMod() {
		super("Particles", "Particle Customization", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		
		ArrayList<String> options = new ArrayList<String>();
		
		options.add("Slime");
		options.add("Note");
		options.add("Redstone");
		options.add("Portal");
		
		this.addBooleanSetting("Always Sharpness", this, false);
		this.addBooleanSetting("Sharpness", this, true);
		this.addBooleanSetting("Criticals", this, false);
		this.addSliderSetting("Multiplier", this, 2, 1, 10, true);
		
		this.addBooleanSetting("Custom Particle", this, false);
		this.addModeSetting("Particle Type", this, "Slime", options);
		this.addSliderSetting("Custom Particle Multiplier", this, 2, 1, 10, true);
	}
	
	@EventTarget
	public void onAttackEntity(EventAttackEntity event) {
		
		EntityPlayer player = mc.thePlayer;
		
		int multiplier = (int) Soar.instance.settingsManager.getSettingByName(this, "Multiplier").getValDouble();
		
		if(!(event.getEntity() instanceof EntityLivingBase)) {
			return;
		}
		
		boolean critical = Soar.instance.settingsManager.getSettingByName(this, "Criticals").getValBoolean() && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null;
		boolean alwaysSharpness = Soar.instance.settingsManager.getSettingByName(this, "Always Sharpness").getValBoolean();
		boolean sharpness = Soar.instance.settingsManager.getSettingByName(this, "Sharpness").getValBoolean() && EnchantmentHelper.getModifierForCreature(player.getHeldItem(), ((EntityLivingBase) event.getEntity()).getCreatureAttribute()) > 0;
		
		if(critical == true) {
			for(int i = 0; i < multiplier - 1; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.CRIT);
			}
		}
		
		if(alwaysSharpness == true || sharpness == true) {
			for(int i = 0; i < multiplier - 1; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.CRIT_MAGIC);
			}
		}
		
		boolean customParticle = Soar.instance.settingsManager.getSettingByName(this, "Custom Particle").getValBoolean();
		String particleType = Soar.instance.settingsManager.getSettingByName(this, "Particle Type").getValString();
		int customParticleMultiplier = Soar.instance.settingsManager.getSettingByName(this, "Custom Particle Multiplier").getValInt();
		EnumParticleTypes enumParticleType = EnumParticleTypes.SLIME;
		
		if(customParticle) {
			switch(particleType) {
				case "Slime":
					enumParticleType = EnumParticleTypes.SLIME;
					break;
				case "Note":
					enumParticleType = EnumParticleTypes.NOTE;
					break;
				case "Portal":
					enumParticleType = EnumParticleTypes.PORTAL;
					break;
				case "Redstone":
					enumParticleType = EnumParticleTypes.REDSTONE;
					break;
			}
			
			for(int i = 0; i < customParticleMultiplier - 1; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.getEntity(), enumParticleType);
			}
		}
	}
}
