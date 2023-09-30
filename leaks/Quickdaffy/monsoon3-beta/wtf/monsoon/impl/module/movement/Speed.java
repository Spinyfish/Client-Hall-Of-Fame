/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Arrays;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventUpdateEnumSetting;
import wtf.monsoon.impl.module.movement.speed.BlocksMCSpeed;
import wtf.monsoon.impl.module.movement.speed.CubecraftSpeed;
import wtf.monsoon.impl.module.movement.speed.FuncraftSpeed;
import wtf.monsoon.impl.module.movement.speed.NCPSpeed;
import wtf.monsoon.impl.module.movement.speed.NorulesSpeed;
import wtf.monsoon.impl.module.movement.speed.VanillaSpeed;
import wtf.monsoon.impl.module.movement.speed.VerusSpeed;
import wtf.monsoon.impl.module.movement.speed.WatchdogSpeed;
import wtf.monsoon.impl.module.movement.speed.YPortSpeed;

public class Speed
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.VANILLA).describedBy("How to control speed");
    @EventLink
    private Listener<EventPreMotion> eventPreMotionListener = e -> {};
    @EventLink
    private Listener<EventUpdateEnumSetting> eventUpdateEnumSettingListener = e -> {
        if (e.getOldValue() instanceof Mode && e.getSetting().equals(this.mode)) {
            ((Mode)e.getOldValue()).getProcessor().onDisable();
            ((Mode)e.getNewValue()).getProcessor().onEnable();
        }
    };

    public Speed() {
        super("Speed", "Go faster", Category.MOVEMENT);
        this.setMetadata(() -> {
            if (this.mode.getValue() == Mode.WATCHDOG) {
                return "Watchdog (" + StringUtil.formatEnum(((WatchdogSpeed)Mode.WATCHDOG.getProcessor()).watchdogMode.getValue()) + ")";
            }
            return StringUtil.formatEnum(this.mode.getValue());
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.mode.getValue().getProcessor().onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Arrays.stream(Mode.values()).forEach(val -> val.getProcessor().onDisable());
    }

    private static enum Mode {
        VANILLA(new VanillaSpeed(Wrapper.getModule(Speed.class))),
        WATCHDOG(new WatchdogSpeed(Wrapper.getModule(Speed.class))),
        FUNCRAFT(new FuncraftSpeed(Wrapper.getModule(Speed.class))),
        BLOCKSMC(new BlocksMCSpeed(Wrapper.getModule(Speed.class))),
        YPORT(new YPortSpeed(Wrapper.getModule(Speed.class))),
        VERUS(new VerusSpeed(Wrapper.getModule(Speed.class))),
        NORULES(new NorulesSpeed(Wrapper.getModule(Speed.class))),
        CUBECRAFT(new CubecraftSpeed(Wrapper.getModule(Speed.class))),
        NCP(new NCPSpeed(Wrapper.getModule(Speed.class)));

        public ModeProcessor processor;

        private Mode(ModeProcessor processor) {
            this.processor = processor;
        }

        public ModeProcessor getProcessor() {
            return this.processor;
        }
    }
}

