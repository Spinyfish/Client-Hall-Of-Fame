package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.chunk.ChunkSystem;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.AttackEvent;
import de.lirium.impl.events.RotationEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ModuleFeature.Info(name = "Fucker", description = "Breaks blocks for you", category = ModuleFeature.Category.PLAYER)
public class FuckerFeature extends ModuleFeature {

    @Value(name = "Range")
    private SliderSetting<Double> range = new SliderSetting<>(3.0, 1.0, 6.0);

    @Value(name = "Delay")
    private SliderSetting<Long> delay = new SliderSetting<>(0L, 0L, 1000L);

    @Value(name = "Bed")
    private CheckBox bed = new CheckBox(true);

    @Value(name = "Egg")
    private CheckBox egg = new CheckBox(true);

    @Value(name = "Raycast")
    private CheckBox rayCast = new CheckBox(false);

    @Value(name = "Rotations")
    private CheckBox rotations = new CheckBox(true);

    @Value(name = "Mouse Sensitivity")
    private CheckBox mouseSensitivity = new CheckBox(true);

    private final TimeHelper timeHelper = new TimeHelper();

    private final Map<Block, Interaction> interactions = new HashMap<>();

    private BlockPos currentPos;

    private boolean startDestroying;

    private final Predicate<Block> predicate = block -> {
        if (block instanceof BlockBed && bed.getValue())
            return true;
        if (block instanceof BlockDragonEgg && egg.getValue())
            return true;
        return false;
    };

    @EventHandler
    public final Listener<RotationEvent> rotationEvent = e -> {
        if (rotations.getValue() && currentPos != null) {
            final float[] rotations = RotationUtil.getRotation(currentPos, getWorld().getBlockState(currentPos).getBoundingBox(getWorld(), currentPos), mouseSensitivity.getValue());
            e.yaw = rotations[0];
            e.pitch = rotations[1];
        }
    };

    @EventHandler
    public final Listener<AttackEvent> attackEvent = e -> {
        interactions.keySet().forEach(block -> {
            if (predicate.test(block)) {
                final Interaction interaction = interactions.get(block);
                if (interaction != null) {
                    ChunkSystem.getPositions(block).forEach(blockPos -> {
                        if (getPlayer().getDistance(blockPos) <= range.getValue() && (!rayCast.getValue() || (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos().equals(blockPos)))) {
                            final EnumFacing side = rayCast.getValue() ? mc.objectMouseOver.sideHit : EnumFacing.DOWN;
                            final Vec3d facing = rayCast.getValue() ? mc.objectMouseOver.hitVec : new Vec3d(0.5, 0.5, 0.5);
                            switch (interaction) {
                                case CLICK:
                                    currentPos = blockPos;
                                    if (timeHelper.hasReached(delay.getValue())) {
                                        mc.playerController.processRightClickBlock(getPlayer(), getWorld(), blockPos, side, facing, EnumHand.MAIN_HAND);
                                        getPlayer().swingArm(EnumHand.MAIN_HAND);
                                        ChunkSystem.removeBlock(block, blockPos);
                                    }
                                    break;
                                case BREAK:
                                    sendPacketUnlogged(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, side));
                                    currentPos = blockPos;
                                    if (timeHelper.hasReached(delay.getValue())) {
                                        sendPacketUnlogged(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, side));
                                        ChunkSystem.removeBlock(block, blockPos);
                                    }
                                    getPlayer().swingArm(EnumHand.MAIN_HAND);
                                    break;
                            }
                        }
                    });

                    if (currentPos != null && getWorld().isAirBlock(currentPos)) {
                        currentPos = null;
                    } else if (currentPos == null)
                        timeHelper.reset();
                }
            }
        });
    };

    @Override
    public void onEnable() {
        super.onEnable();
        startDestroying = false;
        interactions.clear();
        interactions.put(Blocks.DRAGON_EGG, Interaction.CLICK);
        interactions.put(Blocks.BED, Interaction.BREAK);
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
        super.onDisable();
    }

    enum Interaction {
        BREAK,
        CLICK;
    }
}
