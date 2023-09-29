package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.teleport.WatchdogTeleport;
import com.alan.clients.value.impl.ModeValue;

/**
 * @author Alan
 * @since 18/11/2022
 */

@Rise
@ModuleInfo(name = "module.movement.teleport.name", description = "module.movement.teleport.description", category = Category.MOVEMENT)
public class Teleport extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new WatchdogTeleport("Watchdog", this))
            .setDefault("Vanilla");

}