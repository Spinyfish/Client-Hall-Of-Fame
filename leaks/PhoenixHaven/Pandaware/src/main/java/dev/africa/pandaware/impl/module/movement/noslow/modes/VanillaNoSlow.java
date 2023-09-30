package dev.africa.pandaware.impl.module.movement.noslow.modes;

import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.module.movement.noslow.NoSlowModule;

public class VanillaNoSlow extends ModuleMode<NoSlowModule> {
    public VanillaNoSlow(String name, NoSlowModule parent) {
        super(name, parent);
    }
}
