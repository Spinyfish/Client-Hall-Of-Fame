package haptic.module.impl.combat;

import org.lwjgl.input.Keyboard;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventJump;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventPostMotionUpdate;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventStrafing;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.Module;
import haptic.processor.impl.CombatProcessor;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.misc.TimerUtil;
import haptic.util.network.PacketUtils;
import haptic.util.rotations.RotationsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Killaura extends Module {
	
	public Entity target;
	public BlockPos targetPos;
	
	private ModeSetting rotations = new ModeSetting("Rotations", "Normal", "Normal", "Randomised", "Down", "None");
	private ModeSetting renderedRotations = new ModeSetting("Rendered Rots", "Normal", "Normal", "Smooth");
	private NumberSetting aps = new NumberSetting("APS", 10, 1, 20, 1, this);
	private NumberSetting range = new NumberSetting("Range", 4, 3, 6, 0.1, this);
	private NumberSetting startingRange = new NumberSetting("Starting Range", 4, 3, 6, 0.1, this);
	private ModeSetting attackTiming = new ModeSetting("Attack Timing", "Pre", "Pre", "Render");
	private ModeSetting autoblock = new ModeSetting("Autoblock", "Fake", "Vanilla", "NCP", "Fake", "None");
	private BooleanSetting keepSprint = new BooleanSetting("Keep Sprint", true, this);
	public BooleanSetting moveFix = new BooleanSetting("Move Fix", false, this);
	public BooleanSetting backTrack = new BooleanSetting("BackTrack", false, this);
	
	public float yaw, pitch, smoothYaw, smoothPitch;
	
	private int autoblockTicks;
	
	private TimerUtil timer = new TimerUtil();
	
	private boolean blocking;
	
	public Killaura() {
		super("Killaura", Category.COMBAT, Keyboard.KEY_R);
		this.addSettings(rotations, renderedRotations, aps, range, startingRange, attackTiming, autoblock, keepSprint, moveFix, backTrack);
	}
	
	public void onEnable() {
		smoothYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
		smoothPitch = mc.thePlayer.rotationPitch;
		
		autoblockTicks = 0;
	}
	
	public void onDisable() {
		target = null;
		CombatProcessor.setBlocking(false);
		releaseBlocking();
	}
	
	public void onEvent(Event event) {
		
		//Kinda messy killaura lol
		
		if(mc.thePlayer.ticksExisted < 2) {
			this.setEnabled(false);
			CombatProcessor.setBlocking(false);
			return;
		}
		
		if(Haptic.getModuleManager().getModuleByName("Scaffold").isEnabled()) {
			if(target != null) {
				releaseBlocking();
				target = null;
			}
			return;
		}
		
		long randomDelay = (long) (Math.random() * 100 - 20);
		
		boolean attackTick = false;
		
		if(event instanceof EventRender && attackTiming.is("Render")) {
			attackTick = target != null && timer.hasReached((long) (1000 / aps.getValue())) && mc.thePlayer.ticksExisted % 5 != 0 && mc.thePlayer.ticksExisted % 17 != 0 && timer.getTimeElapsed() > 20;
			
			if(mc.thePlayer.ticksExisted % 2 == 0) {
				attackTick = false;
			}
		} else if(event instanceof EventMotionUpdate && attackTiming.is("Pre")) {
			attackTick = target != null && timer.hasReached((long) (1000 / aps.getValue() + Math.random() * 15 - 7)) && mc.thePlayer.ticksExisted % 5 != 0 && mc.thePlayer.ticksExisted % 17 != 0;
		}
		
		if(target != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
			autoblock(event, attackTick);
		} else {
			blocking = false;
		}
		
		if(event instanceof EventStrafing) {
			EventStrafing e = (EventStrafing) event;
			
			if(getClosest() == null && target != null) {
				releaseBlocking();
			}
			
			target = getClosest();
			
			if(target == null) {
				smoothYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
				smoothPitch = mc.thePlayer.rotationPitch;
			}
			
			updateRotations();
			
			if(target != null && moveFix.isEnabled()) {
				e.setYaw(yaw);
			}
		} else if(event instanceof EventJump) {
			EventJump e = (EventJump) event;
			
			updateRotations();
			
			if(target != null && moveFix.isEnabled()) {
				e.setYaw(yaw);
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			CombatProcessor.setBlocking(target != null && !autoblock.is("None") && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword);
			
			if(target != null) {
				rotate(e);
				
				if(mc.thePlayer.ticksExisted % ((int) 20 / aps.getValue()) == 0) {
					mc.thePlayer.swingItemClientSide();
				}
			}
		}
		
		if(attackTick) {
			if(keepSprint.isEnabled()) {
				PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
				mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
			} else {
				PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
				mc.playerController.attackEntity(mc.thePlayer, target);
			}
			timer.reset();
		}
	}
	
	private void autoblock(Event e, boolean attackTick) {
		switch(autoblock.getMode()) {
		case "Vanilla":
			if(e instanceof EventUpdate && !blocking) {
				PacketUtils.sendBlocking(false, false);
				blocking = true;
			}
			break;
		case "NCP":
			if(attackTick) {
				PacketUtils.releaseUseItem(false);
				blocking = false;
			} else if(e instanceof EventPostMotionUpdate) {
				if(!blocking) {
					PacketUtils.sendBlocking(false, false);
					blocking = true;
					autoblockTicks = 0;
				}
				autoblockTicks++;
			}
			break;
		}
	}
	
	private void releaseBlocking() {
		CombatProcessor.setBlocking(blocking = false);
		switch(autoblock.getMode()) {
		case "Vanilla":
			PacketUtils.releaseUseItem(5); // So it doesn't flag verus
			break;
		case "NCP":
			PacketUtils.releaseUseItem(false);
			break;
		}
	}
	
	private double getDistance() {
		if(target != null) {
			return mc.thePlayer.getDistanceToEntity(target);
		} else {
			return Float.NaN;
		}
	}
	
	private void updateRotations() {
		if(target != null) {
			if(mc.thePlayer.getDistanceToEntity(target) < startingRange.getValue() || !backTrack.isEnabled()) {
				targetPos = target.getPosition();
			}
			
			switch(rotations.getMode()) {
			case "Normal":
				normalRotations();
				break;
			case "Randomised":
				randomisedRotations();
				break;
			case "Redesky":
				RedeskyRotations();
				break;
			case "Verus":
				VerusRotations();
				break;
			case "Down":
				DownRotations();
				break;
			}
			
			this.yaw = Math.round(this.yaw * 1000) / 1000;
			this.pitch = Math.round(this.pitch * 1000) / 1000;
			
			if(rotations.is("None")) {
				this.yaw = mc.thePlayer.rotationYaw;
				this.pitch = mc.thePlayer.rotationPitch;
			}
		}
	}
	
	private void normalRotations() {
		float yaw = (float) (getRotations()[0] + Math.random() - 0.5);
		float pitch = (float) (getRotations()[1] + Math.random() - 0.5);
		
		if(pitch > 90.0) {
			pitch = 90;
		} else if(pitch < -90.0) {
			pitch = -90;
		}
		
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	private void randomisedRotations() {
		float yaw = (float) (getRotations()[0] + Math.random() - 0.5);
		float pitch = (float) (getRotations()[1] + Math.random() - 0.5);
		
		if(pitch > 90.0) {
			pitch = 90;
		} else if(pitch < -90.0) {
			pitch = -90;
		}
		
		if(getDistance() > 0.8) {
			if((Math.abs(this.yaw - yaw) > 10 && Math.abs(this.yaw - yaw) < 350) || (Math.abs(this.pitch - pitch) > 15 && Math.abs(this.pitch - pitch) < 345)) {
				this.yaw = yaw;
				this.pitch = pitch;
			}
		} else {
			if(getDistance() > 0.5 && Math.abs(this.yaw - yaw) > 60 && Math.abs(this.yaw - yaw) < 300) {
				this.yaw = yaw;
			}
			this.pitch = 90;
		}
	}
	
	private void RedeskyRotations() {
		if(getDistance() < 1.3) {
			this.yaw = mc.thePlayer.rotationYaw;
			this.pitch = 90;
		} else {
			normalRotations();
			//this.yaw = mc.thePlayer.rotationYaw;
			//this.yaw = (float) (Math.random() * 360) - 180;
			//this.pitch = (float) (Math.random() * 360) - 180;
		}
	}
	
	private void VerusRotations() {
		
	}
	
	private void DownRotations() {
		float yaw = (float) (getRotations()[0] + Math.random() - 0.5);
		float pitch = 90;

		if(Math.abs(this.yaw - yaw) > 10 && Math.abs(this.yaw - yaw) < 350) {
			this.yaw = yaw;
		}
		this.pitch = pitch;
	}
	
	private void rotate(EventMotionUpdate e) {
		e.setYaw(yaw);
		e.setPitch(pitch);
		
		if(renderedRotations.is("Normal")) {
			mc.thePlayer.rotationYawHead = yaw;
			mc.thePlayer.renderYawOffset = yaw;
			mc.thePlayer.rotationPitchHead = pitch;
		} else if(renderedRotations.is("Smooth")) {
			float yaw = getRotations()[0];
			float pitch = getRotations()[1];
			
			double yawTurnSpeed = 30;
			double mult = 0.4;
			
			if(Math.abs(yaw - smoothYaw) < 60) {
				if(smoothYaw > yaw) {
					smoothYaw -= (smoothYaw - yaw) * mult;
				} else {
					smoothYaw += (yaw - smoothYaw) * mult;
				}
			} else if(Math.abs(yaw - smoothYaw) > 360 - 60) {
				if(smoothYaw > yaw) {
					smoothYaw += (360 - (smoothYaw - yaw)) * mult;
				} else {
					smoothYaw -= (360 - (yaw - smoothYaw)) * mult;
				}
			} else {
				if(Math.abs(yaw - smoothYaw) < 180) {
					if(smoothYaw > yaw) {
						smoothYaw -= yawTurnSpeed;
					} else {
						smoothYaw += yawTurnSpeed;
					}
				} else {
					if(smoothYaw > yaw) {
						smoothYaw += yawTurnSpeed;
					} else {
						smoothYaw -= yawTurnSpeed;
					}
				}
			}
			
			double pitchTurnSpeed = 15;
			
			if(Math.abs(pitch - smoothPitch) < 30) {
				if(smoothPitch > pitch) {
					smoothPitch -= (smoothPitch - pitch) * mult;
				} else {
					smoothPitch += (pitch - smoothPitch) * mult;
				}
			} else {
				if(Math.abs(pitch - smoothPitch) < 180) {
					if(smoothPitch > pitch) {
						smoothPitch -= pitchTurnSpeed;
					} else {
						smoothPitch += pitchTurnSpeed;
					}
				} else {
					if(smoothPitch > pitch) {
						smoothPitch += pitchTurnSpeed;
					} else {
						smoothPitch -= pitchTurnSpeed;
					}
				}
			}
			
			mc.thePlayer.rotationYawHead = smoothYaw;
			mc.thePlayer.renderYawOffset = smoothYaw;
			mc.thePlayer.rotationPitchHead = smoothPitch;
		}
	}
	
	private Entity getClosest() {
		Entity finalTarget = null;
		
		for(Entity e : mc.theWorld.loadedEntityList) {
			if(canAttack(e)) {
				if(finalTarget == null) {
					finalTarget = e;
				} else {
					if(mc.thePlayer.getDistanceToEntity(e) < mc.thePlayer.getDistanceToEntity(finalTarget)) {
						finalTarget = e;
					}
				}
			}
		}
		return finalTarget;
	}
	
	public float[] getRotations() {
		double deltaX = target.posX + (target.posX - target.lastTickPosX) - mc.thePlayer.posX,
				deltaY = target.posY - 3.5 + target.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
				deltaZ = target.posZ + (target.posZ - target.lastTickPosZ) - mc.thePlayer.posZ,
				distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

		float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
				pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

		if(deltaX < 0 && deltaZ < 0) {
			yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		}else if(deltaX > 0 && deltaZ < 0) {
			yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		}
		return new float[] {yaw, pitch};
	}
	
	private boolean canAttack(Entity e) {
		if(e != mc.thePlayer) {
			if(e instanceof EntityLivingBase) {
				Antibot antibot = (Antibot) Haptic.getModuleManager().getModuleByName("Antibot");
				if(antibot.isEnabled() && !antibot.canAttack((EntityLivingBase) e)) {
					return false;
				}
				
				if(this.target != null ? mc.thePlayer.getDistanceToEntity(e) <= range.getValue() : mc.thePlayer.getDistanceToEntity(e) <= startingRange.getValue()) {
					if(e instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) e;
						if(player.isInvisible() || player.isInvisibleToPlayer(mc.thePlayer)) {
							return false;
						}
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

}
