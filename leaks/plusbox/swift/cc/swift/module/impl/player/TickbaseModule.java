package cc.swift.module.impl.player;

import cc.swift.Swift;
import cc.swift.events.KeyEvent;
import cc.swift.events.TimeEvent;
import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.module.impl.combat.KillAuraModule;
import cc.swift.util.ChatUtil;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

public class TickbaseModule extends Module {

    public final DoubleValue maxBalance = new DoubleValue("Max Balance", 100D, 0, 5000, 50);
    public final DoubleValue range = new DoubleValue("Range", 3D, 0.1,7D, 0.1);

    public TickbaseModule() { // insane csgo tickbase manipulation https://www.unknowncheats.me/forum/counterstrike-global-offensive/331325-tickbase-manipulation.html
        super("Tickbase", Category.PLAYER);

        this.registerValues(this.maxBalance, this.range);
    }

    private double balance, lastTime;

    private KillAuraModule killAuraModule;


    @Handler
    public final Listener<TimeEvent> timeEventListener = event -> {
        if (mc.thePlayer == null) return;

        if (this.killAuraModule == null)
            this.killAuraModule = Swift.INSTANCE.getModuleManager().getModule(KillAuraModule.class);

        double maxBalance = this.maxBalance.getValue() / 1000D;
        double toCharge = maxBalance - this.balance;

        boolean shouldCharge = toCharge > 0 && (killAuraModule.target == null || !killAuraModule.isEnabled());

        boolean shouldShift = this.balance > 0 && killAuraModule.target != null && mc.thePlayer.getDistanceToEntity(killAuraModule.target) > range.getValue();

        if (shouldShift) {
            this.balance = 0;
        } else if (shouldCharge) {
            this.balance += Math.min(toCharge, event.getTime() - this.lastTime);
        }

        this.lastTime = event.getTime();
        event.setTime(event.getTime() - this.balance);
        event.setLimitTicks(false);
    };

    @Override
    public void onDisable() {
        this.balance = 0;
    }

    @Override
    public void onEnable() {
        this.balance = 0;
        this.lastTime = (double) (System.nanoTime() / 1000000L) / 1000.0D;
    }
}
