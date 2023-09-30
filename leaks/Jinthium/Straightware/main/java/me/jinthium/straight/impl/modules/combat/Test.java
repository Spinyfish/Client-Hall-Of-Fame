package me.jinthium.straight.impl.modules.combat;

import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.modules.combat.testmode.AlsoVanilla;
import me.jinthium.straight.impl.modules.combat.testmode.Vanilla;

public class Test extends Module {

    public Test(){
        super("Testing Module", Category.COMBAT);
        this.registerModes(
                new Vanilla(),
                new AlsoVanilla()
        );
    }
}
