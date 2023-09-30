package de.fanta.module.impl.combat;

import java.awt.Color;
import java.util.ConcurrentModificationException;
import java.util.Random;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.FriendSystem;
import de.fanta.utils.Rotations;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class TriggerBot extends Module {
	public TriggerBot() {

		super("TriggerBot", 0, Type.Combat, Color.WHITE);
		this.settings.add(new Setting("AutoBlock", new CheckBox(true)));
		this.settings.add(new Setting("RotationMode",
				new DropdownBox("Instant", new String[] { "Instant", "Smooth", "Intave", "AAC", "Matrix" })));
		this.settings
				.add(new Setting("Modes", new DropdownBox("Switch", new String[] { "Switch", "Single", "Nearest" })));

	}

	TimeUtil time = new TimeUtil();
	private float[] facing;
	public static Entity Target;
	public static float yaw;
	public static float pitch;
	private float lastYaw;
	private float lastPitch;

	@Override
	public void onEvent(Event event) {

		if (event instanceof EventReceivedPacket) {
			Packet p = EventReceivedPacket.INSTANCE.getPacket();
			if (p instanceof S08PacketPlayerPosLook) {
				//ChatUtil.sendChatInfo("Your Ping is Broke");
			}

		}
		// System.out.println(""+ Target);

		try {
			if (((CheckBox) this.getSetting("AutoBlock").getSetting()).state) {
				
					if (mc.thePlayer.getHeldItem() != null)
						if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
							mc.gameSettings.keyBindUseItem.pressed = false;
						}
				
				if (!mc.thePlayer.isSwingInProgress) {
					if (mc.thePlayer.getHeldItem() != null)
						if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
							mc.gameSettings.keyBindUseItem.pressed = false;
						}
				}
			}
			if (((CheckBox) this.getSetting("AutoBlock").getSetting()).state) {
				if (mc.thePlayer.isSwingInProgress) {
					if (mc.thePlayer.getHeldItem() != null)
						if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
							mc.gameSettings.keyBindUseItem.pressed = true;
						}
				}
			}
			if (((DropdownBox) this.getSetting("Modes").getSetting()).curOption != null || Target == null) {
				Target = modes();
			}
			try {

				if(event instanceof EventUpdate) {
					
					if (mc.objectMouseOver.entityHit != null) {
						int CPS = randomNumber((int) 20, (int) 14);
						if (time.hasReached(1000 / CPS)) {
							// mc.effectRenderer.emitParticleAtEntity(mc.objectMouseOver.entityHit,
							// EnumParticleTypes.CRIT);
							try {
								mc.clickMouse();
							} catch (Exception e) {
							}
							time.reset();
						}
					}
				}

				
				if(event instanceof EventRender3D) {
			          if (Target != null) {
				            float[] rota = Rotations.Intavee(mc.thePlayer, (EntityLivingBase)Target);
				            if ( mc.thePlayer.getDistanceToEntity(Target) >= 3.0F || 
				              FriendSystem.isFriendString(Target.getName()))
				              return; 
				          
				          } 
				          RotationModes(Target);
				        } 
				      } catch (NullPointerException nullPointerException) {}
				    } catch (ConcurrentModificationException concurrentModificationException) {
				    	}
				    

	  if(event instanceof EventPreMotion) {
          ((EventPreMotion)event).setPitch(Rotations.pitch);
          ((EventPreMotion)event).setYaw(Rotations.yaw);
          }
}


				
				

	public static int randomNumber(int max, int min) {
		return Math.round(min + (float) Math.random() * (max - min));
	}

	public Entity modes() {
		Entity Target = null;

		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

		case "Switch":

			break;
		case "Single":
			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;

				if (!e.getName().equals(mc.thePlayer.getName()) && e instanceof EntityPlayer
						&& !(e instanceof EntityArmorStand)) {
					if (Target == null
							|| mc.thePlayer.getDistanceToEntity(e) <= mc.thePlayer.getDistanceToEntity(Target)) {
						Target = e;
					}
				}
			}
			return Target;

		case "Nearest":
		//	if( FriendSystem.isFriendString(Target.getName())) return Target;
			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;

				if (!e.getName().equals(mc.thePlayer.getName()) && e instanceof EntityPlayer
						&& !(e instanceof EntityArmorStand) && mc.thePlayer.getDistanceToEntity(e) <= 3) {
					if (Target == null
							|| mc.thePlayer.getDistanceToEntity(e) < mc.thePlayer.getDistanceToEntity(Target)) {

						Target = e;
					}
				}

			}

		}

		return Target;

	}

	public static float[] Intavee(final EntityPlayerSP player, final EntityLivingBase target) {
		final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 90, 92);
		final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
		final float RotationYaw3 = (float) MathHelper.getRandomDoubleInRange(new Random(), 3.12, 3.13);
		final float RotationYaw2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 175, 180);
		final double posX = target.posX - player.posX;
		final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 178, 180);
		final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
		final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
		final double posY = target.posY + target.getEyeHeight()
				- (player.posY + player.getAge() + player.getEyeHeight());
		final double posZ = target.posZ - player.posZ;
		final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
		float yaw = (float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90;

		float pitch = (float) -(Math.atan2(posY, var14) * RotationYaw2 / 90);
		final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		return new float[] { yaw, MathHelper.clamp_float(pitch, -90, 90) };
	}

//	public static float[] Intavee(final EntityPlayerSP player, final Entity target) {
//
//		final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 90, 92);
//		final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
//		final double posX = target.posX - player.posX;
//		final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 175, 180);
//		final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
//		final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
//		final double posY = target.posY + target.getEyeHeight()
//				- (player.posY + player.getAge() + player.getEyeHeight() + RotationY3);
//		final double posZ = target.posZ - player.posZ;
//		final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
//		float yaw = (float) (Math.atan2(posZ, posX) * RotationY2 / Math.PI) - RotationYaw;
//		float pitch = (float) -(Math.atan2(posY, var14) * RotationY2 / Math.PI);
//		float deltaYaw = yaw - Rotations.yaw;
//		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
//		float f2 = f * f * f * 1.2F;
//		deltaYaw -= deltaYaw % f2;
//		Rotations.yaw = Rotations.yaw + deltaYaw;
//		float deltaPitch = pitch - Rotations.pitch;
//		deltaPitch -= deltaPitch % f2;
//		Rotations.pitch = pitch + deltaPitch;
//		return new float[] { MathHelper.wrapAngleTo180_float(yaw), pitch };
//	}

	public static float interpolateRotation(float par1, float par2, float par3) {
		float f = MathHelper.wrapAngleTo180_float(par2 - par1);
		if (f > par3)
			f = par3;
		if (f < -par3)
			f = -par3;
		return par1 + f;
	}

	public void RotationModes(Entity target) {

		Vec3 randomCenter = Rotations.getRandomCenter(target.getEntityBoundingBox());
		Vec3 Center = Rotations.getCenter(target.getEntityBoundingBox());

		float yaw1 = Rotations.getYawToPoint(Center);
		float pitch1 = Rotations.getPitchToPoint(Center);

		float yaw2 = Rotations.getYawToPoint(randomCenter);
		float pitch2 = Rotations.getPitchToPoint(randomCenter);

		switch (((DropdownBox) this.getSetting("RotationMode").getSetting()).curOption) {

		case "Instant":

			Rotations.setRotation(yaw1, pitch1);

			break;
		case "Smooth":

			Rotations.setYaw(yaw1, randomNumber(33, 14));
			Rotations.setPitch(pitch1, randomNumber(20, 8));

			break;
		case "AAC":
			try {
				if (mc.objectMouseOver.entityHit != null) {
					Rotations.setYaw(yaw1, 0);
					Rotations.setPitch(pitch1, 0);
				}

				if (mc.objectMouseOver.entityHit == null) {
					Rotations.setYaw(yaw2, 90F);
					Rotations.setPitch(pitch2, 180F);
				}
			} catch (NullPointerException e) {

			}

			break;
		case "Intave":
			float[] rota = Rotations.Intavee(mc.thePlayer, (EntityLivingBase) target);
			try {
				
				if (mc.objectMouseOver.entityHit != null) {
					mc.thePlayer.rotationYawHead = rota[0];
					mc.thePlayer.rotationPitchHead = rota[1];
//					Rotations.setYaw(rota[0], 180F);
//					Rotations.setPitch(rota[1], 180F);

				
				}
			} catch (NullPointerException e) {

			}
			/*
			 * if(mc.objectMouseOver.entityHit != kTarget){ if(mc.thePlayer.fallDistance !=
			 * 0) { Rotations.setYaw(yaw1, 11F); }else{ Rotations.setYaw(yaw1, 33F); }
			 * Rotations.setPitch(pitch1, 180F); }
			 * 
			 */
			try {
				if (mc.objectMouseOver.entityHit == null) {
					Rotations.setYaw(rota[0], 180F);
					Rotations.setPitch(rota[1], 180F);
				}
			} catch (NullPointerException e) {

			}
			break;
		case "Matrix":
			float[] rota2 = Rotations.Intaveee(mc.thePlayer, (EntityLivingBase) target);
			try {
				if (mc.objectMouseOver.entityHit != null) {

					Rotations.setYaw(rota2[0], 180F);
					Rotations.setPitch(rota2[1], 180F);

				}
			} catch (NullPointerException e) {

			}
			/*
			 * if(mc.objectMouseOver.entityHit != kTarget){ if(mc.thePlayer.fallDistance !=
			 * 0) { Rotations.setYaw(yaw1, 11F); }else{ Rotations.setYaw(yaw1, 33F); }
			 * Rotations.setPitch(pitch1, 180F); }
			 * 
			 */
			try {
				if (mc.objectMouseOver.entityHit == null) {
					Rotations.setYaw(rota2[0], 180F);
					Rotations.setPitch(rota2[1], 180F);
				}
			} catch (NullPointerException e) {

			}
			break;

		}

	}


}
