package dev.tenacity.module.impl.movement.speeds;

import dev.tenacity.event.ListenerAdapter;
import dev.tenacity.module.impl.movement.speeds.impl.*;
import dev.tenacity.utils.Utils;

import java.util.HashMap;

public class SpeedMode extends ListenerAdapter implements Utils {

    public String name;

    public static final HashMap<Class<? extends SpeedMode>, SpeedMode> modes = new HashMap<>();

    public SpeedMode(String name) {
        this.name = name;
    }

    public static <T extends SpeedMode> T get(Class <? extends SpeedMode> input) {
        return (T) modes.get(input);
    }

    public static SpeedMode get(String name) {
        return modes.values().stream().filter(jump -> jump.getName().equals(name)).findFirst().orElse(null);
    }

    public static void init() {
        modes.put(LatestNCPSpeed.class, new LatestNCPSpeed());
        modes.put(Watchdog2Speed.class, new Watchdog2Speed());
        modes.put(NoRulesSpeed.class, new NoRulesSpeed());
        modes.put(VanillaSpeed.class, new VanillaSpeed());
        modes.put(WatchdogSpeed.class, new WatchdogSpeed());
        modes.put(StrafeSpeed.class, new StrafeSpeed());
        modes.put(PolarSpeed.class, new PolarSpeed());
        modes.put(Kokscraft.class, new Kokscraft());
    }

    public String getName() {
        return name;
    }

    public void onEnable() { /* */ };
    public void onDisable() { /* */ };
}
