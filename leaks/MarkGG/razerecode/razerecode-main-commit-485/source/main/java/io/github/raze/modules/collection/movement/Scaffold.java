package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.utilities.collection.math.RotationUtil;
import io.github.raze.utilities.collection.math.TimeUtil;
import io.github.raze.utilities.collection.world.BlockUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class Scaffold extends BaseModule {

    public ArraySetting rotationMode;
    public BooleanSetting noSwing,safeWalk,slowDown, sprint;

    private final TimeUtil timer;
    private static float lastYaw = 0, lastPitch = 0;
    private static BlockPosition lastBlockPos = null;
    private static EnumFacing lastFacing = null;

    public Scaffold() {
        super("Scaffold", "Scaffold walk", ModuleCategory.MOVEMENT);
        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                rotationMode = new ArraySetting(this, "Rotation Mode", "Advanced", "Advanced", "Simple"),
                noSwing = new BooleanSetting(this, "No Swing", false),
                slowDown = new BooleanSetting(this, "Slowdown", false),
                safeWalk = new BooleanSetting(this, "Safewalk", true),
                sprint = new BooleanSetting(this, "Sprint", true)
        );
        timer = new TimeUtil();
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {

            BlockPosition playerPos = new BlockPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
            BlockUtil.BlockData info = getBlockData(playerPos);

            if(slowDown.get() || mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                mc.thePlayer.motionX *= 0.81;
                mc.thePlayer.motionZ *= 0.81;
            }

            if(!sprint.get())
                mc.thePlayer.setSprinting(false);

            if (lastBlockPos != null && lastFacing != null) {
                assert info != null;
                float[] keepRots = getRotations(info.pos, info.facing);
                lastYaw = keepRots[0];
                lastPitch = keepRots[1];
            }

            eventMotion.setYaw(lastYaw);
            eventMotion.setPitch(lastPitch);

            ItemStack stackToPlace = setStackToPlace();

            if (mc.theWorld == null || mc.thePlayer == null)
                return;

            assert info != null;
            float[] rots = getRotations(info.pos, info.facing);

            switch(rotationMode.get()) {
                case "Advanced":
                    eventMotion.setYaw(rots[0]);
                    eventMotion.setPitch(rots[1]);
                    lastYaw = eventMotion.yaw;
                    lastPitch = eventMotion.pitch;
                    mc.thePlayer.renderYawOffset = rots[1];
                    mc.thePlayer.rotationYawHead = rots[0];
                    break;
                case "Simple":
                    eventMotion.setYaw((float) (88 + Math.random() * 2));
                    eventMotion.setPitch((float) (mc.thePlayer.rotationPitch + 180 + Math.random() * 3 - Math.random() * 3));
                    lastYaw = eventMotion.yaw;
                    lastPitch = eventMotion.pitch;
                    mc.thePlayer.renderYawOffset = ((float) (88 + Math.random() * 2));
                    mc.thePlayer.rotationYawHead = ((float) (mc.thePlayer.rotationYaw + 180 + Math.random() * 3 - Math.random() * 3));
                    break;
            }


            if(timer.elapsed(70, false)) {
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stackToPlace, info.pos, info.facing, RotationUtil.getVectorForRotation(eventMotion.yaw, (eventMotion.pitch)));
                if(noSwing.get())
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                else
                    mc.thePlayer.swingItem();
            }
        }
    }

    private float[] getRotations(BlockPosition paramBlockPos, EnumFacing paramEnumFacing) {
        double d1 = paramBlockPos.getX() + 0.5 - mc.thePlayer.posX + paramEnumFacing.getFrontOffsetX() / 2.0;
        double d2 = paramBlockPos.getZ() + 0.5 - mc.thePlayer.posZ + paramEnumFacing.getFrontOffsetZ() / 2.0;
        double d3 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - (paramBlockPos.getY() + 0.5);
        double d4 = MathHelper.sqrt_double(d1 * d1 + d2 * d2);

        float f1 = (float)(Math.atan2(d2, d1) * 180 / Math.PI) - 90;
        float f2 = (float)(Math.atan2(d3, d4) * 180 / Math.PI);

        if (f1 < 0)
            f1 += 360;

        return new float[] {
                f1,
                f2
        };
    }

    private BlockUtil.BlockData getBlockData(BlockPosition pos) {
        return (mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) ?
                new BlockUtil.BlockData(pos.add(0, -1, 0), EnumFacing.UP) : (
                (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) ?
                        new BlockUtil.BlockData(pos.add(-1, 0, 0), EnumFacing.EAST) : (
                        (mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) ?
                                new BlockUtil.BlockData(pos.add(1, 0, 0), EnumFacing.WEST) : (
                                (mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) ?
                                        new BlockUtil.BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH) : (
                                        (mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) ?
                                                new BlockUtil.BlockData(pos.add(0, 0, 1), EnumFacing.NORTH) : null))));
    }

    private ItemStack setStackToPlace() {
        ItemStack block = mc.thePlayer.getCurrentEquippedItem();

        if (block != null && block.getItem() != null)
            block = null;

        int slot = mc.thePlayer.inventory.currentItem;

        for (int index = 0; index < 9; index++) {

            if (mc.thePlayer.inventoryContainer.getSlot(index + 36).getHasStack()) {
                mc.thePlayer.inventoryContainer.getSlot(index + 36).getStack();
            }

        }
        return block;
    }

}
