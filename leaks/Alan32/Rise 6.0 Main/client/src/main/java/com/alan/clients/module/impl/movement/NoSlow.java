package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.noslow.*;
import com.alan.clients.value.impl.ModeValue;

/**
 * @author Alan
 * @since 20/10/2021
 */
@Rise
@ModuleInfo(name = "module.movement.noslow.name", description = "module.movement.noslow.description", category = Category.MOVEMENT)
public class NoSlow extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaNoSlow("Vanilla", this))
            .add(new NCPNoSlow("NCP", this))
            .add(new NewNCPNoSlow("New NCP", this))
            .add(new IntaveNoSlow("Intave", this))
            .add(new OldIntaveNoSlow("Old Intave", this))
            .add(new VariableNoSlow("Variable", this))
            .add(new PredictionNoSlow("Prediction", this))
            .setDefault("Vanilla");
}