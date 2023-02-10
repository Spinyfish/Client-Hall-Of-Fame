/**
 * @project Myth
 * @author CodeMan
 * @at 30.12.22, 18:29
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.rotation.RotationUtil;
import dev.myth.events.LoadWorldEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;

import java.util.ArrayList;

@Feature.Info(
        name = "BedBreaker",
        description = "Breaks beds",
        category = Feature.Category.PLAYER
)
public class BedBreakerFeature extends Feature {

    public final BooleanSetting instant = new BooleanSetting("Instant", false);

    private ArrayList<BlockPos> ownedBeds = new ArrayList<>();
    private BlockPos currentPos;
    private float[] rotations;

    @Handler
    public final Listener<LoadWorldEvent> loadWorldEventListener = event -> {
        ownedBeds.clear();
        rotations = null;
        currentPos = null;
    };

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {

//        ownedBeds.clear();
        if(event.getState() == EventState.UPDATE) {
            if (currentPos != null) {
                if (MC.theWorld.getBlockState(currentPos).getBlock() != Blocks.bed) {
                    currentPos = null;
                } else {
                    rotations = RotationUtil.getRotationsToVector(currentPos.toVec3().addVector(0.5, 0.5, 0.5), getPlayer().getPositionEyes(1F));

                    Vec3 vec = RotationUtil.getVectorForRotation(rotations[1], rotations[0]);
                    EnumFacing facing = EnumFacing.getFacingFromVector((float) vec.xCoord, (float) vec.yCoord, (float) vec.zCoord);

                    facing = facing.getOpposite();

                    if (instant.getValue()) {
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, facing));
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, facing));
                    } else {
                        MC.playerController.onPlayerDamageBlock(currentPos, facing);
                    }
                    sendPacket(new C0APacketAnimation());
                }
            } else rotations = null;
        }

        if(event.getState() == EventState.PRE) {
            new Thread(() -> {
                double range = 3;
                double closest = 10;
                BlockPos bestPos = null;
                for (double x = -range; x <= range; x++) {
                    for (double z = -range; z <= range; z++) {
                        for (double y = -range; y <= range; y++) {
                            BlockPos pos = new BlockPos(MC.thePlayer.posX + x, MC.thePlayer.posY + y, MC.thePlayer.posZ + z);

                            if (MC.theWorld.getBlockState(pos).getBlock() == Blocks.bed) {
                                if (ownedBeds.isEmpty()) {
                                    ownedBeds.add(pos);
                                    for (EnumFacing facing : EnumFacing.values()) {
                                        if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
                                        if (MC.theWorld.getBlockState(pos.offset(facing)).getBlock() == Blocks.bed) {
                                            ownedBeds.add(pos.offset(facing));
                                        }
                                    }
                                    doLog("Located own Bed at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                                    continue;
                                }
                                if (!ownedBeds.contains(pos)) {
                                    if (bestPos == null || MC.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) < closest) {
                                        bestPos = pos;
                                        closest = MC.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ());
                                    }
                                }
                            }
                        }
                    }
                }
                currentPos = bestPos;
            }).start();

            if(currentPos != null && rotations != null) {
                RotationUtil.doRotation(event, rotations, 180, true);
            }
        }
    };

}
