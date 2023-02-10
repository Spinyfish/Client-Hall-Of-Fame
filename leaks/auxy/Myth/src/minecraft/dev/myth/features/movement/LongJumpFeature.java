/**
 * @project Myth
 * @author CodeMan
 * @at 09.08.22, 15:54
 */
package dev.myth.features.movement;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.PlayerUtil;
import dev.myth.api.utils.inventory.InventoryUtils;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.events.MoveEvent;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.optifine.util.MathUtils;

@Feature.Info(
        name = "LongJump",
        description = "Allows you to jump further",
        category = Feature.Category.MOVEMENT
)
public class LongJumpFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);
    public final NumberSetting blocksMCSpeed = new NumberSetting("BlocksMCSpeed", 5, 1, 8, 0.1)
            .addDependency(() -> mode.is(Mode.BLOCKSMC)).setDisplayName("Speed");
    public final BooleanSetting bow = new BooleanSetting("Bow", true).addDependency(() -> mode.is(Mode.WATCHDOG));
    public final EnumSetting<RedeskyType> redeskyMode = new EnumSetting<>("Type", RedeskyType.HIGH).addDependency(() -> mode.is(Mode.REDESKY));

    public double moveSpeed, lastDist, yBoost, startY;
    public int ticks, ticks2, bowState, oldSlot;
    public boolean damaged, boost, jumped;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() == EventState.PRE) {
            lastDist = MovementUtil.getDist(getPlayer().posX, getPlayer().posZ, getPlayer().lastTickPosX, getPlayer().lastTickPosZ);
        }
        switch (mode.getValue()) {
            case REDESKY:
                if (ticks > 10 && getPlayer().onGround) toggle();
                switch (redeskyMode.getValue()) {
                    case NORMAL:
                        if (getPlayer().onGround && !damaged) {
                            getPlayer().jump();
                            if (getPlayer().motionY > 0.0 && getPlayer().onGround) {
                                getPlayer().motionY += 0.054;
                                getPlayer().speedInAir = 0.04f;
                                damaged = true;
                            }
                        }
                        break;
                    case HIGH:
                        setTimer(1f);
                        if (getPlayer().onGround && !damaged) {
                            if (getPlayer().isMoving()) { // if der spiler bewegen
                                if (!getGameSettings().keyBindJump.isKeyDown())
                                    getPlayer().jump();
                                getPlayer().motionY += 0.3; // Bypassing redesky values bc aac has autism ~ Auxy
                                getPlayer().motionX *= 1.2; // Bypassing redesky values bc aac has autism ~ Auxy
                                getPlayer().motionZ *= 1.2; // Bypassing redesky values bc aac has autism ~ Auxy
                                damaged = true;
                            }
                        }
                        break;
                }
                break;
            case WATCHDOG:
                if (boost && !damaged && bowState > 3 && event.getState() == EventState.PRE) {
                    event.setPitch(-90);
                    event.setSprinting(false);
                }

                if (!boost && !damaged && event.getState() == EventState.PRE) {
                    if (getPlayer().onGround) {
                        getPlayer().jump();
                        bowState++;
                        if(bowState <= 3) event.setOnGround(false);
                        else event.setOnGround(true);
                        startY = getPlayer().posY;
                    }

                    getPlayer().posY = startY;
                    getPlayer().cameraPitch = 0;
                    getPlayer().cameraYaw = 0;
                    getPlayer().setSprinting(false);
//                    MC.timer.timerSpeed = 10;

                    if (bowState > 3) {
                        damaged = true;
                        bowState = 0;
                        boost = true;
//                        MC.timer.timerSpeed = 1;
                    }
                }
                break;

            case BLOCKSMC:
//                if (!MovementUtil.isMoving()) break;
//                if (!MovementUtil.isOnGround()) break;
//                if (event.getState() != EventState.PRE) break;
//                if (damaged) break;
////                double baseMoveSpeed = MovementUtil.getBaseMoveSpeed();
////                double yaw = Math.toRadians(getPlayer().rotationYaw);
////                double xOffset = -Math.sin(yaw) * baseMoveSpeed * 2,
////                        yOffset = Math.cos(yaw) * baseMoveSpeed * 2;
//                if (!MC.theWorld.getCollidingBoundingBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(0, 0.3, 0)).isEmpty()) {
//                    event.setPosY(MathUtil.round(event.getPosY() - 0.078, 1 / 64F));
//                    damaged = true;
//                    ticks2 = 0;
//                }
                break;
        }
    };

    @Handler
    public final Listener<MoveEvent> moveEventListener = event -> {
        double baseMoveSpeed = MovementUtil.getBaseMoveSpeed();
        switch (mode.getValue()) {
            case BLOCKSMC: {

                boolean blockAbove = !MC.theWorld.getCollidingBoundingBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(0, 0.3, 0)).isEmpty();

                if (!blockAbove && !damaged) {
                    toggle();
                    return;
                }

                if (blockAbove && !damaged) {
                    sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(getPlayer().posX, getPlayer().posY, getPlayer().posZ, getPlayer().rotationYaw, getPlayer().rotationPitch, true));
                    sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(getPlayer().posX, getPlayer().posY - 0.078, getPlayer().posZ, getPlayer().rotationYaw, getPlayer().rotationPitch, false));
                    sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(getPlayer().posX, getPlayer().posY, getPlayer().posZ, getPlayer().rotationYaw, getPlayer().rotationPitch, true));
                    damaged = true;
                }
                if (!blockAbove) {
                    if (ticks2 == 0) {
                        MovementUtil.fakeJump();
                        event.setY(getPlayer().motionY = 0.42f);
                        MovementUtil.setSpeed(event, blocksMCSpeed.getValue());
                        doLog("Jumped");
                        ticks2++;
                    } else if (ticks2 == 1) {
                        MovementUtil.setSpeed(event, blocksMCSpeed.getValue() - 0.4);
                        ticks2++;
                    } else if (getPlayer().fallDistance == 0) event.setY(getPlayer().motionY += 0.025);
                }
                if (damaged && ticks2 == 2 && MovementUtil.isOnGround())
                    toggle();
                break;
            }
            case VANILLA:
                if (MovementUtil.isMoving()) {
                    if (getPlayer().onGround) {
                        getPlayer().jump();
                        event.setY(0.42F);
                    }
                    MovementUtil.setSpeed(event, 2);
                }
                break;
            case MINEMORA:
                if (getPlayer().onGround) {
                    MC.timer.timerSpeed = 0.95f;
                    getPlayer().jump();
                    event.setY(getPlayer().motionY = 0.42F);
                    MovementUtil.setSpeed(event, MovementUtil.getBaseMoveSpeed() * 1.2);
                } else {
                    if (getPlayer().motionY > 0) {
                        event.setY(getPlayer().motionY += 0.03 + Math.random() * 0.01);
                    } else {
                        toggle();
                    }
                }
                sendPacket(new C0CPacketInput(Float.MAX_VALUE, Float.MAX_VALUE, true, false));
                break;
            case WATCHDOG:
                if (/*!boost || */damaged) {
                    if (MovementUtil.isOnGround()) {
                        if (jumped) {
                            toggle();
                            return;
                        }
                        MovementUtil.fakeJump();

                        if (boost) {
                            event.setY(getPlayer().motionY = MovementUtil.getJumpMotion() /*+ yBoost*/);
                            moveSpeed = MovementUtil.getBaseMoveSpeed(true) * (getPlayer().isPotionActive(Potion.moveSpeed) ? 1.9 : 2.5);
                        } else {
                            event.setY(getPlayer().motionY = MovementUtil.getJumpMotion());
                            moveSpeed = baseMoveSpeed * (getPlayer().isPotionActive(Potion.moveSpeed) ? 2 : 2.1);
                        }

                        jumped = true;
                        MovementUtil.setSpeed(event, moveSpeed);
                        ticks = 0;

//                        doLog(event.getY() + " " + MovementUtil.getSpeed(event));
                    } else {
                        if (boost) {

                            if (ticks > 1) {
//                                setTimer(0.8F);
                            }

//                            if (ticks == 9) {
//                                event.setY(getPlayer().motionY = yBoost);
//                                moveSpeed = (getPlayer().isPotionActive(Potion.moveSpeed) ? 0.8 : 0.7) - Math.random() / 1000;
//                                MovementUtil.setSpeed(event, moveSpeed);
//                            }

                            if (ticks == 4) {
//                                event.setY(getPlayer().motionY = MovementUtil.getJumpMotion());
                            }

                            if (MovementUtil.isOnGround()) toggle();

                        } else {
                            if (getPlayer().fallDistance == 0) {
                                MovementUtil.setSpeed(event, lastDist - (lastDist / 100));
                            }
                        }
//                        if (getPlayer().motionY < 0 && getPlayer().fallDistance < (boost ? 0.5 : 1)) {
//                            event.setY(getPlayer().motionY += 0.022 + Math.random() * 0.001);
//                        }
//                        doLog(event.getY() + " " + MovementUtil.getSpeed(event));
                    }
                } else if(boost) {
                    if (getPlayer().hurtTime == 9) {
                        damaged = true;
                        getPlayer().inventory.currentItem = oldSlot;
                    } else {
                        int bowSlot = InventoryUtils.canBow();
                        int shotStrength = 4;

                        if (bowState == 0) {
                            oldSlot = getPlayer().inventory.currentItem;
                            getPlayer().inventory.currentItem = bowSlot;
                        } else if (bowState == 4) {
                            getPlayer().inventory.currentItem = bowSlot;
                            sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getHeldItem()));
                        } else if (bowState == 4 + shotStrength) {
                            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        } else if (bowState == 4 + shotStrength + 1) {
                            getPlayer().inventory.currentItem = oldSlot;
                        }
                        bowState++;
                    }
                    MovementUtil.setSpeed(event, 0);
                } else {
                    MovementUtil.setSpeed(event, 0);
                }
                break;
        }
        ticks++;
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (mode.is(Mode.WATCHDOG)) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = event.getPacket();
                if (packet.getEntityID() == getPlayer().getEntityId()) {
                    double y = packet.getMotionY() / 8000D;
                    event.setCancelled(true);
                    yBoost = y;
                }
            }
        }
    };

    @Override
    public void onDisable() {
        super.onDisable();

        MC.timer.timerSpeed = 1;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if(getPlayer() == null) {
            toggle();
            return;
        }

        ticks = 0;
        ticks2 = 0;
        bowState = 0;
        oldSlot = 0;
        damaged = false;
        boost = false;
        jumped = false;

        startY = getPlayer().posY;

        if (mode.is(Mode.WATCHDOG)) {
            if(bow.getValue()){
                boost = InventoryUtils.canBow() != -1;
                if (!boost) {
                    toggle();
                    return;
                }
            }
            if(!MovementUtil.isOnGround()) {
                toggle();
                return;
            }
        }

        SpeedFeature speedFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(SpeedFeature.class);
        if (speedFeature != null && speedFeature.isEnabled()) {
            speedFeature.toggle();
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public enum Mode {
        VANILLA("Vanilla"),
        WATCHDOG("Watchdog"),
        BLOCKSMC("BlocksMC"),
        REDESKY("Redesky"),
        MINEMORA("Minemora");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum RedeskyType {
        NORMAL("NORMAL"),
        HIGH("High"),
        DEV("Dev");

        private final String name;

        RedeskyType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
