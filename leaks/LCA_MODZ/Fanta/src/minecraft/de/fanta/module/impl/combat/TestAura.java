package de.fanta.module.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.MoveFlyingEvent;
import de.fanta.events.listeners.EventClickMouse;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventSilentMove;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.AnimationUtil;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.Colors;
import de.fanta.utils.FriendSystem;
import de.fanta.utils.RandomUtil;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.Rotations;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TestAura extends Module {
	public TestAura() {
		super("TestAura", 0, Type.Combat, new Color(108, 2, 139));
		this.settings.add(new Setting("Range", new Slider(1, 8, 0.1, 4)));
		this.settings.add(new Setting("PreAimRange", new Slider(0, 5, 0.1, 2)));
		this.settings.add(new Setting("CPS-Max", new Slider(0, 20, 1, 10)));
		this.settings.add(new Setting("CPS-Min", new Slider(0, 20, 1, 2)));
		this.settings.add(new Setting("Cracks", new Slider(0, 1000, 1, 15)));
		this.settings.add(new Setting("LockView", new CheckBox(false)));
		this.settings.add(new Setting("TargetESP", new CheckBox(false)));
		this.settings.add(new Setting("Heuristics", new CheckBox(false)));
		this.settings.add(new Setting("HeuristicsYaw", new Slider(85, 95, 1, 90)));
		this.settings.add(new Setting("HeuristicsYaw2", new Slider(85, 95, 1, 90)));
		this.settings.add(new Setting("HeuristicsPitch", new Slider(170, 190, 1, 180)));
		this.settings.add(new Setting("HeuristicsPitch2", new Slider(170, 190, 1, 180)));
		this.settings.add(new Setting("RandomRotSpeed", new CheckBox(false)));
		this.settings.add(new Setting("Speed", new Slider(0, 180, 1, 75)));
		this.settings.add(new Setting("Speed1", new Slider(0, 180, 1, 80)));
		this.settings.add(new Setting("MoveFix", new CheckBox(false)));
		this.settings.add(new Setting("SilentMoveFix", new CheckBox(false)));
		this.settings.add(new Setting("GCD-Fix", new CheckBox(false)));
		this.settings.add(new Setting("ABS-GCD-Fix", new CheckBox(false)));
		this.settings.add(new Setting("JumpFix", new CheckBox(false)));
		this.settings.add(new Setting("KeepSprint", new CheckBox(false)));
		this.settings.add(new Setting("Autoblock", new CheckBox(false)));
		this.settings.add(new Setting("NewPVPSystem", new CheckBox(false)));
		this.settings.add(new Setting("Antibot", new CheckBox(false)));
		this.settings.add(new Setting("Raytrace", new CheckBox(false)));
		this.settings.add(new Setting("Intave14Fix", new CheckBox(false)));
		this.settings.add(new Setting("TargetHUD", new CheckBox(false)));
		this.settings.add(new Setting("TargetESP", new CheckBox(false)));
		this.settings.add(new Setting("Rotations", new CheckBox(true)));
		this.settings.add(new Setting("AttackType", new DropdownBox("Legit", new String[] { "Legit", "Packet" })));
		this.settings
				.add(new Setting("AutoBlock", new DropdownBox("Intave", new String[] { "Intave", "SmartIntave" })));
		this.settings
				.add(new Setting("Modes", new DropdownBox("Nearest", new String[] { "Single", "Nearest", "Switch" })));
		this.settings.add(new Setting("TargetHUD",
				new DropdownBox("Novoline", new String[] { "Fanta", "OldAstolfo", "Novoline" })));
		this.settings.add(new Setting("Color", new ColorValue(Color.white.getRGB())));
	}

	TimeUtil timeUtil = new TimeUtil();
	public static EntityLivingBase target;
	private float lastYaw;
	private float lastPitch;
	public static double range;
	public static double preaimrange;
	public static double minCPS;
	public static double maxCPS;
	public static double cracks;
	public static double heursticsyaw;
	public static double heursticsyaw2;
	public static double heursticspitch;
	public static double heursticspitch2;
	public static double rotSpeed;
	public static double randomrotSpeed;
	public static double randomrotSpeed2;
	private boolean hasRotation = false;
	public static ArrayList<Entity> bots = new ArrayList<>();
	public double current, x, y, dragX, dragY;
	public boolean dragging;
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRender3D) {
			if (((CheckBox) this.getSetting("TargetESP").getSetting()).state) {
				drawTargetESP((EntityPlayer) target, mc.timer.renderPartialTicks);
			}
		}
		range = ((Slider) this.getSetting("Range").getSetting()).curValue;
		preaimrange = ((Slider) this.getSetting("PreAimRange").getSetting()).curValue;
		maxCPS = ((Slider) this.getSetting("CPS-Max").getSetting()).curValue;
		minCPS = ((Slider) this.getSetting("CPS-Min").getSetting()).curValue;
		cracks = ((Slider) this.getSetting("CPS-Min").getSetting()).curValue;
		heursticspitch = ((Slider) this.getSetting("HeuristicsPitch").getSetting()).curValue;
		heursticspitch2 = ((Slider) this.getSetting("HeuristicsPitch2").getSetting()).curValue;
		heursticsyaw = ((Slider) this.getSetting("HeuristicsYaw").getSetting()).curValue;
		heursticsyaw2 = ((Slider) this.getSetting("HeuristicsYaw2").getSetting()).curValue;
		// rotSpeed = ((Slider) this.getSetting("RotSpeed").getSetting()).curValue;
		randomrotSpeed = ((Slider) this.getSetting("Speed").getSetting()).curValue;
		randomrotSpeed2 = ((Slider) this.getSetting("Speed1").getSetting()).curValue;

		if (e instanceof EventPreMotion) {
			if (target == null) current = 0.7;
			
			if (((CheckBox) this.getSetting("Antibot").getSetting()).state) {

				for (final Object entity : mc.theWorld.getLoadedEntityList())
					if (entity instanceof EntityPlayer)
						if ((isBot((EntityPlayer) entity) || ((EntityPlayer) entity).isInvisible())
								&& entity != mc.thePlayer) {
							TestAura.bots.add((EntityPlayer) entity);
							mc.theWorld.removeEntity((Entity) entity);

						}
			}
			if (((CheckBox) this.getSetting("Autoblock").getSetting()).state) {
				if (((CheckBox) this.getSetting("Autoblock").getSetting()).state) {
					if (!mc.thePlayer.isSwingInProgress) {
						if (mc.thePlayer.getHeldItem() != null)
							if (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
								if (target == null) {
									mc.gameSettings.keyBindUseItem.pressed = false;
								}
							}
					}

				}

			}
			target = (EntityLivingBase) modes();
			if (mc.objectMouseOver != null && target != null) {
				hasRotation = true;
				if (((CheckBox) this.getSetting("Rotations").getSetting()).state) {
					if (bots.contains(target) || FriendSystem.isFriendString(target.getName())
							|| target.getName().equalsIgnoreCase("§6Shop"))
						return;
					if (((CheckBox) this.getSetting("LockView").getSetting()).state) {
						mc.thePlayer.rotationYaw = Rotations.yaw;
						mc.thePlayer.rotationPitch = Rotations.pitch;
					} else {
						((EventPreMotion) e).setPitch(Rotations.pitch);
						((EventPreMotion) e).setYaw(Rotations.yaw);
					}
				}
				rotationModes(target);
			} else {
				if (((CheckBox) this.getSetting("Intave14Fix").getSetting()).state) {
					if (hasRotation) {
						resetRotation(Rotations.yaw);
						hasRotation = false;
					}
				}
			}
		}

	/*	if (e instanceof EventRender2D && e.isPre() && target != null
				&& ((CheckBox) this.getSetting("TargetHUD").getSetting()).state) {
			drawTargetHud();
		}*/

		if (e instanceof EventClickMouse) {
			int CPS = randomNumber((int) maxCPS, (int) minCPS);
			if (target != null) {
				if (!((CheckBox) this.getSetting("NewPVPSystem").getSetting()).state) {

					switch (((DropdownBox) this.getSetting("AttackType").getSetting()).curOption) {
					case "Legit":
						if (bots.contains(target) || FriendSystem.isFriendString(target.getName())
								|| target.getName().equalsIgnoreCase("§6Shop"))
							return;
						if (timeUtil.hasReached(1000 / CPS)) {
							mc.clickMouse();
							timeUtil.reset();
						}
						break;
					case "Packet":
						if (bots.contains(getPlayer()) || FriendSystem.isFriendString(target.getName())
								|| target.getName().equalsIgnoreCase("§6Shop"))
							return;
						if (timeUtil.hasReached(1000 / CPS)) {
							mc.thePlayer.swingItem();
							mc.playerController.attackEntity(mc.thePlayer, target);
							timeUtil.reset();
						}
						break;
					}
					// Spawn Particle
					if (timeUtil.hasReached(1000 / CPS)) {
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
								Entity.worldObj.spawnParticle(EntityParticleEmitter.particleTypes, false, d3, d4, d5,
										d0, d1 + 0.2D, d2, new int[1]);
								// END
							}
						}
						timeUtil.reset();
					}
				} else {
					if (mc.thePlayer.ticksExisted % 12 == 0) {
						mc.clickMouse();
					}
				}
				AutoBlock();
			}
		}
	}

	public static int randomNumber(int max, int min) {
		return Math.round(min + (float) Math.random() * (max - min));
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
						if (mc.thePlayer.getDistanceToEntity(e) < range + preaimrange) {
							target = (EntityPlayer) player;
						}
					} else if (player.hurtTime == target.hurtTime) {
						if (mc.thePlayer.getDistanceToEntity(player) < mc.thePlayer.getDistanceToEntity(target)) {
							if (mc.thePlayer.getDistanceToEntity(e) < range + preaimrange) {
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
					if (target == null
							|| mc.thePlayer.getDistanceToEntity(e) <= mc.thePlayer.getDistanceToEntity(target) + 0) {
						if (mc.thePlayer.getDistanceToEntity(e) < range + preaimrange) {
							target = (EntityPlayer) e;
						}

					}
				}
			}
			return target;

		case "Nearest":

			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;
				if (!e.getName().equals(mc.thePlayer.getName()) && e instanceof EntityPlayer
						&& !(e instanceof EntityArmorStand)) {

					if (target == null
							|| mc.thePlayer.getDistanceToEntity(e) < mc.thePlayer.getDistanceToEntity(target)) {
						if (mc.thePlayer.getDistanceToEntity(e) < range + preaimrange) {
							target = (EntityPlayer) e;
						}

					}
				}
			}
		}
		return target;

	}

	public void rotationModes(Entity target) {
		if (((CheckBox) this.getSetting("GCD-Fix").getSetting()).state) {
			if (((CheckBox) this.getSetting("Heuristics").getSetting()).state) {
				float[] rota = getNewKillAuraRotsHeuristicsGCD(mc.thePlayer, (EntityLivingBase) target, lastYaw,
						lastPitch);
				lastYaw = rota[0];
				lastPitch = rota[1];
				if (mc.objectMouseOver.entityHit != null) {

					if (((CheckBox) this.getSetting("RandomRotSpeed").getSetting()).state) {
						final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
								randomrotSpeed, randomrotSpeed2);
						Rotations.setYaw(rota[0], rotationSpeed);
						Rotations.setPitch(rota[1], rotationSpeed);
					} else {
						Rotations.setYaw(rota[0], 180F);
						Rotations.setPitch(rota[1], 180F);

					}
				}

				if (mc.objectMouseOver.entityHit == null) {
					final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(), randomrotSpeed,
							randomrotSpeed2);
					Rotations.setYaw(rota[0], rotationSpeed);
					Rotations.setPitch(rota[1], rotationSpeed);
				}
			} else {
				float[] rota = getNewKillAuraRotsGCD(mc.thePlayer, (EntityLivingBase) target, lastYaw, lastPitch);
				lastYaw = rota[0];
				lastPitch = rota[1];
				if (mc.objectMouseOver.entityHit != null) {
					if (((CheckBox) this.getSetting("RotSpeed").getSetting()).state) {
						Rotations.setYaw(rota[0], (float) rotSpeed);
						Rotations.setPitch(rota[1], (float) rotSpeed);
					} else {
						if (((CheckBox) this.getSetting("RandomRotSpeed").getSetting()).state) {
							final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
									randomrotSpeed, randomrotSpeed2);
							Rotations.setYaw(rota[0], rotationSpeed);
							Rotations.setPitch(rota[1], rotationSpeed);
						} else {
							Rotations.setYaw(rota[0], 180F);
							Rotations.setPitch(rota[1], 180F);
						}
					}
				}
				if (mc.objectMouseOver.entityHit == null) {
					Rotations.setYaw(rota[0], 180F);
					Rotations.setPitch(rota[1], 180F);
				}
			}

		} else {
			if (((CheckBox) this.getSetting("ABS-GCD-Fix").getSetting()).state) {
				if (((CheckBox) this.getSetting("Heuristics").getSetting()).state) {
					float[] rota = getNewKillAuraRotsHeuristicsGCDABS(mc.thePlayer, (EntityLivingBase) target, lastYaw,
							lastPitch);
					lastYaw = rota[0];
					lastPitch = rota[1];
					if (mc.objectMouseOver.entityHit != null) {

						if (((CheckBox) this.getSetting("RandomRotSpeed").getSetting()).state) {
							final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
									randomrotSpeed, randomrotSpeed2);
							Rotations.setYaw(rota[0], rotationSpeed);
							Rotations.setPitch(rota[1], rotationSpeed);
						} else {
							Rotations.setYaw(rota[0], 180F);
							Rotations.setPitch(rota[1], 180F);

						}
					}

					if (mc.objectMouseOver.entityHit == null) {
						final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
								randomrotSpeed, randomrotSpeed2);
						Rotations.setYaw(rota[0], rotationSpeed);
						Rotations.setPitch(rota[1], rotationSpeed);
					}
				} else {

					float[] rota = getNewKillAuraRotsGCD(mc.thePlayer, (EntityLivingBase) target, lastYaw, lastPitch);
					lastYaw = rota[0];
					lastPitch = rota[1];
					if (mc.objectMouseOver.entityHit != null) {
						if (((CheckBox) this.getSetting("RotSpeed").getSetting()).state) {
							Rotations.setYaw(rota[0], (float) rotSpeed);
							Rotations.setPitch(rota[1], (float) rotSpeed);
						} else {
							if (((CheckBox) this.getSetting("RandomRotSpeed").getSetting()).state) {
								final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
										randomrotSpeed, randomrotSpeed2);
								Rotations.setYaw(rota[0], rotationSpeed);
								Rotations.setPitch(rota[1], rotationSpeed);
							} else {
								Rotations.setYaw(rota[0], 180F);
								Rotations.setPitch(rota[1], 180F);
							}
						}
					}
					if (mc.objectMouseOver.entityHit == null) {
						Rotations.setYaw(rota[0], 180F);
						Rotations.setPitch(rota[1], 180F);
					}
				}
			} else {
				if (((CheckBox) this.getSetting("Heuristics").getSetting()).state) {
					float[] rota = getNewKillAuraRotsHeuristics(mc.thePlayer, (EntityLivingBase) target, lastYaw,
							lastPitch);
					lastYaw = rota[0];
					lastPitch = rota[1];
					if (mc.objectMouseOver.entityHit != null) {

						if (((CheckBox) this.getSetting("RandomRotSpeed").getSetting()).state) {
							final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
									randomrotSpeed, randomrotSpeed2);
							Rotations.setYaw(rota[0], rotationSpeed);
							Rotations.setPitch(rota[1], rotationSpeed);
						} else {
							Rotations.setYaw(rota[0], 180F);
							Rotations.setPitch(rota[1], 180F);

						}
					}

					if (mc.objectMouseOver.entityHit == null) {
						final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
								randomrotSpeed, randomrotSpeed2);
						Rotations.setYaw(rota[0], rotationSpeed);
						Rotations.setPitch(rota[1], rotationSpeed);
					}
				} else {

					float[] rota = getNewKillAuraRots(mc.thePlayer, (EntityLivingBase) target, lastYaw, lastPitch);
					lastYaw = rota[0];
					lastPitch = rota[1];
					if (mc.objectMouseOver.entityHit != null) {
						if (((CheckBox) this.getSetting("RotSpeed").getSetting()).state) {
							Rotations.setYaw(rota[0], (float) rotSpeed);
							Rotations.setPitch(rota[1], (float) rotSpeed);
						} else {
							if (((CheckBox) this.getSetting("RandomRotSpeed").getSetting()).state) {
								final float rotationSpeed = (float) MathHelper.getRandomDoubleInRange(new Random(),
										randomrotSpeed, randomrotSpeed2);
								Rotations.setYaw(rota[0], rotationSpeed);
								Rotations.setPitch(rota[1], rotationSpeed);
							} else {
								Rotations.setYaw(rota[0], 180F);
								Rotations.setPitch(rota[1], 180F);
							}
						}
					}
					if (mc.objectMouseOver.entityHit == null) {
						Rotations.setYaw(rota[0], 180F);
						Rotations.setPitch(rota[1], 180F);
					}
				}
			}
		}

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

	@Override
	public void onEnable() {
		hasRotation = false;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		bots.clear();
		if (hasRotation) {
			resetRotation(Rotations.yaw);
		}
		super.onDisable();
	}

	public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}

	public static float[] getNewKillAuraRots(final EntityPlayerSP player, final EntityLivingBase target,
			float currentYaw, float currentPitch) {
		Vec3 positionEyes = player.getPositionEyes(1.0F);
		float f11 = target.getCollisionBorderSize();
		double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11,
				target.getEntityBoundingBox().maxX + f11);
		double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11,
				target.getEntityBoundingBox().maxY + f11);
		double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11,
				target.getEntityBoundingBox().maxZ + f11);
		double x = ex - player.posX;
		double y = ey - (player.posY + (double) player.getEyeHeight());
		double z = ez - player.posZ;
		float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - 90.0F);
		float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D
				/ Math.PI)));
		float yawSpeed = 180;
		float pitchSpeed = 180;
		float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
		float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
		double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
		double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
		if ((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed))
				|| (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
			yaw += RandomUtil.nextFloat(1, 2) * Math.sin(pitch * Math.PI);
			pitch += RandomUtil.nextFloat(1, 2) * Math.sin(yaw * Math.PI);
		}

		return new float[] { yaw, pitch };
	}

	public static float[] getNewKillAuraRotsHeuristics(final EntityPlayerSP player, final EntityLivingBase target,
			float currentYaw, float currentPitch) {
		Vec3 positionEyes = player.getPositionEyes(1.0F);
		float f11 = target.getCollisionBorderSize();
		double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11,
				target.getEntityBoundingBox().maxX + f11);
		double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11,
				target.getEntityBoundingBox().maxY + f11);
		double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11,
				target.getEntityBoundingBox().maxZ + f11);
		double x = ex - player.posX;
		double y = ey - (player.posY + (double) player.getEyeHeight());
		double z = ez - player.posZ;
		final float heursticsyawXD = (float) MathHelper.getRandomDoubleInRange(new Random(), heursticsyaw,
				heursticsyaw2);
		final float heursticspitchXD = (float) MathHelper.getRandomDoubleInRange(new Random(), heursticspitch,
				heursticspitch2);
		float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - heursticsyawXD);
		float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z))
				* heursticspitchXD / Math.PI)));
		float yawSpeed = 180;
		float pitchSpeed = 180;
		float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
		float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
		double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
		double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
		if ((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed))
				|| (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
			yaw += RandomUtil.nextFloat(1, 2) * Math.sin(pitch * Math.PI);
			pitch += RandomUtil.nextFloat(1, 2) * Math.sin(yaw * Math.PI);
		}
		return new float[] { yaw, pitch };
	}

	public static float[] getNewKillAuraRotsHeuristicsGCD(final EntityPlayerSP player, final EntityLivingBase target,
			float currentYaw, float currentPitch) {
		Vec3 positionEyes = player.getPositionEyes(1.0F);
		float f11 = target.getCollisionBorderSize();
		double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11,
				target.getEntityBoundingBox().maxX + f11);
		double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11,
				target.getEntityBoundingBox().maxY + f11);
		double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11,
				target.getEntityBoundingBox().maxZ + f11);
		double x = ex - player.posX;
		double y = ey - (player.posY + (double) player.getEyeHeight());
		double z = ez - player.posZ;
		final float heursticsyawXD = (float) MathHelper.getRandomDoubleInRange(new Random(), heursticsyaw,
				heursticsyaw2);
		final float heursticspitchXD = (float) MathHelper.getRandomDoubleInRange(new Random(), heursticspitch,
				heursticspitch2);
		float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - heursticsyawXD);
		float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z))
				* heursticspitchXD / Math.PI)));
		float yawSpeed = 180;
		float pitchSpeed = 180;
		float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
		float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
		double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
		double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
		if ((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed))
				|| (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
			yaw += RandomUtil.nextFloat(1, 2) * Math.sin(pitch * Math.PI);
			pitch += RandomUtil.nextFloat(1, 2) * Math.sin(yaw * Math.PI);
		}
		final float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= (yaw % f3);
		pitch -= (pitch % (f3 * f2));
		return new float[] { yaw, pitch };
	}

	public static float[] getNewKillAuraRotsGCD(final EntityPlayerSP player, final EntityLivingBase target,
			float currentYaw, float currentPitch) {
		Vec3 positionEyes = player.getPositionEyes(1.0F);
		float f11 = target.getCollisionBorderSize();
		double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11,
				target.getEntityBoundingBox().maxX + f11);
		double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11,
				target.getEntityBoundingBox().maxY + f11);
		double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11,
				target.getEntityBoundingBox().maxZ + f11);
		double x = ex - player.posX;
		double y = ey - (player.posY + (double) player.getEyeHeight());
		double z = ez - player.posZ;
		float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - 90.0F);
		float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D
				/ Math.PI)));
		float yawSpeed = 180;
		float pitchSpeed = 180;
		float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
		float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
		double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
		double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
		if ((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed))
				|| (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
			yaw += RandomUtil.nextFloat(1, 2) * Math.sin(pitch * Math.PI);
			pitch += RandomUtil.nextFloat(1, 2) * Math.sin(yaw * Math.PI);
		}
		final float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= (yaw % f3);
		pitch -= (pitch % (f3 * f2));
		return new float[] { yaw, pitch };
	}

	public static float[] getNewKillAuraRotsHeuristicsGCDABS(final EntityPlayerSP player, final EntityLivingBase target,
			float currentYaw, float currentPitch) {
		Vec3 positionEyes = player.getPositionEyes(1.0F);
		float f11 = target.getCollisionBorderSize();
		double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11,
				target.getEntityBoundingBox().maxX + f11);
		double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11,
				target.getEntityBoundingBox().maxY + f11);
		double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11,
				target.getEntityBoundingBox().maxZ + f11);
		double x = ex - player.posX;
		double y = ey - (player.posY + (double) player.getEyeHeight());
		double z = ez - player.posZ;
		final float heursticsyawXD = (float) MathHelper.getRandomDoubleInRange(new Random(), heursticsyaw,
				heursticsyaw2);
		final float heursticspitchXD = (float) MathHelper.getRandomDoubleInRange(new Random(), heursticspitch,
				heursticspitch2);
		float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - heursticsyawXD);
		float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z))
				* heursticspitchXD / Math.PI)));
		float yawSpeed = 180;
		float pitchSpeed = 180;
		float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
		float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
		double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
		double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
		if ((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed))
				|| (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
			yaw += RandomUtil.nextFloat(1, 2) * Math.sin(pitch * Math.PI);
			pitch += RandomUtil.nextFloat(1, 2) * Math.sin(yaw * Math.PI);
		}
		final float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= Math.abs(yaw % f3);
		pitch -= Math.abs(pitch % (f3 * f2));
		return new float[] { yaw, pitch };
	}

	public static float[] getNewKillAuraRotsGCDABS(final EntityPlayerSP player, final EntityLivingBase target,
			float currentYaw, float currentPitch) {
		Vec3 positionEyes = player.getPositionEyes(1.0F);
		float f11 = target.getCollisionBorderSize();
		double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11,
				target.getEntityBoundingBox().maxX + f11);
		double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11,
				target.getEntityBoundingBox().maxY + f11);
		double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11,
				target.getEntityBoundingBox().maxZ + f11);
		double x = ex - player.posX;
		double y = ey - (player.posY + (double) player.getEyeHeight());
		double z = ez - player.posZ;
		float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - 90.0F);
		float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D
				/ Math.PI)));
		float yawSpeed = 180;
		float pitchSpeed = 180;
		float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
		float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
		double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
		double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
		if ((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed))
				|| (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
			yaw += RandomUtil.nextFloat(1, 2) * Math.sin(pitch * Math.PI);
			pitch += RandomUtil.nextFloat(1, 2) * Math.sin(yaw * Math.PI);
		}
		final float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= Math.abs(yaw % f3);
		pitch -= Math.abs(pitch % (f3 * f2));
		return new float[] { yaw, pitch };
	}

	public static float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
		float f = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);
		if (f > p_70663_3_) {
			f = p_70663_3_;
		}

		if (f < -p_70663_3_) {
			f = -p_70663_3_;
		}

		return p_70663_1_ + f;
	}

	public void AutoBlock() {
		if (((CheckBox) this.getSetting("Autoblock").getSetting()).state) {

			if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
				switch (((DropdownBox) this.getSetting("AutoBlock").getSetting()).curOption) {

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

				}

			}
		}
	}

	boolean isBot(EntityPlayer player) {
		if (!isInTablist(player))
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

	float lostHealthPercentage = 0;
	float lastHealthPercentage = 0;

	public void drawTargetHud() {
		Entity target1 = target;
		ScaledResolution s = new ScaledResolution(mc);
		Color backgroundColor = new Color(0, 0, 0, 120);
		Color emptyBarColor = new Color(59, 59, 59, 160);
		Color healthBarColor = Color.green;
		Color distBarColor = new Color(20, 81, 208);
		int left = (int)x + s.getScaledWidth() / 2 + 5;
		int right2 = (int)x + 80;
		int right = s.getScaledWidth() / 900 + right2 - 5;
		int right3 = (int)x + 80 + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target1.getName()) / 2 - 15;
		int top = (int)y + s.getScaledHeight() / 2 - 25;
		int bottom = (int)y + s.getScaledHeight() / 2 + 25;

		float curTargetHealth = ((EntityPlayer) target1).getHealth();
		float maxTargetHealth = ((EntityPlayer) target1).getMaxHealth();
		float healthProcent = target.getHealth() / target.getMaxHealth();
		if (lastHealthPercentage != healthProcent) {
			lostHealthPercentage += lastHealthPercentage - healthProcent;
		}
		lastHealthPercentage = healthProcent;
		final float finalHealthPercentage = healthProcent + lostHealthPercentage;
		lostHealthPercentage = Math.max(0, lostHealthPercentage - (lostHealthPercentage / 20));
		float calculatedHealth = finalHealthPercentage;
		int rectRight = right + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target1.getName()) / 2 - 5;
		float healthPos = calculatedHealth * right3;
		// Client.blurHelper.blur2(left, top,
		// right +
		// +Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target1.getName())
		// - 5, bottom,
		// (float) 10);
		RenderUtil.drawRoundedRect2(left - 0.5F, top - 0.5F,
				right + (1) + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target1.getName()) - 5, 53, 10,
				Color.cyan);
		RenderUtil.drawRoundedRect2(left, top,
				right + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target1.getName()) - 5, 52, 10,
				new Color(25, 25, 25, 255));

		if (lastHealthPercentage != healthProcent) {
			lostHealthPercentage += lastHealthPercentage - healthProcent;
		}
		lastHealthPercentage = healthProcent;
		lostHealthPercentage = Math.max(0, lostHealthPercentage - (lostHealthPercentage / 20));
		
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		List NetworkMoment = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
		Iterator itarlor = NetworkMoment.iterator();
		while (itarlor.hasNext()) {
			Object nextObject = itarlor.next();
			NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo) nextObject;
			if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
				GlStateManager.enableCull();
				mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());

				Gui.drawScaledCustomSizeModalRect(s.getScaledWidth() / 2 + 10, s.getScaledHeight() / 2 - 22, 8.0F, 8.0F,
						8, 8, 32, 32, 64.0F, 66.0F);
				if (target.hurtTime != 0) {
					GL11.glColor4d(255, 0, 0, 15);
				}
			}
		}
		drawRect(left + 5, bottom - 13,
				left + healthPos + Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(target.getName()) / 2,
				bottom - 5, Color.cyan);
		Client.INSTANCE.unicodeBasicFontRenderer5.drawStringWithShadow(target.getName(), left + 40, top + 4,
				Color.WHITE.getRGB());
		Client.INSTANCE.unicodeBasicFontRenderer5.drawStringWithShadow("Health: " + Math.round(curTargetHealth),
				left + 40, top + 20, Color.WHITE.getRGB());
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

}