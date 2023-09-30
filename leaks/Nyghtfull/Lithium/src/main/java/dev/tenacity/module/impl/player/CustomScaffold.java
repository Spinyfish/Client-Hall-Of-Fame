package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.player.input.LegitClickEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.player.scaffold.ScaffoldUtil;
import lombok.Getter;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class CustomScaffold extends Module {

    public NumberSetting customYaw = new NumberSetting("Custom Yaw", 180, 180, 0, 5);
    public NumberSetting customPitch = new NumberSetting("Custom Pitch", 80, 90, -90, 0.5);

    private static class BlockInfo {

        @Getter private final BlockPosition position;
        @Getter private final EnumFacing enumFacing;

        public BlockInfo(BlockPosition position, EnumFacing enumFacing) {
            this.position = position;
            this.enumFacing = enumFacing;
        }

    }

    public float[] rotations, lastRotations;
    public BlockPosition position, lastPosition;
    public CustomScaffold() {
        super("CustomScaffold", Category.PLAYER, "Automatically places blocks under you.");

        this.addSettings(
                customYaw, customPitch
        );
    }

    @Override
    public void onEnable() {

        this.rotations = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };

        this.lastRotations = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void calculateRotations() {
        this.lastRotations = this.rotations;

        this.rotations = RotationUtils.getFixedRotations(
                new float[] {
                        mc.thePlayer.rotationYaw + customYaw.getValue().floatValue(),
                        customPitch.getValue().floatValue()
                },
                this.lastRotations
        );
    }




    @Override
    public void onTickEvent(TickEvent event) {
        this.calculateRotations();
        super.onTickEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        event.setRotations(
                this.rotations[0],
                this.rotations[1]
        );

        RotationUtils.setVisualRotations(event);

        super.onMotionEvent(event);
    }

    @Override
    public void onLegitClickEvent(LegitClickEvent event) {


        super.onLegitClickEvent(event);
    }

    public boolean canPlaceAt(BlockPosition position) {
        BlockPosition test = new BlockPosition(position);

        boolean flag = false;

        for (int x = -1; x <= 1; x++) {
            final BlockPosition check = position.add(x, 0, 0);

            if (position.getX() == check.getX()) {
                continue;
            }

            if (ScaffoldUtil.isValidBlock(check)) {
                this.lastPosition = check;
                flag = true;
            }
        }
        for (int z = -1; z <= 1; z++) {
            final BlockPosition check = test.add(0, 0, z);

            if (position.getZ() == check.getZ()) {
                continue;
            }

            if (ScaffoldUtil.isValidBlock(check)) {
                this.lastPosition = check;
                flag = true;
            }
        }

        BlockPosition check = test.add(0, -1, 0);

        if (ScaffoldUtil.isValidBlock(check)) {
            this.lastPosition = check;
            flag = true;
        }

        return flag;
    }

    public BlockPosition searchPosition(int radius, int yRadius) {
        Vec3 bestPosition = null;

        for (int x = -radius; x < radius; x++) { for (int y = -yRadius; y < 0; y++) { for (int z = -radius; z < radius; z++) {
            Vec3 player = mc.thePlayer.getPositionVector().addVector(x, y, z);
            BlockPosition position = new BlockPosition(player);

            if (mc.theWorld.isAirBlock(position)) {
                double radiusVal = (mc.thePlayer.onGround || ScaffoldUtil.isBlockUnder()) ? 0 : radius;

                if (
                        mc.thePlayer.getEntityBoundingBox().addCoord(0, -1, 0)
                                .expand(radiusVal, 1, radiusVal)
                                .isVecInside(new Vec3(player.xCoord, player.yCoord, player.zCoord))
                ) {
                    if (canPlaceAt(position) && bestPosition == null || player.distanceTo(mc.thePlayer.getPositionVector()) < bestPosition.distanceTo(mc.thePlayer.getPositionVector())) {
                        bestPosition = player;
                    }
                }
            }
        }}}

        return bestPosition != null ? new BlockPosition(bestPosition) : null;
    }

}
