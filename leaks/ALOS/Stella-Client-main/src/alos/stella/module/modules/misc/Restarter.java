package alos.stella.module.modules.misc;

import alos.stella.Stella;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;

    @ModuleInfo(name = "Restarter", description = "Restarter", category = ModuleCategory.MISC)
    public class Restarter extends Module {

        @Override
        public void onEnable() {
            super.onEnable();
            Stella.INSTANCE.startClient();
            Stella.INSTANCE.isStarting();
            setState(false);
        }
    }
