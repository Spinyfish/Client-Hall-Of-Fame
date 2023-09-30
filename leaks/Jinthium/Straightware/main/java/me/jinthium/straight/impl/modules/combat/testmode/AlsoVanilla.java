package me.jinthium.straight.impl.modules.combat.testmode;

import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.combat.Test;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;

@ModeInfo(name = "Also Vanila", parent = Test.class)
public class AlsoVanilla extends ModuleMode<Test> {

    private final NumberSetting numberSetting = new NumberSetting("Hello", 3, 1, 10, 1);

    public AlsoVanilla() {

        this.registerSettings(numberSetting);
    }

}
