package dev.africa.pandaware.impl.module.movement;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.event.TaskedEventListener;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.event.player.*;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.packet.PacketBalance;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.client.ServerUtils;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import dev.africa.pandaware.utils.network.ProtocolUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import dev.africa.pandaware.utils.player.RotationUtils;
import dev.africa.pandaware.utils.player.block.BlockUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.animator.Animator;
import dev.africa.pandaware.utils.render.animator.Easing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

@Getter
@ModuleInfo(name = "Scaffold", category = Category.MOVEMENT)
public class ScaffoldModule extends Module {
    private final EnumSetting<ScaffoldMode> scaffoldMode = new EnumSetting<>("Mode", ScaffoldMode.HYPIXEL);
    private final EnumSetting<HypixelMode> hypixelMode = new EnumSetting<>("Hypixel Mode", HypixelMode.NORMAL, () ->
            this.scaffoldMode.getValue() == ScaffoldMode.HYPIXEL);
    private final BooleanSetting tower = new BooleanSetting("Tower", true);
    private final EnumSetting<RotationMode> rotationMode = new EnumSetting<>("Rotation mode", RotationMode.NEW);
    private final BooleanSetting sprint = new BooleanSetting("Sprint", true);
    private final EnumSetting<SpoofMode> spoofMode = new EnumSetting<>("Spoof mode", SpoofMode.SPOOF);
    private final EnumSetting<TowerMode> towerMode = new EnumSetting<>("Tower mode", TowerMode.NCP,
            this.tower::getValue);
    private final BooleanSetting swing = new BooleanSetting("Swing", false);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
    private final BooleanSetting keepRotation = new BooleanSetting("Keep rotation", true,
            this.rotate::getValue);
    private final BooleanSetting downwards = new BooleanSetting("Downwards", true);
    private final BooleanSetting towerMove = new BooleanSetting("Tower move", true,
            this.tower::getValue);
    private final BooleanSetting useSpeed = new BooleanSetting("Use Speed Modifier", false);
    private final BooleanSetting safeWalk = new BooleanSetting("SafeWalk", true);
    private final BooleanSetting overwriteAura = new BooleanSetting("Overwrite Aura rotations", true);
    private final BooleanSetting replaceBlocks = new BooleanSetting("Replace Blocks", true);
    private final NumberSetting speedModifier = new NumberSetting("Speed modifier", 1.5, 0.1,
            1, 0.01, this.useSpeed::getValue);
    private final NumberSetting expand = new NumberSetting("Expand", 6, 0, 0, 0.1);
    private final NumberSetting timerSpeed = new NumberSetting("Timer", 5, 1, 1, 0.1);

    private final EnumFacing[] invert = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};

    private BlockEntry blockEntry;
    private BlockEntry aimBlockEntry;
    private Vec2f rotations;
    private int lastSlot;
    private double startY;
    private Vec2f smoothRotations;
    private Vec2f currentRotation;
    private final TimeHelper towerTimer = new TimeHelper();
    private final TimeHelper vulcanTimer = new TimeHelper();
    private double lastDistance;
    private boolean sloted;
    private int c09slot;

    float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
    float gcd = f * f * f * 1.2F;
    float smoothness;
    float smoothnessValue;
    Vec2f smoothed;

    public ScaffoldModule() {
        this.registerSettings(
                this.scaffoldMode,
                this.hypixelMode,
                this.spoofMode,
                this.rotationMode,
                this.towerMode,
                this.swing,
                this.rotate,
                this.overwriteAura,
                this.keepRotation,
                this.replaceBlocks,
                this.downwards,
                this.tower,
                this.towerMove,
                this.sprint,
                this.safeWalk,
                this.useSpeed,
                this.speedModifier,
                this.timerSpeed,
                this.expand
        );

        this.setTaskedEvent(this.taskedEventListener);
    }

    @Override
    public void onEnable() {
        this.blockEntry = null;
        this.aimBlockEntry = null;
        this.rotations = null;
        this.currentRotation = null;
        this.startY = mc.thePlayer.posY;
        this.sloted = false;
        this.c09slot = mc.thePlayer.inventory.currentItem;
        this.lastSlot = mc.thePlayer.inventory.currentItem;

        if (this.scaffoldMode.getValue() == ScaffoldMode.VULCAN) {
            if (!mc.thePlayer.onGround) {
                mc.thePlayer.motionX *= 0.7;
                mc.thePlayer.motionZ *= 0.7;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        this.blockEntry = null;
        this.aimBlockEntry = null;
        this.rotations = null;

        if (this.scaffoldMode.getValue() == ScaffoldMode.VULCAN) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            mc.thePlayer.motionX *= 0.7;
            mc.thePlayer.motionZ *= 0.7;
        }

        if (this.spoofMode.getValue() == SpoofMode.SWITCH) {
            mc.thePlayer.inventory.currentItem = this.lastSlot;
        } else {
            if (mc.thePlayer.inventory.currentItem != c09slot) mc.thePlayer.sendQueue.getNetworkManager().sendPacket(
                    new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    TaskedEventListener<ScaffoldModule> taskedEventListener
            = new TaskedEventListener<ScaffoldModule>("Tasked scaffold event", this) {
        private final Animator animator = new Animator();

        @EventHandler
        EventCallback<RenderEvent> onRender = event -> {
            if (event.getType() == RenderEvent.Type.RENDER_2D) {
                int slot = this.getModule().getItemSlot(false);
                float height = (slot != -1 ? 35 : 17);

                GlStateManager.pushMatrix();
                this.animator.setMin(0).setMax(1).setSpeed(3.3f);

                boolean toggled = this.getModule().getData().isEnabled();
                if (toggled && this.animator.getValue() <= 1F) {
                    this.animator.setEase(Easing.QUINTIC_OUT).setReversed(false).update();
                } else if (!toggled && this.animator.getValue() > 0F) {
                    this.animator.setEase(Easing.QUINTIC_IN).setReversed(true).update();
                }

                if (this.animator.getValue() > 0) {
                    float width = 30;
                    float x = event.getResolution().getScaledWidth() / 2f - (width / 2);
                    float y = event.getResolution().getScaledHeight() / 2f + 10;
                    GlStateManager.translate(x, y, 0);

                    if (this.animator.getValue() < 1D) {
                        GlStateManager.translate((width / 2f) * (1 - this.animator.getValue()),
                                (height / 2f) * (1 - this.animator.getValue()), 0);
                        GlStateManager.scale(this.animator.getValue(), this.animator.getValue(), this.animator.getValue());
                    }

                    RenderUtils.drawRoundedRect(0, 0, width, height, 4,
                            UISettings.INTERNAL_COLOR);

                    if (slot != -1) {
                        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slot);

                        if (itemStack.getItem() != null) {
                            RenderUtils.renderItemOnScreenNoDepth(itemStack, (int) (width / 2) - 8, 4);
                        }
                    }

                    Fonts.getInstance().getProductSansMedium().drawCenteredStringWithShadow(
                            String.valueOf(Math.max(this.getModule().getItemSlot(true), 0)),
                            width / 2, height - 13, -1
                    );
                }
                GlStateManager.popMatrix();
            }
        };
    };

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.FRAME) {
            if (this.rotate.getValue()) {
                if (this.aimBlockEntry != null && ((this.blockEntry != null
                        || this.rotationMode.getValue() == RotationMode.NEW) ||
                        this.rotationMode.getValue() == RotationMode.HYPIXEL && mc.thePlayer.getDiagonalTicks() > 0)) {
                    this.rotations = this.keepRotation.getValue() ?
                            RotationUtils.getBlockRotations(this.aimBlockEntry.getVector()) :
                            RotationUtils.getBlockRotations(this.aimBlockEntry.getPosition());
                }
            }
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (this.spoofMode.getValue() == SpoofMode.SPOOF) {
            if (event.getPacket() instanceof C09PacketHeldItemChange) {
                C09PacketHeldItemChange packet = event.getPacket();

                int slotId = this.getItemSlot(false);

                if (mc.thePlayer.inventory.currentItem == packet.getSlotId()) {
                    event.cancel();
                }
            }
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (startY > mc.thePlayer.posY) this.startY = ApacheMath.floor(mc.thePlayer.posY);
        this.blockEntry = null;
        int slot = this.getItemSlot(false);

        if (this.replaceBlocks.getValue() && slot == -1) {
            if (getBlockCount(true) > 0) {

                int toSwitch = 8;

                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
                    if (itemStack == null) {
                        toSwitch = i;
                        break;
                    }
                }

                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 0, toSwitch, 0, mc.thePlayer);
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);

                for (int i1 = 9; i1 < 45; ++i1) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i1).getHasStack()) {
                        ItemStack is = this.mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                        if (is != null) {
                            if (!(is.getItem() instanceof ItemBlock) || !isValid(((ItemBlock) is.getItem()).getBlock()))
                                continue;

                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i1, toSwitch, 2, mc.thePlayer);
                            break;
                        }
                    }
                }
            }
        }

        int oldSlot = mc.thePlayer.inventory.currentItem;

        if (slot != -1) {
            if (this.spoofMode.getValue() == SpoofMode.SWITCH) {
                mc.thePlayer.inventory.currentItem = slot;
            } else {
                if (!sloted || c09slot != slot) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(slot));
                    c09slot = slot;
                    sloted = true;
                }
            }
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && downwards.getValue() ||
                Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
            this.startY = Math.floor(mc.thePlayer.posY);
        }

        boolean canGoDown = this.downwards.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
                && mc.currentScreen == null;

        double length = (this.tower.getValue() && !mc.gameSettings.keyBindJump.isKeyDown()
                || !this.tower.getValue()) ? this.expand.getValue().intValue() : 0;
        double dX = -Math.sin(Math.toRadians(MovementUtils.getDirection())) * length;
        double dZ = Math.cos(Math.toRadians(MovementUtils.getDirection())) * length;

        double y = !canGoDown ? this.startY : mc.thePlayer.posY;
        BlockPos blockPos = new BlockPos(mc.thePlayer.posX + dX, y, mc.thePlayer.posZ + dZ).down();

        if (this.downwards.getValue()) {
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.currentScreen == null) {
                mc.gameSettings.keyBindSneak.pressed = false;
                blockPos = blockPos.down();
            }
        }

        this.blockEntry = this.getBlockEntry(blockPos);

        if (this.aimBlockEntry == null || this.blockEntry != null) {
            this.aimBlockEntry = this.blockEntry;
        }

        if (event.getEventState() == Event.EventState.PRE) {
            if (this.blockEntry != null) {
                if (this.towerMode.getValue() == TowerMode.HYPIXEL && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
                        this.place(slot);
                    }
                }
            }
        }

        if (event.getEventState() == Event.EventState.POST) return;

        if (this.blockEntry != null && !(this.towerMode.getValue() == TowerMode.HYPIXEL && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()))) {
            if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
                event.setOnGround(true);
                this.place(slot);
            }
        }

        lastDistance = MovementUtils.getLastDistance();

        if (this.rotate.getValue() && this.rotations != null) {
            switch (this.rotationMode.getValue()) {
                case SNAP:
                case NEW:
                case OLD:
                case HYPIXEL:
                    if (this.blockEntry != null || this.rotationMode.getValue() != RotationMode.SNAP) {
                        event.setYaw(this.rotations.getX());
                        event.setPitch(this.rotations.getY());
                    }
                    break;
                case FACING:
                    event.setPitch(90f);
                    break;
                case MMC:
                    event.setYaw(mc.thePlayer.rotationYaw - 180);
                    event.setPitch(80.5f);
                    break;
                case STATIC:
                    switch (this.aimBlockEntry.getFacing()) {
                        case NORTH:
                            event.setYaw(0);
                            break;

                        case SOUTH:
                            event.setYaw(180);
                            break;

                        case WEST:
                            event.setYaw(-90);
                            break;

                        case EAST:
                            event.setYaw(90);
                            break;
                    }
                    event.setPitch(80.5f);
                    break;
                case BACKWARDS:
                    event.setYaw(this.rotations.getX());
                    event.setPitch(80.5f);
                    break;

                case SMOOTH:
                    switch (this.aimBlockEntry.getFacing()) {
                        case NORTH:
                            event.setYaw(0);
                            break;

                        case SOUTH:
                            event.setYaw(180);
                            break;

                        case WEST:
                            event.setYaw(-90);
                            break;

                        case EAST:
                            event.setYaw(90);
                            break;
                    }

                    float smoothness = 60f;

                    if (this.currentRotation == null) {
                        this.currentRotation = new Vec2f(
                                this.smoothRotations.getX(),
                                this.smoothRotations.getY()
                        );
                    }

                    float smoothnessValue = 1 * (smoothness / 100f);

                    Vec2f smoothed = new Vec2f(
                            RotationUtils.updateRotation(
                                    this.currentRotation.getX(),
                                    this.smoothRotations.getX(),
                                    Math.max(1, 180 * smoothnessValue)
                            ),
                            RotationUtils.updateRotation(
                                    this.currentRotation.getY(),
                                    this.smoothRotations.getY(),
                                    Math.max(1, 90f * smoothnessValue)
                            )
                    );

                    smoothed.setX(smoothed.getX() - ((smoothed.getX() % gcd) - f));

                    this.currentRotation = smoothed;

                    event.setYaw(smoothed.getX());
                    event.setPitch(smoothed.getY());

                    event.setPitch(80.5f + RandomUtils.nextFloat(0, 1));
                    break;
                case SMOOTHGCD:
                    if (this.aimBlockEntry != null) {
                        if (this.smoothRotations == null) {
                            this.smoothRotations = new Vec2f();
                        }

                        this.smoothRotations.setY(90);

                        switch (this.aimBlockEntry.getFacing()) {
                            case NORTH:
                                this.smoothRotations.setX(0);
                                break;

                            case SOUTH:
                                this.smoothRotations.setX(180);
                                break;

                            case WEST:
                                this.smoothRotations.setX(-90);
                                break;

                            case EAST:
                                this.smoothRotations.setX(90);
                                break;
                        }

                        smoothness = 70f;

                        if (this.currentRotation == null) {
                            this.currentRotation = new Vec2f(
                                    this.smoothRotations.getX(),
                                    this.smoothRotations.getY()
                            );
                        }

                        smoothnessValue = 1 - (smoothness / 100f);

                        smoothed = new Vec2f(
                                RotationUtils.updateRotation(
                                        this.currentRotation.getX(),
                                        this.smoothRotations.getX(),
                                        Math.max(1, 180 * smoothnessValue)
                                ),
                                RotationUtils.updateRotation(
                                        this.currentRotation.getY(),
                                        this.smoothRotations.getY(),
                                        Math.max(1, 90f / smoothnessValue)
                                )
                        );

                        smoothed.setX(smoothed.getX() - ((smoothed.getX() % gcd) - f));

                        this.currentRotation = smoothed;

                        event.setYaw(smoothed.getX());
                        event.setPitch(smoothed.getY());
                    }
                    break;
                case VULCAN:
                    event.setYaw(mc.thePlayer.rotationYaw - 180 + (RandomUtils.nextInt(-5, 5)));
                    event.setPitch(80.5f);
                    break;
            }
        }

        if (this.scaffoldMode.getValue() == ScaffoldMode.HYPIXEL && this.hypixelMode.getValue() == HypixelMode.NORMAL) {
            if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) && !(ServerUtils.compromised)) {
                if (MovementUtils.isMoving() &&
                        !Client.getInstance().getModuleManager().getByClass(SpeedModule.class).getData().isEnabled()
                        && (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) &&
                        !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) && PlayerUtils.isMathGround() &&
                        mc.thePlayer.ticksExisted % 2 == 0) {
                    event.setY(event.getY() + 0.1);
                    event.setOnGround(false);
                }
            }
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (getItemSlot(true) == -1) return;
        boolean canSprint = mc.thePlayer != null && PlayerUtils.isMathGround() &&
                !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.getFoodStats().getFoodLevel() > 6 &&
                MovementUtils.isMoving() && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.getAirTicks() == 0;
        if (MovementUtils.canSprint()) {
            mc.thePlayer.setSprinting(this.sprint.getValue());
        }
        switch (scaffoldMode.getValue()) {
            case HYPIXEL:
                switch (this.hypixelMode.getValue()) {
                    case NORMAL:
                        if (PlayerUtils.isMathGround() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                            MovementUtils.strafe(event, 0.2625 * (mc.thePlayer.getDiagonalTicks() > 0 ? 0.85 : 1) *
                                    (this.useSpeed.getValue() ? this.speedModifier.getValue().floatValue() : 1));
                        } else if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                            mc.thePlayer.motionX *= 0.25;
                            mc.thePlayer.motionZ *= 0.25;
                        }
                        break;
                    case KEEPY:
                        if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) && !(ServerUtils.compromised)) {
                            if (mc.thePlayer.onGround && MovementUtils.isMoving() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
                                    && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                                event.y = mc.thePlayer.motionY = 0.4f;

                                MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() *
                                        (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.8 : 1.1) *
                                        (mc.thePlayer.getDiagonalTicks() > 0 ? 0.85 : 1) * (this.useSpeed.getValue() ?
                                        this.speedModifier.getValue().floatValue() : 1));
                            } else if (!mc.thePlayer.onGround && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())
                                    && MovementUtils.isMoving()) {
                                MovementUtils.strafe(event, MovementUtils.getSpeed());
                            }
                        } else {
                            event.y = mc.thePlayer.motionY = Math.random() - (MovementUtils.getBaseMoveSpeed() / 6);
                        }
                        break;
                }
                break;
            case BLOCKSMC:
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    event.y = mc.thePlayer.motionY = 0.42f;
                    MovementUtils.strafe(event, 0.49);
                } else if (!mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    MovementUtils.strafe(event);
                }
                break;
            case NORMAL:
                mc.thePlayer.motionX *= (this.useSpeed.getValue() ? this.speedModifier.getValue().floatValue() : 1);
                mc.thePlayer.motionZ *= (this.useSpeed.getValue() ? this.speedModifier.getValue().floatValue() : 1);
                break;
            case VULCAN:
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    int slot = spoofMode.getValue() == SpoofMode.SPOOF ? c09slot : mc.thePlayer.inventory.currentItem;
                    if (mc.thePlayer.inventory.getStackInSlot(slot) != null) {
                        if (mc.thePlayer.inventory.getStackInSlot(slot).stackSize >= 7) {
                            event.y = mc.thePlayer.motionY = 0.42f;
                            MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.85 : 2.1));
                        } else {
                            MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed() * 0.7);
                        }
                        if (this.vulcanTimer.reach(500)) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                            vulcanTimer.reset();
                        }
                        if (this.vulcanTimer.getMs() == 75 + RandomUtils.nextInt(0, 100)) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                        }
                    }
                }
                break;
        }

        if (this.tower.getValue() && mc.gameSettings.keyBindJump.isKeyDown()
                && (!MovementUtils.isMoving() || this.towerMove.getValue())) {
            int count = this.getItemSlot(true);

            if (count > 0) {
                switch (this.towerMode.getValue()) {
                    case NCP:
                        if (!MovementUtils.isMoving()) {
                            MovementUtils.strafe(event, 0);
                        }

                        if (this.blockEntry != null) {
                            event.y = mc.thePlayer.motionY = (mc.thePlayer.ticksExisted % 10 == 0 ? 0.02f : 0.42f);
                        }
                        break;

                    case VULCAN:
                        /*if (!MovementUtils.isMoving()) {
                            MovementUtils.strafe(event, 0);
                        }
                        if (this.blockEntry != null) {
                            if (mc.thePlayer.onGround) {
                                event.y = mc.thePlayer.motionY = 0.42f;
                            } else if (mc.thePlayer.getAirTicks() == 4 && !MovementUtils.isMoving()) {
                                event.y = mc.thePlayer.motionY = -0.15f;
                            }
                        }*/
                        ItemStack itemStack = mc.thePlayer.inventory.mainInventory[this.spoofMode.getValue() == SpoofMode.SWITCH ? mc.thePlayer.inventory.currentItem : this.c09slot];
                        boolean canJump = itemStack == null || (itemStack.stackSize > 2);
                        if (canJump) {
                            // disables vulcan tower checks?? LOOL
                            for (int i = 0; i < 2; i++) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                                        new BlockPos(-1, -1, -1),
                                        255, mc.thePlayer.getCurrentEquippedItem(),
                                        0F, 0F, 0F));
                            }

                            if (mc.thePlayer.movementInput.jump && !mc.thePlayer.isPotionActive(Potion.jump) && !MovementUtils.isMoving()) {
                                event.y = mc.thePlayer.motionY = 0.42f;
                                mc.timer.timerSpeed = 0.9f;
                            } else {
                                mc.timer.timerSpeed = 1;
                            }
                        }
                        break;

                    case FAST:
                        if (!MovementUtils.isMoving()) {
                            MovementUtils.strafe(event, 0);
                        }

                        if (this.blockEntry != null) {
                            event.y = mc.thePlayer.motionY = (MovementUtils.isMoving() ? 0.42f : 0);
                        }
                        break;

                    case HYPIXEL:
                        /*
                        if (!mc.thePlayer.isPotionActive(Potion.jump)) {
                            boolean onHypixel = (ServerUtils.isOnServer("mc.hypixel.net") ||
                                    ServerUtils.isOnServer("hypixel.net")) && !ServerUtils.compromised;

                            if (!MovementUtils.isMoving()) {
                                MovementUtils.strafe(event, 0);
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.jump();
                                    event.y = mc.thePlayer.motionY = 0.419f;
                                }
                            } else {
                                MovementUtils.strafe(event, (mc.thePlayer.getDiagonalTicks() > 0 ?
                                        0.14 - MovementUtils.getHypixelFunny() * 1E-4 : 0.2 - MovementUtils.getHypixelFunny() * 1E-4));

                                double offset = onHypixel ? 0.41 : 0.15;
                                boolean towerMove = PlayerUtils.isOnGround(offset) && MovementUtils.isMoving();
                                boolean shouldRun = !MovementUtils.isMoving() || towerMove;

                                if (this.blockEntry != null && shouldRun) {
                                    long stopTime = 1000L;
                                    if (mc.thePlayer.ticksExisted % 4 == 0) {
                                        event.y = mc.thePlayer.motionY = 0.4199f;
                                    }
                                    if (this.towerTimer.reach(stopTime)) {
                                        if (onHypixel || mc.thePlayer.getDiagonalTicks() > 0) {
                                            event.y = mc.thePlayer.motionY = -0.05f;
                                        }

                                        this.towerTimer.reset();
                                    }
                                }
                            }
                        }
                        break;*/
                        if (!mc.thePlayer.isPotionActive(Potion.jump)) {
                            boolean onHypixel = (ServerUtils.isOnServer("mc.hypixel.net") ||
                                    ServerUtils.isOnServer("hypixel.net")) && !ServerUtils.compromised;

                            if (!onHypixel) {
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.jump();
                                }
                            } else {
                                if (this.blockEntry != null) {
                                    event.y = mc.thePlayer.motionY = 0.42f;
                                }
                            }
                        }
                        break;

                    case TELEPORT:
                        if (!MovementUtils.isMoving()) {
                            MovementUtils.strafe(event, 0);
                        }

                        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).down()).getBlock() != Blocks.air) {
                            event.y = mc.thePlayer.motionY = 0;

                            if (mc.thePlayer.ticksExisted % 2 == 0) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ);
                            }
                        }
                        break;

                    case VERUS:
                        if (!MovementUtils.isMoving()) {
                            MovementUtils.strafe(event, 0);
                        }

                        if (this.blockEntry != null) {
                            event.y = mc.thePlayer.motionY = (MovementUtils.isMoving() ? 0.5f : 0f);
                        }
                        break;

                    case LEGIT:
                        if (mc.gameSettings.keyBindJump.isKeyDown() && PlayerUtils.isMathGround()) {
                            mc.gameSettings.keyBindJump.pressed = true;
                        }
                        break;
                }
            }
        }
    };

    @EventHandler
    EventCallback<SafeWalkEvent> onSafeWalk = event -> {
        if (safeWalk.getValue() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) event.cancel();
    };

    @EventHandler
    EventCallback<TickEvent> onTick = event -> {
        if (this.timerSpeed.getValue().floatValue() != 1f) {
            mc.timer.timerSpeed = this.timerSpeed.getValue().floatValue();
        }
        if (!(mc.currentScreen instanceof GuiMultiplayer) && !(mc.getCurrentServerData() == null) &&
                mc.getCurrentServerData().serverIP.contains("hypixel")) {
            if (this.scaffoldMode.getValue() == ScaffoldMode.HYPIXEL) {
                if (PacketBalance.getInstance().getBalance() > 0) {
                    mc.timer.timerSpeed = 1f;
                }
            }
        }
    };

    private void place(int slot) {
        if (this.expand.getValue().doubleValue() > 0 && (this.tower.getValue()
                && !mc.gameSettings.keyBindJump.isKeyDown() || !this.tower.getValue())) {
            for (double i = 0; i < this.expand.getValue().doubleValue(); i += 0.5) {
                double dX = -Math.sin(Math.toRadians(MovementUtils.getDirection())) * i;
                double dZ = Math.cos(Math.toRadians(MovementUtils.getDirection())) * i;

                boolean canGoDown = this.downwards.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
                        && mc.currentScreen == null && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());

                double blockBelowY = !canGoDown && MovementUtils.isMoving() ? startY : mc.thePlayer.posY;

                BlockPos blockBelow1 = canGoDown ? new BlockPos(mc.thePlayer.posX + dX, mc.thePlayer.posY - 0.5,
                        mc.thePlayer.posZ + dZ).down()
                        : new BlockPos(mc.thePlayer.posX + dX, blockBelowY, mc.thePlayer.posZ + dZ).down();

                this.blockEntry = this.getBlockEntry(new BlockPos(blockBelow1));

                if (this.blockEntry != null) {
                    if (this.blockEntry.facing == EnumFacing.UP || this.blockEntry.facing == EnumFacing.DOWN) {
                        return;
                    }
                }

                if (BlockUtils.getBlockAtPos(blockBelow1) == Blocks.air) {
                    this.placeBlock(this.blockEntry, slot);
                }
            }

            return;
        }

        this.placeBlock(this.blockEntry, slot);
    }

    private void placeBlock(BlockEntry blockEntry, int slot) {
        if (blockEntry == null || slot == -1) return;

        if (ProtocolUtils.isOneDotEight()) {
            if (this.swing.getValue()) {
                mc.thePlayer.swingItem();
            } else {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C0APacketAnimation());
            }
        }

        BlockUtils.placeBlock(
                this.blockEntry.getPosition(), this.blockEntry.getFacing(), this.blockEntry.getVector(),
                mc.thePlayer.inventory.getStackInSlot(slot)
        );

        if (!ProtocolUtils.isOneDotEight()) {
            if (this.swing.getValue()) {
                mc.thePlayer.swingItem();
            } else {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C0APacketAnimation());
            }
        }
    }

    private int getItemSlot(boolean count) {
        int itemCount = (count ? 0 : -1);

        for (int i = 8; i >= 0; i--) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                if (count) {
                    itemCount += itemStack.stackSize;
                } else {
                    itemCount = i;
                }
            }
        }

        return itemCount;
    }

    private BlockEntry getBlockEntry(BlockPos pos) {
        for (EnumFacing facingVal : EnumFacing.values()) {
            BlockPos offset = pos.offset(facingVal);
            if (isValid(mc.theWorld.getBlockState(offset).getBlock())) {
                return new BlockEntry(offset, invert[facingVal.ordinal()]);
            }
        }
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(face, 1);
            for (EnumFacing face2 : EnumFacing.values()) {
                if (face2 == EnumFacing.DOWN || face2 == EnumFacing.UP) {
                    continue;
                }
                BlockPos offset = offsetPos.offset(face2);
                if (isValid(mc.theWorld.getBlockState(offset).getBlock())) {
                    return new BlockEntry(offset, invert[face2.ordinal()]);
                }
            }
        }
        return null;
    }

    private int getBlockCount(Boolean count) {
        int itemCount = (count ? 0 : -1);

        for (int i = 36; i >= 0; i--) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && !BlockUtils.INVALID_BLOCKS.contains(((ItemBlock) itemStack.getItem()).getBlock())) {
                if (count) {
                    itemCount += itemStack.stackSize;
                } else {
                    itemCount = i;
                }
            }
        }

        return itemCount;
    }

    private boolean isValid(Block block) {
        return !BlockUtils.INVALID_BLOCKS.contains(block);
    }

    @AllArgsConstructor
    private enum SpoofMode {
        SWITCH("Switch"),
        SPOOF("Spoof");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum TowerMode {
        NCP("NCP"),
        VULCAN("Vulcan"),
        FAST("Fast"),
        HYPIXEL("Hypixel"),
        VERUS("Verus"),
        LEGIT("Legit"),
        TELEPORT("Teleport");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum RotationMode {
        SNAP("Snap"),
        OLD("Old"),
        NEW("New"),
        HYPIXEL("Hypixel"),
        FACING("Facing"),
        MMC("MMC"),
        STATIC("Static"),
        SMOOTH("Smooth"),
        BACKWARDS("Backwards"),
        SMOOTHGCD("Smooth GCD"),
        VULCAN("Vulcan");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum ScaffoldMode {
        NORMAL("Normal"),
        BLOCKSMC("BlocksMC"),
        HYPIXEL("Hypixel"),
        VULCAN("Vulcan");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum HypixelMode {
        NORMAL("Normal"),
        KEEPY("KeepY");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public String getSuffix() {
        return this.scaffoldMode.getValue().label;
    }

    @AllArgsConstructor
    @Getter
    public static class CustomHitVec {
        private final float x;
        private final float y;
        private final float z;
        private final float scale;
    }

    @Getter
    @Setter
    public static class BlockEntry {
        private final BlockPos position;
        private final EnumFacing facing;
        private final Vec3 vector;

        public BlockEntry(BlockPos position, EnumFacing facing) {
            this.position = position;
            this.facing = facing;

            CustomHitVec customHitVec = new CustomHitVec(.5f, .5f, .5f, .5f);
            this.vector = new Vec3(position)
                    .add(new Vec3(customHitVec.getX(), customHitVec.getY(), customHitVec.getZ()))
                    .add(new Vec3(facing.getDirectionVec()).scale(customHitVec.getScale()));
        }
    }
}