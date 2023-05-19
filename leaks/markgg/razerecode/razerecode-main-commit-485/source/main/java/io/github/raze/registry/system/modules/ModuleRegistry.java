package io.github.raze.registry.system.modules;

import io.github.raze.events.collection.game.EventKeyboard;
import io.github.raze.events.collection.visual.EventRender2D;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.collection.client.ActiveModules;
import io.github.raze.modules.collection.client.ModuleManager;
import io.github.raze.modules.collection.client.Watermark;
import io.github.raze.modules.collection.combat.*;
import io.github.raze.modules.collection.movement.*;
import io.github.raze.modules.collection.player.*;
import io.github.raze.modules.collection.visual.*;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.modules.system.ingame.ModuleIngame;
import io.github.raze.utilities.system.BaseUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleRegistry implements BaseUtility {

    public List<BaseModule> modules;

    public List<BaseModule> getModules() {
        return modules;
    }

    public ModuleRegistry() {
        this.modules = new ArrayList<>();
    }

    public void bootstrap() {
        this.register(

                // Combat
                new Aura(),
                new Velocity(),
                new NoClickDelay(),
                new AutoClicker(),

                // Movement
                new AntiVoid(),
                new NoFall(),
                new NoSlow(),
                new Sprint(),
                new Speed(),
                new Flight(),
                new Spider(),
                new Eagle(),
                new Phase(),
                new Glide(),
                new Scaffold(),
                new SafeWalk(),

                // Player
                new HackerDetector(),
                new AutoRespawn(),
                new ChestStealer(),
                new AutoTool(),
                new FastPlace(),
                new FastEat(),
                new Disabler(),
                new Blink(),
                new Regen(),
                new PingSpoof(),
                new Timer(),

                // Visual
                new Animations(),
                new FullBright(),
                new AntiBlind(),
                new CustomCape(),

                // Client
                new ModuleManager(),
                new Watermark(),
                new ActiveModules()

        );
    }

    public void register(BaseModule input) {
        modules.add(input);
    }

    public void register(BaseModule... input) {
        modules.addAll(Arrays.asList(input));
    }

    public <module extends BaseModule> module getModule(Class<module> input) {
        return (module) modules.stream().filter(module -> module.getClass() == input).findFirst().get();
    }

    public <module extends BaseModule> module getModule(String input) {
        return (module) modules.stream().filter(module -> module.getName().equalsIgnoreCase(input)).findFirst().get();
    }

    public List<BaseModule> getModulesByCategory(ModuleCategory input) {
        return modules.stream().filter(module -> module.getCategory().equals(input)).collect(Collectors.toList());
    }

    public List<BaseModule> getEnabledModules() {
        return modules.stream().filter(BaseModule::isEnabled).collect(Collectors.toList());
    }

    @SubscribeEvent
    private void onKeyboard(EventKeyboard eventKeyboard) {
        getModules().forEach(module -> {
            if (module.getKeyCode() == eventKeyboard.getKeyCode()) {
                module.toggle();
            }
        });
    }

    @SubscribeEvent
    private void onRender2D(EventRender2D eventRender2D) {
        getModules().forEach(module -> {
            if (module instanceof ModuleIngame) {
                ModuleIngame ingame = (ModuleIngame) module;

                if (mc.currentScreen == null && module.isEnabled()) {
                    ingame.renderIngame();
                }
            }
        });
    }
}
