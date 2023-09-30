package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.PlayerJumpEvent;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.PlayerStrafeEvent;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.player.RotationUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.shaders.Blur;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.Timer;
import java.util.TimerTask;

@ModuleInfo(name = "Speed", renderName = "Speed", category = Category.MOVEMENT, keybind = Keyboard.KEY_NONE)

public class Speed extends Module {

    private static final double[] LOW_HOP_Y_POSITIONS = {
            MathUtil.round(0.4, 0.001),
            MathUtil.round(0.71, 0.001),
            MathUtil.round(0.75, 0.001),
            MathUtil.round(0.55, 0.001),
            MathUtil.round(0.41, 0.001)
    };

    private double moveSpeed, lastDistance;
    boolean shouldBoost;
    public boolean disable;
    private long timeOfFlag;
    int stage;
    private Timer turnBackOnDelay = new Timer();
    private EnumProperty<MODES> mode = new EnumProperty<MODES>("Mode", MODES.BHOP);
    private DoubleProperty timerAm = new DoubleProperty("Timer Amount", 1, 1, 2, 0.1, () -> mode.getValue() == MODES.BHOP);
    private final DoubleProperty randomizedTimerSpeed = new DoubleProperty("Randomized Timer Speed", 0.05, 0.01, 1.2, 0.01, this::shouldTimerBhop);
    private final DoubleProperty above2TimerSpeedTicks = new DoubleProperty("Above 2.0 Timer Ticks", 2, 1, 10, 1, () -> this.shouldTimerBhop() && timerAm.getValue().floatValue() >= 2.f);
    private final EnumProperty<Above2TimerTicksMode> above2TimerTicksModeEnumProperty = new EnumProperty<Above2TimerTicksMode>("A2T Ticks Mode",
            Above2TimerTicksMode.DOESNOTEQUAL0, () -> this.shouldTimerBhop() && timerAm.getValue().floatValue() >= 2.f);
    private final DoubleProperty above2TimerSpeedStartingTimerTicks = new DoubleProperty("A2TS Starting Ticks", 1, 1,
            9, 1, () -> this.shouldTimerBhop() && timerAm.getValue().floatValue() >= 2.f && above2TimerTicksModeEnumProperty.getValue() == Above2TimerTicksMode.DOESNOTEQUAL0);

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
    }

    private boolean shouldTimerBhop(){
        return mode.getValue() == MODES.BHOP && timerAm.getValue().floatValue() > 1.0f;
    }

    public void reset(){
        stage = 0;
        disable = false;
        mc.timer.timerSpeed = 1.0f;
        timeOfFlag = 0;
        moveSpeed = MovementUtil.getBaseMoveSpeed();
        shouldBoost = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.motionX *= mc.thePlayer.motionZ *= 0;
        reset();
    }

    @Override
    public void init() {
        this.addValueChangeListener(this.mode);
        super.init();
    }

    private enum Above2TimerTicksMode {
        EQUALS0("ticks == 0"),
        DOESNOTEQUAL0("ticks != 0");

        private final String name;

        Above2TimerTicksMode(String name){
            this.name = name;
        }

        @Override
        public String toString() {return name;}
    }

    private boolean simJumpShouldDoLowHop(final double baseMoveSpeedRef) {
        // Calculate the direction moved in
        final float direction = RotationUtil.calculateYawFromSrcToDst(mc.thePlayer.rotationYaw,
                mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosZ,
                mc.thePlayer.posX, mc.thePlayer.posZ);
        final Vec3 start = new Vec3(mc.thePlayer.posX,
                mc.thePlayer.posY + LOW_HOP_Y_POSITIONS[2],
                mc.thePlayer.posZ);
        // Cast a ray at waist height in the direction moved in for 10 blocks
        final MovingObjectPosition rayTrace = mc.theWorld.rayTraceBlocks(start,
                RotationUtil.getDstVec(start, direction, 0.0F, 5),
                false, true, true);
        // If did not hit anything just continue
        if (rayTrace == null) return true;
        if (rayTrace.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return true;
        if (rayTrace.hitVec == null) return true;

        // Check if player can fit above
        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox();
        if (mc.theWorld.checkBlockCollision(
                bb.offset(bb.minX - rayTrace.hitVec.xCoord,
                        bb.minY - rayTrace.hitVec.yCoord,
                        bb.minZ - rayTrace.hitVec.zCoord)))
            return false;

        // Distance to the block hit
        final double dist = start.distanceTo(rayTrace.hitVec);
        final double normalJumpDist = 4.0;
        return dist > normalJumpDist;
    }

    private double lowHopYModification(final double baseMotionY,
                                       final double yDistFromGround) {
        if (yDistFromGround == LOW_HOP_Y_POSITIONS[0]) {
            return 0.31;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[1]) {
            return 0.04;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[2]) {
            return -0.2;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[3]) {
            return -0.14;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[4]) {
            return -0.2;
        }

        return baseMotionY;
    }


    @EventHandler
    private final Listener<PlayerStrafeEvent> playerStrafeEventListener = event -> {
        if(disable) return;
        TargetStrafe targetStrafe = Pulsive.INSTANCE.getModuleManager().getModule(TargetStrafe.class);
        double baseMoveSpeed = MovementUtil.getBaseMoveSpeed();

        if(mc.thePlayer.isMoving() && targetStrafe.shouldStrafe()){
            Vec3 point = targetStrafe.currentPoint.getPoint();
            event.setYaw(RotationUtil.calculateYawFromSrcToDst(mc.thePlayer.rotationYaw, mc.thePlayer.posX, mc.thePlayer.posZ, point.xCoord, point.zCoord));
        }
        switch(mode.getValue()) {
            case BHOP: {
                if (mc.thePlayer.isMoving()) {
                    if(above2TimerSpeedStartingTimerTicks.getValue() >= above2TimerSpeedTicks.getValue()){
                        above2TimerSpeedStartingTimerTicks.setValue(above2TimerSpeedTicks.getValue() - 1);
                    }

                    if(!MovementUtil.isOnGround(1 / 64) && !mc.thePlayer.onGround && shouldTimerBhop()){
                        if(timerAm.getValue().floatValue() >= 2.f) {
                            switch(above2TimerTicksModeEnumProperty.getValue()){
                                case EQUALS0:{
                                    mc.timer.timerSpeed = mc.thePlayer.ticksExisted % above2TimerSpeedTicks.getValue() == 0 ?
                                            timerAm.getValue().floatValue() + getRandomTimer() : 1.6f + getRandomTimer();
                                    break;
                                }
                                case DOESNOTEQUAL0:{
                                    mc.timer.timerSpeed = mc.thePlayer.ticksExisted % above2TimerSpeedTicks.getValue() != above2TimerSpeedStartingTimerTicks.getValue() ?
                                            timerAm.getValue().floatValue() + getRandomTimer() : 1.6f + getRandomTimer();
                                    break;
                                }
                            }
                        }else{
                            mc.timer.timerSpeed = timerAm.getValue().floatValue() + MathUtil.randomFloat(-randomizedTimerSpeed.getValue().floatValue(), randomizedTimerSpeed.getValue().floatValue());
                        }
                    }

                    if (mc.thePlayer.onGround) {
                        shouldBoost = false;
                        mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42F);
                        stage = 0;
                    }
                    switch (stage) {
                        case 0: {
                            moveSpeed = baseMoveSpeed * (shouldBoost ? 1.73 : 2.145);
                            break;
                        }
                        case 1: {
                            moveSpeed *= 0.579;
                            break;
                        }
                        case 4: {
                            moveSpeed = baseMoveSpeed * 1.2;
                            break;
                        }
                        default: {
                            moveSpeed = moveSpeed / 100 * 98.5f;
                            break;
                        }
                    }
                    stage++;
                    event.setMotionPartialStrafe((float) ApacheMath.max(baseMoveSpeed, moveSpeed), (float) (0.235F + ApacheMath.random() / 500));
                } else {
                    event.setFriction(0);
                }
                break;
            }
            case ZONECRAFT:{
                PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(-1, C02PacketUseEntity.Action.ATTACK));
                if (MovementUtil.isMovingOnGround()) {
                    if (shouldBoost) {
                        mc.thePlayer.motionY = .42F;
                        shouldBoost = false;
                    } else {
                        shouldBoost = true;
                    }
                    moveSpeed += 0.525;
                } else if (mc.thePlayer.fallDistance == 0) {
                    mc.thePlayer.motionY = -.4804F;
                }
                moveSpeed = ApacheMath.min(moveSpeed, 6.5);
                moveSpeed *= 0.91;
                event.setMotion(ApacheMath.max(moveSpeed, applyPotion(0.2873)));
                if (!MovementUtil.isMoving() || mc.thePlayer.isCollidedHorizontally) {
                    moveSpeed = 0.26;
                }
                break;
            }
            case LOWHOP:
                boolean doInitialLowHop =
                                !mc.thePlayer.isPotionActive(Potion.jump) &&
                                !mc.thePlayer.isCollidedHorizontally &&
                                simJumpShouldDoLowHop(baseMoveSpeed);

                if (mc.thePlayer.isMoving()) {
                    if (!mc.thePlayer.onGround && shouldBoost && mc.thePlayer.fallDistance < 0.54)
                        mc.thePlayer.motionY = lowHopYModification(mc.thePlayer.motionY, MathUtil.round(mc.thePlayer.posY - (int) mc.thePlayer.posY, 0.001));

                    if (!MovementUtil.isOnGround(1 / 64) && timerAm.getValue() != 0) {
                        mc.timer.timerSpeed = timerAm.getValue().floatValue();
                    } else if (MovementUtil.isOnGround(1 / 64)) {
                        mc.timer.timerSpeed = 1.0f;
                    }

                    if (mc.thePlayer.onGround) {
                        shouldBoost = doInitialLowHop;
                        mc.thePlayer.motionY = shouldBoost ? MovementUtil.getJumpHeight(0.4F) : MovementUtil.getJumpHeight(0.42F);
                        stage = 0;
                    }
                    switch (stage) {
                        case 0: {
                            moveSpeed = baseMoveSpeed * (shouldBoost ? 1.7 : 2.145);
                            break;
                        }
                        case 1: {
                            moveSpeed *= 0.579;
                            break;
                        }
                        case 4: {
                            moveSpeed = baseMoveSpeed * 1.2;
                            break;
                        }
                        default: {
                            moveSpeed = moveSpeed / 100 * 98.5f;
                            break;
                        }
                    }
                    stage++;
                    event.setMotionPartialStrafe((float) ApacheMath.max(baseMoveSpeed, moveSpeed), (float) (0.235F + ApacheMath.random() / 500));
                } else {
                    event.setFriction(0);
                }
                break;
            case VANILLA:
                if(mc.thePlayer.isMoving()) {
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.42;
                    }
                    if(!MovementUtil.isOnGround(1 / 64 * 4)){
                        mc.thePlayer.motionY -= 0.22;
                    }
                    if(mc.thePlayer.isCollidedHorizontally){
                        mc.thePlayer.stepHeight = 2f;
                    }else{
                        mc.thePlayer.stepHeight = 0.5f;
                    }
                    event.setMotion(2 * MathUtil.randomDouble(0.93, 0.99));
                }
                break;
            case TEST:{
               // mc.timer.timerSpeed = 1.2f;
                double baseSpeed = MovementUtil.getBaseMoveSpeed();
                if(MovementUtil.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.42F;
                        this.moveSpeed = baseSpeed * 2.1;
                        this.shouldBoost = true;
//                        if(Pulsive.INSTANCE.getModuleManager().getModule(Aura.class).block){
//                            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//                        }
                        double[] xz = MovementUtil.yawPos(mc.thePlayer.getDirection(), -0.063);
                        for (int i = 0; i < 3; i++) {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - xz[0], mc.thePlayer.posY, mc.thePlayer.posZ - xz[1], true));
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                        }
                    } else if (this.shouldBoost) {
                        this.moveSpeed = baseSpeed * 1.15D;
                        this.shouldBoost = false;
                    } else if (mc.thePlayer.fallDistance < 0.9F) {
                        switch (this.stage) {
                            case 1:
                                this.moveSpeed = baseSpeed * 1.7D;
                                break;
                            case 2:
                                this.moveSpeed = baseSpeed * 1.45D;
                                break;
                            default:
                                this.moveSpeed = baseSpeed * 1.05D;
                                this.stage = 0;
                                break;
                        }
                        this.stage++;
                    } else {
                        this.moveSpeed *= 0.905D;
                    }

                    event.setMotion((float) moveSpeed);
                    if(mc.thePlayer.ticksExisted % 2 == 0) moveSpeed += 1e-7;
                }
                break;
            }
            case GHOSTLY:{
                stage++;
                if(MovementUtil.isMoving()) {
                    event.setMotion(stage % 3 == 0 ? 3 : MovementUtil.getBaseMoveSpeed());
                }
                break;
            }
        }
    };

    private double applyPotion(double base) {
        if (mc.thePlayer.isPotionActive(1))
            base *= 1 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        return base;
    }


    @EventHandler
    private final Listener<PlayerJumpEvent> playerJumpEventListener = event -> {
        if(isToggled() && !disable) event.setCancelled(true);
    };

    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        final ScaledResolution scaledResolution = event.getScaledResolution();

        if (disable) {
            final long currentMillis = System.currentTimeMillis();
            final long timeSinceFlagged = currentMillis - timeOfFlag;

            if (timeSinceFlagged < 2500) {
                final double xRegionBuffer = 4;
                final double yRegionBuffer = 2;

                final String text = String.format("Re-enabling in %.1f seconds", (float) (2500 - timeSinceFlagged) / 1000L);

                final double width = mc.fontRendererObj.getStringWidth(text) + xRegionBuffer * 2;
                final double height = 20 + yRegionBuffer * 2;

                final double barHeight = height - yRegionBuffer * 3 - 12;

                final double centrePosX = scaledResolution.getScaledWidth() / 2.0;
                final double centrePosY = scaledResolution.getScaledHeight() / 2.0 + 100;

                final double progress = (double) timeSinceFlagged / 2500;

                final double leftBackground = centrePosX - width / 2.0;
                final double rightBackground = centrePosX + width / 2.0;

                StencilUtil.initStencilToWrite();
                RoundedUtil.drawRoundedRect((float) leftBackground, (float) centrePosY, (float) ((float) leftBackground + width), (float) ((float) centrePosY + height), 0,-1);
                StencilUtil.readStencilBuffer(1);
                Blur.renderBlur(15);
                StencilUtil.uninitStencilBuffer();

                RoundedUtil.drawRoundedRect((float) leftBackground, (float) centrePosY, (float) ((float) leftBackground + width), (float) ((float) centrePosY + height), 0,0x80000000);

                RenderUtil.glDrawGradientLine(leftBackground, centrePosY, rightBackground, centrePosY,
                        1, HUD.getColor());


                final double left = centrePosX - (width - xRegionBuffer * 2) / 2.0;
                final double top = centrePosY + height - barHeight - yRegionBuffer;

                RoundedUtil.drawRoundedRect((float) left, (float) top,
                        (float) ((float) left + (width - xRegionBuffer * 2)),
                        (float) ((float) top + barHeight),
                        0,0x80000000);

                RoundedUtil.drawRoundedRect((float) left, (float) top,
                        (float) (left + (width - xRegionBuffer * 2) * progress),
                        (float) (top + barHeight),
                        0, HUD.getColor());

                mc.fontRendererObj.drawStringWithShadow(text, (float) left, (float) centrePosY + 4, 0xFFFFFFFF);
            }
        }
    };

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
      if(event.getEventState() == PacketEvent.EventState.RECEIVING){
          if (event.getPacket() instanceof S08PacketPlayerPosLook && !disable) {
              timeOfFlag = System.currentTimeMillis();
              mc.timer.timerSpeed = 1.0f;
              disable = true;

              turnBackOnDelay.purge();

              turnBackOnDelay.schedule(new TimerTask() {
                  @Override
                  public void run() {
                      reset();
                  }
              }, 2500);
          }
      }
    };


    @EventHandler
    private final Listener<PlayerMotionEvent> playerUpdateEvent = event -> {
        if(disable) return;
        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        this.lastDistance = ApacheMath.hypot(x, z);

        if(mode.getValue() == MODES.VULCAN){
            if (!event.isPre()) {
                return;
            }
            if (MovementUtil.isMovingOnGround()) {
                double speed = ApacheMath.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
                boolean boost = mc.thePlayer.isPotionActive(1);
                switch (stage) {
                    case 1:
                        moveSpeed = 0.42f;
                        speed = boost ? speed + 0.2 : 0.48;
                        event.setGround(true);
                        break;
                    case 2:
                        speed = boost ? speed * 0.71  : 0.19;
                        moveSpeed -= 0.0784f;
                        event.setGround(false);
                        break;
                    default:
                        stage = 0;
                        speed /= boost ? 0.64 : 0.66;
                        event.setGround(true);
                        break;
                }
                MovementUtil.setSpeed(speed);
                stage++;
                event.setPosY(event.getPosY() + moveSpeed);
            } else {
                stage = 0;
            }
        }
    };

    @AllArgsConstructor
    private enum MODES {
        BHOP("Bhop"),
        LOWHOP("LowHop"),
        VANILLA("Vanilla"),
        TEST("VL Abuse"),
        ZONECRAFT("Zonecraft"),
        VULCAN("Vulcan"),
        GHOSTLY("Ghostly");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }

    public float getRandomTimer(){
        return MathUtil.randomFloat(-randomizedTimerSpeed.getValue().floatValue(), randomizedTimerSpeed.getValue().floatValue());
    }

}
