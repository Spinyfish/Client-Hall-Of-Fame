package de.lirium.impl.module;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.event.EventListener;
import de.lirium.base.feature.Manager;
import de.lirium.impl.events.KeyEvent;
import de.lirium.impl.module.impl.combat.*;
import de.lirium.impl.module.impl.misc.*;
import de.lirium.impl.module.impl.movement.*;
import de.lirium.impl.module.impl.player.*;
import de.lirium.impl.module.impl.ui.*;
import de.lirium.impl.module.impl.visual.*;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ModuleManager extends EventListener implements Manager<ModuleFeature> {

    private final ArrayList<ModuleFeature> features = new ArrayList<>();

    public static ArrayList<ModuleFeature> sortedModList = new ArrayList<>();

    @Getter
    private static AntiBotFeature antiBot;

    @Getter
    private static TargetFeature target;

    @Getter
    private static FriendFeature friend;

    @Getter
    private static FlightFeature flight;

    public ModuleManager() {
        init();
        features.addAll(Arrays.asList(new AuraFeature(),
                new VelocityFeature(),
                new FlightFeature(),
                new HighJumpFeature(),
                new NoSlowFeature(),
                new FriendFeature(),
                new SpeedFeature(),
                new BetterHotbarFeature(),
                new SprintFeature(),
                new StepFeature(),
                new TimeShiftFeature(),
                new DisablerFeature(),
                new NoFallFeature(),
                new AutoArmorFeature(),
                new ClickGuiFeature(),
                new HUDFeature(),
                new TabGUIFeature(),
                new WeatherFeature(),
                new ESPFeature(),
                new ContainerESPFeature(),
                new OldBlockingFeature(),
                new MidClickFriendsFeature(),
                new LongJumpFeature(),
                new InventoryMoveFeature(),
                new StealerFeature(),
                new BlockFlyFeature(),
                new ChatUnblockerFeature(),
                new TeleportFeature(),
                new GalaxyFeature(),
                new CorrectMovementFeature(),
                new TargetStrafeFeature(),
                new AntiVoidFeature(),
                new ParticleTimerFeature(),
                new MiniMeFeature(),
                new PingSpoofFeature(),
                new ChatTranslatorFeature(),
                new AntiCactusFeature(),
                new FuckerFeature(),
                new SwingModifierFeature(),
                new AntiToastsFeature(),
                new AntiStrikeFeature(),
                new ScaffoldWalkFeature(),
                new ParticleESPFeature(),
                new AntiBotFeature(),
                new TargetFeature(),
                new VanishDetectorFeature(),
                new SilentMurderFeature(),
                new AntiFireballFeature(),
                new FireModifierFeature(),
                new PlayerResizerFeature(),
                new FreeLookFeature(),
                new NoRotateFeature(),
                new AutoTranslateFeature(),
                new WorldColorFeature(),
                new BlinkFeature(),
                new DebugFeature()));
        features.sort(Comparator.comparing(ModuleFeature::getName));
        features.removeIf(moduleFeature -> moduleFeature.wip && !Client.INSTANCE.isDeveloper());
        sortedModList.addAll(features);

        antiBot = get(AntiBotFeature.class);
        friend = get(FriendFeature.class);
        target = get(TargetFeature.class);
        flight = get(FlightFeature.class);
        target.init();
    }

    @Override
    public ArrayList<ModuleFeature> getFeatures() {
        return features;
    }


    @Override
    public ModuleFeature get(Type type) {
        return this.getFeatures().stream().filter(m -> m.getClass().getName().equals(type.getTypeName())).findFirst().orElse(null);
    }

    @Override
    public <U extends ModuleFeature> U get(Class<U> clazz) {
        final ModuleFeature feature = this.getFeatures().stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        if (feature == null) return null;
        return clazz.cast(feature);
    }

    public ModuleFeature get(String name) {
        return this.getFeatures().stream().filter(m -> m.getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))).findFirst().orElse(null);
    }

    @EventHandler
    public final Listener<KeyEvent> keyEventListener = e -> {
        this.getFeatures().forEach(m -> {
            if (m.getKeyBind() == e.key) {
                m.setEnabled(!m.isEnabled());
            }
        });
    };
}