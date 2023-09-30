package dev.africa.pandaware.impl.module.combat;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.ServerJoinEvent;
import dev.africa.pandaware.impl.event.player.*;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.module.movement.ScaffoldModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberRangeSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.client.ServerUtils;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import dev.africa.pandaware.utils.player.PlayerUtils;
import dev.africa.pandaware.utils.player.RotationUtils;
import dev.africa.pandaware.utils.render.ColorUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@ModuleInfo(name = "Kill Aura", description = "Attacks small children because no one likes them", category = Category.COMBAT)
public class KillAuraModule extends Module {
    private final TimeHelper timer = new TimeHelper();
    private final List<EntityLivingBase> entities = new ArrayList<>();
    private final MouseFilter filterX = new MouseFilter();
    private final MouseFilter filterY = new MouseFilter();
    private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
    private final EnumSetting<TargetMode> targetMode = new EnumSetting<>("Target Mode", TargetMode.SINGLE);
    private final EnumSetting<SortingMode> sortingMode = new EnumSetting<>("Sorting Mode", SortingMode.DISTANCE);
    private final EnumSetting<AimMode> aimMode
            = new EnumSetting<>("Aim Mode", AimMode.NORMAL, this.rotate::getValue);
    private final EnumSetting<ClickMode> clickMode
            = new EnumSetting<>("Click Mode", ClickMode.SECURE_RANDOM);
    private final EnumSetting<RangeCalculation> rangeCalculationMode
            = new EnumSetting<>("Range Calculation Mode", RangeCalculation.RAYTRACE);
    private final EnumSetting<RenderEvent.Type> rotationEvent
            = new EnumSetting<>("Rotation Event Type", RenderEvent.Type.FRAME, this.rotate::getValue);
    private final EnumSetting<Event.EventState> eventType
            = new EnumSetting<>("Event Type", Event.EventState.PRE);
    private final EnumSetting<RotationUtils.RotationAt> lookAt
            = new EnumSetting<>("Look At", RotationUtils.RotationAt.CHEST, this.rotate::getValue);
    private final BooleanSetting autoBlock = new BooleanSetting("Auto Block", false);
    private final EnumSetting<AutoBlockMode> autoBlockMode
            = new EnumSetting<>("Auto Block Mode", AutoBlockMode.NORMAL, this.autoBlock::getValue);
    private final EnumSetting<Event.EventState> blockState
            = new EnumSetting<>("Block State", Event.EventState.POST, this.autoBlock::getValue);
    private final EnumSetting<Event.EventState> unblockState
            = new EnumSetting<>("Unblock State", Event.EventState.PRE, this.autoBlock::getValue);
    private final EnumSetting<Strafemode> strafeMode
            = new EnumSetting<>("Strafe Mode", Strafemode.NONE);
    private final BooleanSetting cinematic = new BooleanSetting("Cinematic", false, this.rotate::getValue);
    private final NumberSetting cinematicSpeed = new NumberSetting("Cinematic Speed",
            1, 0, 0.05f, 0.01f, this.cinematic::getValue);
    private final BooleanSetting cinematicFilterAfterRotation
            = new BooleanSetting("Cinematic Filter After Rotation", false,
            () -> this.cinematic.getValue() && this.rotationEvent.getValue() == RenderEvent.Type.RENDER_3D && this.rotate.getValue());
    private final NumberSetting range =
            new NumberSetting("Range", 6, 0.1, 4.5, 0.05);
    private final NumberRangeSetting aps =
            new NumberRangeSetting("APS", 20, 0.5, 9, 11);
    private final NumberSetting switchSpeed = new NumberSetting("Switch Speed",
            20, 1, 3, 1, () -> this.targetMode.getValue() == TargetMode.SWITCH);
    private final NumberSetting attackAngle = new NumberSetting("Attack Angle",
            180, 1, 180, 1, this.rotate::getValue);
    private final NumberSetting hitChance = new NumberSetting("Hit Chance",
            100, 0, 100, 1);
    private final BooleanSetting middleRotation
            = new BooleanSetting("Middle Rotation", true, this.rotate::getValue);
    private final BooleanSetting randomizeAimPoint
            = new BooleanSetting("Randomize Aim Point", false, this.rotate::getValue);
    private final BooleanSetting gcd = new BooleanSetting("GCD", true, this.rotate::getValue);
    private final EnumSetting<GCDMode> gcdMode = new EnumSetting<>("GCD Mode", GCDMode.NORMAL,
            () -> this.gcd.getValue() && this.rotate.getValue());
    private final BooleanSetting lockView
            = new BooleanSetting("LockView", false, this.rotate::getValue);
    private final BooleanSetting keepSprint
            = new BooleanSetting("Keep Sprint", true);
    private final BooleanSetting onlySprintInAir = new BooleanSetting("Only in air", false,
            this.keepSprint::getValue);
    private final BooleanSetting antiSnap
            = new BooleanSetting("Anti snap", false, this.rotate::getValue);
    private final BooleanSetting swing
            = new BooleanSetting("Swing", true);
    private final BooleanSetting sprint
            = new BooleanSetting("Sprint", true);
    private final BooleanSetting players
            = new BooleanSetting("Players", true);
    private final BooleanSetting invisibles
            = new BooleanSetting("Invisibles", true);
    private final BooleanSetting mobs
            = new BooleanSetting("Mobs", false);
    private final BooleanSetting teams
            = new BooleanSetting("Teams", false,
            this.players::getValue);
    private final BooleanSetting returnOnScaffold = new BooleanSetting("Return on Scaffold", false);
    private final BooleanSetting esp = new BooleanSetting("ESP", true).setSaveConfig(false);
    private final BooleanSetting unblockOnAttack = new BooleanSetting("Unblock on attack", false, this.autoBlock::getValue);
    private final BooleanSetting rayCast = new BooleanSetting("Raycast", false);
    private final BooleanSetting rayTrace = new BooleanSetting("Raytrace", false);
    private final BooleanSetting vulcant = new BooleanSetting("Funny Packet", false);

    private final TimeHelper clickTimer = new TimeHelper();
    private final SecureRandom random = new SecureRandom();
    public double ticks = 0;
    public long lastFrame = 0;
    private int backRotationTicks;
    public EntityLivingBase target;
    private int attacks;
    private boolean sentMagicPacket;

    public KillAuraModule() {
        this.registerSettings(
                this.targetMode,
                this.sortingMode,
                this.aimMode,
                this.clickMode,
                this.rangeCalculationMode,
                this.rotationEvent,
                this.eventType,
                this.lookAt,
                this.gcdMode,
                this.autoBlockMode,
                this.blockState,
                this.unblockState,
                this.strafeMode,
                this.range,
                this.aps,
                this.cinematicSpeed,
                this.switchSpeed,
                this.attackAngle,
                this.hitChance,
                this.autoBlock,
                this.unblockOnAttack,
                this.rotate,
                this.antiSnap,
                this.vulcant,
                this.returnOnScaffold,
                this.middleRotation,
                this.randomizeAimPoint,
                this.gcd,
                this.cinematic,
                this.cinematicFilterAfterRotation,
                this.lockView,
                this.keepSprint,
                this.onlySprintInAir,
                this.rayCast,
                this.rayTrace,
                this.swing,
                this.sprint,
                this.players,
                this.invisibles,
                this.mobs,
                this.teams,
                this.esp
        );
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (this.target != null && !this.sprint.getValue()) {
            mc.thePlayer.setSprinting(false);
            mc.gameSettings.keyBindSprint.pressed = false;
        }
    };
    private int arrayIndex;
    private Vec2f rotationVector = new Vec2f(0, 0);
    @EventHandler
    EventCallback<StrafeEvent> onStrafe = event -> {
        if (this.target != null && this.rotationVector != null && this.rotate.getValue() && mc.thePlayer != null) {
            switch (this.strafeMode.getValue()) {
                case STRICT:
                    event.setYaw(this.rotationVector.getX());
                    break;
                case SILENT:
                    this.silentLegitMovement(event, this.rotationVector.getX());
                    break;
            }
        }
    };
    @EventHandler
    EventCallback<JumpEvent> onJump = event -> {
        if (this.target != null && this.rotationVector != null && this.rotate.getValue() && mc.thePlayer != null) {
            if (this.strafeMode.getValue() == Strafemode.STRICT) {
                event.setYaw(this.rotationVector.getX());
            }
        }
    };
    private Vec2f lastRotation = new Vec2f(0, 0);
    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = event.getPacket();

            if (packet.getRotating()) {
                this.lastRotation = new Vec2f(packet.getYaw(), packet.getPitch());
            }
        }
    };
    private float smoothCamYaw;
    private float smoothCamPitch;
    private float smoothCamPartialTicks;
    private float smoothCamFilterX;
    private float smoothCamFilterY;
    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == this.rotationEvent.getValue()) {
            if (this.target != null && this.rotate.getValue()) {
                this.rotationVector = this.generateRotations(event);

                if (this.cinematicFilterAfterRotation.getValue()) {
                    if (this.cinematic.getValue()) {
                        float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                        float gcd = f * f * f * 8.0F;

                        this.smoothCamFilterX = this.filterX.smooth(this.smoothCamYaw,
                                cinematicSpeed.getValue().floatValue() * gcd);
                        this.smoothCamFilterY = this.filterY.smooth(this.smoothCamPitch,
                                cinematicSpeed.getValue().floatValue() * gcd);
                        this.smoothCamPartialTicks = 0.0F;
                        this.smoothCamYaw = 0.0F;
                        this.smoothCamPitch = 0.0F;
                    } else {
                        this.smoothCamFilterX = 0.0F;
                        this.smoothCamFilterY = 0.0F;
                        this.filterX.reset();
                        this.filterY.reset();
                    }
                }
            }
        }

        if (event.getType() == RenderEvent.Type.RENDER_3D) {
            if (this.target == null) {
                return;
            }

            if (esp.getValue()) {
                GlStateManager.pushMatrix();
                if (this.targetMode.getValue() == TargetMode.MULTI) {
                    this.entities.forEach(target -> {
                        this.drawCircle(target, target.width, false);
                        this.drawCircle(target, target.width, true);
                    });
                } else {
                    this.drawCircle(this.target, this.target.width, false);
                    this.drawCircle(this.target, this.target.width, true);
                }
                GlStateManager.popMatrix();
            }
        }
    };

    private int blockCount;
    private double increaseClicks;
    private int nextClickTime;
    private long nextLUp;
    private long nextLDown;
    private long nextDrop;
    private long nextExhaust;
    private double dropRate;
    private boolean dropping;
    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.returnOnScaffold.getValue() && Client.getInstance().getModuleManager()
                .getByClass(ScaffoldModule.class).getData().isEnabled()) return;
        if (event.getEventState() == Event.EventState.PRE) {
            this.target = this.getTarget(this.antiSnap.getValue() ? this.range.getValue().floatValue() + 1 : this.range.getValue().floatValue());

            if (this.target != null && RotationUtils.getYawRotationDifference(this.target) >
                    this.attackAngle.getValue().doubleValue()) {
                this.target = null;
            }
        }

        if (this.target == null) {
            if (this.antiSnap.getValue() && this.rotationVector != null) {
                if (this.lockView.getValue()) {
                    mc.thePlayer.rotationYaw = this.rotationVector.getX();
                    mc.thePlayer.rotationPitch = this.rotationVector.getY();
                } else {
                    event.setYaw(this.rotationVector.getX());
                    event.setPitch(this.rotationVector.getY());
                }

                if (this.backRotationTicks > 10) {
                    this.rotationVector = null;
                }

                this.backRotationTicks++;
            } else {
                this.rotationVector = null;
            }

            this.sentMagicPacket = false;
            this.clickTimer.reset();
            this.attacks = 0;
            this.increaseClicks = 0;
            this.nextClickTime = 0;
            this.blockCount = 1;

            if (mc.thePlayer.isBlockingSword() || mc.thePlayer.isAnimateBlocking()) {
                unblock();
                mc.thePlayer.setBlockingSword(false);
                mc.thePlayer.setAnimateBlocking(false);
            }
        } else {
            this.backRotationTicks = 0;

            if (this.rotationVector != null || !this.rotate.getValue()) {
                if (this.rotate.getValue()) {
                    if (this.lockView.getValue()) {
                        mc.thePlayer.rotationYaw = this.rotationVector.getX();
                        mc.thePlayer.rotationPitch = this.rotationVector.getY();
                    } else {
                        event.setYaw(this.rotationVector.getX());
                        event.setPitch(this.rotationVector.getY());
                    }
                }
                if (event.getEventState() == this.eventType.getValue()) {
                    if (this.shouldClickMouse()) {
                        if (!this.sentMagicPacket && this.vulcant.getValue()) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,
                                    this.rotationVector.getX() == 0 ? mc.thePlayer.rotationYaw : this.rotationVector.getX(),
                                    this.rotationVector.getY() == 0 ? mc.thePlayer.rotationPitch : this.rotationVector.getY(),
                                    mc.thePlayer.onGround
                            ));

                            this.sentMagicPacket = true;
                        }
                        if (mc.thePlayer.getDistanceToEntity(this.target) <= this.range.getValue().floatValue()) {
                            EntityLivingBase rayCastTarget = this.target;

                            if (this.rayCast.getValue()) {
                                Vec2f rayCastRotations = RotationUtils.getRotations(rayCastTarget);

                                float yaw = this.rotationVector != null ? this.rotationVector.getX() :
                                        rayCastRotations != null ? rayCastRotations.getX() : mc.thePlayer.rotationYaw;

                                float pitch = this.rotationVector != null ? this.rotationVector.getY() :
                                        rayCastRotations != null ? rayCastRotations.getY() : mc.thePlayer.rotationPitch;

                                EntityLivingBase rayCasted = PlayerUtils.rayCast(this.range.getValue().doubleValue(),
                                        yaw, pitch
                                );

                                if (rayCasted instanceof EntityMob || rayCasted instanceof EntityPlayer) {
                                    rayCastTarget = rayCasted;
                                }
                            }
                            this.attack(rayCastTarget);

                            mc.thePlayer.resetCooldown();
                            mc.leftClickCounter = 0;
                            this.timer.reset();
                        }
                    }
                }
                if (this.autoBlock.getValue()) {
                    if (event.getEventState() == this.blockState.getValue()) {
                        this.block();
                    }
                }
            }
        }

        if (this.autoBlock.getValue() && event.getEventState() == this.unblockState.getValue() && this.target != null) {
            this.unblock();
        }
    };

    @EventHandler
    EventCallback<ServerJoinEvent> onJoin = event -> this.toggle(false);

    @Override
    public void onEnable() {
        super.onEnable();

        if (this.vulcant.getValue()) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround
            ));
            this.sentMagicPacket = true;
        }

        this.attacks = 0;
        this.blockCount = 0;
        this.target = null;
        this.filterX.reset();
        this.filterY.reset();
        this.rotationVector = null;
        this.arrayIndex = 0;
        this.increaseClicks = 0;
        this.nextClickTime = 0;
        this.nextLUp = 0;
        this.nextLDown = 0;
        this.nextDrop = 0;
        this.nextExhaust = 0;
        this.dropRate = 0;
        this.dropping = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.vulcant.getValue()) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw,
                    mc.thePlayer.rotationPitch, mc.thePlayer.onGround
            ));
        }

        this.sentMagicPacket = false;
        this.blockCount = 0;
        this.target = null;
        this.filterX.reset();
        this.filterY.reset();
        this.arrayIndex = 0;
        this.increaseClicks = 0;
        this.nextClickTime = 0;
        this.nextLUp = 0;
        this.nextLDown = 0;
        this.nextDrop = 0;
        this.nextExhaust = 0;
        this.dropRate = 0;
        this.dropping = false;

        if (!this.antiSnap.getValue()) {
            this.rotationVector = null;
        }

        mc.thePlayer.setAnimateBlocking(false);
        if (mc.thePlayer.isBlockingSword()) {
            mc.playerController.onStoppedUsingItem(mc.thePlayer);
            mc.gameSettings.keyBindUseItem.pressed = false;
            mc.thePlayer.setBlockingSword(false);
        }
    }

    private Vec2f generateRotations(RenderEvent event) {
        if (this.target == null || mc.thePlayer == null) return new Vec2f(0, 0);

        RotationUtils.RotationAt rotationAt = (this.randomizeAimPoint.getValue() ?
                RotationUtils.RotationAt.values()
                        [RandomUtils.nextInt(0, RotationUtils.RotationAt.values().length - 1)] :
                this.lookAt.getValue());
        Vec2f newRotation = (this.middleRotation.getValue() ?
                RotationUtils.getMiddlePointRotations(this.target, rotationAt) :
                RotationUtils.getRotations(this.target, rotationAt));

        float sensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
        float f = sensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 1.2F;

        float deltaYaw = MathHelper.wrapAngleTo180_float(newRotation.getX() - this.lastRotation.getX());
        float deltaPitch = newRotation.getY() - this.lastRotation.getY();

        //Apply the GCD Fix if enabled
        if (this.gcd.getValue()) {
            switch (this.gcdMode.getValue()) {
                case NORMAL: {
                    newRotation.setX(newRotation.getX() - (deltaYaw % f1));
                    newRotation.setY(newRotation.getY() - (deltaPitch % f1));
                    break;
                }
                case MODULO: {
                    double fixedGcdDivision = (ThreadLocalRandom.current().nextBoolean() ? 45 : 90);
                    double fixedGcd = f1 / fixedGcdDivision + f1;

                    newRotation.setX((float) (newRotation.getX() - ApacheMath.floor(newRotation.getX()) % fixedGcd));
                    newRotation.setY((float) (newRotation.getY() - ApacheMath.floor(newRotation.getY()) % fixedGcd));
                    break;
                }
                case MODULO1: {
                    newRotation.setX(newRotation.getX() - ((newRotation.getX() % f1) - f));
                    newRotation.setY(newRotation.getY() - ((newRotation.getY() % f1) - f));
                    break;
                }
                case HI: {

                    int deltaX = ApacheMath.round(deltaYaw / f1);
                    int deltaY = ApacheMath.round(deltaPitch / f1);

                    float clampedX = deltaX * f1;
                    float clampedY = deltaY * f1;

                    clampedX -= clampedX % f1;
                    clampedY -= clampedY % f1;

                    float f2 = f * f * f * 8.0F;

                    clampedX += f2;
                    clampedY += f2;

                    newRotation.setX(this.lastRotation.getX() + clampedX);
                    newRotation.setY(MathHelper.clamp_float(this.lastRotation.getY() + clampedY, -90, 90));
                    break;
                }
                case GREEK: {

                    int deltaX = ApacheMath.round(deltaYaw / f1);
                    int deltaY = ApacheMath.round(deltaPitch / f1);

                    float f2 = (float) deltaX * f1;
                    float f3 = (float) deltaY * f1;

                    newRotation.setX((this.lastRotation.getX() + f2) + f1);
                    newRotation.setY((this.lastRotation.getY() + f3) + f1);
                    break;
                }
                case GREEK_SMOOTH:

                    int deltaX = ApacheMath.round(deltaYaw / f1);
                    int deltaY = ApacheMath.round(deltaPitch / f1);

                    float smoothedF2 = (deltaX * f1) / 3;
                    float smoothedF3 = (deltaY * f1) / 3;

                    newRotation.setX((this.lastRotation.getX() + smoothedF2) + f1);
                    newRotation.setY((this.lastRotation.getY() + smoothedF3) + f1);

                    break;
            }
        }

        //Apply cinematic if enabled
        if (this.cinematic.getValue()) {
            if (!this.cinematicFilterAfterRotation.getValue()) {
                this.smoothCamFilterX = this.filterX.smooth(this.smoothCamYaw,
                        this.cinematicSpeed.getValue().floatValue() * f1);
                this.smoothCamFilterY = this.filterY.smooth(this.smoothCamPitch,
                        this.cinematicSpeed.getValue().floatValue() * f1);
                this.smoothCamPartialTicks = 0.0F;
                this.smoothCamYaw = 0.0F;
                this.smoothCamPitch = 0.0F;
            }

            this.smoothCamYaw += newRotation.getX();
            this.smoothCamPitch += newRotation.getY();

            float smoothedPartialTicks = event.getPartialTicks() - this.smoothCamPartialTicks;

            this.smoothCamPartialTicks = event.getPartialTicks();

            newRotation.setX(this.smoothCamFilterX * smoothedPartialTicks);
            newRotation.setY(this.smoothCamFilterY * smoothedPartialTicks);
        } else {
            if (!this.cinematicFilterAfterRotation.getValue()) {
                this.smoothCamFilterX = 0.0F;
                this.smoothCamFilterY = 0.0F;
                this.filterX.reset();
                this.filterY.reset();
            }

            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
        }

        //Now that we've applied all the above, We can finally apply the rotation mode.
        switch (this.aimMode.getValue()) {
            case NORMAL: {
                newRotation.setX(newRotation.getX());
                newRotation.setY(newRotation.getY());
                break;
            }
            case RANDOMIZE: {
                newRotation.setX(newRotation.getX() + RandomUtils.nextFloat(-4, 4));
                newRotation.setY(newRotation.getY() + RandomUtils.nextFloat(-2, 2));
                break;
            }
            case LOCK: {

                /*
                This should be done properly by using the Hitbox
                Instead of a fixed time.
                 */

                boolean update = mc.thePlayer.ticksExisted % 5 == 0;

                //Grab a new rotation if we should update, otherwise return the old one
                newRotation.setX(update ? newRotation.getX() : this.lastRotation.getX());
                newRotation.setY(update ? newRotation.getY() : this.lastRotation.getY());

                break;
            }
            case ROUND: {
                newRotation.setX(ApacheMath.round(newRotation.getX()));
                newRotation.setY(ApacheMath.round(newRotation.getY()));
                break;
            }
            case MATH_RANDOM: {
                newRotation.setX((float) ((newRotation.getX() * ApacheMath.random()) + RandomUtils.nextFloat(-180f, 180f)));
                newRotation.setY((float) ((newRotation.getY() * ApacheMath.random()) + RandomUtils.nextFloat(-90f, 90f)));
                break;
            }
            case ATANH: {
                newRotation.setX((float) ApacheMath.atanh(newRotation.getX()));
                newRotation.setY((float) ApacheMath.atanh(newRotation.getY()));
                break;
            }
        }

        //Finally clamp the pitch to make sure we didn't mess up.
        newRotation.setY(MathHelper.clamp_float(newRotation.getY(), -90, 90));

        return newRotation;
    }

    public void drawCircle(Entity entity, double rad, boolean shade) {
        ticks += .004 * (System.currentTimeMillis() - lastFrame);

        lastFrame = System.currentTimeMillis();

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
                * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosX;
        double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
                * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosY) + ApacheMath.sin(ticks) + 1;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
                * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosZ;

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        Color color = Color.WHITE;
        double TAU = ApacheMath.PI * 2.D;
        for (float i = 0; i < TAU; i += TAU / 64.F) {

            double vecX = x + rad * ApacheMath.cos(i);
            double vecZ = z + rad * ApacheMath.sin(i);

            color = ColorUtils.getColorSwitch(
                    UISettings.FIRST_COLOR,
                    UISettings.SECOND_COLOR,
                    3000, (int) i * 60, 55, 4
            );

            if (shade) {
                ColorUtils.glColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));

                GL11.glVertex3d(vecX, y - ApacheMath.sin(ticks + 1) / 2.7f, vecZ);
            }

            ColorUtils.glColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));

            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();

        GlStateManager.pushAttribAndMatrix();
        GL11.glPushMatrix();
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glLineWidth(2);
        ColorUtils.glColor(color);
        GL11.glBegin(1);
        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex3d(x + rad * ApacheMath.cos((double) i * (ApacheMath.PI * 2) / 45), y, z + rad * ApacheMath.sin((double) i * (ApacheMath.PI * 2) / 45));
        }
        ColorUtils.glColor(Color.WHITE);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        mc.entityRenderer.disableLightmap();
        GlStateManager.disableLighting();
        GlStateManager.popAttribAndMatrix();
    }

    private void block() {
        boolean swordBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword &&
                !Client.getInstance().getModuleManager().getByClass(ScaffoldModule.class).getData().isEnabled();
        if (this.unblockOnAttack.getValue() && attacks <= 1) return;
        if (swordBlock && !mc.thePlayer.isBlockingSword()) {
            switch (this.autoBlockMode.getValue()) {
                case FAKE:
                    mc.thePlayer.setAnimateBlocking(true);
                    break;

                case HYPIXEL:
                    if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) &&
                            !ServerUtils.compromised) {
                        if (mc.thePlayer.swingProgressInt == -1) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
                                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        } else if (mc.thePlayer.swingProgressInt == -0.8) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                                    new BlockPos(-1, -1, -1), 1, mc.thePlayer.inventory.getCurrentItem(),
                                    0.1f, 0.1f, 0.1f
                            ));
                        } else if (mc.thePlayer.swingProgressInt < 0.8 && mc.thePlayer.swingProgressInt > -0.7) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement
                                    (new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(),
                                            0.0081284124F, 0.00004921712F, 0.0081248912F));
                        }
                    }
                    mc.thePlayer.setBlockingSword(true);
                    break;

                case NORMAL:
                    if (!mc.thePlayer.isBlockingSword() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                        mc.thePlayer.sendQueue.getNetworkManager()
                                .sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1),
                                        255, mc.thePlayer.inventory.getCurrentItem(),
                                        0.0F, 0.0F, 0.0F));

                        mc.thePlayer.setBlockingSword(true);
                    }
                    break;

                case SYNC:
                    if (!mc.thePlayer.isBlockingSword() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                        mc.playerController.syncCurrentPlayItem();
                        mc.thePlayer.sendQueue
                                .addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));

                        mc.thePlayer.setBlockingSword(true);
                    }

                    this.blockCount++;
                    break;

                case VANILLA:
                    if (!mc.thePlayer.isBlockingSword() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                        mc.playerController.syncCurrentPlayItem();

                        mc.thePlayer.setBlockingSword(true);
                        mc.gameSettings.keyBindUseItem.pressed = true;
                    }
                    break;
            }
        } else {
            mc.thePlayer.setAnimateBlocking(true);
            mc.thePlayer.setBlockingSword(false);
        }
    }

    private void unblock() {
        boolean swordBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        if (swordBlock) {
            switch (this.autoBlockMode.getValue()) {
                case FAKE:
                    mc.thePlayer.setAnimateBlocking(false);
                    break;

                case HYPIXEL:
                    if (mc.thePlayer.isBlockingSword() && this.getTarget() == null) {
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new
                                C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                    mc.thePlayer.setBlockingSword(false);
                    break;

                case NORMAL:
                    if (mc.thePlayer.isBlockingSword()) {
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new
                                C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                        mc.thePlayer.setBlockingSword(false);
                    }
                    break;

                case VANILLA:
                    mc.playerController.syncCurrentPlayItem();
                    mc.gameSettings.keyBindUseItem.pressed = false;

                    mc.thePlayer.setBlockingSword(false);
                    break;

                case SYNC:
                    if (mc.thePlayer.isBlockingSword() && this.blockCount > 2) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging
                                .Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        mc.thePlayer.setBlockingSword(false);

                        this.blockCount = 0;
                        break;
                    }
            }
        } else {
            mc.thePlayer.setAnimateBlocking(true);
            mc.thePlayer.setBlockingSword(false);
        }
    }

    private void attack(EntityLivingBase entity) {
        boolean swordBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        if (this.autoBlockMode.getValue() == AutoBlockMode.SYNC && this.autoBlock.getValue() && !(swordBlock)) {
            mc.thePlayer.setBlockingSword(false);
            this.blockCount = 0;
        }

        if (!this.autoBlock.getValue() || this.blockCount > RandomUtils.nextInt(1, 2)
                || this.autoBlockMode.getValue() != AutoBlockMode.SYNC || !(swordBlock)) {
            if (this.targetMode.getValue() == TargetMode.MULTI) {
                this.entities.forEach(this::doCallEventAndAttack);
            } else {
                this.doCallEventAndAttack(entity);
            }
        }
    }

    private void doCallEventAndAttack(Entity entity) {
        AttackEvent attackEvent = new AttackEvent(entity, Event.EventState.PRE);
        Client.getInstance().getEventDispatcher().dispatch(attackEvent);

        attacks++;
        if (this.onlySprintInAir.getValue() && !PlayerUtils.isMathGround()) {
            PlayerUtils.attackEntityProtocol(entity, this.swing.getValue(), true);
        } else if (this.onlySprintInAir.getValue() && mc.thePlayer.onGround) {
            PlayerUtils.attackEntityProtocol(entity, this.swing.getValue(), false);
        } else {
            PlayerUtils.attackEntityProtocol(entity, this.swing.getValue(), this.keepSprint.getValue());
        }

        attackEvent.setEventState(Event.EventState.POST);
        Client.getInstance().getEventDispatcher().dispatch(attackEvent);
    }

    private EntityLivingBase getTarget(float distance) {
        this.entities.clear();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (this.isValid(entity)) {
                boolean hittable = true;
                if (this.rangeCalculationMode.getValue() == RangeCalculation.RAYTRACE) {
                    AxisAlignedBB targetBox = entity.getEntityBoundingBox();
                    Vec2f rotation = RotationUtils.getRotations((EntityLivingBase) entity);
                    Vec3 origin = mc.thePlayer.getPositionEyes(1.0f);
                    Vec3 look = entity.getVectorForRotation(rotation.getY(), rotation.getX());

                    look = origin.addVector(
                            look.xCoord * distance,
                            look.yCoord * distance,
                            look.zCoord * distance
                    );

                    MovingObjectPosition collision = targetBox.calculateIntercept(origin, look);

                    if (collision == null) {
                        hittable = false;
                    }
                } else {
                    if (mc.thePlayer.getDistanceToEntity(entity) >= distance) {
                        hittable = false;
                    }
                }

                if (hittable) {
                    this.entities.add((EntityLivingBase) entity);
                }
            }
        }
        this.entities.sort(Comparator.comparing(entityLivingBase -> {
            switch (this.sortingMode.getValue()) {
                case DISTANCE:
                    return mc.thePlayer.getDistanceToEntity(entityLivingBase);
                case HEALTH:
                    return entityLivingBase.getHealth();
                default:
                    return (float) RotationUtils.getRotationDifference(entityLivingBase);
            }
        }));

        if (mc.thePlayer.ticksExisted % this.switchSpeed.getValue().intValue() == 0) {
            this.arrayIndex++;
        }
        if (this.arrayIndex >= this.entities.size()) {
            this.arrayIndex = 0;
        }
        int finalIndex = this.targetMode.getValue() == TargetMode.SWITCH
                ? MathHelper.clamp_int(this.arrayIndex, 0, this.entities.size()) : 0;

        return this.entities.size() > 0 ? this.entities.get(finalIndex) : null;
    }

    private boolean isValid(Entity entity) {
        boolean valid = entity instanceof EntityLivingBase;

        if (entity != null) {
            if (entity == mc.thePlayer) {
                valid = false;
            }

            if (this.rayTrace.getValue() && !mc.thePlayer.canEntityBeSeen(entity)) {
                valid = false;
            }

            if (entity instanceof EntityPlayer) {
                if (!this.players.getValue() ||
                        Client.getInstance().getIgnoreManager().isIgnoreBoth((EntityPlayer) entity) ||
                        (this.teams.getValue() && PlayerUtils.isTeam((EntityPlayer) entity))) {
                    valid = false;
                }
            }

            if (!entity.isEntityAlive() || entity.isDead) {
                valid = false;
            }

            if (entity.isInvisible() && !this.invisibles.getValue()) {
                valid = false;
            }

            if (!(entity instanceof EntityPlayer) && !this.mobs.getValue()) {
                valid = false;
            }

            if (entity instanceof EntityArmorStand) {
                valid = false;
            }
        }

        return valid;
    }

    private boolean shouldClickMouse() {
        switch (this.clickMode.getValue()) {
            case RANDOM: {
                double time = RandomUtils.nextDouble(this.aps.getFirstValue().doubleValue(),
                        this.aps.getSecondValue().doubleValue());

                return this.timer.reach((float) (1000L / time));
            }

            case SECURE_RANDOM: {
                double min = this.aps.getFirstValue().doubleValue();
                double max = this.aps.getSecondValue().doubleValue();

                double time = MathHelper.clamp_double(
                        min + ((max - min) * new SecureRandom().nextDouble()), min, max);

                return this.timer.reach((float) (1000L / time));
            }

            case FULL_RANDOM: {
                double min = this.aps.getFirstValue().doubleValue() * RandomUtils.nextDouble(0, 1);
                double max = this.aps.getSecondValue().doubleValue() * RandomUtils.nextDouble(0, 1);

                double time = (max / min) * (RandomUtils.nextDouble(min, max));

                return this.timer.reach((float) (1000L / time));
            }

            case INCREASE: {
                double min = this.aps.getFirstValue().doubleValue();
                double max = this.aps.getSecondValue().doubleValue();

                if (this.increaseClicks > min) {
                    this.increaseClicks -= RandomUtils.nextDouble(0.2, 0.45);
                }
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(30, 50);

                    this.increaseClicks += RandomUtils.nextDouble(0.2, 0.45);
                    this.clickTimer.reset();
                }
                this.increaseClicks = MathHelper.clamp_double(this.increaseClicks, 0, max);

                return this.timer.reach((float) (1000L / this.increaseClicks));
            }

            case DROP: {
                double randomTime = 0;
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(450, 900);

                    randomTime -= RandomUtils.nextDouble(3, 5);
                    this.clickTimer.reset();
                }

                double min = this.aps.getFirstValue().doubleValue();
                double max = this.aps.getSecondValue().doubleValue();

                double time = (min + ((max - min) * new SecureRandom().nextDouble())) + randomTime;

                return this.timer.reach((float) (1000L / time));
            }

            case SPIKE: {
                double randomTime = 0;
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(450, 900);

                    randomTime += RandomUtils.nextDouble(3, 5);
                    this.clickTimer.reset();
                }

                double min = this.aps.getFirstValue().doubleValue();
                double max = this.aps.getSecondValue().doubleValue();

                double time = (min + ((max - min) * new SecureRandom().nextDouble())) + randomTime;

                return this.timer.reach((float) (1000L / time));
            }

            case DROP_INCREASE: {
                double min = this.aps.getFirstValue().doubleValue();
                double max = this.aps.getSecondValue().doubleValue();

                if (this.increaseClicks > min) {
                    this.increaseClicks -= RandomUtils.nextDouble(0.2, 0.45);
                }
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(30, 50);

                    this.increaseClicks += RandomUtils.nextDouble(0.2, 0.45);
                    this.clickTimer.reset();
                }
                if (RandomUtils.nextInt(0, 10) == RandomUtils.nextInt(0, 10)) {
                    this.increaseClicks -= RandomUtils.nextDouble(1.2, 1.7);
                }

                this.increaseClicks = MathHelper.clamp_double(this.increaseClicks, 0, max);

                return this.timer.reach((float) (1000L / this.increaseClicks));
            }

            case ONE_DOT_NINE_PLUS: {
                float delay = mc.thePlayer.getCooledAttackStrength(0.0f);

                return delay > 0.9f;
            }
        }

        return false;
    }

    private void silentLegitMovement(StrafeEvent event, float eventYaw) {
        int dif = (int) ((MathHelper.wrapAngleTo180_double(mc.thePlayer.rotationYaw - eventYaw - 23.5f - 135) + 180) / 45);

        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();

        float calcForward = 0f;
        float calcStrafe = 0f;

        switch (dif) {
            case 0: {
                calcForward = forward;
                calcStrafe = strafe;
                break;
            }
            case 1: {
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            }
            case 2: {
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            }
            case 3: {
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            }
            case 4: {
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            }
            case 5: {
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            }
            case 6: {
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            }
            case 7: {
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
                break;
            }
        }

        if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
            calcForward *= 0.5f;
        }

        if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
            calcStrafe *= 0.5f;
        }

        float d = calcStrafe * calcStrafe + calcForward * calcForward;

        if (d >= 1.0E-4f) {
            d = MathHelper.sqrt_float(d);
            if (d < 1.0f) d = 1.0f;
            d = friction / d;
            calcStrafe *= d;
            calcForward *= d;
            float yawSin = MathHelper.sin((float) (eventYaw * ApacheMath.PI / 180f));
            float yawCos = MathHelper.cos((float) (eventYaw * ApacheMath.PI / 180f));
            mc.thePlayer.motionX += calcStrafe * yawCos - calcForward * yawSin;
            mc.thePlayer.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }

        event.cancel();
    }

    @Override
    public String getSuffix() {
        return this.getTargetMode().getValue().label;
    }

    @AllArgsConstructor
    private enum GCDMode {
        NORMAL("Normal"),
        MODULO("Modulo"),
        MODULO1("Modulo1"),
        HI("Hi"),
        GREEK("Greek"),
        GREEK_SMOOTH("Greek Smooth");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum AimMode {
        NORMAL("Normal"),
        ROUND("Round"),
        LOCK("Lock"),
        RANDOMIZE("Randomize"),
        MATH_RANDOM("Math.Random"),
        ATANH("Atanh");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum TargetMode {
        SINGLE("Single"),
        SWITCH("Switch"),
        MULTI("Multi");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum SortingMode {
        DISTANCE("Distance"),
        HEALTH("Health"),
        AIM("Aim");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum RangeCalculation {
        NORMAL("Normal"),
        RAYTRACE("Raytrace");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum AutoBlockMode {
        NORMAL("Normal"),
        SYNC("Sync"),
        VANILLA("Vanilla"),
        HYPIXEL("Hypixel"),
        FAKE("Fake");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum Strafemode {
        SILENT("Silent"),
        STRICT("Strict"),
        NONE("None");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum ClickMode {
        RANDOM("Random"),
        SECURE_RANDOM("Secure Random"),
        FULL_RANDOM("Full Random"),
        INCREASE("Increase"),
        DROP("Drop"),
        SPIKE("Spike"),
        DROP_INCREASE("Drop Increase"),
        ONE_DOT_NINE_PLUS("1.9+");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
