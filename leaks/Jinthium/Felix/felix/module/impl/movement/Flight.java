package felix.module.impl.movement;

import felix.events.Event;
import felix.events.packet.EventPacketReceive;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventCollide;
import felix.events.player.EventJump;
import felix.events.player.EventMove;
import felix.gui.notification.Notifications;
import felix.value.impl.NumberValue;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import felix.Client;
import felix.api.annotations.Handler;
import felix.api.annotations.Priority;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.module.impl.combat.KillAura;
import felix.module.impl.world.Disabler;
import felix.util.other.PlayerUtil;
import felix.util.other.TimeHelper;
import felix.util.player.MovementUtils;
import felix.value.Value;
import felix.value.impl.BooleanValue;
import felix.value.impl.EnumValue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Flight extends Module {

	private EnumValue<Mode> flightMode = new EnumValue<>("Flight Mode", Mode.Vanilla);

	private NumberValue<Double> flightSpeed = new NumberValue<Double>("Watchdog Boost", 1.5d, 0.5d, 1.5d);

	private NumberValue<Float> timerValue = new NumberValue<Float>("Timer Speed", 1.2f, 1.0f, 3.0f);

	private NumberValue<Float> timerDuration = new NumberValue<Float>("Timer Duration", 1000f, 100f, 2000f);

	private BooleanValue damage = new BooleanValue("Damage", true);

	private BooleanValue timerBoost = new BooleanValue("Timer Boost", false);

	private BooleanValue viewBobbing = new BooleanValue("View Bobbing", false);

	private EventPacketSend packetEvent;
	private TimeHelper longJumpTimerXD = new TimeHelper();

	private double lastDist;
	private double moveSpeed;
	private double moveSpeedMotion = 1F;
	private int stage = 0, counter;

	private TimeHelper timerStopwatch = new TimeHelper();

	private TargetStrafe ts;

	private float glideAmount2 = 4f;

	public Flight() {
		super("Flight", Keyboard.KEY_F, ModuleCategory.MOVEMENT);
		addValues(flightMode, flightSpeed, timerValue, timerDuration, timerBoost, viewBobbing, damage);
	}

	private enum Mode {
		Vanilla, Watchdog, Verus, Motion, Test, Gay;
	}

	public void onDisable() {
		super.onDisable();
		switch (flightMode.getValue()) {
			case Vanilla:
				mc.thePlayer.capabilities.isFlying = false;
				break;
			case Watchdog:
				final EntityPlayerSP player = mc.thePlayer;
				player.motionX = 0.0;
				player.motionZ = 0.0;
				break;
			case Motion:
				MovementUtils.setMotion(0.0);
				mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, mc.thePlayer.onGround));
		    	mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, mc.thePlayer.onGround));
				break;
			case Verus:
				break;
		case Test:
			break;
		default:
			break;
		}
		if (ts == null) {
			ts = (TargetStrafe) Client.INSTANCE.getModuleManager().getModule(TargetStrafe.class);
		}

		mc.timer.timerSpeed = 1.0f;
		timerStopwatch.reset();
	}

	public void onEnable() {
		super.onEnable();
		if (mc.thePlayer == null) return;
		timerStopwatch.reset();
		longJumpTimerXD.reset();
		stage = 0;
		lastDist = 0.0;
		mc.timer.timerSpeed = 1.0f;
		moveSpeed = MovementUtils.getSpeed();
		switch (flightMode.getValue()) {
			case Watchdog:
//				final Disabler disabler = (Disabler) Client.INSTANCE.getModuleManager().getModule(Disabler.class);
//				if (!disabler.isEnabled()) {
//					toggle();
//					Notifications.getManager().post("Warning", "Relog and enable disabler.");
//				}
				counter = 0;
				break;
			case Motion:

				break;
			case Vanilla:
				break;
			case Verus:
				break;
			case Test:

				break;
		}
	}

	public void damage2() {
	      double offset = 0.060100000351667404D;
	      NetHandlerPlayClient netHandler = mc.getNetHandler();
	      EntityPlayerSP player = mc.thePlayer;
	      double x = player.posX;
	      double y = player.posY;
	      double z = player.posZ;

	      for(int i = 0; (double)i < (double)getMaxFallDist() / 0.05510000046342612D + 1.0D; ++i) {
	         netHandler.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404D, z, false));
	         netHandler.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4D, z, false));
	         netHandler.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.004999999888241291D + 6.01000003516674E-8D, z, false));
	      }

	      netHandler.addToSendQueueNoEvent(new C03PacketPlayer(true));
	}

	@Handler
	public void onMove(final EventMove event) {
		final EntityPlayerSP player = mc.thePlayer;
		final NetHandlerPlayClient netHandler = mc.getNetHandler();
		final GameSettings gameSettings = mc.gameSettings;
		switch (flightMode.getValue()) {
			case Watchdog:
				if (this.mc.thePlayer.isMovingOnGround()) {
					//event.setY(this.mc.thePlayer.motionY = MovementUtil.getJumpBoostModifier(0.20000000298023224));
				}
				else {
					BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1.0D, mc.thePlayer.posZ);
					Vec3d vec = new Vec3d(blockPos).addVector(0.4D, 0.4D, 0.4D).mul(0.4F);
					mc.playerController.onPlayerRightClick3d(mc.thePlayer, mc.theWorld, new ItemStack(Blocks.barrier), blockPos, EnumFacing.UP, vec);
				}
				MovementUtils.setSpeed(event, 0.08);
				break;
			case Motion:
				event.setY(player.motionY = -0.0625);
				if (gameSettings.keyBindJump.isKeyDown()) {
					event.setY(player.motionY += 1.0);
				}
				else if (gameSettings.keyBindSneak.isKeyDown()) {
					event.setY(player.motionY -= 1.0);
				}
				if (!mc.thePlayer.isMoving()) {
					MovementUtils.setMotion(0);
				}

				//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + glideValue2, mc.thePlayer.posZ);
				MovementUtils.setSpeed(event, 2);
				//mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
				//netHandler.addToSendQueueNoEvent(new C0CPacketInput(player.moveStrafing, player.moveForward, player.movementInput.jump, player.movementInput.sneak));
				break;
			case Vanilla:
				break;
			case Verus:
				break;
			case Gay:
				break;
			case Test:
				MovementUtils.setMotion(2f);
                float newSpeed = 2 * 0.725f;
                event.setY(mc.thePlayer.movementInput.jump ? newSpeed : mc.thePlayer.movementInput.sneak ? -newSpeed : mc.thePlayer.onGround ? 0.645443F : 0);
                mc.thePlayer.motionY = 0;
            	EntityPig entityHorse = new EntityPig(mc.theWorld);
            	mc.thePlayer.setPosition(entityHorse.posX, entityHorse.posY, entityHorse.posZ);
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(entityHorse, Action.RIDING_JUMP));
				break;
		}
	}

	@Handler
	public void a(EventCollide event){
		if(flightMode.getValue() == Mode.Watchdog){
			if (event.getBlock() instanceof BlockAir && event.getY() < mc.thePlayer.posY) {
				event.setBoundingBox(new AxisAlignedBB((double) event.getX(), (double) event.getY(), (double) event.getZ(), event.getX() + 1.0, mc.thePlayer.posY, event.getZ() + 1.0));
			}
		}
	}
	
    private boolean nigger() {
    	if (longJumpTimerXD.isDelayComplete(300)) {
    		
    		return true;
    	}
    	
		return false;
	}

	public void damage() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        
        for (int i = 0; i < 10; i++) {
        	mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.42, z, false));
        	mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + MovementUtils.getJumpBoostModifier(0.42 % .0000625), z, false));
        	mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(false));
        }
    }

    public float getMaxFallDist() {
        PotionEffect potioneffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        int f = potioneffect != null ? potioneffect.getAmplifier() + 1 : 0;
        return (float) (mc.thePlayer.getMaxFallHeight() + f);
    }
    
    public float getMinFallDist() {
        float minDist = 3.0F;
        if (mc.thePlayer.isPotionActive(Potion.jump))
            minDist += mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0F;
        return minDist;
    }
    
    @Handler
    public void onJump(final EventJump event) {
    	final KillAura killaura = (KillAura) Client.INSTANCE.getModuleManager().getModule(KillAura.class);
    	event.setCancelled(killaura.target == null);
    	
    }
    
    @Handler(Priority.LOW)
    public void onReceivePacket(final EventPacketReceive event) {
    	if (event.getPacket() instanceof S08PacketPlayerPosLook) {
        	switch (flightMode.getValue()) {
        	case Watchdog:
        		//toggle();
        		break;
			case Motion:
				break;
			case Vanilla:
				break;
			case Verus:
				break;
			case Test:
			default:
				break;
        	}
    	}
    }
    
    @Handler
    public void onPacketSend(final EventPacketSend event) {

    	switch (flightMode.getValue()) {
	    	case Gay: {
	    		break;
	    	}
	    	case Motion: {
	    		break;
	    	}
    	}
    }

    @Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		setSuffix(flightMode.getValueAsString());
		final EntityPlayerSP player = mc.thePlayer;
		final Timer timer = mc.timer;
		if (event.isPre()) {
			final double posX = player.posX - player.prevPosX;
			final double posZ = mc.thePlayer.posZ - player.prevPosZ;
			lastDist = Math.sqrt(posX * posX + posZ * posZ);
            counter++;
            if (viewBobbing.isEnabled()) {
            	player.cameraYaw = 0.08f;
            }
			switch (flightMode.getValue()) {
				case Watchdog:
//					if (timerBoost.isEnabled()) {
//						if (!timerStopwatch.isDelayComplete(timerDuration.getValue()) && stage > 0) {
//							timer.timerSpeed = timerValue.getValue();
//						} else {
//							timer.timerSpeed = 1.0f;
//						}
//					}
//					float glideAmount = 0.0009f;
//					float glideValue = mc.thePlayer.ticksExisted % 2 == 0 ? glideAmount : -glideAmount;
//					//mc.thePlayer.motionY = 0.42f;
//					mc.thePlayer.motionY = 0.0;
//					//MovementUtils.setMotion(MovementUtils.getSpeed());
//					//player.setPosition(player.posX, player.posY + glideValue, player.posZ);
//					switch (counter) {
//					case 1:
//						PlayerUtil.tpRel(0, 3.0E-13D, 0);
//						break;
//					case 2:
//						PlayerUtil.tpRel(0, 3.0E-124D, 0);
//						counter = 0;
//						break;
//					}
					break;
				case Vanilla:
					player.capabilities.isFlying = true;
					break;
				case Verus:
					event.setOnGround(true);
					player.motionY = 0;
					player.onGround = true;
					break;
				case Motion:
				case Test:
					
					break;
				case Gay:
					double nigger = ThreadLocalRandom.current().nextDouble(1.6, 2.0);
					mc.thePlayer.motionY = (mc.thePlayer.movementInput.jump ? nigger : mc.thePlayer.movementInput.sneak ? -nigger : 0);
	                MovementUtils.setMotion(nigger);
					break;
				default:
					break;
			}
		}
	}
}
