package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author Alan
 * @since 31.03.2022
 */

public class BlockFlight extends Mode<Flight>  {

    public BlockFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        RotationComponent.setRotations(new Vector2f(mc.thePlayer.rotationYaw, 90), 100, MovementFix.OFF);
        if (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) {
            PacketUtil.send(new C0APacketAnimation());

            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.getCurrentEquippedItem(),
                    new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ),
                    EnumFacing.UP, mc.objectMouseOver.hitVec);
        }
    };
}