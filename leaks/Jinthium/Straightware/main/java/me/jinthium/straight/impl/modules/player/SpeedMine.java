package me.jinthium.straight.impl.modules.player;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.checkerframework.checker.units.qual.C;

public class SpeedMine extends Module {

    private final NumberSetting speed = new NumberSetting("Speed", 1.5, 1, 3.5, 0.1);
    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost;
    private float damage;

    public SpeedMine() {
        super("SpeedMine", Category.PLAYER);
        this.addSettings(speed);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            mc.playerController.blockHitDelay = 0;
            if (pos != null && boost) {
                IBlockState blockState = mc.theWorld.getBlockState(pos);
                if (blockState == null) return;

                try {
                    damage += blockState.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer) * speed.getValue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                if (damage >= 1) {
                    try {
                        mc.theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 11);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
                    damage = 0;
                    boost = false;
                }
            }
        }
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.SENDING){
            if(event.getPacket() instanceof C07PacketPlayerDigging c07){
                switch(c07.getStatus()){
                    case START_DESTROY_BLOCK -> {
                        boost = true;
                        pos = c07.getPosition();
                        facing = c07.getFacing();
                        damage = 0;
                    }
                    case ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> {
                        boost = false;
                        pos = null;
                        facing = null;
                    }
                }
            }
        }
    };
}
