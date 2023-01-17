package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.speed.*;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ModeValue;

/**
 * @author Patrick (implementation)
 * @since 10/19/2021
 */
@Rise
@ModuleInfo(name = "Speed", description = "module.movement.speed.description", category = Category.MOVEMENT)
public class Speed extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaSpeed("Vanilla", this))
            .add(new StrafeSpeed("Strafe", this))
            .add(new InteractSpeed("Interact", this))
            .add(new MatrixSpeed("Matrix", this))
            .add(new VulcanSpeed("Vulcan", this))
            .add(new WatchdogSpeed("Watchdog", this))
            .add(new NCPSpeed("NCP", this))
            .add(new VerusSpeed("Verus", this))
            .add(new BlocksMCSpeed("BlocksMC", this))
            .add(new MineMenClubSpeed("MineMenClub", this))
            .add(new KoksCraftSpeed("KoksCraft", this))
            .setDefault("Vanilla");

    private final BooleanValue disableOnTeleport = new BooleanValue("Disable on Teleport", this, false);
    private final BooleanValue stopOnDisable = new BooleanValue("Stop on Disable", this, false);

    @Override
    protected void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        if (stopOnDisable.getValue()) {
            MoveUtil.stop();
        }
    }

    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (disableOnTeleport.getValue()) {
            this.toggle();
        }
    };
}