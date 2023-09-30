package dev.tenacity.event.impl.render;

import dev.tenacity.event.Event;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class ShaderEvent extends Event {

    private final boolean bloom;

    private final MultipleBoolSetting bloomOptions;

    public ShaderEvent(boolean bloom, MultipleBoolSetting bloomOptions){
        this.bloom = bloom;
        this.bloomOptions = bloomOptions;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean isBloom() {
        return bloom;
    }

    public MultipleBoolSetting getBloomOptions() { return bloomOptions; }

}
