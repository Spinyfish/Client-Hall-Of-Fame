/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.ghost;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Random;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPreMotion;

public class AutoClicker
extends Module {
    private final Timer clickTimer = new Timer();
    private final Setting<Double> cps = new Setting<Double>("CPS", 10.0).minimum(1.0).maximum(20.0).incrementation(1.0).describedBy("The amount of clicks per second.");
    private final Setting<Boolean> block = new Setting<Boolean>("Auto Block", false).describedBy("Whether to block automatically.");
    private final Setting<Double> blockFrequency = new Setting<Double>("Block Frequency", 4.0).minimum(0.0).maximum(10.0).incrementation(1.0).describedBy("The block frequency.").childOf(this.block);
    private final Setting<Boolean> enableRandomization = new Setting<Boolean>("Enable Randomization", false).describedBy("Whether to enable randomization.");
    private final Setting<Double> randomization = new Setting<Double>("Randomization", 3.0).minimum(0.5).maximum(5.0).incrementation(0.5).describedBy("The amount of randomization.").childOf(this.enableRandomization);
    private final Setting<Boolean> enableJitter = new Setting<Boolean>("Enable Jitter", false).describedBy("Whether to enable jittering.");
    private final Setting<Double> jitterAmount = new Setting<Double>("Jitter Amount", 0.2).minimum(0.05).maximum(0.5).incrementation(0.05).describedBy("The amount of jitter.").childOf(this.enableJitter);
    @EventLink
    private final Listener<EventPreMotion> eventUpdateEventListener = e -> {
        if (this.mc.gameSettings.keyBindAttack.isKeyDown() && this.clickTimer.hasTimeElapsed((long)((double)((long)(1000.0 / this.cps.getValue())) + (this.enableRandomization.getValue() != false ? new Random().nextDouble() * this.randomization.getValue() : 0.0)), true)) {
            if (this.block.getValue().booleanValue()) {
                this.mc.gameSettings.keyBindUseItem.pressed = false;
            }
            this.mc.clickMouse();
            if ((double)this.mc.thePlayer.ticksExisted % (10.0 - this.blockFrequency.getValue()) == 0.0 && this.block.getValue().booleanValue()) {
                this.mc.gameSettings.keyBindUseItem.pressed = true;
            }
        } else if (this.mc.gameSettings.keyBindAttack.isKeyDown() && this.enableJitter.getValue().booleanValue()) {
            this.mc.thePlayer.rotationYaw = (float)((double)this.mc.thePlayer.rotationYaw + (this.jitterAmount.getValue() + new Random().nextDouble()) * (double)(new Random().nextBoolean() ? -1 : 1));
            this.mc.thePlayer.rotationPitch = (float)((double)this.mc.thePlayer.rotationPitch + (this.jitterAmount.getValue() + new Random().nextDouble()) * (double)(new Random().nextBoolean() ? -1 : 1));
        }
    };

    public AutoClicker() {
        super("Auto Clicker", "Automatically clicks for you.", Category.GHOST);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.clickTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public Setting<Double> getCps() {
        return this.cps;
    }

    public Setting<Boolean> getBlock() {
        return this.block;
    }

    public Setting<Double> getBlockFrequency() {
        return this.blockFrequency;
    }

    public Setting<Boolean> getEnableRandomization() {
        return this.enableRandomization;
    }

    public Setting<Double> getRandomization() {
        return this.randomization;
    }

    public Setting<Boolean> getEnableJitter() {
        return this.enableJitter;
    }

    public Setting<Double> getJitterAmount() {
        return this.jitterAmount;
    }
}

