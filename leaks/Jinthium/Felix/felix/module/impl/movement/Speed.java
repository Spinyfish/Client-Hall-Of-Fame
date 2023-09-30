package felix.module.impl.movement;

import java.util.List;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventMove;
import felix.events.Event;
import felix.events.player.EventMotionUpdate;
import felix.events.player.EventStep;
import felix.gui.notification.Notifications;
import felix.module.Module;
import felix.util.other.MathUtils;
import felix.util.other.PlayerUtil;
import felix.util.player.MovementUtils;
import felix.value.impl.BooleanValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.client.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.*;
import net.minecraft.entity.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.client.entity.*;
import org.apache.commons.lang3.RandomUtils;

public class Speed extends Module {

	private double nextMotionSpeed;
	private double xMotionSpeed;
	private double zDist;
	private double moveSpeed;
	int stage;
	int ncpStage;
	public boolean reset, doSlow;

	private NumberValue<Integer> vanillaSpeed = new NumberValue<>("Vanilla Speed", 4, 1, 10, 1);

	private BooleanValue flagbackcheck = new BooleanValue("Flagback Check", true);

	private EnumValue<Mode> mode = new EnumValue<>("Speed Mode", Mode.WatchdogHop);

	private TargetStrafe ts;

	public Speed() {
		super("Speed", 0, ModuleCategory.MOVEMENT);
		addValues(mode, vanillaSpeed, flagbackcheck);
		stage = 0;
	}

	private enum Mode {
		WatchdogHop, WatchdogLowhop, NCP, Vanilla, Test, Mineplex;
	}

	@Handler
	public void onMove(final EventMove event) {
		if (PlayerUtil.isOnLiquid() && Client.INSTANCE.getModuleManager().getModule("liquidwalk").isEnabled())
			return;
		switch (mode.getValue()) {
			case Mineplex: {
                mc.timer.timerSpeed = 1.0f;
                if (mc.thePlayer.isMovingOnGround()) {
                    //mc.timer.timerSpeed = 2.48F;
                    if (reset)
                        moveSpeed = 0.95F;
                    else
                        moveSpeed += MathUtils.getRandomInRange(0.42, 0.6);
                    doSlow = true;
                    reset = false;
                    event.setY(mc.thePlayer.motionY = 0.42F);
                    MovementUtils.setSpeed(event, 0.00001);
                } else {

                    if (doSlow)
						nextMotionSpeed = moveSpeed;
                    doSlow = false;
                    if (moveSpeed <= 0.8F)
                        moveSpeed = nextMotionSpeed - 0.01F;
                    else if (moveSpeed < 2.2F)
                        moveSpeed = nextMotionSpeed * 0.9823F;
                    else
                        moveSpeed = nextMotionSpeed * 0.97F;
                    moveSpeed = Math.min(moveSpeed + 0.053123F, moveSpeed);
                    moveSpeed = Math.max(moveSpeed, MovementUtils.getSpeed() + 0.1628F);
                    MovementUtils.setSpeed(event, moveSpeed);
                }
				break;
			}
		case WatchdogHop: {
			moveSpeed = MovementUtils.getSpeed();
			if (stage < 1) {
				++stage;
				nextMotionSpeed = 0.0;
			}
			if (stage == 2 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)
					&& mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
				xMotionSpeed = 0.42F;
				if (mc.thePlayer.isPotionActive(Potion.jump)) {
					xMotionSpeed += (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
				}
				event.setY(mc.thePlayer.motionY = xMotionSpeed);
				moveSpeed *= 1.64;
			} else if (stage == 3) {
				xMotionSpeed = 0.72
						* (nextMotionSpeed - MovementUtils.getSpeed());
				moveSpeed = nextMotionSpeed - xMotionSpeed;
			} else {
				if ((mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
						mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0
						|| mc.thePlayer.isCollidedVertically) && stage > 0) {
					stage = ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) ? 1 : 0);
				}
				moveSpeed = nextMotionSpeed - nextMotionSpeed / 99.0;
			}
			moveSpeed = Math.max(moveSpeed, MovementUtils.getSpeed());
			xMotionSpeed = mc.thePlayer.movementInput.moveForward;
			zDist = mc.thePlayer.movementInput.moveStrafe;
			float rotationYaw = mc.thePlayer.rotationYaw;
			if (xMotionSpeed == 0.0 && zDist == 0.0) {
				// mc.thePlayer.setPosition(mc.thePlayer.posX + 1.0, mc.thePlayer.posY,
				// mc.thePlayer.posZ + 1.0);
				// mc.thePlayer.setPosition(mc.thePlayer.prevPosX, mc.thePlayer.posY,
				// mc.thePlayer.prevPosZ);
				event.setX(0.0);
				event.setZ(0.0);
			} else if (xMotionSpeed != 0.0) {
				if (zDist >= 1.0) {
					rotationYaw += ((xMotionSpeed > 0.0) ? -45.0f : 45.0f);
					zDist = 0.0;
				} else if (zDist <= -1.0) {
					rotationYaw += ((xMotionSpeed > 0.0) ? 45.0f : -45.0f);
					zDist = 0.0;
				}
				if (xMotionSpeed > 0.0) {
					xMotionSpeed = 1.0;
				} else if (xMotionSpeed < 0.0) {
					xMotionSpeed = -1.0;
				}
			}
			final double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
			final double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
			final double x = (xMotionSpeed * moveSpeed * cos + zDist * moveSpeed * sin) * 0.987;
			final double z = (xMotionSpeed * moveSpeed * sin - zDist * moveSpeed * cos) * 0.987;
			if (Math.abs(x) < 1.0 && Math.abs(z) < 1.0) {
				event.setX(x);
				event.setZ(z);
			}
			mc.thePlayer.stepHeight = 0.6f;
			if (xMotionSpeed == 0.0 && zDist == 0.0) {
				event.setX(0.0);
				event.setZ(0.0);
				// mc.thePlayer.setPosition(mc.thePlayer.posX + 1.0, mc.thePlayer.posY,
				// mc.thePlayer.posZ + 1.0);
				// mc.thePlayer.setPosition(mc.thePlayer.prevPosX, mc.thePlayer.posY,
				// mc.thePlayer.prevPosZ);
			} else if (xMotionSpeed != 0.0) {
				if (zDist >= 1.0) {
					final float n = rotationYaw + ((xMotionSpeed > 0.0) ? -45.0f : 45.0f);
					zDist = 0.0;
				} else if (zDist <= -1.0) {
					final float n2 = rotationYaw + ((xMotionSpeed > 0.0) ? 45.0f : -45.0f);
					zDist = 0.0;
				}
				if (xMotionSpeed > 0.0) {
					xMotionSpeed = 1.0;
				} else if (xMotionSpeed < 0.0) {
					xMotionSpeed = -1.0;
				}
			}
			++stage;


			if (mc.thePlayer.isMoving()) {
				MovementUtils.setSpeed(event, moveSpeed);
			}
			break;
		}
		case WatchdogLowhop: {
			if (MathUtils.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtils.roundToPlace(0.4,
					3)) {
				event.setY(mc.thePlayer.motionY = 0.31);
			} else if (MathUtils.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtils
					.roundToPlace(0.71, 3)) {
				event.setY(mc.thePlayer.motionY = 0.04);
			} else if (MathUtils.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtils
					.roundToPlace(0.75, 3)) {
				event.setY(mc.thePlayer.motionY = -0.2);
			} else if (MathUtils.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtils
					.roundToPlace(0.55, 3)) {
				event.setY(mc.thePlayer.motionY = -0.14);
			} else if (MathUtils.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtils
					.roundToPlace(0.41, 3)) {
				event.setY(mc.thePlayer.motionY = -0.2);
			}
			if (stage == 1 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
			} else if (stage == 2 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
				event.setY(mc.thePlayer.motionY = 0.4F);
				moveSpeed *= 1.45;
			} else if (stage == 3) {
				final double difference = 0.72 * (nextMotionSpeed - MovementUtils.getSpeed());
				moveSpeed = nextMotionSpeed - difference;
			} else {
				final List collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
						mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0));
				if ((collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
					stage = ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) ? 1 : 0);
				}
				moveSpeed = nextMotionSpeed - nextMotionSpeed / 99.0;
			}
			MovementUtils.setSpeed(event, moveSpeed = Math.max(moveSpeed, MovementUtils.getSpeed()));
			if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
				++stage;
			}
			break;
		}
		case Vanilla: {
			MovementUtils.setSpeed(event, vanillaSpeed.getValue());
			break;
		}
		case NCP: {
			if (!PlayerUtil.isInLiquid() && mc.thePlayer.isCollidedVertically && MovementUtils.isOnGround(0.01)
					&& (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
				ncpStage= 0;
				mc.thePlayer.jump();
				event.setY(mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.39999));
				if (ncpStage < 4)
					ncpStage++;
			}
			moveSpeed = getAACSpeed(ncpStage, stage) + 0.009;
			if ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
				if (PlayerUtil.isInLiquid()) {
					moveSpeed = 0.2;
				}
				MovementUtils.setSpeed(event, moveSpeed);
			}

			if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
				++ncpStage;
			}
			break;
		}
		case Test: {
			if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
				if (mc.thePlayer.ticksExisted % 14 != 5) {
					mc.timer.timerSpeed = 5f;
				} else {
					mc.timer.timerSpeed = 0.35f;
				}
			} else {
				mc.timer.timerSpeed = 1.0f;
			}
		}
			break;
		}
	}

	private double getAACSpeed(int stage, int jumps) {
        double value = 0.29;
        double firstvalue = 0.3019;
        double thirdvalue = 0.0286 - (double) stage / 1000;
        if (stage == 0) {
            //JUMP
            value = 0.497;
            if (jumps >= 2) {
                value += 0.1069;
            }
            if (jumps >= 3) {
                value += 0.046;
            }
        } else if (stage == 1) {
            value = 0.3031;
            if (jumps >= 2) {
                value += 0.0642;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 2) {
            value = 0.302;
            if (jumps >= 2) {
                value += 0.0629;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 3) {
            value = firstvalue;
            if (jumps >= 2) {
                value += 0.0607;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 4) {
            value = firstvalue;
            if (jumps >= 2) {
                value += 0.0584;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 5) {
            value = firstvalue;
            if (jumps >= 2) {
                value += 0.0561;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 6) {
            value = firstvalue;
            if (jumps >= 2) {
                value += 0.0539;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 7) {
            value = firstvalue;
            if (jumps >= 2) {
                value += 0.0517;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 8) {
            value = firstvalue;
            if (MovementUtils.isOnGround(0.05))
                value -= 0.002;

            if (jumps >= 2) {
                value += 0.0496;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 9) {
            value = firstvalue;
            if (jumps >= 2) {
                value += 0.0475;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 10) {

            value = firstvalue;
            if (jumps >= 2) {
                value += 0.0455;
            }
            if (jumps >= 3) {
                value += thirdvalue;
            }
        } else if (stage == 11) {

            value = 0.3;
            if (jumps >= 2) {
                value += 0.045;
            }
            if (jumps >= 3) {
                value += 0.018;
            }

        } else if (stage == 12) {
            value = 0.301;
            if (jumps <= 2)
                stage = 0;
            if (jumps >= 2) {
                value += 0.042;
            }
            if (jumps >= 3) {
                value += thirdvalue + 0.001;
            }
        } else if (stage == 13) {
            value = 0.298;
            if (jumps >= 2) {
                value += 0.042;
            }
            if (jumps >= 3) {
                value += thirdvalue + 0.001;
            }
        } else if (stage == 14) {

            value = 0.297;
            if (jumps >= 2) {
                value += 0.042;
            }
            if (jumps >= 3) {
                value += thirdvalue + 0.001;
            }
        }
        if (mc.thePlayer.moveForward <= 0) {
            value -= 0.06;
        }

        if (mc.thePlayer.isCollidedHorizontally) {
            value -= 0.1;
            stage = 0;
        }
        return value;
    }
	
	@Handler
	public void onReceivePacket(final EventPacketReceive event) {
		if (event.getPacket() instanceof S08PacketPlayerPosLook && flagbackcheck.isEnabled()) {
			toggle();
			Notifications.getManager().post("Disabled Modules", "Speed was disabled to prevent flags/errors.");
		}
	}

	@Override
	public void onEnable() {
		super.onEnable();
		if (mc.thePlayer == null)
			return;
		if (mc.thePlayer != null) {
			moveSpeed = MovementUtils.getSpeed();
		}
		nextMotionSpeed = 0.0;
		doSlow = false;
		reset = false;
		stage = 2;
		mc.timer.timerSpeed = 1.0f;
		if (ts == null) {
			ts = (TargetStrafe) Client.INSTANCE.getModuleManager().getModule("targetstrafe");
		}
		ncpStage = 0;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		doSlow = false;
		reset = false;
	}

	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		setSuffix(mode.getValueAsString());
		switch (mode.getValue()) {
		case WatchdogHop:
			if (event.getType() == EventMotionUpdate.Type.PRE) {
					if (MovementUtils.isOnGround(0.001) && mc.thePlayer.isMovingOnGround()) {
						event.setPosY(event.getPosY() +  RandomUtils.nextFloat(0.0004f + MathUtils.randomFloatValue(), 0.00049f + MathUtils.randomFloatValue()));
					}
			}

			if (event.getType() == EventMotionUpdate.Type.PRE) {
				xMotionSpeed = mc.thePlayer.posX - mc.thePlayer.prevPosX;
				zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
				nextMotionSpeed = Math.sqrt(xMotionSpeed * xMotionSpeed + zDist * zDist);
			}
			break;
		case WatchdogLowhop:
			if (event.getType() == EventMotionUpdate.Type.PRE) {
				if (MovementUtils.isOnGround(0.001) && mc.thePlayer.isMovingOnGround()) {
					event.setPosY(event.getPosY() +  RandomUtils.nextFloat(0.0004f + MathUtils.randomFloatValue(), 0.00049f + MathUtils.randomFloatValue()));
				}
			}

			if (event.getType() == EventMotionUpdate.Type.PRE) {
				xMotionSpeed = mc.thePlayer.posX - mc.thePlayer.prevPosX;
				zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
				nextMotionSpeed = Math.sqrt(xMotionSpeed * xMotionSpeed + zDist * zDist);
			}
			break;
			case Mineplex:
				if (event.getType() == EventMotionUpdate.Type.PRE) {
					xMotionSpeed = mc.thePlayer.posX - mc.thePlayer.prevPosX;
					zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
					nextMotionSpeed = Math.sqrt(xMotionSpeed * xMotionSpeed + zDist * zDist);
				}
				break;
		case Vanilla:
			break;
		case NCP:
			break;
		case Test:
			break;
		default:
			break;
		}
	}
}
