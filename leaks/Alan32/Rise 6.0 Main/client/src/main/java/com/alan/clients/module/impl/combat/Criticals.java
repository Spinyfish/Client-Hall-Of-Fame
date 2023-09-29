package com.alan.clients.module.impl.combat;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.combat.criticals.EditCriticals;
import com.alan.clients.module.impl.combat.criticals.NoGroundCriticals;
import com.alan.clients.module.impl.combat.criticals.PacketCriticals;
import com.alan.clients.module.impl.combat.criticals.WatchdogCriticals;
import com.alan.clients.value.impl.ModeValue;

@Rise
@ModuleInfo(name = "module.combat.criticals.name", description = "module.combat.criticals.description", category = Category.COMBAT)
public final class Criticals extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new PacketCriticals("Packet", this))
            .add(new EditCriticals("Edit", this))
            .add(new NoGroundCriticals("No Ground", this))
            .add(new WatchdogCriticals("Watchdog", this))
            .setDefault("Packet");
}
