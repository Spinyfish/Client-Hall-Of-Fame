/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.ghost;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventUpdate;

public class Reach
extends Module {
    Setting<Boolean> randomized = new Setting<Boolean>("Randomized", true).describedBy("Whether the reach is randomized [max-min]");
    Setting<Double> minReach = new Setting<Double>("MinReach", 3.0).minimum(0.1).maximum(8.0).incrementation(0.1).describedBy("Maximum hit reach");
    Setting<Double> maxReach = new Setting<Double>("MaxReach", 3.0).minimum(0.1).maximum(8.0).incrementation(0.1).describedBy("Minimum hit reach");
    Setting<Double> vBlockReach = new Setting<Double>("BlockReach", 3.0).minimum(0.1).maximum(8.0).incrementation(0.1).describedBy("Total block reach");
    public double hitReach;
    public double blockReach;
    @EventLink
    private final Listener<EventUpdate> updateListener = e -> {
        this.hitReach = this.randomized.getValue() != false ? Math.random() * (this.maxReach.getValue() - this.minReach.getValue()) + this.minReach.getValue() : this.maxReach.getValue();
        this.blockReach = this.vBlockReach.getValue();
    };

    public Reach() {
        super("Reach", "Extends your hit and block reach", Category.GHOST);
    }
}

