/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  com.sun.javafx.geom.Vec2f
 *  org.apache.commons.lang3.RandomUtils
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import com.sun.javafx.geom.Vec2f;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lodomir.dev.November;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventPostMotion;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.event.impl.render.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.math.apache.ApacheMath;
import lodomir.dev.utils.player.BlockUtils;
import lodomir.dev.utils.player.MovementUtils;
import lodomir.dev.utils.player.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import viamcp.ViaMCP;

public class NewScaffold
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Custom", "Custom", "Verus", "Vulcan", "NCP", "Hypixel");
    public NumberSetting delay = new NumberSetting("Delay", 0.0, 1.0, 0.0, 0.05);
    public BooleanSetting spoof = new BooleanSetting("Item Spoof", true);
    public NumberSetting speed = new NumberSetting("Speed Modifier", 0.0, 5.0, 1.0, 0.1);
    public BooleanSetting sprint = new BooleanSetting("Sprint", true);
    public BooleanSetting swing = new BooleanSetting("Swing", false);
    public BooleanSetting tower = new BooleanSetting("Tower", true);
    public ModeSetting towerMode = new ModeSetting("Tower Mode", "Vanilla", "Vanilla", "Verus", "NCP", "Slow");
    public NumberSetting towerMotion = new NumberSetting("Tower Motion", 0.0, 1.0, 0.5, 0.05);
    public BooleanSetting towerMove = new BooleanSetting("Tower Move", false);
    public BooleanSetting downwards = new BooleanSetting("Downwards", false);
    public BooleanSetting eagle = new BooleanSetting("Eagle", false);
    public NumberSetting eagleDelay = new NumberSetting("Eagle Delay", 0.0, 20.0, 3.0, 1.0);
    public BooleanSetting sameY = new BooleanSetting("Same Y", true);
    public BooleanSetting jump = new BooleanSetting("Jump", true);
    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public ModeSetting rotations = new ModeSetting("Rotations", "Normal", "Down", "Simple", "Static", "Static", "Backwards", "Forward", "Snap", "Vulcan");
    public NumberSetting timer = new NumberSetting("Timer", 0.1, 5.0, 1.0, 0.1);
    public NumberSetting towerTimer = new NumberSetting("Tower Timer", 0.1, 5.0, 1.0, 0.1);
    public BooleanSetting safewalk = new BooleanSetting("Safe Walk", false);
    public NumberSetting expand = new NumberSetting("Expand", 0.0, 6.0, 0.0, 0.1);
    public NumberSetting range = new NumberSetting("Range", 0.0, 6.0, 3.0, 0.5);
    public ModeSetting placeOn = new ModeSetting("Place On", "Pre", "Pre", "Post");
    public BooleanSetting aura = new BooleanSetting("Disable Killaura", true);
    private Vec3 targetBlock;
    private List<Vec3> placePossibilities = new ArrayList<Vec3>();
    private Vec2f rots;
    private int lastSlot;
    private boolean diagonal = true;
    private BlockPos currentPos;
    private BlockPos lastPos;
    private EnumFacing currentFacing;
    private EnumFacing lastFacing;
    private double startY;
    private Vec2f currentRotation;
    private final TimeUtils time = new TimeUtils();
    private double lastDistance;
    private boolean sloted;
    private float yaw;
    private float pitch;
    private int c09slot;
    private int currentDelay;

    public NewScaffold() {
        super("NewScaffold", 21, Category.PLAYER);
        this.addSettings(this.mode, this.rotate, this.rotations, this.spoof, this.timer, this.speed, this.range, this.swing, this.delay, this.expand, this.tower, this.towerMode, this.towerTimer, this.towerMove, this.towerMotion, this.sprint, this.jump, this.sameY, this.downwards, this.eagle, this.eagleDelay, this.safewalk, this.placeOn, this.aura);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (!this.tower.isEnabled()) {
            this.towerMotion.setVisible(false);
            this.towerMode.setVisible(false);
            this.towerTimer.setVisible(false);
            this.towerMove.setVisible(false);
        } else {
            this.towerMotion.setVisible(true);
            this.towerMode.setVisible(true);
            this.towerTimer.setVisible(true);
            this.towerMove.setVisible(true);
        }
        if (!this.rotate.isEnabled()) {
            this.rotations.setVisible(false);
        } else {
            this.rotations.setVisible(true);
        }
        if (!this.eagle.isEnabled()) {
            this.eagleDelay.setVisible(false);
        } else {
            this.eagleDelay.setVisible(true);
        }
        super.onGuiUpdate(e);
    }

    @Override
    public void onEnable() {
        this.targetBlock = null;
        this.placePossibilities.clear();
        this.rotations = null;
        this.currentRotation = null;
        this.startY = NewScaffold.mc.thePlayer.posY;
        this.sloted = false;
        this.c09slot = NewScaffold.mc.thePlayer.inventory.currentItem;
        this.lastSlot = NewScaffold.mc.thePlayer.inventory.currentItem;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.currentDelay = 0;
        NewScaffold.mc.timer.timerSpeed = 1.0f;
        this.rotations = null;
        super.onDisable();
        if (!this.spoof.isEnabled()) {
            NewScaffold.mc.thePlayer.inventory.currentItem = this.lastSlot;
        } else if (NewScaffold.mc.thePlayer.inventory.currentItem != this.c09slot) {
            NewScaffold.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(NewScaffold.mc.thePlayer.inventory.currentItem));
        }
    }

    @Override
    @Subscribe
    public void on3D(EventRender3D event) {
        if (this.rotate.isEnabled()) {
            if (this.currentFacing == null || this.currentPos == null) {
                return;
            }
            NewScaffold.mc.thePlayer.rotationYawHead = this.yaw;
            NewScaffold.mc.thePlayer.renderYawOffset = this.yaw;
            NewScaffold.mc.thePlayer.rotationPitchHead = this.pitch;
        }
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (this.spoof.isEnabled() && event.getPacket() instanceof C09PacketHeldItemChange) {
            C09PacketHeldItemChange packet = (C09PacketHeldItemChange)event.getPacket();
            int slotId = this.getItemSlot(false);
            if (NewScaffold.mc.thePlayer.inventory.currentItem == packet.getSlotId()) {
                event.setCancelled(true);
            }
        }
    }

    private int getSlot() {
        int slot = this.getItemSlot(false);
        if (slot == -1 && this.getBlockCount(true) > 0) {
            int toSwitch = 8;
            for (int i = 0; i < 9; ++i) {
                ItemStack itemStack = NewScaffold.mc.thePlayer.inventory.mainInventory[i];
                if (itemStack != null) continue;
                toSwitch = i;
                break;
            }
            NewScaffold.mc.playerController.windowClick(NewScaffold.mc.thePlayer.inventoryContainer.windowId, 0, toSwitch, 0, NewScaffold.mc.thePlayer);
            for (int i1 = 9; i1 < 45; ++i1) {
                if (!NewScaffold.mc.thePlayer.inventoryContainer.getSlot(i1).getHasStack()) continue;
                ItemStack is = NewScaffold.mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if (is == null || !(is.getItem() instanceof ItemBlock) || !this.isValid(((ItemBlock)is.getItem()).getBlock())) continue;
                NewScaffold.mc.playerController.windowClick(NewScaffold.mc.thePlayer.inventoryContainer.windowId, i1, toSwitch, 2, NewScaffold.mc.thePlayer);
                break;
            }
        }
        int oldSlot = NewScaffold.mc.thePlayer.inventory.currentItem;
        if (slot != -1) {
            if (!this.spoof.isEnabled()) {
                NewScaffold.mc.thePlayer.inventory.currentItem = slot;
            } else if (!this.sloted || this.c09slot != slot) {
                NewScaffold.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(slot));
                this.c09slot = slot;
                this.sloted = true;
            }
        }
        return slot;
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        BlockPos pos;
        if (this.startY > NewScaffold.mc.thePlayer.posY) {
            this.startY = ApacheMath.floor(NewScaffold.mc.thePlayer.posY);
        }
        if (MovementUtils.isMoving() && NewScaffold.mc.thePlayer.onGround && this.jump.isEnabled()) {
            NewScaffold.mc.thePlayer.jump();
        }
        if (NewScaffold.mc.thePlayer.onGround || NewScaffold.mc.gameSettings.keyBindJump.isKeyDown() && !this.sameY.isEnabled()) {
            this.startY = NewScaffold.mc.thePlayer.posY;
        }
        if (NewScaffold.mc.theWorld.getBlockState(pos = new BlockPos(NewScaffold.mc.thePlayer.posX, NewScaffold.mc.thePlayer.posY - 1.0, NewScaffold.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) {
            this.setBlockFacing(pos);
        }
        if (this.downwards.isEnabled() && NewScaffold.mc.gameSettings.keyBindSneak.isKeyDown() && NewScaffold.mc.thePlayer.onGround && this.currentFacing != null && this.currentFacing != null) {
            this.currentFacing = EnumFacing.DOWN;
        }
        if (this.sameY.isEnabled() && NewScaffold.mc.thePlayer.posY < this.startY) {
            this.startY = NewScaffold.mc.thePlayer.posY;
        }
        if (this.currentFacing != null) {
            this.lastPos = this.currentPos;
            this.lastFacing = this.currentFacing;
        }
        if (this.getSlot() < 0 || this.getSlot() > 9) {
            return;
        }
        NewScaffold.mc.timer.timerSpeed = (float)this.timer.getValue();
        if (this.tower.isEnabled() && NewScaffold.mc.gameSettings.keyBindJump.isKeyDown() && this.towerMove.isEnabled() && !MovementUtils.isMoving() && !(PlayerUtils.getBlockRelativeToPlayer(0.0, -1.0, 0.0) instanceof BlockAir) && !November.INSTANCE.getModuleManager().getModule("Speed").isEnabled()) {
            NewScaffold.mc.timer.timerSpeed = this.towerTimer.getValueFloat();
            switch (this.towerMode.getMode()) {
                case "Vanilla": {
                    NewScaffold.mc.thePlayer.motionY = 0.42;
                }
            }
        }
        this.placeBlock(this.getSlot());
        if (!this.rotate.isEnabled()) {
            this.yaw = NewScaffold.mc.thePlayer.rotationYaw;
            this.pitch = NewScaffold.mc.thePlayer.rotationPitch;
        } else {
            event.setYaw(this.yaw);
            event.setPitch(this.pitch);
            NewScaffold.mc.thePlayer.rotationYawHead = this.yaw;
            NewScaffold.mc.thePlayer.renderYawOffset = this.yaw;
            NewScaffold.mc.thePlayer.rotationPitchHead = this.pitch;
        }
        if (this.rotate.isEnabled()) {
            if (this.currentFacing == null || this.targetBlock == null) {
                return;
            }
            switch (this.rotations.getMode()) {
                case "Snap": {
                    this.yaw = this.rots.x;
                    this.pitch = this.rots.y;
                    break;
                }
                case "Forward": {
                    this.pitch = 90.0f;
                    this.yaw = 0.0f;
                    break;
                }
                case "Static": {
                    switch (this.currentFacing) {
                        case NORTH: {
                            this.yaw = 0.0f;
                            break;
                        }
                        case SOUTH: {
                            this.yaw = 180.0f;
                            break;
                        }
                        case WEST: {
                            this.yaw = -90.0f;
                            break;
                        }
                        case EAST: {
                            this.yaw = 90.0f;
                        }
                    }
                    this.pitch = 80.5f;
                    event.setYaw(this.yaw);
                    event.setPitch(this.pitch);
                    break;
                }
                case "Backwards": {
                    float rotationYaw = NewScaffold.mc.thePlayer.rotationYaw;
                    if (NewScaffold.mc.thePlayer.moveForward < 0.0f && NewScaffold.mc.thePlayer.moveStrafing == 0.0f) {
                        rotationYaw += 180.0f;
                    }
                    if (NewScaffold.mc.thePlayer.moveStrafing > 0.0f) {
                        rotationYaw -= 90.0f;
                    }
                    if (NewScaffold.mc.thePlayer.moveStrafing < 0.0f) {
                        rotationYaw += 90.0f;
                    }
                    this.yaw = (float)(Math.toRadians(rotationYaw) * 57.29577951308232 - 180.0 + Math.random());
                    this.pitch = (float)(87.0 + Math.random());
                    break;
                }
                case "Vulcan": {
                    this.yaw = NewScaffold.mc.thePlayer.rotationYaw - 180.0f + (float)RandomUtils.nextInt((int)-5, (int)5);
                    this.pitch = 80.5f;
                    event.setPitch(this.pitch);
                }
            }
        }
    }

    @Override
    @Subscribe
    public void onPostMotion(EventPostMotion event) {
        if (this.placeOn.isMode("Post")) {
            this.placeBlock(this.getSlot());
        }
    }

    public int getBlocksCount() {
        int var1 = 0;
        for (int var2 = 0; var2 < 45; ++var2) {
            if (!NewScaffold.mc.thePlayer.inventoryContainer.getSlot(var2).getHasStack()) continue;
            ItemStack var3 = NewScaffold.mc.thePlayer.inventoryContainer.getSlot(var2).getStack();
            Item var4 = var3.getItem();
            if (!(var3.getItem() instanceof ItemBlock) || !this.isValid(Block.getBlockFromItem(var4))) continue;
            var1 += var3.stackSize;
        }
        return var1;
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
    }

    private void placeBlock(int slot) {
        boolean sameY;
        if (slot == -1) {
            return;
        }
        boolean bl = sameY = (this.sameY.isEnabled() || November.INSTANCE.getModuleManager().getModule("Speed").isEnabled() && !NewScaffold.mc.gameSettings.keyBindJump.isKeyDown()) && MovementUtils.isMoving();
        if ((int)this.startY - 1 != (int)this.targetBlock.yCoord && sameY) {
            return;
        }
        if (ViaMCP.getInstance().getVersion() > 47) {
            if (this.swing.isEnabled()) {
                NewScaffold.mc.thePlayer.swingItem();
            } else {
                this.sendPacket(new C0APacketAnimation());
            }
        }
        if (this.negativeExpand(0.02)) {
            NewScaffold.mc.playerController.onPlayerRightClick(NewScaffold.mc.thePlayer, NewScaffold.mc.theWorld, NewScaffold.mc.thePlayer.inventory.getStackInSlot(slot), this.currentPos, this.currentFacing, NewScaffold.getVec3(this.currentPos, this.currentFacing));
        }
        if (ViaMCP.getInstance().getVersion() <= 47) {
            if (this.swing.isEnabled()) {
                NewScaffold.mc.thePlayer.swingItem();
            } else {
                this.sendPacket(new C0APacketAnimation());
            }
        }
    }

    private boolean negativeExpand(double negativeExpandValue) {
        return NewScaffold.mc.theWorld.getBlockState(new BlockPos(NewScaffold.mc.thePlayer.posX + negativeExpandValue, NewScaffold.mc.thePlayer.posY - 1.0, NewScaffold.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && NewScaffold.mc.theWorld.getBlockState(new BlockPos(NewScaffold.mc.thePlayer.posX - negativeExpandValue, NewScaffold.mc.thePlayer.posY - 1.0, NewScaffold.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir && NewScaffold.mc.theWorld.getBlockState(new BlockPos(NewScaffold.mc.thePlayer.posX - negativeExpandValue, NewScaffold.mc.thePlayer.posY - 1.0, NewScaffold.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && NewScaffold.mc.theWorld.getBlockState(new BlockPos(NewScaffold.mc.thePlayer.posX + negativeExpandValue, NewScaffold.mc.thePlayer.posY - 1.0, NewScaffold.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && NewScaffold.mc.theWorld.getBlockState(new BlockPos(NewScaffold.mc.thePlayer.posX, NewScaffold.mc.thePlayer.posY - 1.0, NewScaffold.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && NewScaffold.mc.theWorld.getBlockState(new BlockPos(NewScaffold.mc.thePlayer.posX, NewScaffold.mc.thePlayer.posY - 1.0, NewScaffold.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir;
    }

    private int getItemSlot(boolean count) {
        int itemCount = count ? 0 : -1;
        for (int i = 8; i >= 0; --i) {
            ItemStack itemStack = NewScaffold.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) continue;
            if (count) {
                itemCount += itemStack.stackSize;
                continue;
            }
            itemCount = i;
        }
        return itemCount;
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing facing) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double random1 = ThreadLocalRandom.current().nextDouble(0.6, 1.0);
        double random2 = ThreadLocalRandom.current().nextDouble(0.9, 1.0);
        if (facing.equals(EnumFacing.UP)) {
            x += random1;
            z += random1;
            y += 1.0;
        } else if (facing.equals(EnumFacing.DOWN)) {
            x += random1;
            z += random1;
        } else if (facing.equals(EnumFacing.WEST)) {
            y += random2;
            z += random1;
        } else if (facing.equals(EnumFacing.EAST)) {
            y += random2;
            z += random1;
            x += 1.0;
        } else if (facing.equals(EnumFacing.SOUTH)) {
            y += random2;
            x += random1;
            z += 1.0;
        } else if (facing.equals(EnumFacing.NORTH)) {
            y += random2;
            x += random1;
        }
        return new Vec3(x, y, z);
    }

    public void setBlockFacing(BlockPos pos) {
        if (this.diagonal) {
            if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, -1, 0);
                this.currentFacing = EnumFacing.UP;
                if (this.yaw != 0.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 0.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(-1, 0, 0);
                this.currentFacing = EnumFacing.EAST;
                if (this.yaw != 90.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 90.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(1, 0, 0);
                this.currentFacing = EnumFacing.WEST;
                if (this.yaw != -90.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = -90.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, 0, -1);
                this.currentFacing = EnumFacing.SOUTH;
                if (this.yaw != 180.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 180.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, 0, 1);
                this.currentFacing = EnumFacing.NORTH;
                if (this.yaw != 0.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 0.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(-1, 0, -1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.EAST;
                this.currentPos = pos.add(-1, 0, -1);
                if (this.yaw != 135.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 135.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(1, 0, 1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.WEST;
                this.currentPos = pos.add(1, 0, 1);
                if (this.yaw != -45.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = -45.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(1, 0, -1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.SOUTH;
                this.currentPos = pos.add(1, 0, -1);
                if (this.yaw != 135.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = -135.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(-1, 0, 1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.NORTH;
                this.currentPos = pos.add(-1, 0, 1);
                if (this.yaw != 45.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 45.0f;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, -1, 1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, -1, 1);
                this.currentFacing = EnumFacing.UP;
                this.yaw = NewScaffold.mc.thePlayer.rotationYaw;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, -1, -1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, -1, -1);
                this.currentFacing = EnumFacing.UP;
                this.yaw = NewScaffold.mc.thePlayer.rotationYaw;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(1, -1, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(1, -1, 0);
                this.currentFacing = EnumFacing.UP;
                this.yaw = NewScaffold.mc.thePlayer.rotationYaw;
            } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(-1, -1, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(-1, -1, 0);
                this.currentFacing = EnumFacing.UP;
                this.yaw = NewScaffold.mc.thePlayer.rotationYaw;
            } else {
                this.currentPos = null;
                this.currentFacing = null;
            }
        } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(0, -1, 0);
            this.currentFacing = EnumFacing.UP;
        } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(-1, 0, 0);
            this.currentFacing = EnumFacing.EAST;
        } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(1, 0, 0);
            this.currentFacing = EnumFacing.WEST;
        } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(0, 0, -1);
            this.currentFacing = EnumFacing.SOUTH;
        } else if (NewScaffold.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(0, 0, 1);
            this.currentFacing = EnumFacing.NORTH;
        } else {
            this.currentPos = null;
            this.currentFacing = null;
        }
    }

    private boolean goingDiagonally() {
        return Math.abs(NewScaffold.mc.thePlayer.motionX) > 0.07 && Math.abs(NewScaffold.mc.thePlayer.motionZ) > 0.07;
    }

    private int getBlockCount(Boolean count) {
        int itemCount = count != false ? 0 : -1;
        for (int i = 36; i >= 0; --i) {
            ItemStack itemStack = NewScaffold.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || BlockUtils.BlackList.contains(((ItemBlock)itemStack.getItem()).getBlock())) continue;
            if (count.booleanValue()) {
                itemCount += itemStack.stackSize;
                continue;
            }
            itemCount = i;
        }
        return itemCount;
    }

    private boolean isValid(Block block) {
        return !BlockUtils.BlackList.contains(block);
    }
}

