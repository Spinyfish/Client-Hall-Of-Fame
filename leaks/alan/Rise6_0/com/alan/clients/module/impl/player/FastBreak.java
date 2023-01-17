package com.alan.clients.module.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.value.impl.NumberValue;

@Rise
@ModuleInfo(name = "Fast Break", description = "module.player.fastbreak.description", category = Category.PLAYER)
public final class FastBreak extends Module {

    private final NumberValue speed = new NumberValue("Speed", this, 0.5, 0, 1, 0.1);

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        mc.playerController.blockHitDelay = 0;

        if (mc.playerController.curBlockDamageMP > 1 - speed.getValue().doubleValue())
            mc.playerController.curBlockDamageMP = 1;
    };
}
