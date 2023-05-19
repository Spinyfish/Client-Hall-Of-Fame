package club.marsh.bloom.impl.mods.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.events.StrafeEvent;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.impl.events.Render3DEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.mods.player.MiddleClickFriends;
import club.marsh.bloom.impl.utils.combat.ClickTimer;
import club.marsh.bloom.impl.utils.combat.RotationUtils;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;


public class KillAura extends Module {
	public KillAura() {
		super("Kill Aura",Keyboard.KEY_G,Category.COMBAT);
	}

	float yaw = 0;
	float pitch = 0;
	int ticks = 4;
	private Random random = new Random();
	public static boolean toggled = false;
	RenderManager renderManager = mc.getRenderManager();
	static ModeValue gcdmode = new ModeValue("GCD", "Normal", new String[] {"Normal", "None"});
	ModeValue targetMode = new ModeValue("Target Sorting","Health", new String[] {"Health", "Distance", "Pit", "None"});
	BooleanValue reverseSorting = new BooleanValue("Reverse Sorting", false, () -> true);
	NumberValue aimingReach = new NumberValue("Aiming Reach",4D,0,6D,() -> true);
	NumberValue reach = new NumberValue("Reach",3D,0,6D,() -> true);
	public static BooleanValue autoblock = new BooleanValue("Auto Block",false,() -> true);
	public static ModeValue autoblockmode = new ModeValue("Auto Block Mode","Vanilla",new String[] {"Vanilla", "Watchdog", "Fake"});
	NumberValue aps = new NumberValue("Cps",10,5,20,1,() -> true);
	BooleanValue playerOnly = new BooleanValue("Player Only",false,() -> true);
	BooleanValue silent = new BooleanValue("Silent",false,() -> true);
	BooleanValue rc = new BooleanValue("Real Click",false,() -> true);
	BooleanValue mark = new BooleanValue("Mark",true,() -> true);
	BooleanValue movefix = new BooleanValue("Move Fix", true, () -> silent.isOn());
	BooleanValue silentMoveFix = new BooleanValue("Silent move fix", true, () -> movefix.isOn() && movefix.isVisible());
	static BooleanValue randomization = new BooleanValue("Rotation Randomization",true,() -> true);
	static NumberValue<Double> randomizationAmount = new NumberValue<>("Randomize Amount",5D,0D,15D,1,() -> randomization.isOn());
	ClickTimer clickTimer = new ClickTimer(10D);

	@Subscribe
	public void onStrafe(StrafeEvent e) {
		if (movefix.isOn() && movefix.isVisible()) {

		}
	}

	@Subscribe
	public void onRender3D(Render3DEvent e) {
		try {
			if (target != null && mark.isOn() && e.post) {
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glLineWidth(10.0f);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				AxisAlignedBB bb = new AxisAlignedBB(target.boundingBox.minX - target.posX + target.posX - renderManager.renderPosX, target.boundingBox.minY - target.posY + target.posY - renderManager.renderPosY, target.boundingBox.minZ - target.posZ + target.posZ - renderManager.renderPosZ, target.boundingBox.maxX - target.posX + target.posX - renderManager.renderPosX, target.boundingBox.maxY - target.posY + target.posY - renderManager.renderPosY, target.boundingBox.maxZ - target.posZ + target.posZ - renderManager.renderPosZ);
				Color color = Color.getHSBColor(Math.max(0.0F, Math.min(((EntityLivingBase) target).getHealth(), ((EntityLivingBase) target).getMaxHealth()) / ((EntityLivingBase) target).getMaxHealth()) / 3.0F, 1.0F, 0.75F);
				RenderGlobal.drawOutlinedBoundingBox(bb,color.getRed(),color.getGreen(),color.getBlue(),255);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(true);
				GL11.glDisable(GL11.GL_BLEND);
				GlStateManager.resetColor();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public float[] getFixedGCD(float yaw, float pitch) {
		if (gcdmode.mode == "None") {
			return new float[] {yaw, pitch};
		}

		return RotationUtils.getFixedGCD(yaw,pitch);
	}

	public static EntityLivingBase target;
	private long time = System.currentTimeMillis();

	public EntityLivingBase getClosest(double range) {
		List<EntityLivingBase> entities = new ArrayList<>();

		for (Entity entity : mc.theWorld.loadedEntityList) {
			if (entity instanceof EntityLivingBase && canAttack((EntityLivingBase) entity)) {
				entities.add((EntityLivingBase) entity);
			}
		}

		entities.removeIf(entity -> entity.getHealth() == 0);

		switch (targetMode.getMode()) {
			case "Health": {
				entities.stream().sorted(Comparator.comparingInt(entity -> (int) entity.getHealth()));
				break;
			}

			case "Distance": {
				entities.sort(Comparator.comparingInt(entity -> (int) -mc.thePlayer.getDistanceToEntity(entity)));
				break;
			}

			case "Pit": {
				if (entities.size() > 1) {
					entities.removeIf((entity) -> entity.hurtTime >= 4);
				}

				entities.stream().sorted(Comparator.comparingInt(entity -> (int) entity.getHealth()));
				break;
			}
		}

		if (!reverseSorting.isOn()) {
			Collections.reverse(entities);
		}

		return entities.size() > 0 ? entities.get(0) : null;
	}

	private boolean canAttack(EntityLivingBase entity) {
		if (entity == mc.thePlayer) {
			return false;
		}

		if (playerOnly.isOn() && !(entity instanceof EntityPlayer)) {
			return false;
		}

		if (AntiBot.invalid.contains(entity)) {
			return false;
		}

		if (entity.getHealth() == 0 || mc.thePlayer.isDead) {
			return false;
		}

		if (MiddleClickFriends.on && MiddleClickFriends.friends.contains(entity)) {
			return false;
		}

		if (!mc.thePlayer.canEntityBeSeen(entity)) {
			return false;
		}

		return mc.thePlayer.getDistanceToEntity(entity) <= aimingReach.getValDouble();
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		try {
			clickTimer.setMinCps(aps.getObject().doubleValue());
			EntityLivingBase lasttarget = target;
			target = getClosest(aimingReach.getObject().doubleValue());
			ticks++;

			if (lasttarget != target) {
				ticks = 3;
			}

			if (target != null && canAttack(target)) {
				clickTimer.update();
				if (autoblock.isOn() && mc.thePlayer.inventory.getCurrentItem() != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem()) && mc.playerController.interactWithEntitySendPacket(mc.thePlayer,target));
				float[] rotations = RotationUtils.getRotations(this.target);
				yaw = rotations[0];
				pitch = rotations[1];

				if (this.randomization.isOn()) {
					double random1 = random.nextDouble();
					double random2 = random.nextDouble();

					if (random1 < 0.5F) {
						yaw += random1 * this.randomizationAmount.getValDouble();
					} else {
						yaw -= (random1 - 0.5F) * this.randomizationAmount.getValDouble();
					}

					if (random1 < 0.5F) {
						pitch += random2 * this.randomizationAmount.getValDouble();
					} else {
						pitch -= (random2 - 0.5F) * this.randomizationAmount.getValDouble();
					}
				}

				if (this.silent.isOn()) {
					this.mc.thePlayer.rotationYawHead = yaw;
					this.mc.thePlayer.rotationPitchHead = pitch;
					this.mc.thePlayer.renderYawOffset = yaw;
					e.yaw = yaw;
					e.pitch = pitch;
				} else {
					this.mc.thePlayer.rotationYaw = yaw;
					this.mc.thePlayer.rotationPitch = pitch;
				}

				if (clickTimer.hasEnoughTimeElapsed(true)) {
					time = System.currentTimeMillis();

					if (rc.isOn()) {
						mc.gameSettings.keyBindAttack.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
					} else {
						mc.thePlayer.swingItem();

						if (mc.thePlayer.getDistanceToEntity(target) <= reach.getValDouble()) {
							mc.playerController.attackEntity(mc.thePlayer, target);
						}
					}
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		ticks = 3;
		toggled = false;
		EntityRenderer.reach = 3;
		target = null;
	}

	@Override
	public void onEnable() {
		ticks = 3;
		toggled = true;
		EntityRenderer.reach = reach.getValDouble();
	}
}
