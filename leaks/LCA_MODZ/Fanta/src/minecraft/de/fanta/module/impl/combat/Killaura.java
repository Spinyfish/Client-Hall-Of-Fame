package de.fanta.module.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.movement.Speed;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.Colors;
import de.fanta.utils.FriendSystem;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.Rotations;
import de.fanta.utils.TimeUtil;
import fr.lavache.anime.Animate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Killaura extends Module {

	public Killaura() {
		super("Killaura", Keyboard.KEY_R, Type.Combat, Color.green);

		/*
		 * Always use new Setting() in combination with setting name and the type ofaut
		 * setting (CheckBox, Slider or DropdownBox) There isf no limit for how many
		 * settings + you can user the same names in different modules. Examples for how
		 * to setup settings are shown here
		 */
		this.settings.add(new Setting("RedColoring", new CheckBox(false)));
		this.settings.add(new Setting("Raytrace", new CheckBox(false)));
		this.settings.add(new Setting("Antibot", new CheckBox(false)));
		this.settings.add(new Setting("SoundCheck", new CheckBox(false)));
		this.settings.add(new Setting("AutoBlock", new CheckBox(false)));
		this.settings.add(new Setting("Swing", new CheckBox(false)));
		//this.settings.add(new Setting("EntityStrafe", new CheckBox(false)));
		this.settings.add(new Setting("TargetHUD", new CheckBox(false)));
		this.settings.add(new Setting("LegitRange", new CheckBox(false)));
		this.settings.add(new Setting("Multy", new CheckBox(false)));
		this.settings.add(new Setting("KeepSprint", new CheckBox(false)));
		this.settings.add(new Setting("1.12 Hit", new CheckBox(false)));
		this.settings.add(new Setting("TargetESP", new CheckBox(false)));
		this.settings.add(new Setting("Range", new Slider(1, 8, 0.1, 4)));
		this.settings.add(new Setting("PreAimRange", new Slider(0, 5, 0.1, 2)));
		this.settings.add(new Setting("CPS-Max", new Slider(0, 20, 1, 10)));
		this.settings.add(new Setting("CPS-Min", new Slider(0, 20, 1, 2)));
		this.settings.add(new Setting("Cracks", new Slider(0, 1000, 1, 50)));
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
//		this.settings
//				.add(new Setting("RotationEvent", new DropdownBox("Pre", new String[] { "Pre", "Frame", "OnUpdate" })));
		this.settings.add(new Setting("AutoBlockMode",
				new DropdownBox("Intave", new String[] { "Intave", "SmartIntave", "NCP", "Hypixel", "Other" })));
		this.settings.add(new Setting("Modes",
				new DropdownBox("Nearest", new String[] { "Single", "Nearest", "Multy", "Switch" })));
		this.settings.add(new Setting("RotationMode",
				new DropdownBox("Instant", new String[] { "Instant", "Smooth", "Intave", "AAC" })));
		this.settings.add(new Setting("TargetHUDMode",
				new DropdownBox("Novoline", new String[] { "Fanta", "OldAstolfo", "Novoline" })));
		this.settings.add(new Setting("TargetHUDColor", new DropdownBox("DARK", new String[] { "DARK", "WHITE" })));
	}

	public static EntityLivingBase kTarget;
	public static double range;
	public static double preAimRange;
	public static double cracks;
	float yaw;
	float pitch;
	private float lastYaw;
	private float lastPitch;
	double minCPS;
	double maxCPS;
	public static float yaww;
	public static float pitchh;
	public static float[] facing;
	Animate anim = new Animate();
	private final CopyOnWriteArrayList<Entity> copyEntities = new CopyOnWriteArrayList<>();
	private final ArrayList<Entity> madeSound = new ArrayList<>();
	TimeUtil time = new TimeUtil();
	TimeUtil timeAB = new TimeUtil();
	Random random = new Random();
	public static ArrayList<Entity> bots = new ArrayList<>();

	public void onEnable() {

		mc.gameSettings.keyBindUseItem.pressed = false;
		super.onEnable();
	}

	public void onDisable() {

		bots.clear();
		mc.gameSettings.keyBindSprint.pressed = false;
		super.onDisable();
	}

	public void onEvent(Event event) {

		if (event instanceof EventPacket) {

			if (((EventPacket) event).getPacket() instanceof C03PacketPlayer) {
				// if (kTarget != null) {
				// C03PacketPlayer c03 = (C03PacketPlayer) ((EventPacket) event).getPacket();
				// mc.thePlayer.rotationYawHead = c03.yaw;
				// mc.thePlayer.rotationPitchHead = c03.pitch;
				// ((EventPacket) event).setPacket(c03);
			}
		}

		if (event instanceof EventReceivedPacket) {
			if (((CheckBox) this.getSetting("SoundCheck").getSetting()).state) {
				final EventReceivedPacket packetEvent = (EventReceivedPacket) event;
				final Packet<? extends INetHandler> packet = packetEvent.getPacket();
				if (packet instanceof S29PacketSoundEffect) {
					final S29PacketSoundEffect soundEffect = (S29PacketSoundEffect) packet;
					copyEntities.addAll(mc.theWorld.loadedEntityList);
					copyEntities.forEach(entity -> {
						if (entity != mc.thePlayer && entity.getDistance(soundEffect.getX(), soundEffect.getY(),
								soundEffect.getZ()) <= 0.8)
							madeSound.add(entity);

					});
					copyEntities.clear();

				}
			}
		}

		if (event instanceof EventPreMotion) {


			if (((CheckBox) this.getSetting("Antibot").getSetting()).state) {

				for (final Object entity : mc.theWorld.getLoadedEntityList())
					if (entity instanceof EntityPlayer)
						if ((isBot((EntityPlayer) entity) || ((EntityPlayer) entity).isInvisible())
								&& entity != mc.thePlayer) {
							Killaura.bots.add((EntityPlayer) entity);
							mc.theWorld.removeEntity((Entity) entity);

						}
			}
			 if (((CheckBox) this.getSetting("AutoBlock").getSetting()).state) {
			if (((CheckBox) this.getSetting("AutoBlock").getSetting()).state) {
				if(!mc.thePlayer.isSwingInProgress) {
				if (mc.thePlayer.getHeldItem() != null)
					if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
						if(!Killaura.hasTarget()) {
						mc.gameSettings.keyBindUseItem.pressed = false;
						}
					}
				}

			}

			 }
			if (((DropdownBox) this.getSetting("Modes").getSetting()).curOption != null || kTarget == null) {
				kTarget = (EntityLivingBase) modes();
			}

			/*
			 * Use the right cast to get the value of every setting Can be used in
			 * combination with Client.INSTANCE.modulemanager.getModule() to get values from
			 * other modules
			 */

			range = ((Slider) this.getSetting("Range").getSetting()).curValue;
			preAimRange = ((Slider) this.getSetting("PreAimRange").getSetting()).curValue;
			maxCPS = ((Slider) this.getSetting("CPS-Max").getSetting()).curValue;
			minCPS = ((Slider) this.getSetting("CPS-Min").getSetting()).curValue;

			int CPS = randomNumber((int) maxCPS, (int) minCPS);
			// !((EntityLivingBase) kTarget).isPlayerSleeping() && !kTarget.isInvisible()
			// Client.INSTANCE.moduleManager.getModule("Killaura").state && Killaura.kTarget
			// != null
			if (kTarget != null && kTarget != mc.thePlayer
					&& mc.thePlayer.getDistanceToEntity(kTarget) < range + preAimRange
					&& ((EntityPlayer) kTarget).getHealth() != 0) {

				// .RotationInUse = false;
				if (bots.contains(kTarget) || FriendSystem.isFriendString(kTarget.getName())
						|| Killaura.kTarget.getName().equalsIgnoreCase("§6Shop"))
					return;
				if (!((CheckBox) this.getSetting("Multy").getSetting()).state) {
					// yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
					// pitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

					// switch (((DropdownBox)
					// this.getSetting("RotationEvent").getSetting()).curOption) {

					// case "Pre":
					// if (event instanceof EventPreMotion) {
					// mc.thePlayer.rotationYawHead = Rotations.yaw;
					// mc.thePlayer.rotationPitchHead = Rotations.pitch;
					((EventPreMotion) event).setPitch(Rotations.pitch);
					((EventPreMotion) event).setYaw(Rotations.yaw);
					RotationModes(kTarget);
					// }
					// break;

				}
				// mouseFix();

				if (((CheckBox) this.getSetting("LegitRange").getSetting()).state) {
					if (mc.thePlayer != null && mc.thePlayer.getDistanceToEntity(kTarget) <= range && kTarget != null
							&& mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
						if (madeSound.contains(kTarget) || bots.contains(kTarget)
								|| FriendSystem.isFriendString(kTarget.getName())
								|| Killaura.kTarget.getName().equalsIgnoreCase("§6Shop"))
							return;

						if (((CheckBox) this.getSetting("1.12 Hit").getSetting()).state) {
							if (mc.thePlayer.ticksExisted % 12 == 0) {
								mc.clickMouse();
//								mc.effectRenderer.emitParticleAtEntity(kTarget, EnumParticleTypes.CRIT);
								for (int i = 0; i < cracks; ++i) {
									double d0 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
									double d1 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
									double d2 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);

									if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D) {
										double d3 = EntityParticleEmitter.attachedEntity.posX
												+ d0 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
										double d4 = EntityParticleEmitter.attachedEntity.getEntityBoundingBox().minY
												+ (double) (EntityParticleEmitter.attachedEntity.height / 2.0F)
												+ d1 * (double) EntityParticleEmitter.attachedEntity.height / 4.0D;
										double d5 = EntityParticleEmitter.attachedEntity.posZ
												+ d2 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
										Entity.worldObj.spawnParticle(EntityParticleEmitter.particleTypes, false, d3,
												d4, d5, d0, d1 + 0.2D, d2, new int[1]);

										time.reset();

									}
								}
							}
						} else {

							if (time.hasReached(1000 / CPS)) {
								//mc.effectRenderer.emitParticleAtEntity(kTarget, EnumParticleTypes.CRIT);
								for (int i = 0; i < cracks; ++i) {
									double d0 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
									double d1 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
									double d2 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);

									if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D) {
										double d3 = EntityParticleEmitter.attachedEntity.posX
												+ d0 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
										double d4 = EntityParticleEmitter.attachedEntity.getEntityBoundingBox().minY
												+ (double) (EntityParticleEmitter.attachedEntity.height / 2.0F)
												+ d1 * (double) EntityParticleEmitter.attachedEntity.height / 4.0D;
										double d5 = EntityParticleEmitter.attachedEntity.posZ
												+ d2 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
										Entity.worldObj.spawnParticle(EntityParticleEmitter.particleTypes, false, d3, d4,
												d5, d0, d1 + 0.2D, d2, new int[1]);
									}
								}
								time.reset();

							
								mc.clickMouse();
								time.reset();
							}
						}
					}
					AutoBlock();
				} else {

					if (((CheckBox) this.getSetting("1.12 Hit").getSetting()).state) {
						if (mc.thePlayer.ticksExisted % 12 == 0) {
							if (mc.thePlayer.getDistanceToEntity(kTarget) <= range && kTarget != null) {
								if (madeSound.contains(kTarget) || bots.contains(kTarget)
										|| FriendSystem.isFriendString(kTarget.getName())
										|| Killaura.kTarget.getName().equalsIgnoreCase("§6Shop"))
									return;
								AutoBlock();

								if (time.hasReached(1000 / CPS)) {

									for (int i = 0; i < cracks; ++i) {
										double d0 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
										double d1 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
										double d2 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);

										if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D) {
											double d3 = EntityParticleEmitter.attachedEntity.posX
													+ d0 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
											double d4 = EntityParticleEmitter.attachedEntity.getEntityBoundingBox().minY
													+ (double) (EntityParticleEmitter.attachedEntity.height / 2.0F)
													+ d1 * (double) EntityParticleEmitter.attachedEntity.height / 4.0D;
											double d5 = EntityParticleEmitter.attachedEntity.posZ
													+ d2 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
											Entity.worldObj.spawnParticle(EntityParticleEmitter.particleTypes, false, d3, d4,
													d5, d0, d1 + 0.2D, d2, new int[1]);
										}
									}
									time.reset();


									mc.playerController.attackEntity(mc.thePlayer, kTarget);
									mc.thePlayer.swingItem();
									time.reset();
								}

							}
						}
					} else {
						if (mc.thePlayer.getDistanceToEntity(kTarget) <= range && kTarget != null) {
							if (bots.contains(kTarget) || FriendSystem.isFriendString(kTarget.getName())
									|| Killaura.kTarget.getName().equalsIgnoreCase("§6Shop"))
								return;
							AutoBlock();

							if (time.hasReached(1000 / CPS)) {

								for (int i = 0; i < cracks; ++i) {
									double d0 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
									double d1 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);
									double d2 = (double) (Entity.rand.nextFloat() * 2.0F - 1.0F);

									if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D) {
										double d3 = EntityParticleEmitter.attachedEntity.posX
												+ d0 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
										double d4 = EntityParticleEmitter.attachedEntity.getEntityBoundingBox().minY
												+ (double) (EntityParticleEmitter.attachedEntity.height / 2.0F)
												+ d1 * (double) EntityParticleEmitter.attachedEntity.height / 4.0D;
										double d5 = EntityParticleEmitter.attachedEntity.posZ
												+ d2 * (double) EntityParticleEmitter.attachedEntity.width / 4.0D;
										Entity.worldObj.spawnParticle(EntityParticleEmitter.particleTypes, false, d3, d4,
												d5, d0, d1 + 0.2D, d2, new int[1]);
									}
								}
								time.reset();

								mc.thePlayer.swingItem();
								mc.playerController.attackEntity(mc.thePlayer, kTarget);

								time.reset();
							}
						}
					}

				}
			} else {
				kTarget = null;
			}
		}

		if (event instanceof EventRender3D) {
			if (((CheckBox) this.getSetting("TargetESP").getSetting()).state) {
				drawTargetESP((EntityPlayer) kTarget, mc.timer.renderPartialTicks);
			}
		}

		if (event instanceof EventRender2D && event.isPre()) {
			drawTargetHUD();
		}

		// dm.out.println(((DropdownBox)
		// this.getSetting("Modes").getSetting()).curOption);
	}

	public void mouseFix() {
		float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float f2 = f * f * f * 1.2F;
		/*
		 * float deltaYaw = Rotations.yaw - yaw; deltaYaw -= deltaYaw % f2;
		 * Rotations.yaw = Rotations.yaw + deltaYaw;
		 * 
		 * float deltaPitch = Rotations.pitch - pitch; deltaPitch -= deltaPitch % f2;
		 * Rotations.pitch = Rotations.pitch + deltaPitch;
		 * 
		 * 
		 */
		Rotations.yaw -= Rotations.yaw % f2;
		Rotations.pitch -= Rotations.pitch % (f2 * f);

	}

	public Entity modes() {
		EntityPlayer target = null;
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
		case "Switch":
			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;

				if (!e.getName().equals(mc.thePlayer.getName()) && e instanceof EntityPlayer
						&& !(e instanceof EntityArmorStand)) {
					EntityPlayer player = (EntityPlayer) e;
					if (target == null || player.hurtTime < target.hurtTime) {
						if(mc.thePlayer.getDistanceToEntity(e) <  range + preAimRange) {
							target = (EntityPlayer) player;
						}
					} else if (player.hurtTime == target.hurtTime) {
						if (mc.thePlayer.getDistanceToEntity(player) < mc.thePlayer.getDistanceToEntity(target)) {
							if(mc.thePlayer.getDistanceToEntity(e) <  range +preAimRange) {
								target = (EntityPlayer) player;
							}
						
						
						}
					}
				}
			}
			return target;

		case "Single":
			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;

				if (!e.getName().equals(mc.thePlayer.getName()) && e instanceof EntityPlayer
						&& !(e instanceof EntityArmorStand)) {
					if (target == null || mc.thePlayer
							.getDistanceToEntity(e) <= mc.thePlayer.getDistanceToEntity(target) + preAimRange) {
						if(mc.thePlayer.getDistanceToEntity(e) <  range+ preAimRange) {
							target = (EntityPlayer) e;
						}
						
					}
				}
			}
			return target;

		case "Multy":

			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;
				if (!e.getName().equals(mc.thePlayer.getName()) && e instanceof EntityPlayer
						&& !(e instanceof EntityArmorStand)) {
					if (target == null) {

						if(mc.thePlayer.getDistanceToEntity(e) <  range+ preAimRange) {
							target = (EntityPlayer) e;
						}
					}
				}
			}

		case "Nearest":

			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;
				if (!e.getName().equals(mc.thePlayer.getName()) && e instanceof EntityPlayer
						&& !(e instanceof EntityArmorStand)) {
					
					if (target == null
							|| mc.thePlayer.getDistanceToEntity(e) < mc.thePlayer.getDistanceToEntity(target)) {
						if(mc.thePlayer.getDistanceToEntity(e) <  range + preAimRange) {
							target = (EntityPlayer) e;
						}
						
					}
				}
			}
		}
		return target;

	}
	public static int randomNumber(int max, int min) {
		return Math.round(min + (float) Math.random() * (max - min));
	}

	public void RotationModes(Entity target) {
		if (target == null) {
			//Rotations.yaw = mc.thePlayer.rotationYaw;
			//Rotations.pitch = mc.thePlayer.rotationPitch;
		//	lastYaw = mc.thePlayer.prevRotationYaw;
			//lastPitch = mc.thePlayer.prevRotationPitch;
			return;
		}
		Vec3 randomCenter = Rotations.getRandomCenter(target.getEntityBoundingBox());
		Vec3 Center = Rotations.getCenter(target.getEntityBoundingBox());
		Vec3 Center2 = Rotations.getCenter2(target.getEntityBoundingBox());

		float yaw1 = Rotations.getYawToPoint(Center);

		float pitch1 = Rotations.getPitchToPoint(Center);
		float pitch3 = Rotations.getPitchToPoint(Center2);

		float yaw2 = Rotations.getYawToPoint(randomCenter);
		float pitch2 = Rotations.getPitchToPoint(randomCenter);

		switch (((DropdownBox) this.getSetting("RotationMode").getSetting()).curOption) {

		case "Instant":

			Rotations.setRotation(yaw1, pitch3);

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
					if (bots.contains(kTarget) || FriendSystem.isFriendString(kTarget.getName())
							|| Killaura.kTarget.getName().equalsIgnoreCase("§6Shop"))
						return;
				}

				if (mc.objectMouseOver.entityHit == null) {
					Rotations.setYaw(yaw2, 90F);
					Rotations.setPitch(pitch2, 180F);
					if (bots.contains(kTarget) || FriendSystem.isFriendString(kTarget.getName())
							|| Killaura.kTarget.getName().equalsIgnoreCase("§6Shop"))
						return;
				}
			} catch (NullPointerException e) {

			}

			break;
		case "Intave":
			float[] rota = Rotations.getNewKillAuraRots(mc.thePlayer, (EntityLivingBase) target, lastYaw, lastPitch);
			lastYaw = rota[0];
			lastPitch = rota[1];
			try {
				if (mc.objectMouseOver.entityHit != null) {
					if (bots.contains(kTarget) || FriendSystem.isFriendString(kTarget.getName())
							|| Killaura.kTarget.getName().equalsIgnoreCase("§6Shop"))
						return;

					Rotations.setYaw(rota[0], 180F);
					Rotations.setPitch(rota[1], 180F);

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

		}
	}

	public static boolean hasTarget() {
		return kTarget != null;
	}

	public void AutoBlock() {
		if (((CheckBox) this.getSetting("AutoBlock").getSetting()).state) {

			if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
				switch (((DropdownBox) this.getSetting("AutoBlockMode").getSetting()).curOption) {

				case "Intave":

					if (mc.thePlayer.isSwingInProgress) {
						if (mc.thePlayer.getHeldItem() != null)
							if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
								mc.gameSettings.keyBindUseItem.pressed = true;
								// Speed.setSpeed(0.2875);
							}
					}

				case "SmartIntave":

					if (mc.thePlayer.isSwingInProgress && mc.thePlayer.hurtTime != 0) {
						if (mc.thePlayer.getHeldItem() != null)
							if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
								mc.gameSettings.keyBindUseItem.pressed = true;
								// Speed.setSpeed(0.2875);
							}
					}
					break;
				case "NCP":
					if (mc.thePlayer.getHeldItem() != null)
						if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {

							mc.playerController.sendUseItem((EntityPlayer) mc.thePlayer, (World) mc.theWorld,
									mc.thePlayer.getHeldItem());
						}
//					}
					break;
				case "Hypixel":
//					if(mc.thePlayer.ticksExisted % 3 == 0) {
					if (mc.thePlayer.getHeldItem() != null)
						if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
							mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 100);
//							mc.playerController.sendUseItem((EntityPlayer) mc.thePlayer, (World) mc.theWorld,
//									mc.thePlayer.getHeldItem());
						}
//					}
					//mc.gameSettings.keyBindUseItem.pressed = false;

					break;
				case "Other":
					if (timeAB.hasReached(randomNumber(63, 56))) {
						mc.gameSettings.keyBindUseItem.pressed = true;
						mc.gameSettings.keyBindSprint.pressed = false;
						mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(),
								mc.thePlayer.getHeldItem().getMaxItemUseDuration());
						mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
						mc.gameSettings.keyBindSprint.pressed = false;
						timeAB.reset();
					}

					mc.gameSettings.keyBindUseItem.pressed = false;
					break;

				}

			}
		}
	}

	public boolean smartblock(Entity entity) {

		float yaw = entity.rotationYaw;
		while (yaw > 360.0F) {
			yaw -= 360.0F;
		}
		while (yaw < 0.0F) {
			yaw += 360.0F;
		}
		while (yaw > 360.0F) {
			yaw -= 360.0F;
		}
		while (yaw < 0.0F) {
			yaw += 360.0F;
		}

		if (Math.abs(yaw - mc.thePlayer.rotationYawHead) >= 130.0F
				&& Math.abs(yaw - mc.thePlayer.rotationYawHead) <= 280.0F) {
			return true;
		}
		return false;
	}

	float lostHealthPercentage = 0;
	float lastHealthPercentage = 0;

	public void drawTargetHUD() {
		switch (((DropdownBox) this.getSetting("TargetHUDMode").getSetting()).curOption) {
		case "Novoline":
			if (((CheckBox) this.getSetting("TargetHUD").getSetting()).state && kTarget != null
					&& kTarget instanceof EntityPlayer && !FriendSystem.isFriendString(Killaura.kTarget.getName())) {
				EntityLivingBase target = (EntityLivingBase) kTarget;

				if (target != null && target instanceof EntityPlayer || target instanceof EntityAnimal
						|| target instanceof EntityVillager || target instanceof EntityMob) {

					GlStateManager.pushMatrix();
					GlStateManager.translate(ScaledResolution.INSTANCE.getScaledWidth() / 2F,
							ScaledResolution.INSTANCE.getScaledHeight() / 1.8F, 0);

					// drawRect(0, 0, 149, 0.5F, getColor(0, 0, 0, 75));
					// drawRect(0, 1, 0.5F, 59.5F, getColor(0, 0, 0, 75));
//			drawRect(0, 59.5F, 139, 3, getColor(0, 0, 0, 75));
//			drawRect(149.5F, 0, 150, 20, getColor(0, 0, 0, 75));
					switch (((DropdownBox) this.getSetting("TargetHUDColor").getSetting()).curOption) {

					case "DARK":
						drawRect(8, 4, 151, 42, getColor(0, 0, 0, 255));
						drawRect(9, 5, 150, 41, getColor(40, 40, 40, 255));

						break;
					case "WHITE":
						drawRect(0, 0, 150, 50, getColor(255, 255, 255, 32));
						break;
					}

					mc.fontRendererObj.drawStringWithShadow(target.getName(), 46.5F, (int) 7F,
							getColor(255, 255, 255, 255));

//			mc.fontRendererObj.drawStringWithShadow(target.getName(), 46.5F, (int) 8F,
//					getColor(255, 255, 255, 255));

					// renderPlayer(25, 55, 23, target);

					float healthProcent = target.getHealth() / target.getMaxHealth();
					if (lastHealthPercentage != healthProcent) {
						lostHealthPercentage += lastHealthPercentage - healthProcent;
					}
					lastHealthPercentage = healthProcent;
					final float finalHealthPercentage = healthProcent + lostHealthPercentage;

					// Set Easing Method
					lostHealthPercentage = Math.max(0, lostHealthPercentage - (lostHealthPercentage / 20));
					// Draw Rectangle, X, Y, WIDTH, HEIGHT, COLOR.
//			drawRect(47, 18, 55 + (90 * finalHealthPercentage), 28,
//					Color.HSBtoRGB(Math.min(-finalHealthPercentage + 0.3F, 0), 1, 1));
					drawRect(47, 18, 55 + (90 * finalHealthPercentage), 28, Color.white.getRGB());
//			drawRect(55, 15, 55 + (90 * healthProcent), 25,
//					Color.HSBtoRGB(Math.min(-healthProcent + 0.3F, 0), 1, 1));
					mc.fontRendererObj.drawStringWithShadow(
							String.valueOf(Math.round(target.getHealth()) + ".0" + " ❤"), 47, (int) 31F,
							Color.white.getRGB());

					double winChance = 0;

					double TargetStrength = getWeaponStrength(target.getHeldItem());
					winChance = getWeaponStrength(mc.thePlayer.getHeldItem()) - TargetStrength;
					winChance += getProtection(mc.thePlayer) - getProtection(target);
					winChance += mc.thePlayer.getHealth() - (target).getHealth();

					String message = winChance == 0 ? "You could win"
							: winChance < 0 ? "You could lose" : "You are going to win";
					Client.INSTANCE.unicodeBasicFontRenderer.drawString(message,
							46.5F - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(message)
									+ Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(message) / 1F,
							30F, getColor(255, 240, 0, 1));
					GlStateManager.popMatrix();
				}

				List NetworkMoment = GuiPlayerTabOverlay.field_175252_a
						.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
				Iterator itarlor = NetworkMoment.iterator();
				while (itarlor.hasNext()) {
					Object nextObject = itarlor.next();
					NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo) nextObject;
					if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target) {
						GlStateManager.enableCull();
						mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
						Gui.drawScaledCustomSizeModalRect(ScaledResolution.INSTANCE.getScaledWidth() / 2 + 10,
								ScaledResolution.INSTANCE.getScaledHeight() / 2 + 35, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F,
								66.0F);

					}
				}

			}

			break;
		case "OldAstolfo":
			if (((CheckBox) this.getSetting("TargetHUD").getSetting()).state && kTarget != null
					&& kTarget instanceof EntityPlayer && !FriendSystem.isFriendString(Killaura.kTarget.getName())) {
				EntityLivingBase target = (EntityLivingBase) kTarget;

				if (target != null && target instanceof EntityPlayer || target instanceof EntityAnimal
						|| target instanceof EntityVillager || target instanceof EntityMob) {

					GlStateManager.pushMatrix();
					GlStateManager.translate(ScaledResolution.INSTANCE.getScaledWidth() / 2F,
							ScaledResolution.INSTANCE.getScaledHeight() / 1.8F, 0);

					// drawRect(0, 0, 149, 0.5F, getColor(0, 0, 0, 75));
					// drawRect(0, 1, 0.5F, 59.5F, getColor(0, 0, 0, 75));
//			drawRect(0, 59.5F, 139, 3, getColor(0, 0, 0, 75));
//			drawRect(149.5F, 0, 150, 20, getColor(0, 0, 0, 75));
					switch (((DropdownBox) this.getSetting("TargetHUDColor").getSetting()).curOption) {

					case "DARK":
						drawRect(0, 0, 150, 50, getColor(0, 0, 0, 160));
						break;
					case "WHITE":
						drawRect(0, 0, 150, 50, getColor(255, 255, 255, 32));
						break;
					}

					Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(target.getName(), 46.5F, (int) 3F,
							getColor(255, 240, 0, 255));

					// renderPlayer(25, 55, 23, target);

					float healthProcent = target.getHealth() / target.getMaxHealth();
					if (lastHealthPercentage != healthProcent) {
						lostHealthPercentage += lastHealthPercentage - healthProcent;
					}
					lastHealthPercentage = healthProcent;
					final float finalHealthPercentage = healthProcent + lostHealthPercentage;

					// Set Easing Method
					lostHealthPercentage = Math.max(0, lostHealthPercentage - (lostHealthPercentage / 20));
					// Draw Rectangle, X, Y, WIDTH, HEIGHT, COLOR.
					drawRect(47, 18, 55 + (90 * finalHealthPercentage), 28,
							Color.HSBtoRGB(Math.min(-finalHealthPercentage + 0.3F, 0), 1, 1));

//			drawRect(55, 15, 55 + (90 * healthProcent), 25,
//					Color.HSBtoRGB(Math.min(-healthProcent + 0.3F, 0), 1, 1));
//			Client.INSTANCE.unicodeBasicFontRenderer.drawString(String.valueOf(Math.round(target.getHealth())),
//			 100, (int) 16F,
//			Color.HSBtoRGB(Math.min(-healthProcent + 0.3F, 0), 1, 1));

					double winChance = 0;

					double TargetStrength = getWeaponStrength(target.getHeldItem());
					winChance = getWeaponStrength(mc.thePlayer.getHeldItem()) - TargetStrength;
					winChance += getProtection(mc.thePlayer) - getProtection(target);
					winChance += mc.thePlayer.getHealth() - (target).getHealth();

					String message = winChance == 0 ? "You could win"
							: winChance < 0 ? "You could lose" : "You are going to win";
					Client.INSTANCE.unicodeBasicFontRenderer.drawString(message,
							46.5F - Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(message)
									+ Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(message) / 1F,
							30F, getColor(255, 240, 0, 1));
					GlStateManager.popMatrix();
				}

				if (((CheckBox) this.getSetting("RedColoring").getSetting()).state) {
					if (kTarget.hurtTime != 0) {
						GL11.glColor4d(255, 0, 0, 255);
					}
				}

				List NetworkMoment = GuiPlayerTabOverlay.field_175252_a
						.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
				Iterator itarlor = NetworkMoment.iterator();
				while (itarlor.hasNext()) {
					Object nextObject = itarlor.next();
					NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo) nextObject;
					if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target) {
						GlStateManager.enableCull();
						mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
						Gui.drawScaledCustomSizeModalRect(ScaledResolution.INSTANCE.getScaledWidth() / 2 + 10,
								ScaledResolution.INSTANCE.getScaledHeight() / 2 + 35, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F,
								66.0F);

					}
				}

			}
			break;
		}

		switch (((DropdownBox) this.getSetting("TargetHUDMode").getSetting()).curOption) {

		case "Fanta":
			if (((CheckBox) this.getSetting("TargetHUD").getSetting()).state && kTarget != null
					&& kTarget instanceof EntityPlayer && !FriendSystem.isFriendString(Killaura.kTarget.getName())) {

				Entity target = kTarget;
				ScaledResolution s = new ScaledResolution(mc);
				Color backgroundColor = new Color(0, 0, 0, 120);
				Color emptyBarColor = new Color(59, 59, 59, 160);
				Color healthBarColor = Color.green;
				Color distBarColor = new Color(20, 81, 208);
				int left = s.getScaledWidth() / 2 + 5;
				int right2 = 80;
				int right = s.getScaledWidth() / 2 + right2;
				int right3 = 80 + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target.getName()) / 2 - 15;
				int top = s.getScaledHeight() / 2 - 25;
				int bottom = s.getScaledHeight() / 2 + 25;
				float curTargetHealth = ((EntityPlayer) target).getHealth();

				float maxTargetHealth = ((EntityPlayer) target).getMaxHealth();
				float healthProcent = kTarget.getHealth() / kTarget.getMaxHealth();
				if (lastHealthPercentage != healthProcent) {
					lostHealthPercentage += lastHealthPercentage - healthProcent;
				}
				lastHealthPercentage = healthProcent;
				final float finalHealthPercentage = healthProcent + lostHealthPercentage;

				// Set Easing Method
				lostHealthPercentage = Math.max(0, lostHealthPercentage - (lostHealthPercentage / 20));
				float calculatedHealth = finalHealthPercentage;
				int rectRight = right + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target.getName()) / 2
						- 5;
				float healthPos = calculatedHealth * right3;
				// WHITEMODE
				Client.blurHelper.blur2(left, top,
						right + +Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target.getName()) - 5, bottom,
						(float) 10);
//				new Color(25, 25, 25, 205)
				drawRect(left, top,
						right + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target.getName()) - 5, bottom,
						new Color(25, 25, 25, 205));
				// float healthProcent = kTarget.getHealth() / kTarget.getMaxHealth();
				if (lastHealthPercentage != healthProcent) {
					lostHealthPercentage += lastHealthPercentage - healthProcent;
				}
				lastHealthPercentage = healthProcent;
				// final float finalHealthPercentage = healthProcent + lostHealthPercentage;

				// Set Easing Method
				lostHealthPercentage = Math.max(0, lostHealthPercentage - (lostHealthPercentage / 20));
				// Draw Rectangle, X, Y, WIDTH, HEIGHT, COLOR.
//		drawRect(47, 18, 55 + (90 * finalHealthPercentage), 28,
//				Color.HSBtoRGB(Math.min(-finalHealthPercentage + 0.3F, 0), 1, 1));

				// Util.drawRect(left, top, rectRight, bottom, new Color(255,255,255,255));

				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				List NetworkMoment = GuiPlayerTabOverlay.field_175252_a
						.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
				Iterator itarlor = NetworkMoment.iterator();
				while (itarlor.hasNext()) {
					Object nextObject = itarlor.next();
					NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo) nextObject;
					if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target) {
						GlStateManager.enableCull();
						mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());

						Gui.drawScaledCustomSizeModalRect(s.getScaledWidth() / 2 + 10, s.getScaledHeight() / 2 - 22,
								8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 66.0F);

					}
				}

//		// Draw Rectangle, X, Y, WIDTH, HEIGHT, COLOR.
//		drawRect(47, 15, 55 + (90 * finalHealthPercentage), 25,
//				Color.HSBtoRGB(Math.min(-finalHealthPercentage + 0.3F, 0), 1, 1));
				// Util.drawRect(left + 5, bottom - 14, left + right3, bottom - 12, Color.CYAN);

				drawRect(left + 5, bottom - 13,
						left + healthPos
								+ Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target.getName()) / 2,
						bottom - 5, healthBarColor);

				// drawRect(left + 5, bottom - 13, left + healthPos + 35, bottom - 5,
				// healthBarColor);

				// drawRect(left + 5, bottom - 8, left + right3, bottom - 6, Color.BLUE);

				// renderPlayer(260, 260, 0, Killaura.instance.getTarget());
				// WhiteMODE

				Client.INSTANCE.unicodeBasicFontRenderer5.drawStringWithShadow(target.getName(), left + 40, top + 4,
						Color.WHITE.getRGB());
				Client.INSTANCE.unicodeBasicFontRenderer5.drawStringWithShadow("Health: " + Math.round(curTargetHealth),
						left + 40, top + 20, Color.WHITE.getRGB());

			}

			break;
		}

	}

//
//                String name = kTarget.getName();
//                double distance = Math.round(mc.thePlayer.getDistanceToEntity(kTarget));
//                RenderItem item = mc.getRenderItem();
//                ScaledResolution sr = new ScaledResolution(mc);
//
//                int health = (int) ((EntityPlayer) kTarget).getHealth();
//                int healthBar = (int) (((EntityPlayer) kTarget).getHealth() * 7.3);
//
//                Gui.drawRect(sr.getScaledWidth() / 2 - 150, sr.getScaledHeight() / 2 - 60, sr.getScaledWidth() / 2 - 10, sr.getScaledHeight() / 2 + 10, Integer.MIN_VALUE);
//                Client.INSTANCE.unicodeBasicFontRenderer.drawString("Name: " + name, sr.getScaledWidth() / 2 - 150, sr.getScaledHeight() / 2 - 60, Integer.MAX_VALUE);
//                Client.INSTANCE.unicodeBasicFontRenderer.drawString("Distance: " + distance, sr.getScaledWidth() / 2 - 150, sr.getScaledHeight() / 2 - 45, Integer.MAX_VALUE);
//
//                drawFace(sr.getScaledWidth() / 2 - 54, sr.getScaledHeight() / 2 - 60, 8, 8, 8, 8, 44, 44, 64, 64, (AbstractClientPlayer) kTarget);
//
//                if(health <= 20 && health >= 0) {
//                    Gui.drawRect(sr.getScaledWidth() / 2 - 156 + healthBar, sr.getScaledHeight() / 2 - 10, sr.getScaledWidth() / 2 - 150, sr.getScaledHeight() / 2 + 10, Color.GRAY.getRGB());
//                    Gui.drawRect(sr.getScaledWidth() / 2 - 150, sr.getScaledHeight() / 2 - 10, sr.getScaledWidth() / 2 - 156 + healthBar, sr.getScaledHeight() / 2 + 10, Color(health));
//                }else if(health >= 0){
//                    Client.INSTANCE.unicodeBasicFontRenderer.drawString("Too much! Endboss!", sr.getScaledWidth() / 2- 150, sr.getScaledHeight() / 2 - 10, Color.RED.getRGB());
//                }else{
//                    Client.INSTANCE.unicodeBasicFontRenderer.drawString("NaN", sr.getScaledWidth() / 2- 150, sr.getScaledHeight() / 2 - 10, Color.RED.getRGB());
//
//                }
//            }

	public void drawFace(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height,
			float tileWidth, float tileHeight, AbstractClientPlayer e) {
		mc.getTextureManager().bindTexture(e.getLocationSkin());
		GL11.glEnable(GL11.GL_BLEND);
		Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public int Color(int health) {
		if (health == 20) {
			return 0xFF2FEA29;
		} else if (health == 19) {
			return 0xFF40EA29;
		} else if (health == 18) {
			return 0xFF72EA29;
		} else if (health == 17) {
			return 0xFF8AEA29;
		} else if (health == 16) {
			return 0xFFA1EA29;
		} else if (health == 15) {
			return 0xFFB8EA29;
		} else if (health == 14) {
			return 0xFFC7EA29;
		} else if (health == 13) {
			return 0xFFD3EA29;
		} else if (health == 12) {
			return 0xFFE4EA29;
		} else if (health == 11) {
			return 0xFFE4EA29;
		} else if (health == 10) {
			return 0xFFEAE729;
		} else if (health == 9) {
			return 0xFFEAD629;
		} else if (health == 8) {
			return 0xFFEAC129;
		} else if (health == 7) {
			return 0xFFEAB829;
		} else if (health == 6) {
			return 0xFFEAA729;
		} else if (health == 5) {
			return 0xFFEA8F29;
		} else if (health == 4) {
			return 0xFFEA7B29;
		} else if (health == 3) {
			return 0xFFEA6629;
		} else if (health == 2) {
			return 0xFFEA5E29;
		} else if (health == 1) {
			return 0xFFEA4029;
		} else if (health < 1) {
			return 0xFFFC0303;
		}
		return 0xFF000000;
	}

	private static double getProtection(EntityLivingBase target) {
		double protection = 0;

		for (int i = 0; i <= 3; i++) {
			ItemStack stack = target.getCurrentArmor(i);

			if (stack != null) {
				if (stack.getItem() instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) stack.getItem();
					protection += armor.damageReduceAmount;
				}

				protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
			}
		}

		return protection;
	}

	private static double getWeaponStrength(ItemStack stack) {
		double damage = 0;

		if (stack != null) {
			if (stack.getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) stack.getItem();
				damage += sword.getDamageVsEntity();
			}

			if (stack.getItem() instanceof ItemTool) {
				ItemTool tool = (ItemTool) stack.getItem();
				damage += tool.getToolMaterial().getDamageVsEntity();
			}

			damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25;
		}

		return damage;
	}

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, top, 0.0D).endVertex();
		worldrenderer.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static int getColor(int red, int green, int blue, int opacity) {
		int color = MathHelper.clamp_int(opacity, 0, 255) << 24;
		color |= MathHelper.clamp_int(red, 0, 255) << 16;
		color |= MathHelper.clamp_int(green, 0, 255) << 8;
		color |= MathHelper.clamp_int(blue, 0, 255);
		return color;
	}

	private static void renderPlayer(int posX, int posY, int scale, EntityLivingBase player) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.translate((float) posX, (float) posY, 50.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		RenderHelper.enableStandardItemLighting();
		player.rotationYawHead = player.rotationYaw;
		player.prevRotationYawHead = player.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntityWithPosYaw(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		rendermanager.setRenderShadow(true);
		player.renderYawOffset = player.rotationYaw;
		player.prevRotationYawHead = player.rotationYaw;
		player.rotationYawHead = player.rotationYaw;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static void fillRect(final double x, final double y, final double width, final double height,
			final Color color) {

		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
				color.getAlpha() / 255.0F);

		GL11.glBegin(GL11.GL_QUADS);

		GL11.glVertex2d(x, y + height);
		GL11.glVertex2d(x + width, y + height);
		GL11.glVertex2d(x + width, y);
		GL11.glVertex2d(x, y);

	}

	public static void drawRectRound(final int x, final int y, final int width, final int height, final int diameter,
			final Color color) {
		// RenderUtil.fillRect(x - diameter, y - diameter, width - diameter * 2, height
		// - diameter * 2, color);

		// RenderUtil.fillRect(x, y - diameter * 2, width - diameter * 4, diameter,
		// color);

		// RenderUtil.fillRect(x, y + height - diameter * 3, width - diameter * 4,
		// diameter, color);
		fillRect(x, y + diameter, width, height - (diameter * 2), color);
		fillRect(x + 5, y, width - (diameter * 2), height, color);

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		// Oberen Kreise
		drawCircle(x + diameter, y + diameter, diameter, 0, 360, color);
		drawCircle(x + width - diameter, y + diameter, diameter, 0, 360, color);

		// Untere Kreise
		drawCircle(x + diameter, y + height - diameter, diameter, 0, 360, color);
		drawCircle(x + width - diameter, y + height - diameter, diameter, 0, 360, color);

	}

	private static void drawCircle(final float x, final float y, final float diameter, final int start, final int stop,
			final Color color) {
		final double twicePi = Math.PI * 2;

		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
				color.getAlpha() / 255.0F);

		for (int i = start; i <= stop; i++) {
			GL11.glVertex2d(x + diameter * Math.sin(i * twicePi / stop), y + diameter * Math.cos(i * twicePi / stop));
		}

	}

	boolean isBot(EntityPlayer player) {
		if (!isInTablist(player))
			return true;
		if (!madeSound.contains(player) && ((CheckBox) this.getSetting("SoundCheck").getSetting()).state)
			return true;
		return invalidName(player);
	}

	boolean isInTablist(EntityPlayer player) {
		if (Minecraft.getMinecraft().isSingleplayer())
			return false;
		for (final NetworkPlayerInfo playerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap())
			if (playerInfo.getGameProfile().getName().equalsIgnoreCase(player.getName()))
				return true;
		return false;
	}

	boolean invalidName(Entity e) {
		if (e.getName().contains("-"))
			return true;
		if (e.getName().contains("/"))
			return true;
		if (e.getName().contains("|"))
			return true;
		if (e.getName().contains("<"))
			return true;
		if (e.getName().contains(">"))
			return true;
		if (e.getName().contains("ยง"))
			return true;
		return false;
	}

	public static void drawRect(float g, float h, float i, float j, Color col2) {
		GL11.glColor4f(col2.getRed() / 255.0F, col2.getGreen() / 255.0F, col2.getBlue() / 255.0F,
				col2.getAlpha() / 255.0F);

		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);

		GL11.glPushMatrix();
		GL11.glBegin(7);
		GL11.glVertex2d(i, h);
		GL11.glVertex2d(g, h);
		GL11.glVertex2d(g, j);
		GL11.glVertex2d(i, j);
		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}

	public void onPre(EventPreMotion pre) {

		if (kTarget != null) {

			this.facing = getEntityRotations(mc.thePlayer, kTarget);
			pre.setYaw(yaww);
			pre.setPitch(pitchh);
			yaww = interpolateRotation(yaww, facing[0], 180);
			pitchh = interpolateRotation(pitchh, facing[1], 180);
		}
	}

	public static float interpolateRotation(float par1, float par2, float par3) {
		float f = MathHelper.wrapAngleTo180_float(par2 - par1);
		if (f > par3)
			f = par3;
		if (f < -par3)
			f = -par3;
		return par1 + f;
	}

	public static float[] getEntityRotations(EntityPlayerSP player, EntityLivingBase target) {
		final double posX = target.posX - player.posX;
		final double posY = target.posY + target.getEyeHeight() - (player.posY + player.getEyeHeight());
		float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 150, 180);
		final double posZ = target.posZ - player.posZ;
		final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
		float yaw = (float) (Math.atan2(posZ, posX) * RotationY2 / Math.PI) - 90.0f;
		float pitch = (float) (-(Math.atan2(posY, var14) * RotationY2 / Math.PI));
		float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		return new float[] { yaw, pitch };
	}

	public void drawTargetESP(EntityPlayer target, float pt) {
		final double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * pt
				- mc.getRenderManager().viewerPosX;
		final double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * pt
				- mc.getRenderManager().viewerPosY;
		final double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * pt
				- mc.getRenderManager().viewerPosZ;
		int[] rgb = Colors.getRGB(getColor2());
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.disableAlpha();
		GL11.glLineWidth(3F);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_CULL_FACE);
		final double size = target.width * 1.2;
		float factor = (float) Math.sin(System.nanoTime() / 300000000f);
		GL11.glTranslatef(0, factor, 0);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		{
			for (int j = 0; j < 361; j++) {
				RenderUtil.color(Colors.getColor(rgb[0], rgb[1], rgb[2], 200));
				double x1 = x + Math.cos(Math.toRadians(j)) * size;
				double z1 = z - Math.sin(Math.toRadians(j)) * size;
				GL11.glVertex3d(x1, y + 1, z1);
				RenderUtil.color(Colors.getColor(rgb[0], rgb[1], rgb[2], 0));
				GL11.glVertex3d(x1, y + 1 + factor * 0.4f, z1);
			}
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for (int j = 0; j < 361; j++) {
				RenderUtil.color(Colors.getColor(rgb[0], rgb[1], rgb[2], 1));
				GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + 1, z - Math.sin(Math.toRadians(j)) * size);
			}
		}
		GL11.glEnd();
		GlStateManager.enableAlpha();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GlStateManager.enableBlend();

		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.resetColor();
		GlStateManager.popMatrix();
	}

	public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}
}
