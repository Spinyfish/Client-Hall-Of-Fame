package me.jinthium.straight.impl.modules.movement.flight;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

@ModeInfo(name = "Block", parent = Flight.class)
public class BlockFly extends ModuleMode<Flight> {

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            RotationUtils.setRotations(event, RotationUtils.calculate(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)), 40);
            if (MovementUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) {
                PacketUtil.sendPacket(new C0APacketAnimation());

                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                        mc.thePlayer.getCurrentEquippedItem(),
                        new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ),
                        EnumFacing.UP, mc.objectMouseOver.hitVec);
            }
        }
    };
}
