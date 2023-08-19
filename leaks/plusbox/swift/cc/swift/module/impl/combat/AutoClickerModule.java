package cc.swift.module.impl.combat;

import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

import java.util.concurrent.ThreadLocalRandom;

public final class AutoClickerModule extends Module {

    public DoubleValue minCPS = new DoubleValue("Min CPS", 9D, 0.1d, 20, 0.1);
    public DoubleValue maxCPS = new DoubleValue("Max CPS", 12D, 0.1d, 20, 0.1);

    private long nextTime;

    public AutoClickerModule() {
        super("AutoClicker", Category.COMBAT);
        this.registerValues(this.minCPS, this.maxCPS);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = e -> {
        if (this.minCPS.getValue() > this.maxCPS.getValue())
            this.maxCPS.setValue(this.minCPS.getValue());

        if (!mc.gameSettings.keyBindAttack.isKeyDown())
            return;

        if (System.currentTimeMillis() < this.nextTime)
            return;

        double minCpsValue = this.minCPS.getValue(), maxCpsValue = this.maxCPS.getValue();
        double cpsValue = minCpsValue == maxCpsValue ? minCpsValue : ThreadLocalRandom.current().nextDouble(minCpsValue, maxCpsValue);
        this.nextTime = System.currentTimeMillis() + (long) (1000 / cpsValue);

        mc.thePlayer.swingItem();

        if (mc.objectMouseOver.entityHit != null) {
            mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
        }

    };

}
