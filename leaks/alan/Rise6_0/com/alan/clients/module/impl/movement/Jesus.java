package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.JumpEvent;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.module.impl.movement.jesus.GravityJesus;
import com.alan.clients.module.impl.movement.jesus.KarhuJesus;
import com.alan.clients.module.impl.movement.jesus.NCPJesus;
import com.alan.clients.module.impl.movement.jesus.VanillaJesus;
import com.alan.clients.util.player.PlayerUtil;

@Rise
@ModuleInfo(name = "Jesus", description = "module.movement.jesus.description", category = Category.MOVEMENT)
public class Jesus extends Module {

    public final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaJesus("Vanilla", this))
            .add(new GravityJesus("Gravity", this))
            .add(new KarhuJesus("Karhu", this))
            .add(new NCPJesus("NCP", this))
            .setDefault("Vanilla");

    private final BooleanValue allowJump = new BooleanValue("Allow Jump", this, true);

    @EventLink()
    public final Listener<JumpEvent> onJump = event -> {

        if (!allowJump.getValue() && PlayerUtil.onLiquid()) {
            event.setCancelled(true);
        }
    };
}