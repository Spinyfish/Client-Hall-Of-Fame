package alos.stella.module.modules.combat;

import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.MotionEvent;
import alos.stella.event.events.Render3DEvent;
import alos.stella.event.events.TickEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.AnimationUtils;
import alos.stella.utils.ColorUtils;
import alos.stella.utils.RotationUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "KillAura", description = "Target", category = ModuleCategory.COMBAT)
public class KillAura extends Module {
	private int ticks = 0;
	public static float yaw;
	public static float pitch;
	public float iyaw;
	public float ipitch;
	private AxisAlignedBB bb;
	private double direction = 1, yPos, progress = 0;
	private long lastMS = System.currentTimeMillis();
	private long lastDeltaMS = 0L;
	private float al = 0;
	public float alpha;
	public static Entity entity;
	public boolean block;
    public boolean targetHUD;
	public final ListValue modeValue = new ListValue("Mode", new String[] { "None","Head", "Custom"}, "Head");

	public static FloatValue reach = new FloatValue("Reach", 3f, 1f, 9f);

	public static BoolValue rotonattack = new BoolValue("Rotonattack", true);

	public static BoolValue keepSprint = new BoolValue("KeepSprint", true);

	public static FloatValue rotyaw = new FloatValue("CustomYaw", 360f, -360f, 360f);
	public static FloatValue rotpitch = new FloatValue("CustomPitch", 90f, -180f, 180f);
	public static BoolValue silentRotation = new BoolValue("SilentRotation", true);
	public static BoolValue semiKeepSprint = new BoolValue("SemiKeepSprint", true);
	public static BoolValue fakeblock = new BoolValue("Fakeblock", true);
	public static BoolValue autoblock = new BoolValue("AutoBlock", true);
	public static IntegerValue cps = new IntegerValue("Ticks", 2, 2, 20);
	public static BoolValue targetMark = new BoolValue("TargetMark", true);
	private final ListValue colorModeValue = new ListValue("Color",
			new String[] { "Custom", "Rainbow", "Astolfo", "Fade", "Health" }, "Custom");
	private final IntegerValue colorRedValue = new IntegerValue("Red", 255, 0, 255);
	private final IntegerValue colorGreenValue = new IntegerValue("Green", 255, 0, 255);
	private final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
	private final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
	private final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
	private final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
	private final FloatValue jelloAlphaValue = new FloatValue("JelloEndAlphaPercent", 0.4F, 0F, 1F, "x",
			() -> modeValue.get().equalsIgnoreCase("jello"));
	private final FloatValue jelloWidthValue = new FloatValue("JelloCircleWidth", 3F, 0.01F, 5F,
			() -> modeValue.get().equalsIgnoreCase("jello"));
	private final FloatValue jelloGradientHeightValue = new FloatValue("JelloGradientHeight", 3F, 1F, 8F, "m",
			() -> modeValue.get().equalsIgnoreCase("jello"));
	private final FloatValue jelloFadeSpeedValue = new FloatValue("JelloFadeSpeed", 0.1F, 0.01F, 0.5F, "x",
			() -> modeValue.get().equalsIgnoreCase("jello"));
	private final IntegerValue colorAlphaValue = new IntegerValue("Alpha", 255, 0, 255);
	private final BoolValue playerValue = new BoolValue("Player", true);
	private final BoolValue mobValue = new BoolValue("Mob", false);
	private final BoolValue animalValue = new BoolValue("Animal", false);
	private final BoolValue invisibleValue = new BoolValue("Invisible", false);

	@Override
	public void onEnable() {
		yaw = iyaw = mc.thePlayer.rotationYaw;
		pitch = ipitch = mc.thePlayer.rotationPitch;
	}

	@Override
	public void onDisable() {
		block = false;
        targetHUD = false;
		yaw = iyaw = mc.thePlayer.rotationYaw;
		pitch = ipitch = mc.thePlayer.rotationPitch;
	}

	@EventTarget
	public void onMotion(MotionEvent event) {
		if (event.getEventState() == EventState.PRE) {
			ticks++;
			entity = closeEntity();
			if (entity != null) {
				if (mc.thePlayer.getDistanceToEntity(entity) < reach.get()) {
					targetHUD = true;
					block = fakeblock.get();
					if (entity.isInvisible()) {
						return;
					}
					if (entity.isEntityAlive()) {
						if (!rotonattack.get()) {
							if(!silentRotation.get()){
								mc.thePlayer.rotationPitch = pitch;
								mc.thePlayer.rotationYaw = yaw;
							}
							switch (modeValue.get()) {
								case "Custom": {
									float frac = MathHelper.clamp_float(1f - 1 / 100f, 0.1f, 1f);
									float[] rotations = RotationUtils.getCustomAngles((EntityLivingBase) entity);

									iyaw = iyaw + (rotations[0] - iyaw) * frac;
									ipitch = ipitch + (rotations[1] - ipitch) * frac;

									event.setYaw(yaw = iyaw);
									event.setPitch(pitch = ipitch);
									mc.thePlayer.rotationYawHead = yaw;
									mc.thePlayer.renderYawOffset = yaw;
									mc.thePlayer.rotationPitchHead = pitch;
									break;
								}
								case "Head": {
									float frac = MathHelper.clamp_float(1f - 1 / 100f, 0.1f, 1f);
									float[] rotations = RotationUtils.getAngles((EntityLivingBase) entity);

									iyaw = iyaw + (rotations[0] - iyaw) * frac;
									ipitch = ipitch + (rotations[1] - ipitch) * frac;

									event.setYaw(yaw = iyaw);
									event.setPitch(pitch = ipitch);
									mc.thePlayer.rotationYawHead = yaw;
									mc.thePlayer.renderYawOffset = yaw;
									mc.thePlayer.rotationPitchHead = pitch;
									break;
								}
								case "None": {
									break;
								}
							}
						}

						if (autoblock.get()) {
							if (mc.thePlayer.getHeldItem() != null
									&& mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
								mc.gameSettings.keyBindUseItem.pressed = true;
							}
						}

						if (ticks >= 20 / cps.get()) {
							if (rotonattack.get()) {
								if(!silentRotation.get()){
									mc.thePlayer.rotationPitch = pitch;
									mc.thePlayer.rotationYaw = yaw;
								}
								switch (modeValue.get()) {
									case "Custom": {
										float frac = MathHelper.clamp_float(1f - 1 / 100f, 0.1f, 1f);
										float[] rotations = RotationUtils.getCustomAngles((EntityLivingBase) entity);

										iyaw = iyaw + (rotations[0] - iyaw) * frac;
										ipitch = ipitch + (rotations[1] - ipitch) * frac;

										event.setYaw(yaw = iyaw);
										event.setPitch(pitch = ipitch);
										mc.thePlayer.rotationYawHead = yaw;
										mc.thePlayer.renderYawOffset = yaw;
										mc.thePlayer.rotationPitchHead = pitch;
										break;
									}
									case "Head": {
										float frac = MathHelper.clamp_float(1f - 1 / 100f, 0.1f, 1f);
										float[] rotations = RotationUtils.getAngles((EntityLivingBase) entity);

										iyaw = iyaw + (rotations[0] - iyaw) * frac;
										ipitch = ipitch + (rotations[1] - ipitch) * frac;

										event.setYaw(yaw = iyaw);
										event.setPitch(pitch = ipitch);
										mc.thePlayer.rotationYawHead = yaw;
										mc.thePlayer.renderYawOffset = yaw;
										mc.thePlayer.rotationPitchHead = pitch;
										break;
									}
									case "None": {
										break;
									}
								}
							}
							if (semiKeepSprint.get()) {
								mc.thePlayer.isSprinting();
								if (mc.thePlayer.isSprinting()) {
									mc.thePlayer.setSprinting(false);
								}
							}
							if (!keepSprint.get()) {
								mc.playerController.attackEntity(mc.thePlayer, entity);
							}
							mc.thePlayer.swingItem();
							if(keepSprint.get()){
								mc.playerController.syncCurrentPlayItem();
								mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
								if (mc.thePlayer.fallDistance > 0 && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null) {
									mc.thePlayer.onCriticalHit(entity);
								}
							}else {
								mc.playerController.attackEntity(mc.thePlayer, entity);
							}
							ticks = 0;
						}
					}
				}
				if (mc.thePlayer.getDistanceToEntity(entity) > reach.get()) {
					block = false;
					targetHUD = false;
				}
			}
		}
	}

	public Entity closeEntity() {
		Entity close = null;
		for (Object o : mc.theWorld.loadedEntityList) {
			Entity e = (Entity) o;
			if (e instanceof EntityOtherPlayerMP && playerValue.get() || e instanceof EntityMob && mobValue.get()
					|| e instanceof EntitySlime && mobValue.get() || animalValue.get() && e instanceof EntityAnimal
					|| invisibleValue.get()
							&& (e instanceof EntityVillager || invisibleValue.get() && e instanceof EntityGolem)) {
				if (close == null || mc.thePlayer.getDistanceToEntity(e) < mc.thePlayer.getDistanceToEntity(close)) {
					close = e;
				}
			}
		}
		return close;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	// TargetMark
	private double easeInOutQuart(double x) {
		return (x < 0.5) ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
	}

	private void drawCircle(double x, double y, double z, float width, double radius, float red, float green,
			float blue, float alp) {
		GL11.glLineWidth(width);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glColor4f(red, green, blue, alp);

		for (int i = 0; i <= 360; i += 1) {
			double posX = x - Math.sin(i * Math.PI / 180) * radius;
			double posZ = z + Math.cos(i * Math.PI / 180) * radius;
			GL11.glVertex3d(posX, y, posZ);
		}

		GL11.glEnd();
	}

	public static void pre3D() {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glDisable(2884);
	}

	public static void post3D() {
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
	}

	@EventTarget
	public void onTick(TickEvent event) {
		entity = closeEntity();
		if (targetMark.get() && mc.thePlayer.getDistanceToEntity(entity) < reach.get())
			al = AnimationUtils.changer(al, (entity != null ? jelloFadeSpeedValue.get() : -jelloFadeSpeedValue.get()),
					0F, colorAlphaValue.get() / 255.0F);
	}

	@EventTarget
	public void onRender3D(Render3DEvent event) {
		entity = closeEntity();
		if (targetMark.get() && mc.thePlayer.getDistanceToEntity(entity) < reach.get()) {
			double lastY = yPos;

			if (al > 0F) {
				if (System.currentTimeMillis() - lastMS >= 1000L) {
					direction = -direction;
					lastMS = System.currentTimeMillis();
				}
				long weird = (direction > 0 ? System.currentTimeMillis() - lastMS
						: 1000L - (System.currentTimeMillis() - lastMS));
				progress = (double) weird / 1000D;
				lastDeltaMS = System.currentTimeMillis() - lastMS;
			} else { // keep the progress
				lastMS = System.currentTimeMillis() - lastDeltaMS;
			}

			if (entity != null) {
				bb = entity.getEntityBoundingBox();
			}

			if (bb == null || entity == null)
				return;

			double radius = bb.maxX - bb.minX;
			double height = bb.maxY - bb.minY;
			double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
			double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
			double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;

			yPos = easeInOutQuart(progress) * height;

			double deltaY = (direction > 0 ? yPos - lastY : lastY - yPos) * -direction * jelloGradientHeightValue.get();

			if (al <= 0 && entity != null) {
				entity = null;
				return;
			}

			Color colour = getColor(entity);
			float r = colour.getRed() / 255.0F;
			float g = colour.getGreen() / 255.0F;
			float b = colour.getBlue() / 255.0F;

			pre3D();
			// post circles
			GL11.glTranslated(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY,
					-mc.getRenderManager().viewerPosZ);

			GL11.glBegin(GL11.GL_QUAD_STRIP);

			for (int i = 0; i <= 360; i++) {
				double calc = i * Math.PI / 180;
				double posX2 = posX - Math.sin(calc) * radius;
				double posZ2 = posZ + Math.cos(calc) * radius;

				GL11.glColor4f(r, g, b, 0F);
				GL11.glVertex3d(posX2, posY + yPos + deltaY, posZ2);

				GL11.glColor4f(r, g, b, al * jelloAlphaValue.get());
				GL11.glVertex3d(posX2, posY + yPos, posZ2);
			}

			GL11.glEnd();

			drawCircle(posX, posY + yPos, posZ, jelloWidthValue.get(), radius, r, g, b, al);

			post3D();
		}
	
	}

	public final Color getColor(final Entity ent) {
		if (ent instanceof EntityLivingBase) {
			final EntityLivingBase entityLivingBase = (EntityLivingBase) ent;

			if (colorModeValue.get().equalsIgnoreCase("Health"))
				return alos.stella.utils.render.ColorUtils.getHealthColor(entityLivingBase.getHealth(), entityLivingBase.getMaxHealth());
		}

		switch (colorModeValue.get()) {
		case "Custom":
			return new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
		case "Rainbow":
			return new Color(alos.stella.utils.render.ColorUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(),
					brightnessValue.get(), 0));
		case "Astolfo":
			return alos.stella.utils.render.ColorUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
		default:
			return ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
		}
	}
}