package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.module.impl.movement.sneak.HoldSneak;
import com.alan.clients.module.impl.movement.sneak.NCPSneak;
import com.alan.clients.module.impl.movement.sneak.StandardSneak;

/**
 * @author Auth
 * @since 25/06/2022
 */
@Rise
@ModuleInfo(name = "Sneak", description = "module.movement.sneak.description", category = Category.MOVEMENT)
public class Sneak extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new StandardSneak("Standard", this))
            .add(new HoldSneak("Hold", this))
            .add(new NCPSneak("NCP", this))
            .setDefault("Standard");
}