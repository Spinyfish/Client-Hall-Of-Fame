package markgg.modules.combat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.NumberSetting;
import markgg.ui.HUD;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class BowAimbot extends Module {

	public int state;

	public BowAimbot() {
		super("BowAimbot", "Aims at people for you", 0, Category.COMBAT);
	}

	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
			if(e.isPre()) {
				EventMotion event = (EventMotion)e;
				List<EntityLivingBase> targets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
				targets = targets.stream().filter(entity -> entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0  && !entity.isInvisible() && !entity.getName().contains("[NCP]") && !entity.getName().contains(" ") && !(entity.getName().length() > 16) && !(entity.getName().length() < 3) && !entity.getName().contains("npc") && !entity.getName().contains("NPC") && !entity.getName().contains("cit") && !entity.getName().contains("CIT")).collect(Collectors.toList());
				targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));

				if(!targets.isEmpty()) {
					EntityLivingBase target = targets.get(0);
					if (!(mc.currentScreen instanceof GuiChest) || !(mc.currentScreen instanceof GuiInventory)) {
						ItemStack heldItem = mc.thePlayer.getHeldItem();
						if (heldItem != null && heldItem.getItem() instanceof ItemBow && mc.thePlayer.isUsingItem()) {
							mc.thePlayer.rotationYaw = (getRotations(target)[0]);
							mc.thePlayer.rotationPitch = (getRotations(target)[1]);
						}
					}
				}
			}
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
			this.rotationSpeed = (float) ((float) 1 * 1.1 + 0.363F - 1.6F);
			this.cameraShakeSpeed =  + 0.025F;
		case 2:
			this.rotationSpeed = (float) ((float) 1 * 1.1 + 0.3167F - 1.6F);
			this.cameraShakeSpeed =  - 0.03F;	
		case 3:
			this.rotationSpeed = (float) ((float) 1 * 1.1 + 0.32415F - 1.6F);
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
	public float rotationSpeed = 19;

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
