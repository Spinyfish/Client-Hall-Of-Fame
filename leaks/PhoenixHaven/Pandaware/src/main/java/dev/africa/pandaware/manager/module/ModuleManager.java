package dev.africa.pandaware.manager.module;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.impl.container.MapContainer;
import dev.africa.pandaware.impl.event.game.GameLoopEvent;
import dev.africa.pandaware.impl.event.game.KeyEvent;
import dev.africa.pandaware.impl.module.combat.*;
import dev.africa.pandaware.impl.module.combat.antibot.AntiBotModule;
import dev.africa.pandaware.impl.module.combat.criticals.CriticalsModule;
import dev.africa.pandaware.impl.module.combat.velocity.VelocityModule;
import dev.africa.pandaware.impl.module.misc.*;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.module.movement.*;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.module.movement.highjump.HighJumpModule;
import dev.africa.pandaware.impl.module.movement.jesus.JesusModule;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.impl.module.movement.noslow.NoSlowModule;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.module.movement.spider.SpiderModule;
import dev.africa.pandaware.impl.module.movement.step.StepModule;
import dev.africa.pandaware.impl.module.player.*;
import dev.africa.pandaware.impl.module.player.antivoid.AntiVoidModule;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;
import dev.africa.pandaware.impl.module.render.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@SuppressWarnings("unchecked")
public class ModuleManager extends MapContainer<Class<? extends Module>, Module> implements Initializable, EventListenable {

    @Override
    public void init() {
        try {
            this.addModules(
                    // Combat
                    new AntiBotModule(),
                    new VelocityModule(),
                    new CriticalsModule(),
                    new KillAuraModule(),
                    new AimAssistModule(),
                    new TPAuraModule(),
                    new AutoPotModule(),
                    new ReachModule(),
                    new AutoClickerModule(),

                    // Movement
                    new SprintModule(),
                    new FlightModule(),
                    new SpeedModule(),
                    new LongJumpModule(),
                    new HighJumpModule(),
                    new ScaffoldModule(),
                    new InventoryMoveModule(),
                    new TargetStrafeModule(),
                    new StepModule(),
                    new NoSlowModule(),
                    new BlinkModule(),
                    new TimerModule(),
                    new SafeWalkModule(),
                    new JesusModule(),
                    new SpiderModule(),

                    // Visual
                    new AnimationsModule(),
                    new HUDModule(),
                    new ClickGUIModule(),
                    new ESPModule(),
                    new TargetHudModule(),
                    new ChamsModule(),
                    new ItemPhysicsModule(),
                    new DeathEffectModule(),
                    new OnlineInfoModule(),
                    new NameTagsModule(),
                    new ChestESPModule(),
                    new StreamerModule(),
                    new TracersModule(),
                    new WaveyCapesModule(),
                    new CameraNoClipModule(),
                    new FullbrightModule(),
                    new ChinaHatModule(),

                    // Player
                    new NoFallModule(),
                    new FastBowModule(),
                    new AntiServerModule(),
                    new ChestStealerModule(),
                    new InventoryManagerModule(),
                    new AutoArmorModule(),
                    new AutoToolModule(),
                    new AntiVoidModule(),
                    new FastPlaceModule(),
                    new FastEatModule(),
                    new PluginFinderModule(),
                    new PhaseModule(),
                    new BreakerModule(),

                    // Misc
                    new DisablerModule(),
                    new MiddleClickFriendModule(),
                    new AutoJoinModule(),
                    new DebuggerModule(),
                    new KillSultsModule(),
                    new AnticheatDetectorModule(),
                    new ClientBrandChangerModule(),
                    new GhostBlockModule(),
                    new No003Module(),
                    new AntiBanModule(),
                    new AutoDisableModule(),
                    new VLResetModule(),
                    new BillionaireModule()
            );

            Client.getInstance().getEventDispatcher().subscribe(this);

            if (Client.getInstance().isKillSwitch()) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addModules(Module... modules) {
        for (Module module : modules) {
            this.getMap().put(module.getClass(), module);
        }
    }

    public List<Module> getInCategory(Category category) {
        return this.getMap().values()
                .stream()
                .filter(module -> module.getData().getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Module> getAllModules() {
        return new ArrayList<>(this.getMap().values());
    }

    public <T extends Module> T getByClass(Class<T> clazz) {
        return (T) this.getMap().get(clazz);
    }

    public Optional<Module> getByName(String name) {
        return this.getMap().values()
                .stream()
                .filter(module -> module.getData().getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @EventHandler
    EventCallback<GameLoopEvent> onGameLoop = event -> this.getMap().values()
            .stream()
            .filter(module -> module.getModeSetting() != null
                    && module.getModeSetting().getValue() != null
                    && module.getCurrentMode() != null)
            .forEach(Module::updateModes);

    @EventHandler
    EventCallback<KeyEvent> onKey = event -> this.getMap().values()
            .stream()
            .filter(module -> module.getData().getKey() == event.getKey())
            .forEach(Module::toggle);
}
