package markgg.modules.ghost;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.KeybindSetting;
import markgg.settings.NumberSetting;
import markgg.util.Timer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;

public class AimAssist extends Module {

	public Timer timer = new Timer();

	public int state;

	public NumberSetting multiplier = new NumberSetting("Multiplier", this, 1.4, 1, 5, 0.1);
	public BooleanSetting aimaAssistSwordOnly = new BooleanSetting("Sword Only", this, true);
	public BooleanSetting aimaAssistAxeOnly = new BooleanSetting("Axe Only", this, false);
	public BooleanSetting aimAtMobs = new BooleanSetting("Aim at Mobs", this, false);
	public BooleanSetting clickAim = new BooleanSetting("Click to Aim", this, false);

	public AimAssist() {
		super("AimAssist", "Helps you with aiming", 0, Category.GHOST);
		addSettings(multiplier, aimaAssistSwordOnly, aimaAssistAxeOnly);
	}

	public void onEvent(Event e){
		if(e instanceof EventMotion) {
			if(e.isPre()) {

				EventMotion event = (EventMotion)e;

				List<EntityLivingBase> targets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());

				targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < 5 && entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0  && !entity.isInvisible() && !entity.getName().contains("[NCP]") && !entity.getName().contains(" ") && !(entity.getName().length() > 16) && !(entity.getName().length() < 3) && !entity.getName().contains("npc") && !entity.getName().contains("NPC") && !entity.getName().contains("cit") && !entity.getName().contains("CIT")).collect(Collectors.toList());

				targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));

				if(!aimAtMobs.isEnabled()) {
					targets = targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
				}else {
					//not filtered
				}



				if(!targets.isEmpty()) {
					EntityLivingBase target = targets.get(0);
					if (!(mc.currentScreen instanceof GuiChest) || !(mc.currentScreen instanceof GuiInventory)) {
						ItemStack heldItem = mc.thePlayer.getHeldItem();

						if (heldItem != null && heldItem.getItem() instanceof ItemSword) {
							if(clickAim.isEnabled()) {
								if(Mouse.isButtonDown(0)) {
									mc.thePlayer.rotationYaw = (getRotations(target)[0]);
									mc.thePlayer.rotationPitch = (getRotations(target)[1]);
								}
							} else if(!clickAim.isEnabled()) {
								mc.thePlayer.rotationYaw = (getRotations(target)[0]);
								mc.thePlayer.rotationPitch = (getRotations(target)[1]);
							}
						}
					}
				}
			}
		}




		if(mc.pointedEntity != null) {
			this.rotationSpeed *= 1.5F;
		}

		if(this.rotationSpeed < 1.25) {
			this.rotationSpeed *= 1.5F;
		}
		if(this.cameraShakeSpeed < 2.787) {
			this.cameraShakeSpeed *= 1.8F;
		}



		this.state +=1;
		switch(this.state) {
		case 1:
			this.rotationSpeed = (float) ((float) 1.1 * multiplier.getValue() + 0.363F);
			this.cameraShakeSpeed =  + 0.025F;
		case 2:
			this.rotationSpeed = (float) ((float) 1.1 * multiplier.getValue() + 0.3167F);
			this.cameraShakeSpeed =  - 0.03F;

		case 3:
			this.rotationSpeed = (float) ((float) 1.1 * multiplier.getValue() + 0.32415F);
			this.cameraShakeSpeed =  + 0.020F;
		case 4:
			this.cameraShakeSpeed = - 0.015F;

			this.state = 0;
			break;
		default:
			break;


		}


	}

	public float cameraShakeSpeed;
	public float rotationSpeed;

	public float[] getRotations(Entity e) {
		double deltaX = e.posX - (e.posX - e.lastTickPosX) - mc.thePlayer.posX;
		double deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + 0.2;
		double deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ;
		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));

		float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
		float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2))));
		float deltaYaw = yaw - mc.thePlayer.rotationYaw;
		float deltaPitch = pitch - mc.thePlayer.rotationPitch;
		deltaYaw = MathHelper.wrapAngleTo180_float(deltaYaw);
		deltaPitch = MathHelper.wrapAngleTo180_float(deltaPitch);

		float adjustedRotationSpeed = rotationSpeed;
		deltaYaw = Math.min(adjustedRotationSpeed, Math.max(-adjustedRotationSpeed, deltaYaw));
		deltaPitch = Math.min(adjustedRotationSpeed, Math.max(-adjustedRotationSpeed, deltaPitch));
		yaw = mc.thePlayer.rotationYaw + deltaYaw + this.cameraShakeSpeed;
		pitch = mc.thePlayer.rotationPitch + deltaPitch + this.cameraShakeSpeed;

		return new float[] { yaw, pitch };
	}
}
