package dev.africa.pandaware.impl.module.movement.jesus;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.movement.jesus.modes.*;

@ModuleInfo(name = "Jesus", category = Category.MOVEMENT)
public class JesusModule extends Module {
    public JesusModule() {
        this.registerModes(
                new VanillaJesus("Vanilla", this),
                new NCPJesus("NCP", this)
        );
    }
}
