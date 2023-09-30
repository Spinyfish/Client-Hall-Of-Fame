package me.jinthium.straight.impl.modules.movement;

import best.azura.irc.utils.Wrapper;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.combat.TargetStrafe;
import me.jinthium.straight.impl.modules.movement.flight.*;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjglx.input.Keyboard;

public class Flight extends Module {

    public Flight(){
        super("Flight", Keyboard.KEY_G, Category.MOVEMENT);
        this.registerModes(
                new MotionFly(),
                new BlockFly(),
                new VulcanBlockFly(),
                new WatchdogFly(),
                new BlockDropFly(),
                new UNCPFly()
        );
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        this.setSuffix(this.getCurrentMode().getInformationSuffix());
    };
}
