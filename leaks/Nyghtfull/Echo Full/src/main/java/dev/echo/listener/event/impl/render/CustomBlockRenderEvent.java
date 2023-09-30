package dev.echo.listener.event.impl.render;

import dev.echo.listener.event.Event;
import lombok.AllArgsConstructor;


import java.util.function.BiConsumer;

@AllArgsConstructor
public class CustomBlockRenderEvent extends Event {

    private final BiConsumer<Float, Float> transformFirstPersonItem;
    private final Runnable doBlockTransformations;
    private final float swingProgress, equipProgress;

   
    public float getSwingProgress() {
        return swingProgress;
    }

   
    public float getEquipProgress() {
        return equipProgress;
    }

   
    public void transformFirstPersonItem(float equipProgress, float swingProgress) {
        this.transformFirstPersonItem.accept(equipProgress, swingProgress);
    }

   
    public void doBlockTransformations() {
        this.doBlockTransformations.run();
    }

}
