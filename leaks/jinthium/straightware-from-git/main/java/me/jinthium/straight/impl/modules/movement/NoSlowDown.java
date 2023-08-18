package me.jinthium.straight.impl.modules.movement;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.BadPacketsComponent;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.movement.noslowdown.UNCPNoSlow;
import me.jinthium.straight.impl.modules.movement.noslowdown.VanillaNoSlow;
import me.jinthium.straight.impl.modules.movement.noslowdown.WatchdogNoSlow;
import me.jinthium.straight.impl.modules.player.Scaffold;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Objects;

public class NoSlowDown extends Module {

    public NoSlowDown() {
        super("NoSlowDown", 0, Category.MOVEMENT);
        this.registerModes(
                new VanillaNoSlow(),
                new WatchdogNoSlow(),
                new UNCPNoSlow()
        );
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        this.setSuffix(this.getCurrentMode().getInformationSuffix());
    };

}
