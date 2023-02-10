/**
 * @project Myth
 * @author CodeMan
 * @at 06.08.22, 23:19
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.rotation.RotationUtil;
import dev.myth.events.PacketEvent;
import dev.myth.events.Render2DEvent;
import dev.myth.events.SafeWalkEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.display.HUDFeature;
import dev.myth.features.movement.SpeedFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.ListSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

@Feature.Info(name = "Scaffold", description = "Places blocks below your feet", category = Feature.Category.PLAYER)
public class ScaffoldFeature extends Feature {

    public final BooleanSetting rotate = new BooleanSetting("Rotate", false);
    public final ListSetting<RotationSettings> rotationSettings = new ListSetting<>("Rotation Settings", RotationSettings.KEEP).addDependency(rotate::getValue);
    public final EnumSetting<TowerMode> towerMode = new EnumSetting<>("Tower", TowerMode.NCP);
    public final NumberSetting towerTimer = new NumberSetting("Tower Timer", 1, 1, 2, 0.01).addDependency(() -> !towerMode.is(TowerMode.NONE)).addValueAlias(1, "Off");
    public final EnumSetting<SprintSettings> sprintMode = new EnumSetting<>("Sprint", SprintSettings.NONE);
    public final EnumSetting<PlaceTiming> placeTiming = new EnumSetting<>("PlaceTiming", PlaceTiming.PRE);
    public final ListSetting<Bypasses> bypasses = new ListSetting<>("Bypasses", Bypasses.WATCHDOG);
    public final BooleanSetting keepY = new BooleanSetting("KeepY", false);
    public final BooleanSetting autojump = new BooleanSetting("Auto Jump", false).addDependency(keepY::getValue);
    public final BooleanSetting swing = new BooleanSetting("Swing", false);
    public final BooleanSetting safewalk = new BooleanSetting("SafeWalk", false);
    public final BooleanSetting silentSwitch = new BooleanSetting("SilentSwitch", false);
    public final NumberSetting delay = new NumberSetting("Delay", 0, 0, 20, 1).setSuffix("t");
    public final NumberSetting staticYaw = new NumberSetting("StaticYaw", 180, -180, 180, 1).addDependency(() -> rotationSettings.is(RotationSettings.STATIC_YAW) && rotationSettings.isVisible());
    public final NumberSetting staticPitch = new NumberSetting("StaticPitch", 80, -90, 90, 0.1).addDependency(() -> rotationSettings.is(RotationSettings.STATIC_PITCH) && rotationSettings.isVisible());
    public final NumberSetting maxAngleChange = new NumberSetting("MaxAngleChange", 30, 1, 180, 0.25).addDependency(rotate::getValue).addValueAlias(180, "Instant");

    private BlockData blockData;
    private long lastPlaced = 0;
    private int lastSentSlot = -1, oldSlot = -1, slot;
    private float yaw, pitch;
    private double startY;
    private int state, placed;
    final EnumFacing[] invert = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};

    private SpeedFeature speedFeature;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {

        slot = getSlot();
        if (slot == -1) return;

        if (speedFeature == null) {
            speedFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(SpeedFeature.class);
        }

        if (event.getState() == EventState.PRE && keepY.getValue() && autojump.getValue() && MovementUtil.isOnGround() && getPlayer().isMoving())
            getPlayer().jump();

        if (getY() < startY || MovementUtil.isOnGround() || !MovementUtil.isMoving() || (getGameSettings().keyBindJump.isKeyDown() && speedFeature.isEnabled()))
            startY = getY();

        switch (sprintMode.getValue()) {
            case NONE:
                getPlayer().setSprinting(false);
                getGameSettings().keyBindSprint.pressed = false;
                break;
            case PACKET:
                sendPacketUnlogged(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SPRINTING));
                break;
            case NORMAL:
                if (event.getState() == EventState.PRE) getPlayer().setSprinting(true);
                break;
        }

        if (!silentSwitch.getValue()) getPlayer().inventory.currentItem = slot;
        else if(slot != getPlayer().inventory.currentItem) getPlayer().sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));

        if (bypasses.is(Bypasses.WATCHDOG)) {
            if (getPlayer().isPotionActive(Potion.moveSpeed)) {
                getPlayer().motionX *= 0.9;
                getPlayer().motionZ *= 0.9;
            }
        }
        if(bypasses.is(Bypasses.VULCAN)) {
            if(placed % 5 == 0) {
                event.setSneaking(true);
            }
        }

        boolean isTower = /*!MovementUtil.isMoving() && */Keyboard.isKeyDown(getGameSettings().keyBindJump.getKeyCode()) && !getPlayer().isPotionActive(Potion.jump);

        switch (event.getState()) {
            case UPDATE: {
                if(blockData == null) return;
                if(placeTiming.is(PlaceTiming.PRE) && !isTower) {
                    this.performPlacement(blockData, slot);
                }
                break;
            }
            case PRE: {

                lastPlaced++;

                double posY = 1;
                BlockPos playerPos = new BlockPos(getPlayer().posX, getPlayer().posY - posY, getPlayer().posZ);

                if (keepY.getValue()) {
                    playerPos = new BlockPos(getPlayer().posX, startY - posY, getPlayer().posZ);
                }

                blockData = findBlockData(playerPos);

                if (blockData == null) return;

//                if(placed == 5) {
//                    placed = 0;
//                    event.setSneaking(true);
//                    doLog("Sneaking");
//                }

                if (rotate.getValue()) {
                    boolean isAir = MC.theWorld.getBlockState(blockData.pos.offset(blockData.facing)).getBlock().getMaterial() == Material.air;
//                    if (yaw == Float.MAX_VALUE || pitch == Float.MAX_VALUE) {
                        boolean isStaticYaw = rotationSettings.is(RotationSettings.STATIC_YAW), isStaticPitch = rotationSettings.is(RotationSettings.STATIC_PITCH);

                        Vec3 vec3 = MC.thePlayer.getPositionEyes(1F);
                        Vec3 vec31 = RotationUtil.getVectorForRotation(pitch, yaw);
                        Vec3 vec32 = vec3.addVector(vec31.xCoord * 4, vec31.yCoord * 4, vec31.zCoord * 4);

                        AxisAlignedBB bb = AxisAlignedBB.fromBounds(blockData.pos.getX(), blockData.pos.getY(), blockData.pos.getZ(), blockData.pos.getX() + 1, blockData.pos.getY() + 1, blockData.pos.getZ() + 1);

                        if(!rotationSettings.is(RotationSettings.KEEP) || (bb.calculateIntercept(vec3, vec32) == null && isAir)) {
                            float[] rotations = {0F, 0F};
                            if (!isStaticYaw || !isStaticPitch) {
                                rotations = RotationUtil.getRotationsToBlock(blockData.pos, blockData.facing);
                            }
                            if (!isStaticYaw) {
                                yaw = rotations[0];
                            } else {
                                yaw = MovementUtil.getDirection() + staticYaw.getValue().floatValue();
                            }
                            if (!isStaticPitch) {
                                pitch = rotations[1];
                            } else {
                                pitch = MovementUtil.isMoving() ? staticPitch.getValue().floatValue() : 85F;
                            }
                            if (rotationSettings.is(RotationSettings.RANDOM)) {
                                yaw += Math.random() * 5 - 2.5;
                                pitch += Math.random() * 5 - 2.5;
                            }
                            pitch = MathHelper.clamp_float(pitch, -90, 90);
                        }
//                    }

                    if (!rotationSettings.is(RotationSettings.SNAP) || isAir) {
                        RotationUtil.doRotation(event, new float[]{yaw, pitch}, rotationSettings.is(RotationSettings.SNAP) ? 180 : maxAngleChange.getValue().floatValue(), true);
                    }
                }

                if(placeTiming.is(PlaceTiming.PRE) && isTower) {
                    this.performPlacement(blockData, slot);
                }

                switch (towerMode.getValue()) {
                    case NORULES:
                    case NCP:

                        if (!isTower) {
                            if(towerTimer.getValue() != 1) MC.timer.timerSpeed = 1F;
                            break;
                        }

                        if(MovementUtil.isMoving()) {
//                            if(MovementUtil.isOnGround()) {
//                                doLog("Silly");
//                                sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + 0.42F, getPlayer().posZ, false));
//                                sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + 0.753F, getPlayer().posZ, false));
//                                getPlayer().setPosition(getPlayer().posX, Math.floor(getPlayer().posY + 1), getPlayer().posZ);
//                            }
                            break;
                        }
                        KeyBinding.setKeyBindState(MC.gameSettings.keyBindJump.getKeyCode(), false);

                        if (getPlayer().posY - Math.floor(getPlayer().posY) <= 0.1) {
                            getPlayer().setPosition(getPlayer().posX, Math.floor(getPlayer().posY), getPlayer().posZ);
                            getPlayer().motionY = 0.42F;
                            MovementUtil.fakeJump();
                            if(towerMode.is(TowerMode.NORULES)) event.setOnGround(true);
                        }
                        if(towerMode.is(TowerMode.NORULES)) if(getPlayer().motionY == MovementUtil.JUMP_MOTIONS[1]) event.setOnGround(true);
                        getPlayer().motionX = getPlayer().motionZ = 0;
                        MC.timer.timerSpeed = towerTimer.getValue().floatValue();
                        break;
                    case DEV:
                        getPlayer().motionZ = 0.0;
                        getPlayer().motionZ = 0.0;
                        if (getGameSettings().keyBindJump.isKeyDown()) {
                            switch (state) {
                                case 0: {
                                    getPlayer().isAirBorne = true;
                                    getPlayer().motionY = 0.41234324432432432;
                                    ++state;
                                    break;
                                }
                                case 1: {
                                    getPlayer().motionY = 0.398778765654564345;
                                    ++state;
                                    break;
                                }
                                case 2: {
                                    getPlayer().motionY = 0.2667587865643345;
                                    ++state;
                                }
                            }
                        }
                        if (getGameSettings().keyBindJump.isKeyDown() && state <= 2 || !MovementUtil.isOnGround(1.0)) {
                            break;
                        }
                        state = 0;
                        break;
                }

                break;
            }
            case POST: {
                if (blockData == null) return;

                if (placeTiming.is(PlaceTiming.POST)) {
                    this.performPlacement(blockData, slot);
                }
                break;
            }
        }
    };

    @Handler
    public final Listener<Render2DEvent> render2DEventListener = event -> {
        if (getSlot() == -1) return;

        ItemStack stack = getPlayer().inventory.getStackInSlot(getSlot());

        if (stack != null && stack.getItem() instanceof ItemBlock) {
            ScaledResolution sr = new ScaledResolution(MC);
            final int x = sr.getScaledWidth() / 2;
            final int y = sr.getScaledHeight() / 2 - 30;

            // RenderUtil.drawRect(x, y, 30, 30, new Color(0, 0, 0, 100).getRGB());

//            RenderHelper.enableGUIStandardItemLighting();
//            MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x - 8, y + 2);
//            RenderHelper.disableStandardItemLighting();
            if (ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class).fontMode.is(HUDFeature.FontMode.MINECRAFT))
                MC.fontRendererObj.drawStringWithShadow(getBlockCount() + " Blocks", x - MC.fontRendererObj.getStringWidth(getBlockCount() + " Blocks") / 2f, y + MC.fontRendererObj.FONT_HEIGHT, -1);
            else {
                HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);
                hudFeature.getFont().drawStringWithShadow(getBlockCount() + " Blocks", x - hudFeature.getStringWidth(getBlockCount() + " Blocks") / 2f, y + hudFeature.getFontHeight(), -1);
            }
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getState() != EventState.SENDING) return;

        if (sprintMode.is(SprintSettings.SPOOF)) {
            if (event.getPacket() instanceof C0BPacketEntityAction) {
                C0BPacketEntityAction packet = event.getPacket();
                if (packet.getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                    event.setCancelled(true);
                }
                if (packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    event.setCancelled(true);
                }
            }
        }

        if (event.getPacket() instanceof C09PacketHeldItemChange) {
            C09PacketHeldItemChange packet = event.getPacket();

            if (!silentSwitch.getValue()) return;

//            if (packet.getSlotId() != slot) {
//                event.setCancelled(true);
//                return;
//            }
            if (packet.getSlotId() == lastSentSlot) {
                event.setCancelled(true);
                return;
            }
            lastSentSlot = packet.getSlotId();
        }
    };

    @Handler
    public final Listener<SafeWalkEvent> safeWalkEventListener = event -> {
        if (safewalk.getValue()) {
            event.setCancelled(true);
        }
    };

    @Override
    public void onEnable() {
        super.onEnable();
        if (getPlayer() == null) {
            toggle();
            return;
        }
        state = 0;
        placed = 0;
        lastPlaced = 0;
        lastSentSlot = -1;
        oldSlot = getPlayer().inventory.currentItem;
        blockData = null;
        startY = getY();
        yaw = Float.MAX_VALUE;
        pitch = Float.MAX_VALUE;

        if(sprintMode.is(SprintSettings.SPOOF)) {
            if(getPlayer().isSprinting()) {
                sendPacketUnlogged(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(towerTimer.getValue() != 1) MC.timer.timerSpeed = 1F;
        if (getPlayer() == null) return;

        if (silentSwitch.getValue()) {
            if(slot != getPlayer().inventory.currentItem && slot != -1) getPlayer().sendQueue.addToSendQueue(new C09PacketHeldItemChange(getPlayer().inventory.currentItem = oldSlot));
//            MC.playerController.syncCurrentPlayItem();
        } else {
            getPlayer().inventory.currentItem = oldSlot;
        }
    }

    private void performPlacement(BlockData blockData, int slot) {
        if (lastPlaced < delay.getValue()) return;

        if (MC.playerController.onPlayerRightClick(getPlayer(), MC.theWorld, getPlayer().inventory.getStackInSlot(slot), blockData.pos, blockData.facing, blockData.hitVec)) {
            placed++;
            if (swing.getValue()) {
                getPlayer().swingItem();
            } else {
                getPlayer().sendQueue.addToSendQueue(new C0APacketAnimation());
            }
        }
        lastPlaced = 0;
    }

    public BlockData findBlockData(BlockPos pos) {
        if (!MovementUtil.isOnGround()) {
            if (getPlayer().getEntityBoundingBox().offset(0, -0.01F, 0).intersectsWith(AxisAlignedBB.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1))) {
                pos = pos.add(0, -0.5, 0);
            }
        }

        if(Keyboard.isKeyDown(getGameSettings().keyBindSneak.getKeyCode())) {
            pos = pos.add(0, -1, 0);
            KeyBinding.setKeyBindState(getGameSettings().keyBindSneak.getKeyCode(), false);
            getPlayer().setSneaking(false);
        }

        for (EnumFacing facingVal : EnumFacing.values()) {
            BlockPos offset = pos.offset(facingVal);
            if (isValidBlock(MC.theWorld.getBlockState(offset).getBlock(), false)) {
                return new BlockData(offset, invert[facingVal.ordinal()]);
            }
        }
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(face, 1);
            for (EnumFacing face2 : EnumFacing.values()) {
                if (face2 == EnumFacing.DOWN || face2 == EnumFacing.UP) {
                    continue;
                }
                BlockPos offset = offsetPos.offset(face2);
                if (isValidBlock(MC.theWorld.getBlockState(offset).getBlock(), false)) {
                    return new BlockData(offset, invert[face2.ordinal()]);
                }
            }
        }
        return null;
    }

    public static boolean isValidBlock(final Block block, final boolean toPlace) {
        if (block instanceof BlockContainer) return false;
        if (toPlace && (block instanceof BlockFalling || !block.isFullCube())) return false;
        final Material material = block.getMaterial();
        return !material.isReplaceable() && !material.isLiquid();
    }

    private int getBlockCount() {
        int count = 0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = getPlayer().inventory.getStackInSlot(i);
            if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemBlock)) continue;
            count += stack.stackSize;
        }
        return count;
    }

    private int getSlot() {
        for (int k = 0; k < 9; ++k) {
            final ItemStack itemStack = getPlayer().inventory.mainInventory[k];
            if (itemStack == null) continue;
            Block block = Block.getBlockFromItem(itemStack.getItem());
            if (block != null && isValidBlock(block, true) && itemStack.stackSize >= 1) {
                return k;
            }
        }
        return -1;
    }

    public static class BlockData {
        public BlockPos pos;
        public EnumFacing facing;
        public Vec3 hitVec;

        public BlockData(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;

            Vec3i directionVec = facing.getDirectionVec();
            this.hitVec = new Vec3(pos).addVector(0.5, 0.5, 0.5).add(new Vec3(directionVec.getX() * 0.5, directionVec.getY() * 0.5, directionVec.getZ() * 0.5));
        }
    }

    public enum PlaceTiming {
        PRE("Pre"), POST("Post");

        private final String name;

        PlaceTiming(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum SprintSettings {
        NONE("None"), NORMAL("Normal"), PACKET("Packet"), SPOOF("Spoof");

        private final String name;

        SprintSettings(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }


    public enum RotationSettings {
        STATIC_YAW("Static Yaw"), STATIC_PITCH("Static Pitch"), KEEP("Keep"), RANDOM("Randomize"), SNAP("Snap");

        private final String name;

        RotationSettings(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum Bypasses {
        WATCHDOG("Watchdog"), VULCAN("Vulcan");

        private final String name;

        Bypasses(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum TowerMode {
        NONE("None"),
        NCP("NCP"),
        NORULES("NoRules"),
        DEV("Dev");

        private final String name;

        TowerMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}