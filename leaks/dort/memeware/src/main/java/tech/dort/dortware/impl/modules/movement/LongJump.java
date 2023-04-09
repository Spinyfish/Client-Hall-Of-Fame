package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.utils.movement.ACType;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

public class LongJump extends Module {

    private double movementSpeed;
    private boolean done, invert;
    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Mode.values());
    private final BooleanValue flagCheck = new BooleanValue("Flag Check", this, true);
    private final BooleanValue autoDisable = new BooleanValue("Auto Disable", this, true);
    private final NumberValue numberValue = new NumberValue("Speed", this, 2.5, 0.1, 10, SliderUnit.BPT);

    public LongJump(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, numberValue, flagCheck, autoDisable);
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        final double speed = numberValue.getCastedValue();
        switch (enumValue.getValue()) {
            case MINEPLEXMINI: {
//                if(movementSpeed > 1.4) {
//                    mc.timer.timerSpeed = 0.9F;
//                }
//                else {
//                    mc.timer.timerSpeed = 1.3F;
//                }
                if (mc.thePlayer.ticksExisted % 5 == 0) {
                    event.setMotionY(mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.42F : mc.thePlayer.movementInput.sneak ? -0.42F : 0.16);
                    movementSpeed += 0.3F;
                    MotionUtils.setMotion(event, 0);
                } else {
                    MotionUtils.setMotion(event, movementSpeed = (!mc.thePlayer.isMoving() || mc.thePlayer.isCollided) ? 0.32 : Math.max(Math.min(speed, movementSpeed - movementSpeed / 32F), MotionUtils.getBaseSpeed(ACType.MINEPLEX)));
                }
            }
            break;

            case MINEPLEXHOP: {
                event.setMotionY(mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.42F : mc.thePlayer.movementInput.sneak ? -0.42F : mc.thePlayer.ticksExisted % 11 == 0 ? 0.42F : mc.thePlayer.motionY);
                if (mc.thePlayer.movementInput.jump || mc.thePlayer.movementInput.sneak) {
                    MotionUtils.setMotion(0.32);
                    movementSpeed = 0;
                    return;
                }
                if (mc.thePlayer.ticksExisted % 11 == 0) {
                    movementSpeed += 0.25;
                    MotionUtils.setMotion(event, 0);
                } else {
                    MotionUtils.setMotion(event, movementSpeed = (!mc.thePlayer.isMoving() || mc.thePlayer.isCollided) ? 0.32 : Math.max(Math.min(speed, movementSpeed - movementSpeed / 49D), MotionUtils.getBaseSpeed(ACType.MINEPLEX)));
                }
            }
            break;

            case MINEPLEXSMOOTH: {
                mc.timer.timerSpeed = 0.7f;
                event.setMotionY(mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.42F : mc.thePlayer.movementInput.sneak ? -0.42F : mc.thePlayer.isMovingOnGround() ? 0.42F : 0);
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.088F, mc.thePlayer.posZ, true));
                    movementSpeed += 0.3;
                }
                movementSpeed = (!mc.thePlayer.isMoving() || mc.thePlayer.isCollided) ? 0.32 : Math.max(Math.min(Math.min(speed, 1.4), movementSpeed - movementSpeed / 28D), MotionUtils.getBaseSpeed(ACType.MINEPLEX));
                MotionUtils.setMotion(event, mc.thePlayer.isMovingOnGround() ? 0 : movementSpeed);
            }
            break;

            case MINEPLEXLONGJUMP: {
                if (mc.thePlayer.isMovingOnGround()) {
                    if (!done) {
                        if (movementSpeed < speed - 0.5) {
                            event.setMotionY(mc.thePlayer.motionY = .088F);
                        } else if (movementSpeed > speed - 0.5) {
                            event.setMotionY(mc.thePlayer.motionY = 0.42F);
                        }
                    }
                }
                if (!done) {
                    if (mc.thePlayer.isMovingOnGround()) {
                        mc.timer.timerSpeed = 0.9f;
                        movementSpeed = movementSpeed < 0.5 ? 0.8 : movementSpeed + 0.59F;
                        MotionUtils.setMotion(event, 0.0D);
                        if (movementSpeed >= speed) {
                            done = true;
                        }
                    } else {
                        movementSpeed = (!mc.thePlayer.isMoving() || mc.thePlayer.isCollided) ? 0.32 : Math.max(Math.min(speed, movementSpeed - movementSpeed / 48D), MotionUtils.getBaseSpeed(ACType.MINEPLEX));
                        MotionUtils.setMotion(event, invert ? -movementSpeed : movementSpeed);
                        invert = !invert;
                    }
                } else {
                    mc.timer.timerSpeed = 0.7f;
                    event.setMotionY(mc.thePlayer.motionY += 0.03);
                    if (mc.thePlayer.onGround) {
                        if (autoDisable.getValue())
                            this.toggle();
                        done = false;
                    }
                    if (mc.thePlayer.fallDistance <= 1.4) {
                        event.setMotionY(mc.thePlayer.motionY += 0.005);
                    }
                    movementSpeed = (!mc.thePlayer.isMoving() || mc.thePlayer.isCollided) ? 0.32 : Math.max(Math.min(speed, movementSpeed - movementSpeed / 48D), MotionUtils.getBaseSpeed(ACType.MINEPLEX));
                    MotionUtils.setMotion(event, movementSpeed);
                }
            }
            break;
        }
    }

    @Override
    public void onEnable() {
        done = false;
        movementSpeed = 0;
        super.onEnable();
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && flagCheck.getValue()) this.toggle();
    }

    @Override
    public void onDisable() {
        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        super.onDisable();
    }

    public String getSuffix() {
        return " \2477" + enumValue.getValue().getDisplayName();
    }

    public enum Mode implements INameable {
        MINEPLEXLONGJUMP("Mineplex Long Jump"), MINEPLEXMINI("Mineplex Mini-Hop Fly"), MINEPLEXHOP("Mineplex Hop Fly"), MINEPLEXSMOOTH("Mineplex Fly");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
