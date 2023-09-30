package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.math.random.RandomUtils;

@ModuleInfo(name = "FastPlace", description = "Places faster, obviously wtf did you think this does? JESUS CHRIST YOU'RE DUMB AF.", category = Category.PLAYER)
public class FastPlaceModule extends Module {

    private static final NumberSetting speed = new NumberSetting("Speed", 4, 0, 0, 1);
    private static final BooleanSetting randomization = new BooleanSetting("Randomization", true);

    public FastPlaceModule() {
        this.registerSettings(speed, randomization);
    }

    public static int getSpeed() {
        return randomization.getValue() ? RandomUtils.nextInt(0, 2) : speed.getValue().intValue();
    }
}