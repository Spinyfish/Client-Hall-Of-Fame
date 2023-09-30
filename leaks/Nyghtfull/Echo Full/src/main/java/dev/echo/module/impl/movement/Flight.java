package dev.echo.module.impl.movement;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketReceiveEvent;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.listener.event.impl.player.BoundingBoxEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.player.MoveEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.combat.TargetStrafe;
import dev.echo.module.settings.ParentAttribute;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.ui.notifications.NotificationManager;
import dev.echo.ui.notifications.NotificationType;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.player.DamageUtils;
import dev.echo.utils.player.InventoryUtils;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.server.PacketUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public final class Flight extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Zonecraft", "Intave(slow)", "Watchdog", "Vanilla", "AirWalk", "Viper", "Verus", "Minemen", "Old NCP", "Slime", "Custom", "Packet", "Minemora", "Vulcan", "BlocksMC");
    private final NumberSetting teleportDelay = new NumberSetting("Teleport Delay", 5, 20, 1, 1);
    private final NumberSetting teleportLength = new NumberSetting("Teleport Length", 5, 20, 1, 1);
    private final NumberSetting timerAmount = new NumberSetting("Timer Amount", 1, 3, 0.1, 0.1);
    private final NumberSetting horizontalSpeed = new NumberSetting("Horizontal Speed", 2, 5, 0, 0.1);
    private final NumberSetting verticalSpeed = new NumberSetting("Vertical Speed", 1, 5, 0, 0.1);
    private final BooleanSetting viewBobbing = new BooleanSetting("View Bobbing", true);
    private final BooleanSetting antiKick = new BooleanSetting("Anti-kick", false);
    boolean landed = false;
    private int stage;
    private int ticks;
    int bmcticks;
    private boolean doFly;
    private double x, y, z;
    private double lastX, lastY, lastZ;
    private final CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<>();
    private boolean hasClipped;
    private int slot = 0;
    private double speedStage;
    private float clip;
    private double moveSpeed;
    private int stage2;
    private int vulcanticks;
    private final TimerUtil timer = new TimerUtil();
    public static final Set<BlockPos> hiddenBlocks = new HashSet<>();
    private boolean hasS08;
    private boolean hasDamaged;
    private boolean up;
    private int airTicks;

    private boolean adjustSpeed, canSpeed, hasBeenDamaged;
    public double moveSpeed2, lastDist;
    public int stage3;

    // Custom fly settings
    private final BooleanSetting damage = new BooleanSetting("Damage", false);
    private final ModeSetting damageMode = new ModeSetting("Damage Mode", "Vanilla", "Vanilla", "Suffocate", "NCP");

    private final NumberSetting motionY = new NumberSetting("Motion Y", 0, 0.3, -0.3, 0.01);

    private final BooleanSetting speed = new BooleanSetting("Speed", false);
    private final NumberSetting speedAmount = new NumberSetting("Speed Amount", 0.2, 9, 0.05, 0.01);

    public Flight() {
        super("Flight", Category.MOVEMENT, "Makes you hover in the air");
        horizontalSpeed.addParent(mode, m -> m.is("Vanilla"));
        verticalSpeed.addParent(mode, m -> m.is("Vanilla"));
        antiKick.addParent(mode, m -> m.is("Vanilla"));
        damage.addParent(mode, m -> m.is("Custom"));
        damageMode.addParent(damage, ParentAttribute.BOOLEAN_CONDITION);
        motionY.addParent(mode, m -> m.is("Custom"));
        speed.addParent(mode, m -> m.is("Custom"));
        speedAmount.addParent(speed, ParentAttribute.BOOLEAN_CONDITION);
        teleportDelay.addParent(mode, m -> m.is("Packet"));
        teleportLength.addParent(mode, m -> m.is("Packet"));
        this.addSettings(mode, teleportDelay, teleportLength, motionY, damage, damageMode, speed, speedAmount, timerAmount, horizontalSpeed, verticalSpeed, viewBobbing, antiKick);
    }

    @Link
    public Listener<MoveEvent> onMoveEvent = e -> {
        switch (mode.getMode()) {
            case "Vanilla":
                e.setSpeed(MovementUtils.isMoving() ? horizontalSpeed.getValue().floatValue() : 0);
                TargetStrafe.strafe(e, horizontalSpeed.getValue().floatValue());
                break;
            case "Watchdog":
                e.setSpeed(0);
                break;
            case "Slime":
                if (stage < 8) {
                    e.setSpeed(0);
                }
                break;
            case "Packet":
                e.setSpeed(0);
                break;
            case "Minemora":
                if (!hasDamaged) {
                    e.setSpeed(0);
                }
                break;
            default:
                TargetStrafe.strafe(e);
                break;
        }
    };

    @Link
    public Listener<MotionEvent> onMotionEvent = e -> {
        this.setSuffix(mode.getMode());
        if (viewBobbing.isEnabled()) {
            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.08F;
        }
        mc.timer.timerSpeed = timerAmount.getValue().floatValue();

        switch (mode.getMode()) {
            case "Watchdog":
                if (e.isPre()) {
                    mc.thePlayer.motionY = 0;
                    stage++;
                    if (stage == 1) {
                        final double x = e.getX() + -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * 7.99;
                        final double y = e.getY() - 1.75;
                        final double z = e.getZ() + Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * 7.99;
                        if (mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.air) {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                            mc.thePlayer.setPosition(x, y, z);
                        }
                    }
                }
                break;
            case "Vulcan":
                if (e.isPre()) {
                    if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                        MovementUtils.setSpeed(0.44);
                    }
                    mc.thePlayer.motionY = -1E-10D + (mc.gameSettings.keyBindJump.isKeyDown() ? 0.5 : 0.0D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? 0.5 : 0.0D);

                    if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 10 - 0.5 - 0.15) {
                        e.setCancelled(true);
                    } else {
                        ticks++;
                        if (ticks >= 8 && mode.is("Vulcan")) {
                            toggle();
                        }
                    }
                }
                break;
            case "Intave(slow)":
                if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 10 - MovementUtils.getBaseMoveSpeed() - 0.15) {
                    e.setCancelled(true);
                }
                break;
            case "Minemora":
                if (e.isPre()) {
                    if (stage < 3) {
                        e.setOnGround(false);
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            stage++;
                        }
                    } else {
                        if (mc.thePlayer.hurtTime > 0 && !hasDamaged) {
                            hasDamaged = true;
                        }
                        if (hasDamaged) {
                            mc.thePlayer.motionY = -MathUtils.getRandomInRange(0.005, 0.0051);
                            MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * 1.5);
                        }
                    }
                }
                break;
            case "Zonecraft":
                if (e.isPre()) {
                    stage++;
                    switch (stage) {
                        case 1:
                            e.setOnGround(true);
                            MovementUtils.setSpeed(0.55);
                            break;
                    }

                    mc.thePlayer.motionY = 0;
                    e.setY(mc.thePlayer.posY + 0.1);
                }
                break;
            case "BlocksMC": {

            }
            case "Verus":
                if (e.isPre()) {
                    if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.42f;
                            up = true;
                        } else if (up) {
                            if (!mc.thePlayer.isCollidedHorizontally) {
                                mc.thePlayer.motionY = -0.0784000015258789;
                            }
                            up = false;
                        }
                    } else if (mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.thePlayer.motionY = 0.42f;
                    }
                    MovementUtils.setSpeed(mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.33);
                }
                break;
            case "Vanilla":
                if (TargetStrafe.canStrafe()) {
                    mc.thePlayer.motionY = antiKick.isEnabled() ? -0.0625 : 0;
                } else {
                    mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? verticalSpeed.getValue() : mc.gameSettings.keyBindSneak.isKeyDown() ? -verticalSpeed.getValue() : antiKick.isEnabled() ? -0.0625 : 0;
                }
                break;
            case "AirWalk":
                break;
            case "Viper":
                mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1 : mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : 0;
                e.setOnGround(true);
                MovementUtils.setSpeed(0.3);
                break;
            case "Old NCP":
                if (hasDamaged) {
                    e.setOnGround(true);
                    double baseSpeed = MovementUtils.getBaseMoveSpeed();
                    if (!MovementUtils.isMoving() || mc.thePlayer.isCollidedHorizontally) {
                        moveSpeed = baseSpeed;
                    }
                    if (moveSpeed > baseSpeed) {
                        moveSpeed -= moveSpeed / 159.0;
                    }

                    moveSpeed = Math.max(baseSpeed, moveSpeed);

                    if (e.isPre()) {
                        mc.timer.timerSpeed = 1;
                        if (MovementUtils.isMoving()) {
                            MovementUtils.setSpeed(moveSpeed);
                        }
                        mc.thePlayer.motionY = 0;
                        double y = 1.0E-10;
                        e.setY(e.getY() - y);
                    }
                } else if (mc.thePlayer.onGround) {
                    DamageUtils.damage(DamageUtils.DamageType.WATCHDOGUP);
                    mc.thePlayer.jump();
                    hasDamaged = true;
                }
                break;
            case "Slime":
                if (e.isPre()) {
                    stage++;
                    switch (stage) {
                        case 1:
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                            }
                            break;
                        case 7:
                            BlockPos pos = new BlockPos(e.getX(), e.getY() - 2, e.getZ());
                            e.setPitch(mc.thePlayer.rotationPitchHead = 90);
                            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, new Vec3(pos))) {
                                mc.thePlayer.swingItem();
                            }
                            break;
                    }
                    if (stage > 8) {
                        e.setOnGround(true);
                        MovementUtils.setSpeed(0.3);
                        mc.thePlayer.motionY = 0;
                    }
                }
                break;
            case "Custom":
                if (e.isPre()) {
                    stage++;
                    switch (stage) {
                        case 1:
                            if (damage.isEnabled()) {
                                DamageUtils.damage(DamageUtils.DamageType.valueOf(damageMode.getMode().toUpperCase()));
                            }
                            break;
                    }
                    mc.thePlayer.motionY = motionY.getValue();
                    if (speed.isEnabled()) {
                        MovementUtils.setSpeed(speedAmount.getValue());
                    }
                }
                break;
            case "Packet":
                if (e.isPre()) {
                    mc.thePlayer.motionY = 0;
                    if (MovementUtils.isMoving() && mc.thePlayer.ticksExisted % teleportDelay.getValue().intValue() == 0) {
                        final double x = e.getX() + -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * teleportLength.getValue().intValue();
                        final double z = e.getZ() + Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * teleportLength.getValue().intValue();
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, mc.thePlayer.posY, z, false));
                        mc.thePlayer.setPosition(x, mc.thePlayer.posY, z);
                    }
                }
                break;
        }
    };
    @Link
    public Listener<PacketSendEvent> onPacketSendEvent = event -> {
        if (mc.isSingleplayer() || mc.thePlayer == null) {
            return;
        }
        if (mode.is("Slime") && stage > 7 && PacketUtils.isPacketValid(event.getPacket())) {
            event.setCancelled(true);
            packets.add(event.getPacket());
        }
        if (mode.is("Watchdog") && event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
    };

    @Link
    public Listener<BoundingBoxEvent> onBoundingBoxEvent = event -> {
        if (mode.is("AirWalk") || mode.is("Verus")) {
            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            event.setBoundingBox(axisAlignedBB);
        }
    };

    @Link
    public Listener<PacketReceiveEvent> onPacketReceiveEvent = e -> {
        if (e.getPacket() instanceof S08PacketPlayerPosLook && !hasS08) {
            S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) e.getPacket();
            hasS08 = true;
        }
    };

    @Override
    public void onEnable() {
        landed = false;
        bmcticks = 0;
        if (mode.is("Intave(slow)")) {
            PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
        }
        if (mode.is("Vulcan")) {
            ticks = 0;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - 2,
                    mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw,
                    mc.thePlayer.rotationPitch,
                    false
            ));
        }
        hasDamaged = false;
        doFly = false;
        ticks = 0;
        stage = 0;
        stage3 = 0;
        clip = 0;
        hasClipped = false;
        packets.clear();
        timer.reset();
        moveSpeed = 0;
        stage2 = 0;
        hasS08 = false;
        if (mc.thePlayer != null) {
            lastX = mc.thePlayer.posX;
            lastY = mc.thePlayer.posY;
            lastZ = mc.thePlayer.posZ;
            y = 0;
            slot = mc.thePlayer.inventory.currentItem;
            if (mode.is("Old NCP")) {
                moveSpeed = 1.6;
            } else if (mode.is("Slime")) {
                int slimeBlockSlot = InventoryUtils.getBlockSlot(Blocks.slime_block);
                if (slimeBlockSlot != -1) {
                    mc.thePlayer.inventory.currentItem = slimeBlockSlot;
                } else {
                    NotificationManager.post(NotificationType.DISABLE, "Flight", "No slime block found in hotbar!");
                    toggleSilent();
                    return;
                }
            }
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        if (mode.is("Vanilla") || mode.is("Minemen") || mode.is("Old NCP") || mode.is("Watchdog") || mode.is("Vulcan") || mode.is("BlocksMC")) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        } else if (mode.is("Slime")) {
            mc.thePlayer.inventory.currentItem = slot;
        }
        packets.forEach(PacketUtils::sendPacketNoEvent);
        packets.clear();
        super.onDisable();
    }
}
