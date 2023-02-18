/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C03PacketPlayer
 *  net.minecraft.network.play.client.C09PacketHeldItemChange
 *  net.minecraft.network.play.client.C0APacketAnimation
 *  net.minecraft.network.play.client.C0BPacketEntityAction
 *  net.minecraft.network.play.client.C0BPacketEntityAction$Action
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.event.impl.move.EventPreUpdate
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.impl.other.RightClickEvent
 *  vip.astroline.client.service.event.impl.packet.EventSendPacket
 *  vip.astroline.client.service.event.impl.render.Event2D
 *  vip.astroline.client.service.event.impl.render.EventShader
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.movement.Scaffold$BlockData
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.service.module.value.ModeValue
 *  vip.astroline.client.storage.utils.angle.RotationUtil
 *  vip.astroline.client.storage.utils.other.MoveUtils
 *  vip.astroline.client.storage.utils.other.TimeHelper
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.service.module.impl.movement;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.event.impl.move.EventPreUpdate;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.impl.other.RightClickEvent;
import vip.astroline.client.service.event.impl.packet.EventSendPacket;
import vip.astroline.client.service.event.impl.render.Event2D;
import vip.astroline.client.service.event.impl.render.EventShader;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.movement.Scaffold;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.service.module.value.ModeValue;
import vip.astroline.client.storage.utils.angle.RotationUtil;
import vip.astroline.client.storage.utils.other.MoveUtils;
import vip.astroline.client.storage.utils.other.TimeHelper;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

/*
 * Exception performing whole class analysis ignored.
 */
public final class Scaffold
extends Module {
    public static ModeValue keep_y_mode = new ModeValue("Scaffold", "Keep Y Mode", "Always", new String[]{"Speed Only"});
    public static BooleanValue keep_y = new BooleanValue("Scaffold", "Keep Y", Boolean.valueOf(false));
    public static BooleanValue tower_move = new BooleanValue("Scaffold", "Tower Move", Boolean.valueOf(false));
    public static BooleanValue tower = new BooleanValue("Scaffold", "Tower", Boolean.valueOf(false));
    public static BooleanValue swapper = new BooleanValue("Scaffold", "Swapper", Boolean.valueOf(false));
    public static BooleanValue silent = new BooleanValue("Scaffold", "Silent", Boolean.valueOf(false));
    public static FloatValue slot = new FloatValue("Scaffold", "Slot", 6.0f, 1.0f, 8.0f, 1.0f);
    private List<Block> invalid;
    private List<Block> blacklistedBlocks;
    private final TimeHelper timer = new TimeHelper();
    private BlockData blockData;
    private BlockPos blockBelow;
    private int floorY;
    private int oldSlot;
    private int towerTick;
    private boolean canDisable;
    private boolean canPlace;
    private float yaw;
    private float pitch;

    public Scaffold() {
        super("Scaffold", Category.Movement, 34, false);
        this.invalid = Arrays.asList(Blocks.anvil, Blocks.air, Blocks.water, Blocks.fire, Blocks.flowing_water, Blocks.lava, Blocks.skull, Blocks.trapped_chest, Blocks.flowing_lava, Blocks.chest, Blocks.enchanting_table, Blocks.ender_chest, Blocks.crafting_table);
        this.blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.wooden_slab, Blocks.wooden_slab, Blocks.chest, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.skull, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.tnt, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.trapped_chest, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence, Blocks.activator_rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.redstone_torch, Blocks.acacia_stairs, Blocks.birch_stairs, Blocks.brick_stairs, Blocks.dark_oak_stairs, Blocks.jungle_stairs, Blocks.nether_brick_stairs, Blocks.oak_stairs, Blocks.quartz_stairs, Blocks.red_sandstone_stairs, Blocks.sandstone_stairs, Blocks.spruce_stairs, Blocks.stone_brick_stairs, Blocks.stone_stairs, Blocks.wooden_slab, Blocks.double_wooden_slab, Blocks.stone_slab, Blocks.double_stone_slab, Blocks.stone_slab2, Blocks.double_stone_slab2, Blocks.web, Blocks.gravel, Blocks.daylight_detector_inverted, Blocks.daylight_detector, Blocks.soul_sand, Blocks.piston, Blocks.piston_extension, Blocks.piston_head, Blocks.sticky_piston, Blocks.iron_trapdoor, Blocks.ender_chest, Blocks.end_portal, Blocks.end_portal_frame, Blocks.standing_banner, Blocks.wall_banner, Blocks.deadbush, Blocks.slime_block, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate);
    }

    private boolean hotBarContainBlock() {
        int i = 36;
        while (i < 45) {
            try {
                ItemStack slotStack = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (slotStack != null && slotStack.getItem() != null && slotStack.getItem() instanceof ItemBlock) {
                    if (this.isValid(slotStack.getItem())) return true;
                }
                ++i;
            }
            catch (Exception exception) {}
        }
        return false;
    }

    private boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        ItemBlock itemBlock = (ItemBlock)item;
        Block block = itemBlock.getBlock();
        return !this.getBlacklistedBlocks().contains(block);
    }

    private int getBiggestBlockSlotInv() {
        int slot = -1;
        int size = 0;
        if (this.getBlockCount() == 0) {
            return -1;
        }
        int i = 9;
        while (i < 36) {
            Slot s = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i);
            if (s.getHasStack()) {
                Item item = s.getStack().getItem();
                ItemStack is = s.getStack();
                if (item instanceof ItemBlock && this.isValid(item) && is.stackSize > size) {
                    size = is.stackSize;
                    slot = i;
                }
            }
            ++i;
        }
        return slot;
    }

    private int getBiggestBlockSlotHotbar() {
        int slot = -1;
        int size = 0;
        if (this.getBlockCount() == 0) {
            return -1;
        }
        int i = 36;
        while (i < 45) {
            Slot s = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i);
            if (s.getHasStack()) {
                Item item = s.getStack().getItem();
                ItemStack is = s.getStack();
                if (item instanceof ItemBlock && this.isValid(item) && is.stackSize > size) {
                    size = is.stackSize;
                    slot = i;
                }
            }
            ++i;
        }
        return slot;
    }

    public boolean isAirBlock(Block block) {
        if (!block.getMaterial().isReplaceable()) return false;
        return !(block instanceof BlockSnow) || !(block.getBlockBoundsMaxY() > 0.125);
    }

    public double[] getExpandCoords(double y) {
        double expandDist;
        BlockPos underPos = new BlockPos(Scaffold.mc.thePlayer.posX, y, Scaffold.mc.thePlayer.posZ);
        Block underBlock = Scaffold.mc.theWorld.getBlockState(underPos).getBlock();
        MovementInput movementInput = Scaffold.mc.thePlayer.movementInput;
        float forward = movementInput.getMoveForward();
        float strafe = movementInput.getMoveStrafe();
        float yaw = Scaffold.mc.thePlayer.rotationYaw;
        double xCalc = -999.0;
        double zCalc = -999.0;
        double dist = 0.0;
        double d = expandDist = Scaffold.mc.thePlayer.getDistanceY((double)this.blockBelow.getY()) == 1.0 ? 0.0 : 0.0;
        while (!this.isAirBlock(underBlock)) {
            xCalc = Scaffold.mc.thePlayer.posX;
            zCalc = Scaffold.mc.thePlayer.posZ;
            if ((dist += 1.0) > expandDist) {
                dist = expandDist;
            }
            if (dist == expandDist) return new double[]{xCalc, zCalc};
            underPos = new BlockPos(xCalc += ((double)forward * 0.45 * (double)MathHelper.cos((float)((float)Math.toRadians(yaw + 90.0f))) + (double)strafe * 0.45 * (double)MathHelper.sin((float)((float)Math.toRadians(yaw + 90.0f)))) * dist, y, zCalc += ((double)forward * 0.45 * (double)MathHelper.sin((float)((float)Math.toRadians(yaw + 90.0f))) - (double)strafe * 0.45 * (double)MathHelper.cos((float)((float)Math.toRadians(yaw + 90.0f)))) * dist);
            underBlock = Scaffold.mc.theWorld.getBlockState(underPos).getBlock();
        }
        return new double[]{xCalc, zCalc};
    }

    private boolean towering() {
        return tower.getValue() != false && !Scaffold.mc.thePlayer.isMoving() && Keyboard.isKeyDown((int)Scaffold.mc.gameSettings.keyBindJump.getKeyCode()) && !Scaffold.mc.thePlayer.isPotionActive(Potion.jump);
    }

    private boolean keepY() {
        return keep_y.getValue() != false && (keep_y_mode.isCurrentMode("Always") || keep_y_mode.isCurrentMode("Speed Only") && Astroline.INSTANCE.moduleManager.getModule("Speed").isToggled() && !Keyboard.isKeyDown((int)Scaffold.mc.gameSettings.keyBindJump.getKeyCode()));
    }

    private boolean towerMoving() {
        return tower_move.getValue() != false && !this.keepY() && Keyboard.isKeyDown((int)Scaffold.mc.gameSettings.keyBindJump.getKeyCode()) && Scaffold.mc.thePlayer.isMoving();
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate event) {
        double yPos;
        boolean air;
        block16: {
            int spoofSlot;
            int bestSlot;
            block14: {
                block17: {
                    block18: {
                        block15: {
                            if (Scaffold.mc.thePlayer.isSprinting()) {
                                Scaffold.mc.thePlayer.setSprinting(false);
                            }
                            if (!swapper.getValue().booleanValue()) break block15;
                            if (!this.timer.delay(250L)) break block16;
                            int bestInvSlot = this.getBiggestBlockSlotInv();
                            int bestHotBarSlot = this.getBiggestBlockSlotHotbar();
                            bestSlot = bestHotBarSlot > 0 ? bestHotBarSlot : bestInvSlot;
                            spoofSlot = 42;
                            if (bestHotBarSlot > 0 && bestInvSlot > 0 && Scaffold.mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getHasStack() && Scaffold.mc.thePlayer.inventoryContainer.getSlot(bestHotBarSlot).getHasStack() && Scaffold.mc.thePlayer.inventoryContainer.getSlot((int)bestHotBarSlot).getStack().stackSize < Scaffold.mc.thePlayer.inventoryContainer.getSlot((int)bestInvSlot).getStack().stackSize) {
                                bestSlot = bestInvSlot;
                            }
                            if (!this.hotBarContainBlock()) break block17;
                            break block18;
                        }
                        if (this.getBlockCountHotBar() <= 0) {
                            Scaffold.mc.thePlayer.swap(this.getBiggestBlockSlotInv(), slot.getValue().intValue() - 1);
                        }
                        break block16;
                    }
                    for (int a = 36; a < 45; ++a) {
                        Item item;
                        if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(a).getHasStack() || !((item = Scaffold.mc.thePlayer.inventoryContainer.getSlot(a).getStack().getItem()) instanceof ItemBlock) || !this.isValid(item)) continue;
                        spoofSlot = a;
                        break block14;
                    }
                    break block14;
                }
                for (int a = 36; a < 45; ++a) {
                    if (Scaffold.mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) continue;
                    spoofSlot = a;
                    break;
                }
            }
            if (Scaffold.mc.thePlayer.inventoryContainer.getSlot((int)spoofSlot).slotNumber != bestSlot) {
                if (this.hotBarContainBlock()) {
                    Scaffold.mc.thePlayer.swap(bestSlot, spoofSlot - 36);
                } else {
                    Scaffold.mc.thePlayer.swap(bestSlot, slot.getValue().intValue() - 1);
                }
            }
            this.timer.reset();
        }
        if (!this.keepY() || Scaffold.mc.thePlayer.onGround) {
            this.floorY = MathHelper.floor_double((double)Scaffold.mc.thePlayer.posY);
        }
        double xPos = (air = this.isAirBlock(Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX, yPos = (double)(this.floorY - 1), Scaffold.mc.thePlayer.posZ)).getBlock())) ? Scaffold.mc.thePlayer.posX : this.getExpandCoords(yPos)[0];
        double zPos = air ? Scaffold.mc.thePlayer.posZ : this.getExpandCoords(yPos)[1];
        this.blockBelow = new BlockPos(xPos, yPos, zPos);
        boolean setBlockData = Scaffold.mc.theWorld.getBlockState(this.blockBelow).getBlock().getMaterial().isReplaceable() || Scaffold.mc.theWorld.getBlockState(this.blockBelow).getBlock() == Blocks.air;
        BlockData blockData = this.blockData = setBlockData ? this.getBlockData(this.blockBelow) : null;
        if (this.blockData != null) {
            EntityPig entity = new EntityPig((World)Scaffold.mc.theWorld);
            entity.posX = (double)BlockData.access$000((BlockData)this.blockData).getX() + 0.5;
            entity.posY = (double)BlockData.access$000((BlockData)this.blockData).getY() + 0.5;
            entity.posZ = (double)BlockData.access$000((BlockData)this.blockData).getZ() + 0.5;
            float[] rots = RotationUtil.getAngles((EntityLivingBase)entity);
            this.yaw = rots[0];
            this.pitch = rots[1];
        }
        event.setYaw(Scaffold.mc.thePlayer.isMoving() ? this.getYawBackward() : this.yaw);
        event.setPitch(Scaffold.mc.thePlayer.isMoving() ? this.pitch : 90.0f);
        Scaffold.mc.thePlayer.rotationPitchHead = Scaffold.mc.thePlayer.isMoving() ? this.pitch : 90.0f;
        Scaffold.mc.thePlayer.rotationYawHead = Scaffold.mc.thePlayer.isMoving() ? this.getYawBackward() : this.yaw;
        float f = Scaffold.mc.thePlayer.renderYawOffset = Scaffold.mc.thePlayer.isMoving() ? this.getYawBackward() : this.yaw;
        if (this.towerMoving()) {
            this.towerMove();
        }
        if (this.towering()) {
            this.tower();
        }
        if (this.towering() || this.towerMoving()) {
            Scaffold.mc.thePlayer.cameraPitch = 0.0f;
            Scaffold.mc.thePlayer.cameraYaw = 0.0f;
        } else {
            if (Scaffold.mc.thePlayer.getDistanceY((double)this.blockBelow.getY()) != 1.0) return;
            Scaffold.mc.thePlayer.cameraYaw = 0.1f;
            Scaffold.mc.thePlayer.onGround = true;
            Scaffold.mc.thePlayer.motionY = 0.0;
            event.setOnGround(true);
        }
    }

    private void towerMove() {
        if (MoveUtils.isOnGround((double)0.76) && !MoveUtils.isOnGround((double)0.75) && Scaffold.mc.thePlayer.motionY > 0.23 && Scaffold.mc.thePlayer.motionY < 0.25) {
            Scaffold.mc.thePlayer.motionY = (double)Math.round(Scaffold.mc.thePlayer.posY) - Scaffold.mc.thePlayer.posY;
        }
        if (MoveUtils.isOnGround((double)1.0E-4)) {
            Scaffold.mc.thePlayer.motionY = 0.41999998688698;
            Scaffold.mc.thePlayer.setMotion(0.9);
        } else {
            if (!(Scaffold.mc.thePlayer.posY >= (double)Math.round(Scaffold.mc.thePlayer.posY) - 1.0E-4)) return;
            if (!(Scaffold.mc.thePlayer.posY <= (double)Math.round(Scaffold.mc.thePlayer.posY) + 1.0E-4)) return;
            if (Keyboard.isKeyDown((int)Scaffold.mc.gameSettings.keyBindSneak.getKeyCode())) return;
            Scaffold.mc.thePlayer.motionY = 0.0;
        }
    }

    private void tower() {
        Scaffold.mc.thePlayer.setSprinting(false);
        Scaffold.mc.thePlayer.setSpeed(0.0);
        this.towerMove();
    }

    @EventTarget
    public void onRightClick(RightClickEvent event) {
        if (this.blockData == null) return;
        if (this.towering()) {
            if (!this.canPlace) return;
        }
        this.placeBlock();
    }

    @EventTarget
    public void onTower(EventSendPacket event) {
        C09PacketHeldItemChange packet;
        if (silent.getValue().booleanValue() && event.getPacket() instanceof C09PacketHeldItemChange && (packet = (C09PacketHeldItemChange)event.getPacket()).getSlotId() != this.neededSlot()) {
            event.setCancelled(true);
        }
        if (!(event.getPacket() instanceof C03PacketPlayer)) return;
        packet = (C03PacketPlayer)event.getPacket();
        if (!this.towering()) return;
        this.canPlace = !packet.isMoving();
    }

    public float getYawBackward() {
        float yaw = MathHelper.wrapAngleTo180_float((float)Scaffold.mc.thePlayer.rotationYaw);
        MovementInput input = Scaffold.mc.thePlayer.movementInput;
        float strafe = input.getMoveStrafe();
        float forward = input.getMoveForward();
        if (forward != 0.0f) {
            if (strafe < 0.0f) {
                yaw += forward < 0.0f ? 135.0f : 45.0f;
            } else if (strafe > 0.0f) {
                yaw -= forward < 0.0f ? 135.0f : 45.0f;
            } else {
                if (strafe != 0.0f) return MathHelper.wrapAngleTo180_float((float)(yaw - 180.0f));
                if (!(forward < 0.0f)) return MathHelper.wrapAngleTo180_float((float)(yaw - 180.0f));
                yaw -= 180.0f;
            }
        } else if (strafe < 0.0f) {
            yaw += 90.0f;
        } else {
            if (!(strafe > 0.0f)) return MathHelper.wrapAngleTo180_float((float)(yaw - 180.0f));
            yaw -= 90.0f;
        }
        return MathHelper.wrapAngleTo180_float((float)(yaw - 180.0f));
    }

    private void placeBlock() {
        if (silent.getValue().booleanValue()) {
            mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)new C09PacketHeldItemChange(this.neededSlot()));
        } else if (Scaffold.mc.thePlayer.inventory.currentItem != this.neededSlot()) {
            Scaffold.mc.thePlayer.inventory.currentItem = this.neededSlot();
        }
        ItemStack stack = Scaffold.mc.thePlayer.inventory.getStackInSlot(this.neededSlot());
        BlockPos pos = BlockData.access$000((BlockData)this.blockData);
        EnumFacing facing = BlockData.access$100((BlockData)this.blockData);
        Vec3 vector = MathHelper.getVec3((BlockPos)pos, (EnumFacing)facing);
        if (!Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, stack, pos, facing, vector)) return;
        if (!silent.getValue().booleanValue()) {
            Scaffold.mc.thePlayer.swingItem();
        } else {
            mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C0APacketAnimation());
        }
    }

    public int getBlockCount() {
        int blockCount = 0;
        int i = 0;
        while (i < 45) {
            if (Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !this.getBlacklistedBlocks().contains(((ItemBlock)item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
            ++i;
        }
        return blockCount;
    }

    private int getBlockCountHotBar() {
        int blockCount = 0;
        int i = 36;
        while (i < 45) {
            if (Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !this.getBlacklistedBlocks().contains(((ItemBlock)item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
            ++i;
        }
        return blockCount;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (Scaffold.mc.thePlayer.isSprinting()) {
            Scaffold.mc.thePlayer.setSprinting(false);
        }
        if (this.getBlockCount() != 0) return;
        this.enableModule();
    }

    public static void scale(Minecraft mc) {
        switch (mc.gameSettings.guiScale) {
            case 0: {
                GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
                break;
            }
            case 1: {
                GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
                break;
            }
            case 3: {
                GlStateManager.scale((double)0.6666666666666667, (double)0.6666666666666667, (double)0.6666666666666667);
                break;
            }
        }
    }

    @EventTarget
    public void onShader(EventShader event) {
    }

    @EventTarget
    public void onRender2D(Event2D event) {
        GL11.glPushMatrix();
        Scaffold.scale(mc);
        ItemStack stack = Scaffold.mc.thePlayer.inventory.getStackInSlot(this.neededSlot());
        if (stack != null && stack.getItem() instanceof ItemBlock && this.isValid(stack.getItem())) {
            ScaledResolution sr = new ScaledResolution(mc);
            float width = FontManager.normal2.getStringWidth("Amount: " + this.getBlockCount());
            float height = FontManager.normal2.getHeight("/" + this.getBlockCount());
            float x = (float)(sr.getScaledWidthStatic(mc) / 2) - width / 2.0f;
            float y = sr.getScaledHeightStatic(mc) / 2;
            FontManager.normal2.drawString("Amount: ", x + 7.0f, y + 15.0f, -1);
            FontManager.normal2.drawString(" " + this.getBlockCount(), x + 40.0f, y + 15.0f, Hud.hudColor1.getColorInt());
            RenderUtil.drawFastRoundedRect((int)((int)(x - 13.0f)), (float)(y + height), (int)((int)(x + width + 9.0f)), (float)(y + height + 17.5f), (float)4.0f, (int)new Color(0, 0, 0, 120).getRGB());
            RenderUtil.drawStack((FontRenderer)Minecraft.getMinecraft().fontRendererObj, (boolean)false, (ItemStack)stack, (float)(x - 11.0f), (float)(y + 11.0f));
        }
        GL11.glPopMatrix();
    }

    private BlockData getBlockData(BlockPos pos) {
        if (this.isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(this, pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(this, pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(this, pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(this, pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(this, pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (this.isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(this, pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(this, pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(this, pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(this, pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(this, pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(this, pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(this, pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(this, pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(this, pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(this, pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(this, pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(this, pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(this, pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(this, pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(this, pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(this, pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(this, pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(this, pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(this, pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(this, pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos19 = pos.add(-2, 0, 0);
        if (this.isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(this, pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(this, pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(this, pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(this, pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(this, pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos29 = pos.add(2, 0, 0);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(this, pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(this, pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(this, pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(this, pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(this, pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos39 = pos.add(0, 0, 2);
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(this, pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(this, pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(this, pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(this, pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(this, pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos49 = pos.add(0, 0, -2);
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(this, pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(this, pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(this, pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(this, pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(this, pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (this.isPosSolid(pos5.add(0, -1, 0))) {
            return new BlockData(this, pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos5.add(-1, 0, 0))) {
            return new BlockData(this, pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos5.add(1, 0, 0))) {
            return new BlockData(this, pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos5.add(0, 0, 1))) {
            return new BlockData(this, pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos5.add(0, 0, -1))) {
            return new BlockData(this, pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (this.isPosSolid(pos6.add(0, -1, 0))) {
            return new BlockData(this, pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos6.add(-1, 0, 0))) {
            return new BlockData(this, pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos6.add(1, 0, 0))) {
            return new BlockData(this, pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos6.add(0, 0, 1))) {
            return new BlockData(this, pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos6.add(0, 0, -1))) {
            return new BlockData(this, pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (this.isPosSolid(pos7.add(0, -1, 0))) {
            return new BlockData(this, pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos7.add(-1, 0, 0))) {
            return new BlockData(this, pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos7.add(1, 0, 0))) {
            return new BlockData(this, pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos7.add(0, 0, 1))) {
            return new BlockData(this, pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos7.add(0, 0, -1))) {
            return new BlockData(this, pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (this.isPosSolid(pos8.add(0, -1, 0))) {
            return new BlockData(this, pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos8.add(-1, 0, 0))) {
            return new BlockData(this, pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos8.add(1, 0, 0))) {
            return new BlockData(this, pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos8.add(0, 0, 1))) {
            return new BlockData(this, pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos8.add(0, 0, -1))) {
            return new BlockData(this, pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (this.isPosSolid(pos9.add(0, -1, 0))) {
            return new BlockData(this, pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos9.add(-1, 0, 0))) {
            return new BlockData(this, pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos9.add(1, 0, 0))) {
            return new BlockData(this, pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos9.add(0, 0, 1))) {
            return new BlockData(this, pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.isPosSolid(pos9.add(0, 0, -1))) return null;
        return new BlockData(this, pos9.add(0, 0, -1), EnumFacing.SOUTH);
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = Scaffold.mc.theWorld.getBlockState(pos).getBlock();
        return !this.invalid.contains(block);
    }

    private BlockData getBlockDataDownwards(BlockPos pos) {
        if (this.isPosSolid(pos.add(0, 1, 0))) {
            return new BlockData(this, pos.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(this, pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 1, 1))) {
            return new BlockData(this, pos.add(0, 1, 1), EnumFacing.DOWN);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(this, pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(-1, 1, 0))) {
            return new BlockData(this, pos.add(-1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(this, pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(1, 1, 0))) {
            return new BlockData(this, pos.add(1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(this, pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(0, 1, -1))) {
            return new BlockData(this, pos.add(0, 1, -1), EnumFacing.DOWN);
        }
        BlockPos add = pos.add(-1, 0, 0);
        if (this.isPosSolid(add.add(-1, 0, 0))) {
            return new BlockData(this, add.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(add.add(-1, 1, 0))) {
            return new BlockData(this, add.add(-1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add.add(1, 0, 0))) {
            return new BlockData(this, add.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(add.add(1, 1, 0))) {
            return new BlockData(this, add.add(1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add.add(0, 0, -1))) {
            return new BlockData(this, add.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(add.add(0, 1, -1))) {
            return new BlockData(this, add.add(0, 1, -1), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add.add(0, 0, 1))) {
            return new BlockData(this, add.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(add.add(0, 1, 1))) {
            return new BlockData(this, add.add(0, 1, 1), EnumFacing.DOWN);
        }
        BlockPos add2 = pos.add(1, 0, 0);
        if (this.isPosSolid(add2.add(-1, 0, 0))) {
            return new BlockData(this, add2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(add2.add(-1, 1, 0))) {
            return new BlockData(this, add2.add(-1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add2.add(1, 0, 0))) {
            return new BlockData(this, add2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(add2.add(1, 1, 0))) {
            return new BlockData(this, add2.add(1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add2.add(0, 0, -1))) {
            return new BlockData(this, add2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(add2.add(0, 1, -1))) {
            return new BlockData(this, add2.add(0, 1, -1), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add2.add(0, 0, 1))) {
            return new BlockData(this, add2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(add2.add(0, 1, 1))) {
            return new BlockData(this, add2.add(0, 1, 1), EnumFacing.DOWN);
        }
        BlockPos add3 = pos.add(0, 0, -1);
        if (this.isPosSolid(add3.add(-1, 0, 0))) {
            return new BlockData(this, add3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(-1, 1, 0))) {
            return new BlockData(this, add3.add(-1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add3.add(1, 0, 0))) {
            return new BlockData(this, add3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(add3.add(1, 1, 0))) {
            return new BlockData(this, add3.add(1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add3.add(0, 0, -1))) {
            return new BlockData(this, add3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(add3.add(0, 1, -1))) {
            return new BlockData(this, add3.add(0, 1, -1), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add3.add(0, 0, 1))) {
            return new BlockData(this, add3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(add3.add(0, 1, 1))) {
            return new BlockData(this, add3.add(0, 1, 1), EnumFacing.DOWN);
        }
        BlockPos add4 = pos.add(0, 0, 1);
        if (this.isPosSolid(add4.add(-1, 0, 0))) {
            return new BlockData(this, add4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(add4.add(-1, 1, 0))) {
            return new BlockData(this, add4.add(-1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add4.add(1, 0, 0))) {
            return new BlockData(this, add4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(add4.add(1, 1, 0))) {
            return new BlockData(this, add4.add(1, 1, 0), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add4.add(0, 0, -1))) {
            return new BlockData(this, add4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(add4.add(0, 1, -1))) {
            return new BlockData(this, add4.add(0, 1, -1), EnumFacing.DOWN);
        }
        if (this.isPosSolid(add4.add(0, 0, 1))) {
            return new BlockData(this, add4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.isPosSolid(add4.add(0, 1, 1))) return null;
        return new BlockData(this, add4.add(0, 1, 1), EnumFacing.DOWN);
    }

    private int neededSlot() {
        int i = 0;
        while (i < 9) {
            Item item;
            if (Scaffold.mc.thePlayer.inventory.getStackInSlot(i) != null && Scaffold.mc.thePlayer.inventory.getStackInSlot((int)i).stackSize != 0 && this.isValid(item = Scaffold.mc.thePlayer.inventory.getStackInSlot(i).getItem())) {
                return i;
            }
            ++i;
        }
        return Scaffold.mc.thePlayer.inventory.currentItem;
    }

    public void onEnable() {
        if (Scaffold.mc.thePlayer.isSprinting()) {
            Scaffold.mc.thePlayer.setSprinting(false);
        }
        if (this.getBlockCount() == 0) {
            this.canDisable = false;
            return;
        }
        this.canDisable = true;
        this.oldSlot = Scaffold.mc.thePlayer.inventory.currentItem;
        if (Scaffold.mc.theWorld != null) {
            this.floorY = MathHelper.floor_double((double)Scaffold.mc.thePlayer.posY);
        }
        super.onEnable();
    }

    public void onDisable() {
        if (Scaffold.mc.thePlayer.isSprinting()) {
            Scaffold.mc.thePlayer.setSprinting(false);
        }
        if (this.canDisable) {
            if (silent.getValue().booleanValue()) {
                mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)new C09PacketHeldItemChange(Scaffold.mc.thePlayer.inventory.currentItem));
            } else {
                Scaffold.mc.thePlayer.inventory.currentItem = this.oldSlot;
            }
            if (!Scaffold.mc.thePlayer.movementInput.sneak) {
                mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)new C0BPacketEntityAction((Entity)Scaffold.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.canDisable = false;
        }
        super.onDisable();
    }

    public BlockData getBlockData() {
        return this.blockData;
    }

    public List<Block> getBlacklistedBlocks() {
        return this.blacklistedBlocks;
    }

    public BlockPos getBlockBelow() {
        return this.blockBelow;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}
