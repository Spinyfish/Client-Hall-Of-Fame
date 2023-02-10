/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 12.08.22, 09:22
 */
package dev.myth.features.movement;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.network.play.client.C03PacketPlayer;

@Feature.Info(name = "Timer", category = Feature.Category.MOVEMENT)
public class TimerFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.BASIC);
    public final NumberSetting timer = new NumberSetting("Timer", 1, 0.1, 3, 0.1);

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        switch (mode.getValue()) {
            case BASIC:
                setTimer(timer.getValue().floatValue());
                break;
            case ADVANCED:
                double timerus = MathUtil.getRandomValue(1, timer.getValue());
                setTimer((float) timerus);
                break;

        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        switch (mode.getValue()) {
            case ADVANCED:
                if (packetEvent.getPacket() instanceof C03PacketPlayer) {
                    if (getPlayer().ticksExisted % (int) MathUtil.getRandomValue(1, timer.getValue() * 3) == 0)
                        packetEvent.setCancelled(true);
                }
                break;
        }
    };

    @Override
    public void onDisable() {
        setTimer(1f);
        super.onDisable();
    }

    public enum Mode {
        BASIC("Basic"),
        ADVANCED("Advanced");

        private final String name;

        Mode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
