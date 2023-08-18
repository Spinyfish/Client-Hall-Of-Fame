package me.jinthium.straight.impl.modules.movement;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.BlinkComponent;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.combat.TargetStrafe;
import me.jinthium.straight.impl.modules.movement.speed.BlocksMCSpeed;
import me.jinthium.straight.impl.modules.movement.speed.VanillaSpeed;
import me.jinthium.straight.impl.modules.movement.speed.VulcanSpeed;
import me.jinthium.straight.impl.modules.movement.speed.WatchdogSpeed;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.network.BalanceUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import org.lwjglx.input.Keyboard;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Speed extends Module {
    public Speed(){
        super("Speed", Keyboard.KEY_F, Category.MOVEMENT);
        this.registerModes(
                new VanillaSpeed(),
                new VulcanSpeed(),
                new WatchdogSpeed(),
                new BlocksMCSpeed()
        );
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        this.setSuffix(this.getCurrentMode().getInformationSuffix());
    };
}
