package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.phase.NormalPhase;
import com.alan.clients.module.impl.movement.phase.WatchdogAutoPhase;
import com.alan.clients.value.impl.ModeValue;

/**
 * @author Alan
 * @since 20/10/2021
 */

@Rise
@ModuleInfo(name = "module.movement.phase.name", description = "module.movement.phase.description", category = Category.MOVEMENT)
public class Phase extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new NormalPhase("Normal", this))
            .add(new WatchdogAutoPhase("Watchdog Auto Phase", this))
            .setDefault("Normal");

}