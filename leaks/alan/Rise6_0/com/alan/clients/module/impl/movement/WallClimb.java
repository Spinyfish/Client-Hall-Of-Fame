package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.wallclimb.*;
import com.alan.clients.value.impl.ModeValue;

/**
 * @author Alan
 * @since 20/10/2021
 */
@Rise
@ModuleInfo(name = "Wall Climb", description = "module.movement.wallclimb.description", category = Category.MOVEMENT)
public class WallClimb extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VulcanWallClimb("Vulcan", this))
            .add(new VerusWallClimb("Verus", this))
            .add(new KauriWallClimb("Kauri", this))
            .add(new WatchdogWallClimb("Watchdog", this))
            .add(new MineMenClubWallClimb("MineMenClub", this))
            .setDefault("Vulcan");
}