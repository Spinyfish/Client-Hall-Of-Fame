package cc.swift.module.impl.player;

import cc.swift.events.EventState;
import cc.swift.events.UpdateEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.player.InventoryUtils;
import cc.swift.util.player.RotationUtil;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class NukerModule extends Module {

    public final DoubleValue range = new DoubleValue("Range", 4D, 0.1, 6D, 0.1);
    public final BooleanValue breakSurronding = new BooleanValue("Break Surronding", false);
    public final ModeValue<RotationMode> rotationMode = new ModeValue<>("Rotation Mode", RotationMode.values());

    public NukerModule() {
        super("Nuker", Category.PLAYER);
        this.registerValues(this.range, this.breakSurronding, this.rotationMode);
    }

    private BlockPos blockPos;
    private boolean swap;
    private float[] rotations;
    public boolean rotating;

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        mc.playerController.setCurBlockDamageMP(0);
        super.onEnable();
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (blockPos == null) return;

        rotations = RotationUtil.getRotationsToVector(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                new Vec3(blockPos));
        RotationUtil.fixGCD(rotations, new float[]{mc.thePlayer.prevServerYaw, mc.thePlayer.prevServerPitch});
    };

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        blockPos = scanForBlock();

        Block block;
        if (blockPos == null) {
            rotating = false;
            swap = false;
            return;
        }

        block = mc.theWorld.getBlockState(blockPos).getBlock();

        if (event.getState() != EventState.PRE) return;

        rotating = false;

        if (rotationMode.getValue() == RotationMode.FULL) {
            rotating = true;
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        }

        if (mc.playerController.getCurBlockDamageMP() == 0) {
            if (rotationMode.getValue() == RotationMode.START || rotationMode.getValue() == RotationMode.STARTEND) {
                rotating = true;
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
        }

        int slot = mc.thePlayer.inventory.currentItem;
        if (!(block instanceof BlockBed) && findOptimalSlot(block) != -1) {
            slot = findOptimalSlot(block);
        }
        float hardness = InventoryUtils.getPlayerRelativeBlockHardness(mc.theWorld, blockPos, slot);

        mc.playerController.setCurBlockDamageMP(mc.playerController.getCurBlockDamageMP() + hardness);
        mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), blockPos, (int) (mc.playerController.getCurBlockDamageMP() * 10 - 1));

        if (!(block instanceof BlockBed)) {
            if (mc.playerController.getCurBlockDamageMP() + hardness >= 1 && !(mc.playerController.getCurBlockDamageMP() >= 1)) {
                if (slot != -1) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot));
                    swap = true;
                }
            }
        }

        if (mc.playerController.getCurBlockDamageMP() >= 1) {
            if (rotationMode.getValue() == RotationMode.END || rotationMode.getValue() == RotationMode.STARTEND) {
                rotating = true;
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            //mc.playerController.onPlayerDestroyBlock(blockPos,EnumFacing.UP);
            if (swap) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            mc.playerController.setCurBlockDamageMP(0);
        }
    };

    public BlockPos scanForBlock() {

        for (int x = -range.getValue().intValue(); x <= range.getValue().intValue(); x++) {
            for (int y = -range.getValue().intValue(); y <= range.getValue().intValue(); y++) {
                for (int z = -range.getValue().intValue(); z <= range.getValue().intValue(); z++) {

                    final Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(x, y, z)).getBlock();
                    final BlockPos position = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);

                    if (!(block instanceof BlockBed)) {
                        continue;
                    }

                    if (breakSurronding.getValue()) {
                        double hardness = Double.MAX_VALUE;
                        boolean empty = false;

                        BlockPos addVec = null;
                        for (int addX = -1; addX <= 1; addX++) {
                            for (int addY = 0; addY <= 1; addY++) {
                                for (int addZ = -1; addZ <= 1; addZ++) {
                                    if (empty)
                                        continue;
                                    if (Math.abs(addX) + Math.abs(addY) + Math.abs(addZ) != 1) {
                                        continue;
                                    }
                                    Block pblock = mc.theWorld.getBlockState(new BlockPos(position.getX() + addX, position.getY() + addY, position.getZ() + addZ)).getBlock();
                                    if (pblock instanceof BlockBed) {
                                        continue;
                                    } else if (pblock instanceof BlockAir || pblock instanceof BlockLiquid) {
                                        empty = true;
                                        continue;
                                    }
                                    double possibleHardness = pblock.getBlockHardness(mc.theWorld, new BlockPos(position.getX() + addX, position.getY() + addY, position.getZ() + addZ));

                                    if (possibleHardness < hardness) {
                                        hardness = possibleHardness;
                                        addVec = position.add(addX, addY, addZ);
                                    }
                                }
                            }
                        }

                        if (!empty) {
                            assert addVec != null;
                            if (addVec.equals(position)) {
                                return null;
                            } else {
                                return addVec;
                            }
                        }
                    }

                    return position;
                }
            }
        }

        return null;
    }

    private int findOptimalSlot(Block block) {
        float maxStrength = 1.0F;
        int optimalSlot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            float strength = itemStack != null ? itemStack.getStrVsBlock(block) : 0;

            if (strength > maxStrength) {
                optimalSlot = i;
                maxStrength = strength;
            }
        }

        return optimalSlot;
    }

    public enum RotationMode {
        OFF, START, END, STARTEND, FULL
    }

}
