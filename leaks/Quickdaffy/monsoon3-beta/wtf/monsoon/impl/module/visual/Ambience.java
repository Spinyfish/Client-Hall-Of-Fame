/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.function.Supplier;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventUpdate;

public class Ambience
extends Module {
    public final Setting<Time> time = new Setting<Time>("Time", Time.NIGHT).describedBy("The time.");
    private static final Timer timer = new Timer();
    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        if (this.time.getValue() == Time.DYNAMIC && timer.hasTimeElapsed(11500L, false)) {
            timer.reset();
        }
    };

    public Ambience() {
        super("Ambience", "Change the world time.", Category.VISUAL);
        this.setMetadata(() -> StringUtil.formatEnum(this.time.getValue()));
    }

    public static enum Time {
        MORNING(() -> 23000),
        DAY(() -> 30000),
        NIGHT(() -> 15000),
        MIDNIGHT(() -> 18000),
        DYNAMIC(() -> (int)timer.getTime() * 2);

        private final Supplier<Integer> time;

        private Time(Supplier<Integer> time) {
            this.time = time;
        }

        public Supplier<Integer> getTime() {
            return this.time;
        }
    }
}

