package com.alan.clients.module.impl.ghost;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.ghost.wtap.LegitWTap;
import com.alan.clients.module.impl.ghost.wtap.SilentWTap;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.NumberValue;

/**
 * @author Alan
 * @since 29/01/2021
 */

@Rise
@ModuleInfo(name = "module.ghost.wtap.name", description = "module.ghost.wtap.description", category = Category.GHOST)
public class WTap extends Module {
    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new LegitWTap("Legit", this))
            .add(new SilentWTap("Silent", this))
            .setDefault("Legit");

    public final NumberValue chance = new NumberValue("WTap Chance", this, 100, 0, 100, 1);
}