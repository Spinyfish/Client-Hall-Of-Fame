/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 22:56
 */
package dev.myth.features.movement;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.rotation.RotationUtil;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.events.*;
import dev.myth.features.player.NoFallFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;

@Feature.Info(
        name = "Speed",
        description = "Allows you to move faster",
        category = Feature.Category.MOVEMENT
)
public class SpeedFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.BHOP);
    public final EnumSetting<VerusMode> verusMode = new EnumSetting<>("VerusMode", VerusMode.HOP)
            .addDependency(() -> mode.is(Mode.VERUS)).setDisplayName("Type");
    public final NumberSetting speed = new NumberSetting("Speed", 0.1, 0.0, 1.0, 0.01)
            .addDependency(() -> mode.is(Mode.BHOP));
    public final BooleanSetting latest = new BooleanSetting("Latest", false)
            .addDependency(() -> mode.is(Mode.NCP));
    public final NumberSetting timer = new NumberSetting("Timer Boost", 1, 1, 2, 0.01)
            .addDependency(() -> mode.is(Mode.NCP) || mode.is(Mode.WATCHDOG)).addValueAlias(1, "Off");
    public final EnumSetting<StrafeMode> strafeMode = new EnumSetting<>("Strafe Mode", StrafeMode.GROUND)
            .addDependency(() -> mode.is(Mode.WATCHDOG));
    public final NumberSetting strafeSteps = new NumberSetting("Strafe Steps", 45, 0, 180, 1)
            .addDependency(() -> mode.is(Mode.WATCHDOG) && strafeMode.is(StrafeMode.STEP));
    public final BooleanSetting lowHop = new BooleanSetting("Low Hop", false)
            .addDependency(() -> mode.is(Mode.WATCHDOG) || mode.is(Mode.NCP));
    public final BooleanSetting damageBoost = new BooleanSetting("Damage Boost", false)
            .addDependency(() -> mode.is(Mode.VERUS) || mode.is(Mode.WATCHDOG));
    public final BooleanSetting strafeFix = new BooleanSetting("Strafe Fix", false)
            .addDependency(() -> mode.is(Mode.WATCHDOG));
    public final BooleanSetting flagCheck = new BooleanSetting("Flag Check", true);

    private int stage, ticksSinceHurt, disableTicks;
    private double moveSpeed, damageSpeed;
    private double lastDist;
    private boolean ignoreDamage, doLowHop;
    private float strafeYaw;

    // Watchdog
    private boolean watchDogFlagged;
    private ArrayList<C03PacketPlayer> packets = new ArrayList<>();
    private boolean blinking;

    private NoFallFeature noFallFeature;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() != EventState.PRE) return;

        if (noFallFeature == null) {
            noFallFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(NoFallFeature.class);
        }

        if (disableTicks > 0) {
            disableTicks--;
            return;
        }

        switch (this.mode.getValue()) {
            case WATCHDOG_NEW: {
                setTimer(1.8F);
                break;
            }
            case WATCHDOG:
                ticksSinceHurt++;

                blinking = strafeFix.getValue() && getPlayer().fallDistance < 1
                        && MovementUtil.isMoving() && !getPlayer().onGround
                        && packets.size() < 4 && getPlayer().hurtTime == 0;
                event.setOnGround(event.isOnGround() || blinking);

//                doLog(blinking);

                break;
            case TEST: {
                if (doLowHop) {
                    if (stage == 2) {
                        event.setPosY(event.getPosY() + 0.4);
                        event.setOnGround(false);
                        getPlayer().onGround = false;
                    }
                }
                break;
            }
        }

        lastDist = MovementUtil.getDist(getPlayer().posX, getPlayer().posZ, getPlayer().lastTickPosX, getPlayer().lastTickPosZ);
    };

    @Handler
    public final Listener<MoveEvent> moveEventListener = event -> {
        double baseMoveSpeed = MovementUtil.getBaseMoveSpeed();
        if (disableTicks > 0) {
            if (mode.is(Mode.TEST)) {
                stage = 0;
            }
            return;
        }
        switch (this.mode.getValue()) {
            case TEST: {
                double jumpHeight = 0.4F;
                if (MovementUtil.isMoving()) {
                    if (MovementUtil.isOnGround() && stage > 2) {
                        doLowHop = shouldLowHop();
                        MovementUtil.fakeJump();
                        if (!doLowHop) event.setY(getPlayer().motionY = 0.42f);
                        moveSpeed *= 2.15 - MovementUtil.MIN_DIST;
                        stage = 1;
                    } else {
                        if (stage == 2) {
                            moveSpeed = lastDist - 0.66 * (lastDist - baseMoveSpeed);
                            if (doLowHop) {
                                if (!getPlayer().isCollidedHorizontally && MovementUtil.isOnGround(jumpHeight + 0.0001)) {
//                                    event.setY(getPlayer().motionY = -3);
                                }
                            } else {
                                moveSpeed = baseMoveSpeed * 1.38;
                            }
                        } else {
                            moveSpeed = lastDist - lastDist / 159.9;
                        }
                    }
                    MovementUtil.setSpeed(event, Math.max(moveSpeed, baseMoveSpeed));
                    stage++;
                } else {
                    stage = 0;
                }
                break;
            }
            case WATCHDOG:
                if (MovementUtil.isMoving()) {
                    double speed = MovementUtil.getSpeed(event);
                    if (MovementUtil.isOnGround()) {
                        if (stage > 6) {
                            doLowHop = lowHop.getValue() && shouldLowHop() && !getGameSettings().keyBindJump.isKeyDown();
                            MovementUtil.fakeJump();
                            event.setY(getPlayer().motionY = MovementUtil.getJumpMotion());
                            MovementUtil.setSpeed(event, moveSpeed = (MovementUtil.getBaseMoveSpeed() * (getPlayer().isPotionActive(Potion.moveSpeed) ? 1.9 : 2.1)));
                            MC.timer.timerSpeed = 1.0f;
                            stage = 0;
                            strafeYaw = MovementUtil.getDirection();
                        }
                    } else {
                        if (stage > 2) {
                            if (timer.getValue() > 1) MC.timer.timerSpeed = timer.getValue().floatValue();
                        }

                        boolean strafe = !strafeMode.is(StrafeMode.GROUND);

                        if (doLowHop) {
                            if (MathUtil.round(event.getY(), 0.01) == 0.08) { // 3. air tick
                                event.setY(getPlayer().motionY = Math.random() * -0.01);
//                                doLog(MC.session.getSessionID());
                                System.out.println(MC.session.getSessionID());
                            }
                        }

                        if (strafeMode.is(StrafeMode.STEP)) {
                            strafeYaw = RotationUtil.limitAngle(strafeYaw, MovementUtil.getDirection(), strafeSteps.getValue().floatValue());
                        } else if (strafeMode.is(StrafeMode.FULL)) {
                            strafeYaw = MovementUtil.getDirection();
                        }

                        if (false) {
                            if (stage == 1) {
                                moveSpeed = moveSpeed - 0.83 * (moveSpeed - baseMoveSpeed);
                            } else {
                                moveSpeed = moveSpeed * 0.98;
                            }
                        } else {
                            moveSpeed = speed;
                        }

                        if (damageBoost.getValue()) {
                            if (ticksSinceHurt == 0 && damageSpeed > moveSpeed) {
                                moveSpeed = damageSpeed;
                                strafeYaw = MovementUtil.getDirection();
                                strafe = true;
                            } else if (ticksSinceHurt < 19) {
                                strafeYaw = MovementUtil.getDirection();
                                strafe = true;
                            }
                        }

                        if (strafe) {
                            double[] motions = MovementUtil.yawPos(strafeYaw, moveSpeed);
                            event.setX(getPlayer().motionX = motions[0]);
                            event.setZ(getPlayer().motionZ = motions[1]);
                        }
                    }
                    stage++;
                }
                break;
            case MATRIX:
                if (getPlayer().isMoving()) {
                    if (MovementUtil.isOnGround()) {
                        getPlayer().jump();
                        event.setY(0.42F);
                        setTimer(1f);
                    }
                    if (getPlayer().motionY > 0.003) {
                        event.setX(getPlayer().motionX *= 1.0012f);
                        event.setZ(getPlayer().motionZ *= 1.0012f);
                        setTimer(1.05f);
                    }
                }
                break;
            case VERUS:
                switch (verusMode.getValue()) {
                    case HOP:
                        if (MovementUtil.isMoving()) {
                            if (MovementUtil.isOnGround()) {
                                getPlayer().jump();
                                event.setY(0.42F);
                                getPlayer().speedOnGround = 0.17f;
                                MovementUtil.setSpeed(event, 0.41f);
                            } else {
                                getPlayer().speedInAir = 0.041f;
                                if (getPlayer().moveForward > 0)
                                    MovementUtil.setSpeed(event, getPlayer().hurtTime != 0 && damageBoost.getValue() ? 0.53f : 0.35f);
                                else
                                    MovementUtil.setSpeed(event, 0.382f);
                            }
                        }
                        break;
                    case DEV:
                        if (MovementUtil.isMoving()) {
                            double speed = 0.4;

                            if (MovementUtil.isOnGround()) {
                                event.setY(0.42F);
                                getPlayer().motionY = 0;
                                speed = 0.6;
                            }
                            if (Math.abs(MathHelper.wrapAngleTo180_float(MovementUtil.getDirection() - getPlayer().rotationYaw)) > 60)
                                speed -= 0.05;
                            if (getPlayer().fallDistance > 0.5) speed -= 0.05;
                            MovementUtil.setSpeed(event, speed);
                        }
                        break;
                }
                break;
            case NORULES: {
                if (MovementUtil.isMoving()) {
                    if (MovementUtil.isOnGround()) {
                        doLowHop = shouldLowHop() && !getGameSettings().keyBindJump.isKeyDown();
                        MovementUtil.fakeJump();
                        event.setY(getPlayer().motionY = 0.42F);
                        MovementUtil.setSpeed(event, 0.7);
                    } else {
                        if (doLowHop) {
                            event.setY(getPlayer().motionY -= 0.05);
                        }
                        MovementUtil.setSpeed(event, 0.5 - MovementUtil.MIN_DIST);
                    }
                } else {
                    getPlayer().motionX = 0.0D;
                    getPlayer().motionZ = 0.0D;
                }
                break;
            }
            case BHOP:
                if (MovementUtil.isMoving()) {
                    if (MovementUtil.isOnGround()) {
                        MovementUtil.fakeJump();
                        event.setY(getPlayer().motionY = 0.42F);
                    }
                    MovementUtil.setSpeed(event, speed.getValue());
                } else {
                    getPlayer().motionX = 0.0D;
                    getPlayer().motionZ = 0.0D;
                }
                break;
            case BLOCKSMC: {
                if (MovementUtil.isMoving()) {
                    if (Math.abs(getPlayer().rotationYaw - getPlayer().currentEvent.getYaw()) > 60) {
                        baseMoveSpeed -= 0.01;
                    }
                    if (getPlayer().moveStrafing != 0 && getPlayer().moveForward != 0) {
                        baseMoveSpeed -= 0.03;
                    }
                    if (MovementUtil.isOnGround()) {
                        MovementUtil.fakeJump();
                        event.setY(getPlayer().motionY = MovementUtil.getJumpMotion());
                        stage = 0;
                    }
                    switch (stage) {
                        case 0:
                            moveSpeed = baseMoveSpeed * (getPlayer().isPotionActive(Potion.moveSpeed) ? 2 : 2.1);
                            break;
                        case 1:
                            double bunnySlope = 0.83 * (lastDist - baseMoveSpeed);
                            moveSpeed = lastDist - bunnySlope;
                            break;
                        default:
                            moveSpeed = lastDist - lastDist / MovementUtil.BUNNY_DIV_FRICTION;
                            break;
                    }
                    stage++;
                }

                MovementUtil.setSpeed(event, Math.max(moveSpeed, baseMoveSpeed));
                break;
            }
            case NCP:
                if (MovementUtil.isMoving()) {
                    if (MovementUtil.isOnGround()) {
                        MovementUtil.fakeJump();
                        doLowHop = lowHop.getValue() && shouldLowHop() && !getGameSettings().keyBindJump.isKeyDown();
                        event.setY(getPlayer().motionY = doLowHop ? 0.4 : MovementUtil.getJumpMotion());
//                        event.setY(getPlayer().motionY = 0.17 + Math.random() * 0.01);
                        stage = 0;
                    }
                    switch (stage) {
                        case 0:
//                            moveSpeed = baseMoveSpeed * 1.25;
                            moveSpeed = baseMoveSpeed * 2.15 - 1.0E-3D;
                            MC.timer.timerSpeed = 1.0f;
                            break;
                        case 1:
                            double bunnySlope = (latest.getValue() ? 0.83 : 0.66) * (lastDist - baseMoveSpeed);
                            moveSpeed = lastDist - bunnySlope;
                            break;
                        default:
                            moveSpeed = lastDist - lastDist / MovementUtil.BUNNY_DIV_FRICTION;
//                            moveSpeed = MovementUtil.getSpeed(event);
                            if (timer.getValue() > 1) MC.timer.timerSpeed = timer.getValue().floatValue();
                            break;
                    }
                    MovementUtil.setSpeed(event, Math.max(moveSpeed, baseMoveSpeed));
                    double value = event.getY();

//                    if(stage < MovementUtil.JUMP_MOTIONS.length && stage > 0) {
//                        double normalMotion = MovementUtil.JUMP_MOTIONS[stage];
//                        if(normalMotion > 0) {
//                            value = MovementUtil.JUMP_MOTIONS[stage] * 1;
//                        } else {
//                            value = MovementUtil.JUMP_MOTIONS[stage] * 1.2;
//                        }
////                        doLog("Stage: " + stage + " Value: " + value + " Motion: " + MovementUtil.JUMP_MOTIONS[stage]);
//                    }

//                    if (getPlayer().fallDistance < 0.35 && doLowHop) {
//                        double yDistFromGround = MathUtil.round(getPlayer().posY - (int) getPlayer().posY, 0.001);
//                        double[] LOW_HOP_Y_POSITIONS = {0.4, 0.71, 0.75, 0.55, 0.41};
//                        if (yDistFromGround == LOW_HOP_Y_POSITIONS[0]) {
//                            value = 0.31;
//                        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[1]) {
//                            value = 0.04;
//                        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[2]) {
//                            value = -0.2;
//                        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[3]) {
//                            value = -0.14;
//                        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[4]) {
//                            value = -0.2;
//                        }
//                        doLog("Stage: " + stage + " Value: " + value);
//                    }

                    if (doLowHop) switch (stage) {
                        case 1:
                            value = 0.31D;
                            break;
                        case 2:
                            value = 0.04D;
                            break;
                        case 3:
                            value = -0.2D;
                            break;
                        case 4:
                            value = -0.14D;
                            break;
                        case 5:
                            value = -0.2D;
                            break;
                    }
//                    doLog("Stage: " + stage + " Value: " + value);
                    event.setY(getPlayer().motionY = value);
                    stage++;
                }
                break;
        }
    };

    @Handler
    public final Listener<MoveFlyingEvent> moveFlyingEventListener = event -> {
        double baseMoveSpeed = MovementUtil.getBaseMoveSpeed();
        switch (mode.getValue()) {
            case WATCHDOG:
//                if (MovementUtil.isMoving()) {
//                    if (MovementUtil.isOnGround()) {
//                        MovementUtil.fakeJump();
//                        getPlayer().motionY = MovementUtil.getJumpMotion();
//                        event.setSpeedPartialStrafe(getPlayer(), (float) (baseMoveSpeed * 2.13), 0.235F);
//                    }
//                }
                break;
        }
    };

    @Handler
    public final Listener<Render2DEvent> render2DEventListener = event -> {
        if (disableTicks > 0) {
            ScaledResolution sr = event.getScaledResolution();
            double width = 150;
            double height = 38;
            double x = (sr.getScaledWidth_double() - width) / 2;
            double y = sr.getScaledHeight_double() / 2 + 50;

            RenderUtil.drawSkeetRect(x, y, width, height, true, true);

            String text = "Disabled for " + disableTicks + " ticks";
            double textWidth = MC.fontRendererObj.getStringWidth(text);
            MC.fontRendererObj.drawString(text, (int) (x + (width - textWidth) / 2), (int) (y + 10), -1);
            double barWidth = width - 20;
//            RenderUtil.drawRect(x + 10, y + 30, barWidth, 5, );
//            RenderUtil.drawRect(x + 10, y + 30, (barWidth) * (1F - (disableTicks / 20F)), 5, ColorUtil.getClientColor(System.currentTimeMillis()));

            double yOffset = 25, xOffset = 10, sliderThickness = 5;

            int clientColor = ColorUtil.getClientColor(0);

            RenderUtil.drawRect(x + xOffset, y + yOffset, barWidth + 1, sliderThickness, RenderUtil.getColor(0x0C0C0C, 255));
            RenderUtil.drawRect(x + 0.5 + xOffset, y + yOffset + 0.5, barWidth, sliderThickness - 0.5, RenderUtil.getColor(0x3A3A3A, 255));
            RenderUtil.drawRect(x + 0.5 + xOffset, y + yOffset + 0.5, barWidth * (1F - (disableTicks / 20F)), sliderThickness - 0.5, clientColor);

            Gui.drawGradientRect(x + 0.5 + xOffset, y + yOffset + 0.5, x + 0.5 + xOffset + barWidth, y + yOffset + sliderThickness, RenderUtil.getColor(0x3A3A3A, 255), RenderUtil.darker(RenderUtil.getColor(0x3A3A3A, 255), 0.7f));
            Gui.drawGradientRect(x + 0.5 + xOffset, y + yOffset + 0.5, x + 0.5 + xOffset + barWidth * (1F - (disableTicks / 20F)), y + yOffset + sliderThickness, RenderUtil.darker(clientColor, 0.7f), RenderUtil.darker(clientColor, 1.4f));

        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        final Packet<? extends INetHandler> packet = event.getPacket();

        if (packet instanceof S08PacketPlayerPosLook) {
            if (flagCheck.getValue()) disableTicks = 20;
        }

        switch (this.mode.getValue()) {
            case WATCHDOG_NEW: {
                if (packet instanceof S08PacketPlayerPosLook && this.watchDogFlagged) {
                    sendPacketUnlogged(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) packet).x, ((S08PacketPlayerPosLook) packet).y, ((S08PacketPlayerPosLook) packet).z,
                            ((S08PacketPlayerPosLook) packet).yaw, ((S08PacketPlayerPosLook) packet).pitch, false));
                    getPlayer().setPosition(((S08PacketPlayerPosLook) packet).x, ((S08PacketPlayerPosLook) packet).y, ((S08PacketPlayerPosLook) packet).z);
                    event.setCancelled(true);
                }

                if (packet instanceof C03PacketPlayer && getPlayer().ticksExisted % 49 == 0) {

                    ((C03PacketPlayer) packet).x += 1.5D;
                    ((C03PacketPlayer) packet).y -= 1.5D;

                    this.watchDogFlagged = true;
                }
                this.stage++;
                break;
            }
            case WATCHDOG:
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity entityVelocity = event.getPacket();
                    if (entityVelocity.getEntityID() == getPlayer().getEntityId()) {
                        double x = entityVelocity.getMotionX() / 8000D;
                        double y = entityVelocity.getMotionY() / 8000D;
                        double z = entityVelocity.getMotionZ() / 8000D;
                        double dist = Math.sqrt(x * x + z * z);
//                        doLog("Velocity: " + dist);
                        ticksSinceHurt = 0;
                        damageSpeed = dist;
                    }
                }
                if (packet instanceof C03PacketPlayer) {
                    packets.add((C03PacketPlayer) packet);
                    event.setCancelled(true);

                    if (!blinking) {
                        packets.forEach(this::sendPacketUnlogged);
                        packets.clear();
                    }
                }
                break;
        }
    };

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        setTimer(1.0F);
        getPlayer().speedInAir = 0.02f;

        packets.forEach(this::sendPacketUnlogged);
        packets.clear();

        //getPlayer().motionX = 0.0D;
        //getPlayer().motionZ = 0.0D;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ticksSinceHurt = 100;
        if (mode.is(Mode.WATCHDOG)) {
            stage = 9;
        } else {
            stage = 0;
        }
        disableTicks = 0;

        strafeYaw = MovementUtil.getDirection();

        switch (this.mode.getValue()) {
            case WATCHDOG_NEW: {
                this.watchDogFlagged = false;
                getPlayer().ticksExisted = 48;
                break;
            }
        }
    }

    private boolean shouldLowHop() {

        if (getPlayer().isCollidedHorizontally) return false;

        double yaw = MovementUtil.getDirection() * Math.PI / 180D;
        double dist = MovementUtil.getBaseMoveSpeed() * 10;
        double x = -Math.sin(yaw) * dist;
        double z = Math.cos(yaw) * dist;

        AxisAlignedBB bb = getPlayer().getEntityBoundingBox().offset(x, 0, z);
        AxisAlignedBB bb2 = getPlayer().getEntityBoundingBox().offset(x, -2, z);

        return MC.theWorld.getCollidingBoundingBoxes(getPlayer(), bb).isEmpty() && !MC.theWorld.getCollidingBoundingBoxes(getPlayer(), bb2).isEmpty();
    }

    public enum Mode {
        WATCHDOG("Watchdog"),
        WATCHDOG_NEW("Watchdog New"),
        BHOP("Bhop"),
        VERUS("Verus"),
        MATRIX("Matrix"),
        NCP("NCP"),
        BLOCKSMC("BlockMC"),
        NORULES("NoRules"),
        TEST("Test");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum StrafeMode {
        FULL("Full"),
        GROUND("Ground"),
        STEP("Step");

        private final String name;

        StrafeMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum VerusMode {
        HOP("Hop"),
        DEV("Dev");

        private final String name;

        VerusMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}
