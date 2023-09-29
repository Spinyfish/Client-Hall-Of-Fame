
package me.nekoWare.client.option;

import me.nekoWare.client.module.Module;

public class BooleanOption extends Option<Boolean> {

    public BooleanOption(Module module, String parentModuleMode, String name, boolean value) {
        super(module, parentModuleMode, name, value);
    }

    public BooleanOption(Module module, String name, boolean value) {
        super(module, null, name, value);
    }

}
