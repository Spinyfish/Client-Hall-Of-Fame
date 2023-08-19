/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:17
 */

package cc.swift.module;

import cc.swift.events.KeyEvent;
import cc.swift.module.impl.combat.*;
import cc.swift.module.impl.misc.*;
import cc.swift.module.impl.movement.*;
import cc.swift.module.impl.player.*;
import cc.swift.module.impl.render.*;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;

public final class ModuleManager {

    @Getter
    private final HashMap<Class<? extends Module>, Module> modules = new HashMap<>();

    public void init() {
        // Combat
        register(new KillAuraModule());
        register(new VelocityModule());
        register(new HitboxesModule());
        register(new AutoClickerModule());
        register(new AutoHealModule());
        register(new AntiBotModule());

        // Movement
        register(new SprintModule());
        register(new SpeedModule());
        register(new FlightModule());
        register(new NoSlowDownModule());
        register(new LongJumpModule());
        register(new JesusModule());
        register(new SpiderModule());
        register(new StepModule());

        // Player
        register(new ChestStealerModule());
        register(new MovementCorrectionModule());
        register(new TickbaseModule());
        register(new NoFallModule());
        register(new ScaffoldModule());
        register(new FastBreakModule());
        register(new InventoryManagerModule());
        register(new TimerModule());
        register(new FastPlaceModule());
        register(new NukerModule());

        // Render
        register(new ClickGuiModule());
        register(new HudModule());
        register(new FullbrightModule());
        register(new ESPModule());
        register(new BlockAnimationModule());
        register(new ChestESPModule());
        register(new ChamsModule());
        register(new ParticleTimerModule());
        register(new FreelookModule());
        register(new NametagsModule());

        //HUD
        // register(new WatermarkComponent());

        // Misc
        register(new DisablerModule());
        register(new InvMoveModule());
        register(new AutoToolModule());
        register(new BlinkModule());

        getModule(HudModule.class).toggle();
        getModule(ClickGuiModule.class).setKey(Keyboard.KEY_RSHIFT);


    }

    public void register(Module module) {
        modules.put(module.getClass(), module);
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.get(clazz);
    }

    public <T extends Module> T getModule(String name) {
        return (T) modules.values().stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Handler
    public final Listener<KeyEvent> keyEventListener = event -> {
        modules.values().stream().filter(module -> module.getKey() == event.getKey()).forEach(Module::toggle);
    };

}
