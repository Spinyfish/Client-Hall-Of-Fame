package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.inventorymove.*;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;

/**
 * @author Alan
 * @since 20/10/2021
 */

@Rise
@ModuleInfo(name = "module.movement.inventorymove.name", description = "module.movement.inventorymove.description", category = Category.MOVEMENT)
public class InventoryMove extends Module {

    private final ModeValue bypassMode = new ModeValue("Bypass Mode", this)
            .add(new NormalInventoryMove("Normal", this))
            .add(new BufferAbuseInventoryMove("Buffer Abuse", this))
            .add(new CancelInventoryMove("Cancel", this))
            .add(new WatchdogInventoryMove("Watchdog", this))
            .setDefault("Normal");
}
