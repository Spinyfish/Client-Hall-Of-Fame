/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.managers;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.logger.Logger;
import dev.myth.api.manager.Manager;
import dev.myth.events.KeyEvent;
import dev.myth.features.visual.*;
import dev.myth.main.ClientMain;
import lombok.Getter;
import dev.myth.features.combat.*;
import dev.myth.features.movement.*;
import dev.myth.features.player.*;
import dev.myth.features.exploit.*;

import dev.myth.features.display.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeatureManager implements Manager {

    @Getter
    private final Map<Class<? extends Feature>, Feature> features = new HashMap<>();

    @Override
    public void run() {
        /* Combat */
        addFeature(KillAuraFeature.class);
        addFeature(CopsAndCrimsFeature.class);
        addFeature(AntiBotFeature.class);
        addFeature(TargetStrafeFeature.class);
        addFeature(VelocityFeature.class);
        addFeature(CriticalsFeature.class);
        addFeature(AutoHeadFeature.class);
        addFeature(AutoPotFeature.class);

        /* Movement */
        addFeature(FlightFeature.class);
        addFeature(SprintFeature.class);
        addFeature(SpeedFeature.class);
        addFeature(NoSlowFeature.class);
        addFeature(LongJumpFeature.class);
        addFeature(StepFeature.class);
        addFeature(TimerFeature.class);

        /* Player */
        addFeature(AntiVoidFeature.class);
        addFeature(AutoPlayFeature.class);
        addFeature(BedBreakerFeature.class);
        addFeature(BlinkFeature.class);
        addFeature(ChestStealerFeature.class);
        addFeature(InvManagerFeature.class);
        addFeature(InvMoveFeature.class);
        addFeature(NoFallFeature.class);
        addFeature(ScaffoldFeature.class);
        addFeature(AutoToolFeature.class);

        /* Visual */
        addFeature(FullBrightFeature.class);
        addFeature(SessionInfoFeature.class);
        addFeature(AnimationsFeature.class);
        addFeature(JumpCircle.class);
        addFeature(ScoreboardFeature.class);
        addFeature(ESPFeature.class);
        addFeature(WorldTimeFeature.class);
        addFeature(TrailsFeature.class);
        addFeature(BlurFeature.class);
        addFeature(ShadowFeature.class);
        addFeature(GlowFeature.class);
        addFeature(GlowESPFeature.class);
        addFeature(WorldColorFeature.class);
        addFeature(ChinaHatFeature.class);
        addFeature(DamageColorFeature.class);
        addFeature(ShaderFeature.class);

        /* Display */
        addFeature(HUDFeature.class);
        addFeature(ClickGuiFeature.class);
        addFeature(TargetHUDFeature.class);

        /* Exploits */
        addFeature(DisablerFeature.class);
        addFeature(NoRotateSetFeature.class);
        addFeature(PacketLoggerFeature.class);
        addFeature(PhaseFeature.class);
        addFeature(RegenFeature.class);
        addFeature(PingSpoofFeature.class);

        this.features.keySet().removeIf(feature -> ClientMain.INSTANCE.getBuildtype() != ClientMain.BuildType.DEVELOPMENT && feature.isAnnotationPresent(Feature.Debug.class));
        this.features.values().forEach(Feature::reflectSettings);

    }

    @Override
    public void shutdown() {}

    /** Add */
    private void addFeature(final Class<? extends Feature> clazz) {
        try {
            final Feature feature = clazz.newInstance();
            this.features.put(clazz, feature);
        } catch (InstantiationException | IllegalAccessException ignored) {}
    }
    public <T extends Feature> T getFeature(final Class<T> clazz) {
        return (T) this.features.get(clazz);
    }
    public <T extends Feature> T getFeature(final String name) {
        return (T) this.features.values().stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    /** Filters */
    public List<Feature> getEnabledFeatures() {
        return this.features.values().stream()
                .filter(Feature::isEnabled)
                .collect(Collectors.toList());
    }
    public List<Feature> getFeaturesInCategory(final Feature.Category category) {
        return this.features.values().stream()
                .filter(feature -> feature.getCategory() == category)
                .sorted(Comparator.comparing(Feature::getName))
                .collect(Collectors.toList());
    }

    @Handler
    public final Listener<KeyEvent> keyEventListener = event -> {
//        if(Minecraft.getMinecraft().currentScreen instanceof GuiChat) return;

//        Logger.doLog("Key pressed: " + event.getKey());

        this.getFeatures().values().stream().filter(feature -> feature.getKeyBind() == event.getKey()).forEach(Feature::toggle);
    };
}
