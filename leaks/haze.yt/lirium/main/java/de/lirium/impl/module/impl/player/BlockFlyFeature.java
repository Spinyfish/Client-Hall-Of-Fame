package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.AttackEvent;
import de.lirium.impl.events.RotationEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@ModuleFeature.Info(name = "BlockFly", description = "Bridging automatically", category = ModuleFeature.Category.PLAYER, wip = true)
public class BlockFlyFeature extends ModuleFeature {

    @Value(name = "Search Range")
    final SliderSetting<Integer> searchRange = new SliderSetting<>(3, 1, 6);

    @Value(name = "Swinging")
    final CheckBox swinging = new CheckBox(true);

    @Value(name = "Two Hand Support")
    final CheckBox twoHandSupport = new CheckBox(true);

    @Value(name = "Can place on liquid")
    final CheckBox canPlaceOnLiquid = new CheckBox(false);

    @Value(name = "Sprint")
    final CheckBox sprint = new CheckBox(false);

    private BlockPos pos, placedPos;

    @EventHandler
    public final Listener<UpdateEvent> updateEventListener = e -> {
        getPlayer().setSprinting(sprint.getValue());
        getGameSettings().keyBindSprint.pressed = sprint.getValue();
    };

    @EventHandler
    public final Listener<AttackEvent> attackEvent = e -> {
        if (!mc.playerController.getIsHittingBlock()) {
            mc.rightClickDelayTimer = 4;
            if (!getPlayer().isRowingBoat()) {
                if (!getPlayer().isHandActive())
                    for (EnumHand enumhand : EnumHand.values()) {
                        final ItemStack itemstack = getPlayer().getHeldItem(enumhand);
                        if (enumhand.equals(EnumHand.MAIN_HAND) || twoHandSupport.getValue()) {
                            if (itemstack.getItem() instanceof ItemBlock) {
                                BlockPos pos = searchPosition(searchRange.getValue(), 1);
                                if (pos != null && placedPos != null) {
                                    this.pos = pos.add(0, -1,0);
                                    int i = itemstack.func_190916_E();
                                    final EnumFacing currentFacing = RotationUtil.getFacing(placedPos, pos);
                                    if(currentFacing == null) return;
                                    final EnumActionResult result = mc.playerController.processRightClickBlock(getPlayer(), getWorld(), placedPos, currentFacing, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), enumhand);
                                    if (result == EnumActionResult.SUCCESS) {
                                        if (swinging.getValue())
                                            getPlayer().swingArm(enumhand);

                                        if (!itemstack.func_190926_b() && (itemstack.func_190916_E() != i || mc.playerController.isInCreativeMode())) {
                                            mc.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                        if (!itemstack.func_190926_b() && mc.playerController.processRightClick(getPlayer(), getWorld(), enumhand) == EnumActionResult.SUCCESS) {
                            mc.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                            return;
                        }
                    }
            }
        }
    };

    @EventHandler
    public final Listener<RotationEvent> rotationEvent = e -> {
        e.yaw = getPlayer().rotationYaw + 180;
        e.pitch = 83.5F;
    };

    private BlockPos searchPosition(int radius, int yRadius) {
        Vec3d bestPosition = null;
        for (int x = -radius; x < radius; x++) {
            for (int y = -yRadius; y < 0; y++) {
                for (int z = -radius; z < radius; z++) {
                    final Vec3d pos = getPlayer().getPositionVector().addVector(x, y, z);
                    final BlockPos blockPos = new BlockPos(pos);

                    if (getWorld().isAirBlock(blockPos)) {
                        final double radiusVal = (getPlayer().onGround || isBlockUnder(getPlayer())) ? 0 : radius;
                        if (getPlayer().getEntityBoundingBox().addCoord(0, -1, 0).expand(radiusVal, 1, radiusVal).isVecInside(new Vec3d(pos.xCoord, pos.yCoord, pos.zCoord))) {
                            if (canPlaceAt(blockPos))
                                if (bestPosition == null || pos.distanceTo(getPlayer().getPositionVector()) < bestPosition.distanceTo(getPlayer().getPositionVector())) {
                                    bestPosition = pos;
                                }
                        }
                    }
                }
            }
        }
        return bestPosition != null ? new BlockPos(bestPosition) : null;
    }

    private boolean isBlockUnder(EntityPlayer player) {
        for (int y = 0; y >= -2; y--) {
            final Vec3d vector = player.getPositionVector();
            final BlockPos position = new BlockPos(Math.floor(vector.xCoord), Math.floor(vector.yCoord), Math.floor(vector.zCoord));
            if (isValidBlock(position.add(0, y, 0))) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidBlock(BlockPos pos) {
        return !getWorld().isAirBlock(pos) && (canPlaceOnLiquid.getValue() || !getWorld().getBlockState(pos).getMaterial().isLiquid());
    }

    private boolean canPlaceAt(BlockPos pos) {
        final BlockPos test = new BlockPos(pos);
        boolean canPlace = false;
        for (int x = -1; x <= 1; x++) {
            final BlockPos check = pos.add(x, 0, 0);
            if (pos.getX() == check.getX()) continue;
            if (isValidBlock(check)) {
                placedPos = check;
                canPlace = true;
            }
        }
        for (int z = -1; z <= 1; z++) {
            final BlockPos check = test.add(0, 0, z);
            if (pos.getZ() == check.getZ()) continue;
            if (isValidBlock(check)) {
                placedPos = check;
                canPlace = true;
            }
        }

        final BlockPos check = test.add(0, -1, 0);
        if (isValidBlock(check)) {
            placedPos = check;
            canPlace = true;
        }

        return canPlace;
    }
}