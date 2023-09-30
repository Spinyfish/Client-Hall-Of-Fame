package wtf.automn.utils.interfaces;

import wtf.automn.Automn;
import wtf.automn.module.ModuleManager;

public interface MM {
    ModuleManager mm = Automn.instance().moduleManager();
}
