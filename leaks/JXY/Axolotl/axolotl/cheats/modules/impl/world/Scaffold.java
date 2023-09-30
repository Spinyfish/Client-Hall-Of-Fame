package axolotl.cheats.modules.impl.world;


import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.MoveEvent;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.*;
import axolotl.util.BlockUtils;
import axolotl.util.MotionUtil;
import axolotl.util.Timer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {

    public boolean rel = false, reset = false;
    public float yaw = -9000, pitch = -9000;
    private int slot;

    public boolean cooldown = false;
    public BooleanSetting sprint = new BooleanSetting("Sprint", true);
    public ModeSetting eagle = new ModeSetting("Eagle", "Off", "Off", "Sneak", "Silent");
    public NumberSetting timerMod = new NumberSetting("Timer", 1, 0.1, 10, 0.1);
    public BooleanSetting autoJump = new BooleanSetting("AutoJump", false);
    public NumberSetting delay = new NumberSetting("Delay", 1, 0, 50, 1);

    public ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Watchdog");

    public int t, t2, resetTicks;

    public boolean rotated = false;

    public Scaffold() {
        super("Scaffold", Category.WORLD, true);
        this.addSettings(eagle, delay, timerMod, mode, sprint, autoJump);
        this.setSpecialSetting(new SpecialSettings(new SpecialSetting(mode, "M"), new SpecialSetting(delay, "Delay")));
    }

    public void onEnable() {
        t=0;
        t2=0;
        this.slot = mc.thePlayer.inventory.currentItem;
        yaw = -9000;
        pitch = -9000;
    }

    public Timer timer = new Timer();
    public boolean silentSneak = false;

    public void onDisable() {
        setSneaking(false);
        silentSneak = false;

        mc.timer.timerSpeed = 1f;

        this.mc.thePlayer.inventory.currentItem = this.slot;
        this.mc.gameSettings.keyBindUseItem.pressed = false;
        this.mc.thePlayer.setSprinting(Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));

        if(mode.getMode().equalsIgnoreCase("Watchdog")) {
            if(!mc.gameSettings.keyBindSneak.pressed)mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            reset = false;
        }

    }

    public int airTicks;

    public void onEvent(Event event) {

        if (event instanceof MoveEvent && event.eventType == EventType.PRE) {

            MoveEvent e = (MoveEvent)event;

            if(mc.thePlayer.inventory.getCurrentItem() == null || !(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock)) {
                for(int i=0;i<9;i++) {
                    if(mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                        mc.thePlayer.inventory.currentItem = i;
                        break;
                    }
                }
            }

            if(!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock)) return;

            if(!eagle.getMode().equalsIgnoreCase("Off")) {
                setSneaking(rotated);
            }

            if(yaw != -9000 && pitch != -9000) {
                e.setYaw(yaw);
                e.setPitch(pitch);
            }

            mc.thePlayer.setSprinting(sprint.getBValue());

            if(mode.getMode().equalsIgnoreCase("Watchdog")) {
                mc.thePlayer.motionX *= 0.8;
                mc.thePlayer.motionZ *= 0.8;
            }
            BlockPos playerBlock = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);

            if(mc.theWorld.isAirBlock(playerBlock.add(0, -1, 0))) {
                if(isValidBlock(playerBlock.add(0, -2, 0))) {
                    place(playerBlock.add(0, -1, 0), EnumFacing.UP, e);
                } else if(isValidBlock(playerBlock.add(-1, -1, 0))) {
                    place(playerBlock.add(0, -1, 0), EnumFacing.EAST, e);
                } else if(isValidBlock(playerBlock.add(1, -1, 0))) {
                    place(playerBlock.add(0, -1, 0), EnumFacing.WEST, e);
                } else if(isValidBlock(playerBlock.add(0, -1, -1))) {
                    place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH, e);
                } else if(isValidBlock(playerBlock.add(0, -1, 1))) {
                    place(playerBlock.add(0, -1, 0), EnumFacing.NORTH, e);
                } else if(isValidBlock(playerBlock.add(1, -1, 1))) {
                    if(isValidBlock(playerBlock.add(0, -1, 1))) {
                        place(playerBlock.add(0, -1, 1), EnumFacing.NORTH, e);
                    } else place(playerBlock.add(0, -1, 0), EnumFacing.EAST, e);
                } else if(isValidBlock(playerBlock.add(1, -1, 1))) {
                    if(isValidBlock(playerBlock.add(-1, -1, 0))) {
                        place(playerBlock.add(-1, -1, 0), EnumFacing.WEST, e);
                    } else place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH, e);
                } else if(isValidBlock(playerBlock.add(-1, -1, -1))) {
                    if(isValidBlock(playerBlock.add(0, -1, -1))) {
                        place(playerBlock.add(0, -1, -1), EnumFacing.SOUTH, e);
                    } else place(playerBlock.add(-1, -1, -1), EnumFacing.WEST, e);
                } else if(isValidBlock(playerBlock.add(0, -1, -1))) {
                    if(isValidBlock(playerBlock.add(1, -1, 0))) {
                        place(playerBlock.add(1, -1, 0), EnumFacing.EAST, e);
                    } else place(playerBlock.add(0, -1, -1), EnumFacing.NORTH, e);
                }
            }

        }
    }

    private void setSneaking(boolean b) {
        switch(eagle.getMode()) {

            case "Sneak":
                mc.gameSettings.keyBindSneak.pressed = b;
                break;

            case "Silent":
                if(b) {

                    if(silentSneak)return;
                    silentSneak = true;
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));

                } else {

                    if(!silentSneak)return;
                    silentSneak = false;
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));

                }
                break;
        }
    }

    private void place(BlockPos pos, EnumFacing face, MoveEvent e) {

        switch(face) {
            case UP:
                pos = pos.add(0, -1, 0);
                if(mc.thePlayer.posY <= pos.getY() + 2.1 && mc.thePlayer.motionY > 0)return;
                break;
            case NORTH:
                pos = pos.add(0, 0, 1);
                if(mc.thePlayer.posZ - Math.floor(mc.thePlayer.posZ) < 0.682)return;
                break;
            case EAST:
                pos = pos.add(-1, 0, 0);
                if(mc.thePlayer.posX - Math.floor(mc.thePlayer.posX) < 0.282)return;
                break;
            case SOUTH:
                pos = pos.add(0, 0, -1);
                if(mc.thePlayer.posZ - Math.floor(mc.thePlayer.posZ) < 0.282)return;
                break;
            default:
                pos = pos.add(1, 0, 0);
                if(mc.thePlayer.posX - Math.floor(mc.thePlayer.posX) < 0.682)return;
                break;
        }

        cooldown = true;

        if(mc.thePlayer.getHeldItem() != null) {

            rotated = true;

            if(!timer.hasTimeElapsed((long) (delay.getNumberValue() * 50), true)) return;

            rotated = false;

            float[] rotations = BlockUtils.inst.getFacePos(BlockUtils.inst.getVec3(pos));
            yaw = rotations[0];
            pitch = rotations[1];

            mc.timer.timerSpeed = (float) timerMod.getNumberValue();

            e.setYaw(yaw);
            e.setPitch(pitch);

            if(mode.getMode().equalsIgnoreCase("Watchdog")){
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }

            mc.thePlayer.swingItem();
            mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, face, new Vec3(0.5D, 0.5D, 0.5D));

            if(autoJump.isEnabled() && mc.thePlayer.onGround && face != EnumFacing.UP && !mc.thePlayer.isSprinting()) {
                mc.thePlayer.jump();
            }
        }
    }

    private boolean isValidBlock(BlockPos pos) {
        return !(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid) && mc.theWorld.getBlockState(pos).getBlock().getMaterial() != Material.air;
    }

    public boolean executeFakeMove() {
        double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double x = -Math.sin(playerYaw) * MotionUtil.getBaseMoveSpeed();
        double z = Math.cos(playerYaw) * MotionUtil.getBaseMoveSpeed();
        boolean fakeGround = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ + z)).getBlock().getMaterial() != Material.air;
        boolean fakeClipped = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z)).getBlock().getMaterial() != Material.air;
        if(!fakeGround || fakeClipped)return true;

        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, mc.thePlayer.onGround));
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);

        mc.thePlayer.motionX *= 0.95;
        mc.thePlayer.motionZ *= 0.95;

        return false;
    }

}