package me.r.touchgrass.module.modules.movement;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;
import me.r.touchgrass.utils.TimeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import me.r.touchgrass.events.EventUpdate;
import me.r.touchgrass.utils.ReflectionUtil;
@Info(name = "FastBridge", description = "Automatically fastbridges for you", category = Category.Movement, keybind = Keyboard.KEY_Y)
public class Eagle extends Module {

    boolean place = false;

    float yaw1 = -88;
    float pitch1 = 84;
    float renderPitch1 = 84;

    public Eagle() {
        addSetting(new Setting("Auto Place", this, true));
        addSetting(new Setting("Auto Switch", this, true));
    }


    @EventTarget
    public void onUpdate(EventUpdate e) {

        try {

            if (((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)) && (!mc.gameSettings.keyBindJump.isPressed())) {
                BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
//                if (h2.settingsManager.getSettingByName(this,"Auto Place").isEnabled()) {
//                    setYawAndPitch(this.yaw1, this.pitch1, this.renderPitch1);
//                }
                if (mc.theWorld.getBlockState(bp).getBlock() == Blocks.air) {
                    ReflectionUtil.pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, true);
                    if (h2.settingsManager.getSettingByName(this,"Auto Place").isEnabled()) {

                        new Thread(() -> {
                            if (!place) {
                                place = true;
                                try {
                                    Thread.sleep(250);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                placeBlockSimple(bp, true);
                                place = false;
                            }
                        }).start();
                    }
                } else {
                    ReflectionUtil.pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, false);
                }
            }
        } catch (Exception localException) {
        }
    }

    //canonball90
    public void setYawAndPitch(float yaw1) {
        mc.thePlayer.rotationYawHead = yaw1;
        mc.thePlayer.renderYawOffset = yaw1;
    }

    public boolean placeBlockSimple(final BlockPos pos, final boolean place) {
        if (!this.doesSlotHaveBlocks(mc.thePlayer.inventory.currentItem) && h2.settingsManager.getSettingByName(this,"Auto Switch").isEnabled()) {
            mc.thePlayer.inventory.currentItem = this.getFirstHotBarSlotWithBlocks();
        }
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity = mc.getRenderViewEntity();
        final double d0 = entity.posX;
        final double d2 = entity.posY;
        final double d3 = entity.posZ;
        final Vec3 eyesPos = new Vec3(d0, d2 + mc.thePlayer.getEyeHeight(), d3);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                final Vec3 hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {

                    double diffX = hitVec.xCoord - eyesPos.xCoord;
                    double diffY = hitVec.yCoord - eyesPos.yCoord;
                    double diffZ = hitVec.zCoord - eyesPos.zCoord;

                    double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

                    float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
                    float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

                    //mc.getRenderViewEntity().rotationYaw = angles[0];
                    //mc.getRenderViewEntity().rotationPitch = angles[1];
                    if (place) {
                        //mc.thePlayer.rotationYaw = angles[0];
                        //mc.thePlayer.rotationPitch = angles[1];
                        this.yaw1 = yaw;
                        this.pitch1 = pitch;
                        this.renderPitch1 = pitch;
                        setYawAndPitch(this.yaw1);
                        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                        mc.thePlayer.swingItem();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static Block getBlock(final BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    public int getFirstHotBarSlotWithBlocks() {
        for (int i = 0; i < 9; ++i) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return 0;
    }
    public boolean doesSlotHaveBlocks(final int slotToCheck) {
        return mc.thePlayer.inventory.getStackInSlot(slotToCheck) != null && mc.thePlayer.inventory.getStackInSlot(slotToCheck).getItem() instanceof ItemBlock && mc.thePlayer.inventory.getStackInSlot(slotToCheck).stackSize > 0;
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }
    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }
}