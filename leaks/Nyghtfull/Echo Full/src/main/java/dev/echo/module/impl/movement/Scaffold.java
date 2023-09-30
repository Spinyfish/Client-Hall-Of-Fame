package dev.echo.module.impl.movement;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.listener.event.impl.player.*;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.ParentAttribute;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.player.ChatUtil;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.player.RotationUtils;
import dev.echo.utils.player.ScaffoldUtils;
import dev.echo.utils.server.PacketUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;

import static dev.echo.utils.player.ScaffoldUtils.getYLevel;

public class Scaffold extends Module {
    float yaw = 0;
    boolean vulcantest = false;
    private final ModeSetting countMode = new ModeSetting("Block Counter", "Echo", "None", "Echo", "Basic", "Polar");
    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Watchdog", "Watchdog", "NCP", "IntaveExpand", "Back", "45", "Enum", "Down", "0");
    private final ModeSetting placeType = new ModeSetting("Place Type", "Post", "Pre", "Post", "Legit", "Dynamic");
    public static ModeSetting keepYMode = new ModeSetting("Keep Y Mode", "Always", "Always", "Speed toggled");
    public static ModeSetting sprintMode = new ModeSetting("Sprint Mode", "Vanilla", "Vanilla", "Watchdog", "Cancel");
    public static ModeSetting towerMode = new ModeSetting("Tower Mode", "Watchdog", "Vanilla", "NCP", "Watchdog", "Verus", "Vulcan", "VulcanTest");
    public static ModeSetting swingMode = new ModeSetting("Swing Mode", "Client", "Client", "Silent");
    public static NumberSetting delay = new NumberSetting("Delay", 0, 2, 0, 0.05);
    public static NumberSetting extend = new NumberSetting("Extend", 0, 6, 0, 0.5);
    private final NumberSetting timer = new NumberSetting("Timer", 1, 5, 0.1, 0.1);
    public static final BooleanSetting auto3rdPerson = new BooleanSetting("Auto 3rd Person", false);
    public static final BooleanSetting speedSlowdown = new BooleanSetting("Speed Slowdown", true);
    public static final NumberSetting speedSlowdownAmount = new NumberSetting("Slowdown Amount", 0.1, 0.2, 0.01, 0.01);
    public static final BooleanSetting itemSpoof = new BooleanSetting("Item Spoof", false);
    public static final BooleanSetting downwards = new BooleanSetting("Downwards", false);
    public static final BooleanSetting safewalk = new BooleanSetting("Safewalk", false);
    public static final BooleanSetting sprint = new BooleanSetting("Sprint", false);
    private final BooleanSetting fixmove = new BooleanSetting("Move FIx", false);
    private final BooleanSetting sneak = new BooleanSetting("Sneak", false);
    public static final BooleanSetting tower = new BooleanSetting("Tower", false);
    private final NumberSetting towerTimer = new NumberSetting("Tower Timer Boost", 1.2, 5, 0.1, 0.1);
    private final BooleanSetting swing = new BooleanSetting("Swing", true);
    private final BooleanSetting autoJump = new BooleanSetting("Auto Jump", false);
    private final BooleanSetting hideJump = new BooleanSetting("Hide Jump", false);
    private final BooleanSetting baseSpeed = new BooleanSetting("Base Speed", false);
    public static BooleanSetting keepY = new BooleanSetting("Keep Y", false);
    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;
    private float y;
    private float speed;
    private final MouseFilter pitchMouseFilter = new MouseFilter();
    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil timerUtil = new TimerUtil();
    public static double keepYCoord;
    private boolean shouldSendPacket;
    private boolean shouldTower;
    private boolean firstJump;
    private boolean pre;
    private int jumpTimer;
    private int slot;
    private int prevSlot;
    private float[] cachedRots = new float[2];

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT, "Automatically places blocks under you");
        this.addSettings(countMode, rotations, rotationMode, placeType, keepYMode, sprintMode, towerMode, swingMode, delay, extend, timer,
                auto3rdPerson, speedSlowdown, speedSlowdownAmount, itemSpoof, downwards, safewalk, sprint, fixmove, sneak, tower, towerTimer,
                swing, autoJump, hideJump, baseSpeed, keepY);
        rotationMode.addParent(rotations, ParentAttribute.BOOLEAN_CONDITION);
        sprintMode.addParent(sprint, ParentAttribute.BOOLEAN_CONDITION);
        towerMode.addParent(tower, ParentAttribute.BOOLEAN_CONDITION);
        swingMode.addParent(swing, ParentAttribute.BOOLEAN_CONDITION);
        towerTimer.addParent(tower, ParentAttribute.BOOLEAN_CONDITION);
        keepYMode.addParent(keepY, ParentAttribute.BOOLEAN_CONDITION);
        hideJump.addParent(autoJump, ParentAttribute.BOOLEAN_CONDITION);
        speedSlowdownAmount.addParent(speedSlowdown, ParentAttribute.BOOLEAN_CONDITION);
    }

    @Link
    public final Listener<MotionEvent> motionEventListener = e -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        // Timer Stuff
        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.timer.timerSpeed = timer.getValue().floatValue();
        } else {
            mc.timer.timerSpeed = tower.isEnabled() ? towerTimer.getValue().floatValue() : 1;
        }

        if (e.isPre()) {
            // Auto Jump
            if (baseSpeed.isEnabled()) {
                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * 0.7);
            }
            if (autoJump.isEnabled() && mc.thePlayer.onGround && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.jump();
            }

            if (sprint.isEnabled() && sprintMode.is("Watchdog") && mc.thePlayer.onGround && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown() && !isDownwards() && mc.thePlayer.isSprinting()) {
                final double[] offset = MathUtils.yawPos(mc.thePlayer.getDirection(), MovementUtils.getSpeed() / 2);
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - offset[0], mc.thePlayer.posY, mc.thePlayer.posZ - offset[1], true));
            }

            // Rotations
            if (rotations.isEnabled()) {
                float[] rotations = new float[]{0, 0};
                switch (rotationMode.getMode()) {
                    case "Watchdog":
                        rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 180, y};
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "IntaveExpand":
                        float[] rotationsexpand;
                        if (mc.thePlayer.motionX > 0) {
                            final BlockPos belowBlockPos = new BlockPos(mc.thePlayer.posX + Scaffold.extend.getValue(), getYLevel() - (Scaffold.isDownwards() ? 1 : 0), mc.thePlayer.posZ);
                            rotationsexpand = RotationUtils.getRotations(belowBlockPos, EnumFacing.DOWN);
                            e.setRotations(rotationsexpand[0], rotationsexpand[1]);

                        } else {
                            final BlockPos belowBlockPos = new BlockPos(mc.thePlayer.posX, getYLevel() - (Scaffold.isDownwards() ? 1 : 0), mc.thePlayer.posZ + Scaffold.extend.getValue());
                            rotationsexpand = RotationUtils.getRotations(belowBlockPos, EnumFacing.DOWN);
                            e.setRotations(rotationsexpand[0], rotationsexpand[1]);
                        }
                        break;
                    case "NCP":
                        float prevYaw = cachedRots[0];
                        if ((blockCache = ScaffoldUtils.getBlockInfo()) == null) {
                            blockCache = lastBlockCache;
                        }
                        if (blockCache != null && (mc.thePlayer.ticksExisted % 3 == 0
                                || mc.theWorld.getBlockState(new BlockPos(e.getX(), getYLevel(), e.getZ())).getBlock() == Blocks.air)) {
                            cachedRots = RotationUtils.getRotations(blockCache.getPosition(), blockCache.getFacing());
                        }
                        if ((mc.thePlayer.onGround || (MovementUtils.isMoving() && tower.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown())) && Math.abs(cachedRots[0] - prevYaw) >= 90) {
                            cachedRots[0] = MovementUtils.getMoveYaw(e.getYaw()) - 180;
                        }
                        rotations = cachedRots;
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Back":
                        //180
                        rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 150, 77};
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Down":
                        e.setPitch(90);
                        break;
                    case "45":
                        float val;
                        if (MovementUtils.isMoving()) {
                            float f = MovementUtils.getMoveYaw(e.getYaw()) - 180;
                            float[] numbers = new float[]{-135, -90, -45, 0, 45, 90, 135, 180};
                            float lastDiff = 999;
                            val = f;
                            for (float v : numbers) {
                                float diff = Math.abs(v - f);
                                if (diff < lastDiff) {
                                    lastDiff = diff;
                                    val = v;
                                }
                            }
                        } else {
                            val = rotations[0];
                        }
                        rotations = new float[]{
                                (val + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationYawHead)) / 2.0F,
                                (77 + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationPitchHead)) / 2.0F};
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Enum":
                        if (lastBlockCache != null) {
                            float yaw = RotationUtils.getEnumRotations(lastBlockCache.getFacing());
                            e.setRotations(yaw, 77);
                        } else {
                            e.setRotations(mc.thePlayer.rotationYaw + 180, 77);
                        }
                        break;
                    case "0":
                        e.setRotations(0, 0);
                        break;
                }
                RotationUtils.setVisualRotations(e);
            }

            // Speed 2 Slowdown
            if (speedSlowdown.isEnabled() && mc.thePlayer.isPotionActive(Potion.moveSpeed) && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.onGround) {
                MovementUtils.setSpeed(speedSlowdownAmount.getValue());
            }

            if (sneak.isEnabled()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }
            if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
                //    MovementUtils.setSpeed(0.10f);
            }

            // Save ground Y level for keep Y
            if (mc.thePlayer.onGround) {
                keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
            }

            if (tower.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) {
                double centerX = Math.floor(e.getX()) + 0.5, centerZ = Math.floor(e.getZ()) + 0.5;
                switch (towerMode.getMode()) {
                    case "Vanilla":
                        mc.thePlayer.motionY = 0.42f;
                        break;
                    case "Verus":
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            mc.thePlayer.motionY = 0.42f;
                        }
                        break;
                    case "Vulcan":
                        if (mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.offGroundTicks > 3) {
                            mc.thePlayer.onGround = true;
                            mc.thePlayer.motionY = MathUtils.getRandomInRange(0.47F, 0.50F);
                        }
                        break;
                    case "VulcanTest": {
                        if (vulcantest == false) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY - 2,
                                    mc.thePlayer.posZ,
                                    mc.thePlayer.rotationYaw,
                                    mc.thePlayer.rotationPitch,
                                    false
                            ));
                            vulcantest = true;
                        }
                        mc.thePlayer.motionY = 0.42F;
                    }
                    break;
                    case "Watchdog":
                        if (MovementUtils.isMoving()) {
                            if (!mc.gameSettings.keyBindJump.isKeyDown() || !MovementUtils.isMoving()) {
                                return;
                            }

                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42f;
                                mc.thePlayer.motionX *= .65;
                                mc.thePlayer.motionZ *= .65;
                            }
                        }
                        break;
                    case "NCP":
                        if (!MovementUtils.isMoving() || MovementUtils.getSpeed() < 0.16) {
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42;
                            } else if (mc.thePlayer.motionY < 0.23) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                                mc.thePlayer.motionY = 0.42;
                            }
                        }
                        break;

                }
            }

            // Setting Block Cache
            blockCache = ScaffoldUtils.getBlockInfo();
            if (blockCache != null) {
                lastBlockCache = ScaffoldUtils.getBlockInfo();
            } else {
                return;
            }

            if (mc.thePlayer.ticksExisted % 4 == 0) {
                pre = true;
            }

            // Placing Blocks (Pre)
            if (placeType.is("Pre") || (placeType.is("Dynamic") && pre)) {
                if (place()) {
                    pre = false;
                }
            }
        } else {
            // Setting Item Slot
            if (!itemSpoof.isEnabled()) {
                mc.thePlayer.inventory.currentItem = slot;
            }

            // Placing Blocks (Post)
            if (placeType.is("Post") || (placeType.is("Dynamic") && !pre)) {
                place();
            }

            pre = false;
        }
        yaw = e.getYaw();
    };
    @Link
    private final Listener<StrafeEvent> strafeEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (fixmove.isEnabled()) {
            event.setForward(-event.getForward());
            event.setStrafe(-event.getStrafe());
            event.setYaw(yaw);
        }
    };
    @Link
    private final Listener<JumpEvent> jumpEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (fixmove.isEnabled()) {
            event.setYaw(yaw);
        }
    };

    private boolean place() {
        int slot = ScaffoldUtils.getBlockSlot();
        if (blockCache == null || lastBlockCache == null || slot == -1) {
            return false;
        }

        if (this.slot != slot) {
            this.slot = slot;
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
        }

        boolean placed = false;
        if (delayTimer.hasTimeElapsed(delay.getValue() * 1000)) {
            firstJump = false;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(this.slot),
                    lastBlockCache.getPosition(), lastBlockCache.getFacing(),
                    ScaffoldUtils.getHypixelVec3(lastBlockCache))) {
                placed = true;
                y = MathUtils.getRandomInRange(79.5f, 83.5f);
                if (swing.isEnabled()) {
                    if (swingMode.is("Client")) {
                        mc.thePlayer.swingItem();
                    } else {
                        PacketUtils.sendPacket(new C0APacketAnimation());
                    }
                }
            }
            delayTimer.reset();
            blockCache = null;
        }
        return placed;
    }

    @Link
    public final Listener<BlockPlaceableEvent> blockListener = event -> {
        if (placeType.is("Legit")) {
            place();
        }
    };

    @Link
    public final Listener<TickEvent> tickListener = event -> {
        if (mc.thePlayer == null) {
            return;
        }
        if (hideJump.isEnabled() && !mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMoving() && !mc.thePlayer.onGround && autoJump.isEnabled()) {
            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.1F;
        }
        if (downwards.isEnabled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            mc.thePlayer.movementInput.sneak = false;
        }
    };

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            if (!itemSpoof.isEnabled()) {
                mc.thePlayer.inventory.currentItem = prevSlot;
            }
            if (slot != mc.thePlayer.inventory.currentItem && itemSpoof.isEnabled()) {
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }

            if (auto3rdPerson.isEnabled()) {
                mc.gameSettings.thirdPersonView = 0;
            }
            if (mc.thePlayer.isSneaking() && sneak.isEnabled()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindSneak));
            }
        }
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        vulcantest = false;
        lastBlockCache = null;
        if (mc.thePlayer != null) {
            prevSlot = mc.thePlayer.inventory.currentItem;
            slot = mc.thePlayer.inventory.currentItem;
            if (mc.thePlayer.isSprinting() && sprint.isEnabled() && sprintMode.is("Cancel")) {
                PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            if (auto3rdPerson.isEnabled()) {
                mc.gameSettings.thirdPersonView = 1;
            }
        }
        firstJump = true;
        speed = 1.1f;
        timerUtil.reset();
        jumpTimer = 0;
        y = 80;
        super.onEnable();
    }

    @Link
    public final Listener<PacketSendEvent> packetListener = e -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (e.getPacket() instanceof C0BPacketEntityAction
                && ((C0BPacketEntityAction) e.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING
                && sprint.isEnabled() && sprintMode.is("Cancel")) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof C09PacketHeldItemChange && itemSpoof.isEnabled()) {
            e.setCancelled(true);
        }
        if (towerMode.is("Watchdog")) {
            final Packet<?> packet = e.getPacket();

            if (mc.thePlayer.motionY > -0.0784000015258789 && !mc.thePlayer.isPotionActive(Potion.jump) && packet instanceof C08PacketPlayerBlockPlacement && MovementUtils.isMoving()) {
                final C08PacketPlayerBlockPlacement wrapper = ((C08PacketPlayerBlockPlacement) packet);

                if (wrapper.getPosition().equals(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.4, mc.thePlayer.posZ))) {
                    mc.thePlayer.motionY = -0.0784000015258789;
                }
            }
        }
        if (towerMode.is("Vulcan")) {
            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
            if (e.getPacket() instanceof S08PacketPlayerPosLook && mc.gameSettings.keyBindJump.isKeyDown()) {
                PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                e.setCancelled(true);
            }
        }
    };

    @Link
    public final Listener<SafeWalkEvent> safeListener = event -> {
        if ((safewalk.isEnabled() && !isDownwards())) {
            event.setSafe(true);
        }
    };

    public static boolean isDownwards() {
        return downwards.isEnabled() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
    }

}
