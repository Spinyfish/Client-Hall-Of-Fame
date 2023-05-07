package client.module.impl.player;

import client.event.Listener;
import client.event.annotations.EventLink;

import client.event.impl.motion.MotionEvent;
import client.event.impl.packet.PacketReceiveEvent;

import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.player.MoveUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.RandomUtils;

@ModuleInfo(name = "NoFall", description = "", category = Category.EXPLOIT)
public class NoFall extends Module {
    private int offGroundTicks, tick;
    private float fallDist;

    @Override
    protected void onEnable() {
        super.onEnable();
        tick = 1;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }

    @EventLink()
    public final Listener<MotionEvent> event = event -> {
/*
            event.setPosY(mc.thePlayer.posY);
        event.setOnGround(false);


 */

/*
        if (mc.thePlayer.fallDistance > 2){
            mc.thePlayer.motionY = -(mc.thePlayer.posY - (mc.thePlayer.posY - (mc.thePlayer.posY % (1.0 / 64.0))));
            event.setOnGround(true);
            mc.thePlayer.fallDistance = 0;
        }

 */
        if (mc.thePlayer.fallDistance > 2){
            mc.thePlayer.motionY = -(mc.thePlayer.posY - (mc.thePlayer.posY - (mc.thePlayer.posY % (1.0 / 190.0))));
            event.setOnGround(true);
            mc.thePlayer.fallDistance = 0F;
        }


    };

    private boolean isBlockUnder() {
        for (int i = (int) (mc.thePlayer.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }
}

