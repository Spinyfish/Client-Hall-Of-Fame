	package com.srt.module.combat;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.BooleanSetting;
import com.srt.settings.settings.ModeSetting;
import com.srt.settings.settings.NumberSetting;
import com.thunderware.utils.EntityUtils;
import com.thunderware.utils.TimerUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class Killaura extends ModuleBase {

	public NumberSetting cps = new NumberSetting("CPS", 9.5, 0.25, 1, 20);
	public NumberSetting reach = new NumberSetting("Reach", 4.2, 0.1, 2, 6);
	public ModeSetting timing;
	public ModeSetting rotations;
	public BooleanSetting keepSprint = new BooleanSetting("KeepSprint",true);
	public BooleanSetting autoBlock = new BooleanSetting("AutoBlock",true);
	public float[] rots;
	
	public Killaura() {
		super("KillAura", Keyboard.KEY_V,Category.COMBAT);
		setDisplayName("Aura");
		ArrayList<String> times = new ArrayList<>();
		times.add("PRE");
		times.add("POST");
		this.timing = new ModeSetting("Timing", times);
		ArrayList<String> rotations = new ArrayList<>();
		rotations.add("NCP");
		rotations.add("AGC");
		rotations.add("Matrix");
		rotations.add("None");
		this.rotations = new ModeSetting("Rotations", rotations);
		addSettings(timing, this.rotations, keepSprint, autoBlock);
		addSettings(cps, reach);
	}
	
	private TimerUtils timer = new TimerUtils();
	public static Entity currentTarget;
	
	public void onDisable() {
		if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getValue())
			mc.thePlayer.stopUsingItem();
		currentTarget = null;
	}
	
	public void onEvent(Event baseEvent) {
		
		if(baseEvent instanceof EventMotion && mc.thePlayer.ticksExisted > 40) {
			float random = ThreadLocalRandom.current().nextFloat();
			setSuffix(Math.floor(cps.getValue()*10)/10 + " | " + Math.floor(reach.getValue()*10)/10);
			EventMotion event = (EventMotion)baseEvent;
			CopyOnWriteArrayList<Entity> ent = AntiBot.getEntities();
			Entity target = getMainEntity(EntityUtils.distanceSort(ent));
			if(target != null) {
				rots = ncpRotations(target, event);
				if(rotations.getCurrentValue() == "AGC") {
					rots = testRotations(target, event);
					float gg = ThreadLocalRandom.current().nextFloat();
					rots[0] = System.currentTimeMillis()/2%360;
					rots[1] = 90F;
				}else if(rotations.getCurrentValue() == "Matrix") {
					rots = Matrix(target);
				}
				if(target.getDistanceToEntity(mc.thePlayer) <= (reach.getValue() + 2)) {
					currentTarget = target;
					if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getValue() && Math.random()<0.675 && mc.thePlayer.onGround)
						mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 1);
					if(rotations.getCurrentValue() != "None") {
						event.yaw = rots[0];
						event.pitch = rots[1];
					}
					/*
						if(rotations.getCurrentValue() == "Matrix") {
							mc.thePlayer.rotationYaw = rots[0];
							mc.thePlayer.rotationPitch = rots[1];
						}
					*/
					mc.thePlayer.rotationYawHead = event.getYaw();
				}else{
					if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getValue())
						mc.thePlayer.stopUsingItem();
					currentTarget = null;
				}
				double gg = 1000/(cps.getValue()+(random*(1.4+Math.random())));
				if(target.getDistanceToEntity(mc.thePlayer) <= reach.getValue() && timer.hasReached(gg)) {
					if(timing.getCurrentValue().contains("PRE") && event.isPre()) {
						if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getValue())
							mc.thePlayer.stopUsingItem();
						mc.thePlayer.swingItem();
						timer.reset();
						if(keepSprint.getValue()) {
							mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target,C02PacketUseEntity.Action.ATTACK));
						}else {
							mc.playerController.attackEntity(mc.thePlayer, target);
						}
					}else if(timing.getCurrentValue().contains("POST") && event.isPost()) {
						mc.thePlayer.swingItem();
						timer.reset();
						if(keepSprint.getValue()) {
							mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C02PacketUseEntity(target,C02PacketUseEntity.Action.ATTACK));
						}else {
							mc.playerController.attackEntity(mc.thePlayer, target);
						}
					}
				}
			}else {
				currentTarget = null;
			}
		}
	}

	public Entity getMainEntity(CopyOnWriteArrayList<Entity> entities) {
		if(entities.size() > 0)
			for(Entity ent : entities) {
				if(ent != mc.thePlayer && ent instanceof EntityLivingBase && ent.isEntityAlive()) {
					if(ent instanceof EntityPlayer) {
						return ent;
					}
					
					//Delete Line Below When Finished
					return ent;
				}
			}
		
		return null;
	}
	
	/*
	 * From Old Client <3
	*/
	public static float[] ncpRotations(Entity e, EventMotion p) {
		double x = e.posX + (e.posX - e.lastTickPosX) - p.getX();
		double y = (e.posY + e.getEyeHeight()) - (p.getY() + Minecraft.getMinecraft().thePlayer.getEyeHeight()) - 0.1;
		double z = e.posZ + (e.posZ - e.lastTickPosZ) - p.getZ();
		double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

		float yaw = (float) Math.toDegrees(-Math.atan(x / z));
		float pitch = (float) -Math.toDegrees(Math.atan(y / dist));

		if (x < 0 && z < 0)
			yaw = 90 + (float) Math.toDegrees(Math.atan(z / x));
		else if (x > 0 && z < 0)
			yaw = -90 + (float) Math.toDegrees(Math.atan(z / x));

		yaw += Math.random() * 4 - Math.random();
		pitch += Math.random() * 4 - Math.random();

		if (pitch > 90)
			pitch = 90;
		if (pitch < -90)
			pitch = -90;
		if (yaw > 180)
			yaw = 180;
		if (yaw < -180)
			yaw = -180;

		return new float[]{yaw, pitch};
	}
	
	public static float[] testRotations(Entity e, EventMotion p) {
		double x = e.posX + (e.posX - e.lastTickPosX) - p.getX();
		double y = (e.posY + e.getEyeHeight()) - (p.getY() + Minecraft.getMinecraft().thePlayer.getEyeHeight()) - 0.1;
		double z = e.posZ + (e.posZ - e.lastTickPosZ) - p.getZ();
		double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

		float yaw = (float) Math.toDegrees(-Math.atan(x / z));
		float pitch = (float) -Math.toDegrees(Math.atan(y / dist));
		
		if (x < 0 && z < 0)
			yaw = 90 + (float) Math.toDegrees(Math.atan(z / x));
		else if (x > 0 && z < 0)
			yaw = -90 + (float) Math.toDegrees(Math.atan(z / x));
		
		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;

		yaw -= yaw % gcd;
		pitch -= pitch % gcd;

		yaw += Math.random() * (Math.random()*(4.4-Math.random())) - Math.random();
		pitch += Math.random() * (Math.random()*(4.4-Math.random())) - Math.random();

		if (pitch > 90)
			pitch = 90;
		if (pitch < -90)
			pitch = -90;
		if (yaw > 180)
			yaw = 180;
		if (yaw < -180)
			yaw = -180;

		return new float[]{yaw, pitch};
	}
	
	public static float[] Matrix(Entity entity) {
		boolean random = ThreadLocalRandom.current().nextBoolean();
        double diffX = entity.posX - mc.thePlayer.posX, diffY;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9
                    - (mc.thePlayer.posY
                    + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D
                    - (mc.thePlayer.posY
                    + mc.thePlayer.getEyeHeight());
        }
        double diffZ = entity.posZ - mc.thePlayer.posZ, dist = Math.hypot(diffX, diffZ);
        float sensitivity = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F,
                gcd = sensitivity * sensitivity * sensitivity * 1.2F,
                yawRand = random ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F),
                pitchRand = random ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F),
                yaw = ((float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F) + yawRand,
                pitch = MathHelper.clamp_float(((float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI)) + pitchRand + mc.thePlayer.getDistanceToEntity(entity) * 1.25F, -90.0F, 90.0F);
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            pitch = MathHelper.clamp_float(pitch + (random ? RandomUtils.nextFloat(2.0F, 8.0F) : -RandomUtils.nextFloat(2.0F, 8.0F)), -90.0F, 90.0F);
        }
        pitch -= pitch % gcd;
        yaw -= yaw % gcd;
        return new float[]{
                mc.thePlayer.rotationYaw
                        + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                mc.thePlayer.rotationPitch
                        + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)
        };
    }
}
