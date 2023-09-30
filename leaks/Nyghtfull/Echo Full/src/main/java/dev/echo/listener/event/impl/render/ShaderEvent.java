package dev.echo.listener.event.impl.render;

import dev.echo.listener.event.Event;
import dev.echo.module.settings.impl.MultipleBoolSetting;


public class ShaderEvent extends Event {

    private final boolean bloom;

    private final MultipleBoolSetting bloomOptions;

    public ShaderEvent(boolean bloom, MultipleBoolSetting bloomOptions){
        this.bloom = bloom;
        this.bloomOptions = bloomOptions;
    }

   
    public boolean isBloom() {
        return bloom;
    }

    public MultipleBoolSetting getBloomOptions() { return bloomOptions; }

}
