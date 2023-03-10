/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.apache.commons.lang3.RandomUtils
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lodomir.dev.November;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventPostMotion;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.BlockUtils;
import lodomir.dev.utils.player.MovementUtils;
import lodomir.dev.utils.player.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import viamcp.ViaMCP;

public class Scaffold
extends Module {
    private boolean switchedSlot;
    private int towerTicks;
    private List<Vec3> placePossibilities = new ArrayList<Vec3>();
    private BlockPos currentPos;
    private BlockPos lastPos;
    private EnumFacing currentFacing;
    private EnumFacing lastFacing;
    private boolean lastGround;
    private int currentDelay;
    private boolean towering;
    private List invalidBlocks;
    private int verusState = 0;
    private boolean verusJumped = false;
    private int slot;
    private double startY;
    private int blockCount;
    private List validBlocks;
    private boolean diagonal = true;
    private boolean rotated = false;
    private float yaw;
    private float pitch;
    private float lastYaw;
    private float lastPitch;
    private TimeUtils timer = new TimeUtils();
    public ModeSetting mode = new ModeSetting("Mode", "Custom", "Custom", "Hypixel");
    public ModeSetting rots = new ModeSetting("Rotations", "None", "None", "Hypixel", "Vulcan", "Normal", "Simple", "Down");
    private NumberSetting timero = new NumberSetting("Timer", 0.1, 5.0, 1.0, 0.1);
    private NumberSetting delay = new NumberSetting("Delay", 0.0, 1.0, 0.0, 0.05);
    private NumberSetting speed = new NumberSetting("Speed Multiplier", 0.0, 3.0, 1.0, 0.05);
    public static ModeSetting sprint = new ModeSetting("Sprint", "Normal", "Normal", "Legit", "None");
    public ModeSetting tower = new ModeSetting("Tower", "Vanilla", "Vanilla", "Hypixel", "Verus", "NCP", "Slow", "None");
    public NumberSetting towertimer = new NumberSetting("Tower Timer", 0.1, 5.0, 1.0, 0.1);
    public BooleanSetting towermove = new BooleanSetting("Tower Move", false);
    public static BooleanSetting safewalk = new BooleanSetting("Safe Walk", false);
    public BooleanSetting sameY = new BooleanSetting("Same Y", false);
    public BooleanSetting eagle = new BooleanSetting("Eagle", false);
    public BooleanSetting swing = new BooleanSetting("Swing", false);
    public BooleanSetting strafe = new BooleanSetting("Strafe", false);
    public BooleanSetting jump = new BooleanSetting("Jump", false);
    public ModeSetting placing = new ModeSetting("Place on", "Pre", "Pre", "Post");

    public Scaffold() {
        super("Scaffold", 0, Category.PLAYER);
        this.addSettings(this.mode, this.rots, this.timero, this.speed, this.delay, sprint, this.tower, this.towertimer, this.towermove, safewalk, this.eagle, this.jump, this.sameY, this.swing, this.strafe, this.placing);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (this.mode.isMode("Hypixel")) {
            this.rots.setVisible(false);
            this.delay.setVisible(false);
            this.speed.setVisible(false);
            sprint.setVisible(false);
            this.tower.setVisible(false);
            this.towermove.setVisible(false);
            this.towertimer.setVisible(false);
            this.strafe.setVisible(false);
            this.sameY.setVisible(false);
        } else if (this.mode.isMode("Custom")) {
            this.rots.setVisible(true);
            this.delay.setVisible(true);
            this.speed.setVisible(true);
            sprint.setVisible(true);
            this.tower.setVisible(true);
            this.towermove.setVisible(true);
            this.towertimer.setVisible(true);
            this.strafe.setVisible(true);
            this.sameY.setVisible(true);
        }
    }

    @Override
    public void onEnable() {
        this.yaw = this.lastYaw = Scaffold.mc.thePlayer.rotationYaw;
        this.pitch = this.lastPitch = Scaffold.mc.thePlayer.rotationPitch;
        this.startY = Scaffold.mc.thePlayer.posY;
        this.placePossibilities.clear();
        this.slot = Scaffold.mc.thePlayer.inventory.currentItem;
        super.onEnable();
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (event.getPacket() instanceof S2FPacketSetSlot) {
            event.setCancelled(true);
        }
    }

    private boolean isValidItem(Item var1) {
        if (var1 instanceof ItemBlock) {
            ItemBlock var2 = (ItemBlock)var1;
            Block var3 = var2.getBlock();
            return !this.invalidBlocks.contains(var3);
        }
        return false;
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate event) {
        this.setSuffix(this.mode.getMode());
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
        ItemStack item = Scaffold.mc.thePlayer.inventory.getStackInSlot(this.slot);
        int var3 = this.getBlockCount();
        String var4 = var3 + "";
        ScaledResolution sr = new ScaledResolution(mc);
        TTFFontRenderer cfr = November.INSTANCE.fm.getFont("PRODUCT SANS 20");
        int height = sr.getScaledHeight() / 2;
        if (Interface.font.isMode("Client") && var4 != null) {
            cfr.drawStringWithShadow(var4, (double)(sr.getScaledWidth() / 2), (double)height + 6.5, -1);
        } else {
            Scaffold.mc.fontRendererObj.drawStringWithShadow(var4, sr.getScaledWidth() / 2 + 1, height + 9, -1);
        }
        if (var4 != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(item, (int)((float)sr.getScaledWidth() / 2.0f - 17.0f), height + 4);
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        } else if (Interface.font.isMode("Client")) {
            cfr.drawCenteredString("?", (int)((float)sr.getScaledWidth() / 2.0f + 0.5f), height + 6, -1);
        } else {
            Gui.drawCenteredString(Scaffold.mc.fontRendererObj, "?", (int)((float)sr.getScaledWidth() / 2.0f + 0.5f), height + 6, -1);
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        switch (this.mode.getMode()) {
            case "Custom": {
                if (MovementUtils.isMoving() && Scaffold.mc.thePlayer.onGround && this.jump.isEnabled()) {
                    Scaffold.mc.thePlayer.jump();
                }
                if (Scaffold.mc.thePlayer.onGround) {
                    this.startY = Scaffold.mc.thePlayer.posY;
                }
                switch (sprint.getMode()) {
                    case "None": {
                        break;
                    }
                    case "Legit": {
                        if (!(Math.abs(MathHelper.wrapAngleTo180_float(Scaffold.mc.thePlayer.rotationYaw) - MathHelper.wrapAngleTo180_float(this.yaw)) > 90.0f)) break;
                        Scaffold.mc.thePlayer.setSprinting(false);
                    }
                }
                if (this.strafe.isEnabled()) {
                    MovementUtils.strafe();
                }
                BlockPos pos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ);
                if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                    pos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.2, Scaffold.mc.thePlayer.posZ);
                } else if (this.sameY.isEnabled()) {
                    pos = new BlockPos(Scaffold.mc.thePlayer.posX, this.startY - 1.0, Scaffold.mc.thePlayer.posZ);
                }
                if (this.eagle.isEnabled() && Scaffold.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
                    this.setSneaking(true);
                } else if (this.eagle.isEnabled()) {
                    this.setSneaking(false);
                }
                if (this.currentFacing != null) {
                    this.lastPos = this.currentPos;
                    this.lastFacing = this.currentFacing;
                }
                if (Scaffold.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
                    this.setBlockAndFacing(pos);
                    Scaffold.mc.timer.timerSpeed = this.timero.getValueFloat();
                }
                this.getRotations(event);
                if (Scaffold.mc.gameSettings.keyBindJump.pressed && !(PlayerUtils.getBlockRelativeToPlayer(0.0, -1.0, 0.0) instanceof BlockAir)) {
                    Scaffold.mc.timer.timerSpeed = this.towertimer.getValueFloat();
                    this.towering = true;
                    if (this.rotated) {
                        event.setYaw(90.0f);
                    }
                    if (!this.towermove.isEnabled()) {
                        MovementUtils.stop();
                    }
                    switch (this.tower.getMode()) {
                        case "Vanilla": {
                            Scaffold.mc.thePlayer.motionY = 0.42f;
                            break;
                        }
                        case "Verus": {
                            if (Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.2, Scaffold.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) break;
                            Scaffold.mc.thePlayer.motionY = 0.42;
                            break;
                        }
                        case "Vulcan": {
                            break;
                        }
                        case "NCP": {
                            int IntPosY;
                            if (Scaffold.mc.thePlayer.onGround) {
                                this.towerTicks = 0;
                            }
                            if (Scaffold.mc.thePlayer.posY - (double)(IntPosY = (int)Scaffold.mc.thePlayer.posY) < 0.05) {
                                Scaffold.mc.thePlayer.setPosition(Scaffold.mc.thePlayer.posX, IntPosY, Scaffold.mc.thePlayer.posZ);
                                Scaffold.mc.thePlayer.motionY = 0.42;
                                this.towerTicks = 1;
                                break;
                            }
                            if (this.towerTicks == 1) {
                                Scaffold.mc.thePlayer.motionY = 0.34;
                                ++this.towerTicks;
                                break;
                            }
                            if (this.towerTicks != 2) break;
                            Scaffold.mc.thePlayer.motionY = 0.25;
                            ++this.towerTicks;
                            break;
                        }
                        case "Hypixel": {
                            this.yaw = MovementUtils.getPlayerDirection() - 150.0f;
                            if (!Scaffold.mc.thePlayer.onGround || Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                                this.pitch = 90.0f;
                                break;
                            }
                            if (this.diagonal) {
                                this.pitch = 83.0f;
                                break;
                            }
                            this.pitch = 81.5f;
                            break;
                        }
                        case "None": {
                            if (!Scaffold.mc.gameSettings.keyBindJump.isKeyDown() || !MovementUtils.isOnGround(1.0)) break;
                            Scaffold.mc.gameSettings.keyBindJump.pressed = true;
                            break;
                        }
                        case "Slow": {
                            if (Scaffold.mc.thePlayer.onGround) {
                                Scaffold.mc.thePlayer.motionY = 0.4f;
                            } else if (PlayerUtils.getBlockRelativeToPlayer(0.0, -1.0, 0.0) instanceof BlockAir) {
                                Scaffold.mc.thePlayer.motionY -= (double)0.4f;
                            }
                            MovementUtils.stop();
                        }
                    }
                }
                double baseSpeed = this.getBaseSpeed();
                double speedMultiplier = this.speed.getValue();
                if (Math.abs(speedMultiplier - 1.0) > 1.0E-4 && Scaffold.mc.thePlayer.onGround && (!Scaffold.mc.thePlayer.isPotionActive(Potion.moveSpeed) || Scaffold.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() <= 1) && baseSpeed != 0.0) {
                    MovementUtils.strafe((float)(baseSpeed * speedMultiplier));
                }
                this.lastGround = Scaffold.mc.thePlayer.onGround;
                if (this.currentPos == null) {
                    return;
                }
                if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.placeBlock();
                    break;
                }
                if (this.timer.hasReached((long)(1000.0f * this.delay.getValueFloat()))) {
                    if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.getCurrentEquippedItem(), this.currentPos, this.currentFacing, new Vec3(this.currentPos.getX(), this.currentPos.getY(), this.currentPos.getZ()))) {
                        this.timer.setLastMS();
                    }
                    if (this.placing.isMode("Pre")) {
                        this.placeBlock();
                    }
                }
                this.timer.reset();
                break;
            }
            case "Hypixel": {
                Scaffold.mc.thePlayer.setSprinting(false);
                if (MovementUtils.isMoving() && Scaffold.mc.thePlayer.onGround && this.jump.isEnabled()) {
                    Scaffold.mc.thePlayer.jump();
                }
                if (Scaffold.mc.thePlayer.onGround) {
                    this.startY = Scaffold.mc.thePlayer.posY;
                }
                BlockPos hypixelpos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ);
                if (this.eagle.isEnabled() && Scaffold.mc.theWorld.getBlockState(hypixelpos).getBlock() instanceof BlockAir) {
                    this.setSneaking(true);
                } else if (this.eagle.isEnabled()) {
                    this.setSneaking(false);
                }
                if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown() || November.INSTANCE.getModuleManager().getModule("Speed").isEnabled()) {
                    hypixelpos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.2, Scaffold.mc.thePlayer.posZ);
                } else if (this.jump.isEnabled()) {
                    hypixelpos = new BlockPos(Scaffold.mc.thePlayer.posX, this.startY - 1.0, Scaffold.mc.thePlayer.posZ);
                }
                if (Scaffold.mc.theWorld.getBlockState(hypixelpos).getBlock() instanceof BlockAir) {
                    this.setBlockAndFacing(hypixelpos);
                    Scaffold.mc.timer.timerSpeed = this.timero.getValueFloat();
                }
                this.yaw = MovementUtils.getPlayerDirection() - 150.0f;
                this.pitch = !Scaffold.mc.thePlayer.onGround || Scaffold.mc.gameSettings.keyBindJump.isKeyDown() ? 90.0f : (this.diagonal ? 83.0f : 81.5f);
                Scaffold.mc.thePlayer.rotationYawHead = this.yaw;
                Scaffold.mc.thePlayer.renderYawOffset = this.yaw;
                Scaffold.mc.thePlayer.rotationPitchHead = this.pitch;
                event.setYaw(this.yaw);
                event.setPitch(this.pitch);
                if (this.currentPos != null && this.currentFacing != null && this.negativeExpand(0.16) && this.placing.isMode("Pre")) {
                    this.placeBlock();
                }
                if (Math.abs(Scaffold.mc.thePlayer.motionX) > 0.065 && Math.abs(Scaffold.mc.thePlayer.motionZ) > 0.065 && this.negativeExpand(-0.1)) {
                    Scaffold.mc.thePlayer.motionX *= 0.5;
                    Scaffold.mc.thePlayer.motionZ *= 0.5;
                }
                if (!Scaffold.mc.thePlayer.isPotionActive(Potion.moveSpeed)) break;
                double mult = 1.0 - (double)(Scaffold.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.167;
                Scaffold.mc.thePlayer.motionX *= mult;
                Scaffold.mc.thePlayer.motionZ *= mult;
            }
        }
        this.invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web);
        this.validBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava);
        int blocks = 0;
        for (int i = 36; i < 45; ++i) {
            Block block;
            ItemStack itemStack = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || itemStack.stackSize <= 0 || !(block = ((ItemBlock)itemStack.getItem()).getBlock()).isFullCube() || BlockUtils.BlackList.contains(block)) continue;
            blocks += itemStack.getMaxStackSize();
        }
        this.blockCount = blocks;
        int blockSlot = BlockUtils.findBlock() - 36;
        if (blockSlot < 0 || blockSlot > 9) {
            return;
        }
    }

    @Override
    @Subscribe
    public void onPostMotion(EventPostMotion event) {
        if (this.placing.isMode("Post")) {
            this.placeBlock();
        }
    }

    private boolean negativeExpand(double negativeExpandValue) {
        return Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX + negativeExpandValue, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX - negativeExpandValue, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir && Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX - negativeExpandValue, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX + negativeExpandValue, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir;
    }

    public double getBaseSpeed() {
        if (Scaffold.mc.gameSettings.keyBindSprint.isKeyDown()) {
            if (Scaffold.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                if (Scaffold.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 1) {
                    return 0.18386012061481244;
                }
                return 0.21450346015841276;
            }
            return 0.15321676228437875;
        }
        if (Scaffold.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            if (Scaffold.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 1) {
                return 0.14143085686761;
            }
            return 0.16500264553372018;
        }
        return 0.11785905094607611;
    }

    public int getBlockCount() {
        int var1 = 0;
        for (int var2 = 0; var2 < 45; ++var2) {
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(var2).getHasStack()) continue;
            ItemStack var3 = Scaffold.mc.thePlayer.inventoryContainer.getSlot(var2).getStack();
            Item var4 = var3.getItem();
            if (!(var3.getItem() instanceof ItemBlock) || !this.isValidItem(var4)) continue;
            var1 += var3.stackSize;
        }
        return var1;
    }

    @Override
    public void onDisable() {
        if (this.slot != Scaffold.mc.thePlayer.inventory.currentItem) {
            this.sendPacketSilent(new C09PacketHeldItemChange(Scaffold.mc.thePlayer.inventory.currentItem));
        }
        Scaffold.mc.timer.timerSpeed = 1.0f;
        this.currentDelay = 0;
        if (this.mode.isMode("Hypixel")) {
            MovementUtils.stop();
        }
        if (this.eagle.isEnabled()) {
            this.setSneaking(false);
        }
        super.onDisable();
    }

    public float[] getRotations(EventPreMotion event) {
        float[] facing = BlockUtils.getDirectionToBlock(this.currentPos.getX(), this.currentPos.getY(), this.currentPos.getZ(), this.currentFacing);
        if (!this.rots.isMode("None")) {
            switch (this.rots.getMode()) {
                case "Hypixel": {
                    this.rotated = true;
                    event.setYaw(this.yaw);
                    event.setPitch(this.pitch);
                    Scaffold.mc.thePlayer.rotationYawHead = this.yaw;
                    Scaffold.mc.thePlayer.renderYawOffset = this.yaw;
                    Scaffold.mc.thePlayer.rotationPitchHead = this.pitch;
                    break;
                }
                case "Normal": {
                    this.rotated = true;
                    this.yaw = facing[0];
                    this.pitch = Math.min(90.0f, facing[1] + 9.0f);
                    event.setYaw(this.yaw);
                    event.setPitch(this.pitch);
                    Scaffold.mc.thePlayer.rotationYawHead = this.yaw;
                    Scaffold.mc.thePlayer.renderYawOffset = this.yaw;
                    Scaffold.mc.thePlayer.rotationPitchHead = this.pitch;
                    break;
                }
                case "Down": {
                    this.rotated = true;
                    float rotationYaw = Scaffold.mc.thePlayer.rotationYaw;
                    if (Scaffold.mc.thePlayer.moveForward < 0.0f && Scaffold.mc.thePlayer.moveStrafing == 0.0f) {
                        rotationYaw += 180.0f;
                    }
                    if (Scaffold.mc.thePlayer.moveStrafing > 0.0f) {
                        rotationYaw -= 90.0f;
                    }
                    if (Scaffold.mc.thePlayer.moveStrafing < 0.0f) {
                        rotationYaw += 90.0f;
                    }
                    this.yaw = (float)(Math.toRadians(rotationYaw) * 57.29577951308232 - 180.0 + Math.random());
                    this.pitch = (float)(87.0 + Math.random());
                    event.setYaw(this.yaw);
                    event.setPitch(this.pitch);
                    Scaffold.mc.thePlayer.rotationYawHead = this.yaw;
                    Scaffold.mc.thePlayer.renderYawOffset = this.yaw;
                    Scaffold.mc.thePlayer.rotationPitchHead = this.pitch;
                    break;
                }
                case "Simple": {
                    this.rotated = true;
                    switch (this.currentFacing) {
                        case SOUTH: {
                            event.setYaw(180.0f);
                            break;
                        }
                        case EAST: {
                            event.setYaw(90.0f);
                            break;
                        }
                        case WEST: {
                            event.setYaw(-90.0f);
                        }
                    }
                    event.setYaw(this.yaw);
                    event.setPitch(this.pitch);
                    Scaffold.mc.thePlayer.rotationYawHead = this.yaw;
                    Scaffold.mc.thePlayer.renderYawOffset = this.yaw;
                    Scaffold.mc.thePlayer.rotationPitchHead = this.pitch;
                    break;
                }
                case "Vulcan": {
                    this.yaw = (float)(Math.toRadians(Scaffold.mc.thePlayer.rotationYaw) * 57.29577951308232 - 180.0 + (double)RandomUtils.nextInt((int)-5, (int)5));
                    this.pitch = 80.5f;
                    event.setYaw(this.yaw);
                    event.setPitch(this.pitch);
                    Scaffold.mc.thePlayer.rotationYawHead = this.yaw;
                    Scaffold.mc.thePlayer.renderYawOffset = this.yaw;
                    Scaffold.mc.thePlayer.rotationPitchHead = this.pitch;
                }
            }
        } else {
            this.rotated = false;
        }
        return new float[]{this.yaw, this.pitch};
    }

    private void setSneaking(boolean b) {
        if (!Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            KeyBinding sneakBinding = Scaffold.mc.gameSettings.keyBindSneak;
            try {
                Field field = sneakBinding.getClass().getDeclaredField("pressed");
                field.setAccessible(true);
                field.setBoolean(sneakBinding, b);
            }
            catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isAirUnder() {
        Block block = Scaffold.mc.theWorld.getBlockState(new BlockPos(Scaffold.mc.thePlayer.posX, this.startY - 1.0, Scaffold.mc.thePlayer.posZ)).getBlock();
        return block instanceof BlockAir || block instanceof BlockTallGrass || block instanceof BlockLiquid;
    }

    private void placeBlock() {
        boolean sameY;
        int blockSlot = BlockUtils.findBlock() - 36;
        boolean bl = sameY = (this.sameY.isEnabled() || November.INSTANCE.getModuleManager().getModule("Speed").isEnabled() && !Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) && MovementUtils.isMoving();
        if ((int)this.startY - 1 != this.currentPos.getY() && sameY) {
            return;
        }
        if (blockSlot < 0 || blockSlot > 9) {
            return;
        }
        boolean switchedSlot = false;
        if (this.slot != blockSlot) {
            this.slot = blockSlot;
            this.sendPacketSilent(new C09PacketHeldItemChange(this.slot));
            switchedSlot = true;
        }
        ItemStack item = Scaffold.mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();
        if (ViaMCP.getInstance().getVersion() > 47) {
            if (this.swing.isEnabled()) {
                Scaffold.mc.thePlayer.swingItem();
            } else {
                this.sendPacket(new C0APacketAnimation());
            }
        }
        Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, item, this.currentPos, this.currentFacing, Scaffold.getVec3(this.currentPos, this.currentFacing));
        if (ViaMCP.getInstance().getVersion() <= 47) {
            if (this.swing.isEnabled()) {
                Scaffold.mc.thePlayer.swingItem();
            } else {
                this.sendPacket(new C0APacketAnimation());
            }
        }
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

    private void setBlockAndFacing(BlockPos pos) {
        if (this.diagonal) {
            if (Scaffold.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, -1, 0);
                this.currentFacing = EnumFacing.UP;
                if (this.yaw != 0.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 0.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(-1, 0, 0);
                this.currentFacing = EnumFacing.EAST;
                if (this.yaw != 90.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 90.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(1, 0, 0);
                this.currentFacing = EnumFacing.WEST;
                if (this.yaw != -90.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = -90.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, 0, -1);
                this.currentFacing = EnumFacing.SOUTH;
                if (this.yaw != 180.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 180.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, 0, 1);
                this.currentFacing = EnumFacing.NORTH;
                if (this.yaw != 0.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 0.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(-1, 0, -1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.EAST;
                this.currentPos = pos.add(-1, 0, -1);
                if (this.yaw != 135.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 135.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(1, 0, 1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.WEST;
                this.currentPos = pos.add(1, 0, 1);
                if (this.yaw != -45.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = -45.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(1, 0, -1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.SOUTH;
                this.currentPos = pos.add(1, 0, -1);
                if (this.yaw != 135.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = -135.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(-1, 0, 1)).getBlock() != Blocks.air) {
                this.currentFacing = EnumFacing.NORTH;
                this.currentPos = pos.add(-1, 0, 1);
                if (this.yaw != 45.0f) {
                    this.currentDelay = 0;
                }
                this.yaw = 45.0f;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(0, -1, 1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, -1, 1);
                this.currentFacing = EnumFacing.UP;
                this.yaw = Scaffold.mc.thePlayer.rotationYaw;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(0, -1, -1)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(0, -1, -1);
                this.currentFacing = EnumFacing.UP;
                this.yaw = Scaffold.mc.thePlayer.rotationYaw;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(1, -1, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(1, -1, 0);
                this.currentFacing = EnumFacing.UP;
                this.yaw = Scaffold.mc.thePlayer.rotationYaw;
            } else if (Scaffold.mc.theWorld.getBlockState(pos.add(-1, -1, 0)).getBlock() != Blocks.air) {
                this.currentPos = pos.add(-1, -1, 0);
                this.currentFacing = EnumFacing.UP;
                this.yaw = Scaffold.mc.thePlayer.rotationYaw;
            } else {
                this.currentPos = null;
                this.currentFacing = null;
            }
        } else if (Scaffold.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(0, -1, 0);
            this.currentFacing = EnumFacing.UP;
        } else if (Scaffold.mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(-1, 0, 0);
            this.currentFacing = EnumFacing.EAST;
        } else if (Scaffold.mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(1, 0, 0);
            this.currentFacing = EnumFacing.WEST;
        } else if (Scaffold.mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(0, 0, -1);
            this.currentFacing = EnumFacing.SOUTH;
        } else if (Scaffold.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
            this.currentPos = pos.add(0, 0, 1);
            this.currentFacing = EnumFacing.NORTH;
        } else {
            this.currentPos = null;
            this.currentFacing = null;
        }
    }
}

