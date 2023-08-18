package me.jinthium.straight.impl.modules.player;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.components.BlinkComponent;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.player.nofall.*;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {

    public NoFall(){
        super("NoFall", Category.MISC);
        this.registerModes(
                new BlinkNoFall(),
                new CollideNoFall(),
                new GlitchNoFall(),
                new GroundNoFall(),
                new NoGroundNoFall(),
                new PacketNoFall(),
                new ReduceNoFall(),
                new RoundNoFall(),
                new VulcanNoFall(),
                new MatrixNoFall(),
                new InvalidNoFall(),
                new ClutchNoFall(),
                new BlockNoFall(),
                new ChunkLoadNoFall()
        );
    }
    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        this.setSuffix(this.getCurrentMode().getInformationSuffix());
    };
}
